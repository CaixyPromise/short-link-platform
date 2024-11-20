package com.caixy.shortlink.manager.Authorization.factory.TokenLoginFactory;

import com.caixy.shortlink.common.ErrorCode;
import com.caixy.shortlink.exception.BusinessException;
import com.caixy.shortlink.manager.Authorization.factory.AuthorizationService;
import com.caixy.shortlink.model.convertor.user.UserConvertor;
import com.caixy.shortlink.model.entity.User;
import com.caixy.shortlink.model.enums.UserRoleEnum;
import com.caixy.shortlink.model.vo.user.LoginUserVO;
import com.caixy.shortlink.model.vo.user.UserVO;
import com.caixy.shortlink.utils.RedisUtils;
import com.caixy.shortlink.utils.ServletUtils;
import com.caixy.shortlink.utils.StringUtils;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpServletRequest;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Token登录验证服务
 *
 * @author CAIXYPROMISE
 * @since 2024/10/27 02:08
 */
@Service
@ConditionalOnProperty(name = "login.type", havingValue = "TOKEN")
@Slf4j
public class TokenLoginService implements AuthorizationService
{

    private static final UserConvertor userConvertor = UserConvertor.INSTANCE;

    private final RedisUtils redisUtils;

    public TokenLoginService(RedisUtils redisUtils)
    {
        this.redisUtils = redisUtils;
    }

    private static final String TOKEN_CACHE_KEY = "token:login:";
    private static final String TOKEN_CLAIMS_ID_KEY = "tokenId";
    private static final String TOKEN_CLAIMS_USER_ID_KEY = "userId";
    private static final Pattern pattern = Pattern.compile("^(?:Bearer\\s)?(.+)$");


    // Redis中存储所有活跃tokenId的集合key
    private static final String TOKEN_ACTIVE_SET = "token:activeTokens";

    // Redis中存储用户与tokenId映射的key前缀
    private static final String TOKEN_USER_KEY_PREFIX = "token:user:";

    // 是否限制单点登录
    @Value("${login.singleLogin:false}")
    private boolean singleLogin;

    // 令牌自定义标识
    @Value("${login.token.header:Authorization}")
    private String headerKey;

    // 令牌秘钥
    @Value("${login.token.secret:CAIXYPROMISE}")
    private String secret;

    /**
     * token过期时间（默认60分钟）
     */
    @Value("${login.token.expireTime:60}")
    private Long expireTime;

    /**
     * 刷新token时间（默认20分钟）
     */
    @Value("${login.token.autoRefreshTime:20}")
    private Long autoRefreshTime;

    private final Key key = new SecretKeySpec(secret.getBytes(), SignatureAlgorithm.HS256.getJcaName());

    @Override
    public String getName()
    {
        return "Token验证服务";
    }

    @Override
    public Boolean checkLogin(HttpServletRequest request)
    {
        String token = extractToken(request);
        if (StringUtils.isNotEmpty(token))
        {
            UserVO userVO = getUserVOFromCache(token);
            return userVO != null;
        }
        return false;
    }

    @Override
    public Boolean checkLogin()
    {
        return checkLogin(ServletUtils.getRequest());
    }

    @Override
    public Boolean asRole(UserRoleEnum roleEnum)
    {
        UserVO userVO = getLoginUserPermitNull(ServletUtils.getRequest());
        return userVO != null && userVO.getUserRole().equals(roleEnum);
    }

    @Override
    public void checkLoginOrThrow()
    {
        UserVO userVO = getLoginUserPermitNull(ServletUtils.getRequest());
        if (userVO == null)
        {
            throw new BusinessException(ErrorCode.FORBIDDEN_ERROR, "未登录");
        }
    }

    @Override
    public LoginUserVO doLogin(User user, HttpServletRequest request)
    {
        TokenInfo tokenInfo = createToken(user);
        String token = tokenInfo.getToken();
        String tokenId = tokenInfo.getTokenId();

        UserVO userVO = userConvertor.toVO(user);
        userVO.setToken(token);
        userVO.setLoginTime(System.currentTimeMillis());
        userVO.setExpireTime(userVO.getLoginTime() + expireTime * 60 * 1000);

        // 设置登录信息
        setUserLoginInfo(userVO, request);


        // 处理单点登录
        if (singleLogin)
        {
            // 强制下线之前的登录
            forceLogout(user.getId());
        }

        // 存储token到Redis
        redisUtils.setObject(getTokenCacheKey(tokenId), userVO, expireTime * 60);

        // 将tokenId添加到活跃token集合
        redisUtils.addToSet(TOKEN_ACTIVE_SET, tokenId);

        // 将tokenId添加到用户的token集合
        String userTokenKey = getUserTokenKey(user.getId());
        redisUtils.addToSet(userTokenKey, tokenId);
        return getLoginUserVO(userVO);
    }


    @Override
    public boolean doLogout()
    {
        HttpServletRequest request = ServletUtils.getRequest();
        String token = extractToken(request);
        if (StringUtils.isNotEmpty(token))
        {
            Map<String, Object> claims = parseToken(token);
            if (claims != null)
            {
                String tokenId = (String) claims.get(TOKEN_CLAIMS_ID_KEY);
                Long userId = ((Number) claims.get(TOKEN_CLAIMS_USER_ID_KEY)).longValue();
                if (StringUtils.isNotEmpty(tokenId))
                {
                    // 删除Redis中的token信息
                    redisUtils.delete(getTokenCacheKey(tokenId));
                    // 从活跃token集合中移除
                    redisUtils.removeFromSet(TOKEN_ACTIVE_SET, tokenId);
                    // 从用户的token集合中移除
                    String userTokenKey = getUserTokenKey(userId);
                    redisUtils.removeFromSet(userTokenKey, tokenId);
                    return true;
                }
            }
        }
        return false;
    }


    @Override
    public UserVO getLoginUser(HttpServletRequest request)
    {
        UserVO loginUser = getLoginUserPermitNull(request);
        if (loginUser == null)
        {
            throw new BusinessException(ErrorCode.FORBIDDEN_ERROR, "未登录");
        }
        return loginUser;
    }

    @Override
    public UserVO getLoginUserPermitNull(HttpServletRequest request)
    {
        String token = extractToken(request);
        if (StringUtils.isNotEmpty(token))
        {
            UserVO userVO = getUserVOFromCache(token);
            if (userVO != null)
            {
                try
                {
                    checkExpireTime(userVO, token);
                }
                catch (Exception e)
                {
                    log.error("Token检查过期时间异常：{}", e.getMessage(), e);
                    return null;
                }
                return userVO;
            }
        }
        return null;
    }

    @Override
    public Boolean isAdmin()
    {
        return asRole(UserRoleEnum.ADMIN);
    }

    @Override
    public List<UserVO> getAllLoggedInUsers(int currentSize, int size)
    {
        Set<String> tokenIds = redisUtils.getMembersFromSet(TOKEN_ACTIVE_SET);
        List<UserVO> userList = new ArrayList<>();
        if (tokenIds != null && !tokenIds.isEmpty())
        {
            List<String> tokenIdList = new ArrayList<>(tokenIds);

            // 实现分页
            int totalTokens = tokenIdList.size();
            int fromIndex = (currentSize - 1) * size;
            int toIndex = Math.min(fromIndex + size, totalTokens);
            if (fromIndex >= totalTokens)
            {
                return userList; // 返回空列表
            }
            List<String> pagedTokenIds = tokenIdList.subList(fromIndex, toIndex);

            for (String tokenId : pagedTokenIds)
            {
                Optional<UserVO> userVOOptional = redisUtils.getObject(getTokenCacheKey(tokenId), UserVO.class);
                if (userVOOptional.isPresent())
                {
                    userList.add(userVOOptional.get());
                }
                else
                {
                    // 如果缓存中没有找到对应的 UserVO，可能是过期的 tokenId，需要移除
                    redisUtils.removeFromSet(TOKEN_ACTIVE_SET, tokenId);
                }
            }
        }
        return userList;
    }

    @Override
    public int getLoggedInUserCount()
    {
        Long size = redisUtils.getSetSize(TOKEN_ACTIVE_SET);
        return size != null ? size.intValue() : 0;
    }

    @Override
    public void forceLogout(Long userId)
    {
        String userTokenKey = getUserTokenKey(userId);
        Set<String> tokenIds = redisUtils.getMembersFromSet(userTokenKey);
        if (tokenIds != null && !tokenIds.isEmpty())
        {
            // 批量删除 Token 信息
            List<String> keysToDelete = tokenIds.stream()
                                                .map(this::getTokenCacheKey)
                                                .collect(Collectors.toList());
            redisUtils.delete(keysToDelete);
            // 从活跃 Token 集合中移除
            redisUtils.removeFromSet(TOKEN_ACTIVE_SET, tokenIds.toArray(new String[0]));
            // 删除用户的 Token 集合
            redisUtils.delete(userTokenKey);
        }

    }


    private TokenInfo createToken(User user)
    {
        String tokenId = UUID.randomUUID().toString();

        Map<String, Object> claims = new HashMap<>();
        claims.put(TOKEN_CLAIMS_ID_KEY, tokenId);
        claims.put(TOKEN_CLAIMS_USER_ID_KEY, user.getId());

        String token = createToken(claims);

        return new TokenInfo(token, tokenId);
    }

    private String createToken(Map<String, Object> claims)
    {

        return Jwts.builder()
                   .setSubject("exampleSubject")
                   .signWith(key, SignatureAlgorithm.HS256)
                   .compact();
    }

    private void refreshToken(UserVO userVO, String tokenId)
    {
        userVO.setLoginTime(System.currentTimeMillis());
        userVO.setExpireTime(userVO.getLoginTime() + expireTime * 60 * 1000);

        redisUtils.setObject(getTokenCacheKey(tokenId), userVO, expireTime * 60); // 转换为秒
    }


    private String getTokenCacheKey(String tokenId)
    {
        return TOKEN_CACHE_KEY + tokenId;
    }

    private void checkExpireTime(UserVO userVO, String token)
    {
        try
        {
            Map<String, Object> claims = parseToken(token);
            if (claims != null)
            {
                String tokenId = (String) claims.get(TOKEN_CLAIMS_ID_KEY);
                if (StringUtils.isNotEmpty(tokenId))
                {
                    Long expire = redisUtils.getExpire(getTokenCacheKey(tokenId));

                    if (expire != null && expire < autoRefreshTime * 60)
                    {
                        refreshToken(userVO, tokenId);
                        log.info("Token即将过期，已自动刷新");
                    }
                }
            }
        }
        catch (Exception e)
        {
            log.error("Error in checkExpireTime: {}", e.getMessage(), e);
            // 根据需要处理异常，例如：抛出异常或返回特定结果
        }
    }


    private Map<String, Object> parseToken(String token)
    {
        try
        {
            return Jwts.parserBuilder()
                       .setSigningKey(key)
                       .build()
                       .parseClaimsJws(token)
                       .getBody();
        }
        catch (ExpiredJwtException e)
        {
            log.warn("Token已过期：{}", token);
            throw new BusinessException(ErrorCode.FORBIDDEN_ERROR, "Token已过期");
        }
        catch (JwtException e)
        {
            log.warn("Token无效：{}", token);
            throw new BusinessException(ErrorCode.FORBIDDEN_ERROR, "Token无效");
        }
        catch (Exception e)
        {
            log.error("Token解析异常：{}", e.getMessage(), e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "Token解析异常");
        }
    }


    private UserVO getUserVOFromCache(String token)
    {
        if (StringUtils.isNotEmpty(token))
        {
            Map<String, Object> claims = parseToken(token);
            if (claims != null)
            {
                String tokenId = (String) claims.get(TOKEN_CLAIMS_ID_KEY);
                if (StringUtils.isNotEmpty(tokenId))
                {
                    Optional<UserVO> userVOOptional = redisUtils.getObject(getTokenCacheKey(tokenId), UserVO.class);
                    return userVOOptional.orElse(null);
                }
            }
        }
        return null;
    }


    private String getUserTokenKey(Long userId)
    {
        return TOKEN_USER_KEY_PREFIX + userId;
    }

    /**
     * 从Header提取token
     *
     * @author CAIXYPROMISE
     * @version 1.0
     * @since 2024/10/31 上午1:20
     */
    private String extractToken(HttpServletRequest request)
    {
        String token = request.getHeader(headerKey);
        if (StringUtils.isNotEmpty(token))
        {
            // 正则表达式提取token
            Matcher matcher = pattern.matcher(token);
            if (matcher.find())
            {
                return matcher.group(1);
            }
            else
            {
                return null;
            }
        }
        return null;
    }


    @Data
    private static class TokenInfo
    {
        private String token;
        private String tokenId;

        public TokenInfo(String token, String tokenId)
        {
            this.token = token;
            this.tokenId = tokenId;
        }
    }
}
