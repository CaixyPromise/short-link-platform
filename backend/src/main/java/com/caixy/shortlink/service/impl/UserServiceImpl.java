package com.caixy.shortlink.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.caixy.shortlink.common.ErrorCode;
import com.caixy.shortlink.constant.CommonConstant;
import com.caixy.shortlink.constant.UserConstant;
import com.caixy.shortlink.exception.BusinessException;
import com.caixy.shortlink.exception.ThrowUtils;
import com.caixy.shortlink.manager.email.models.captcha.CommonEmailCaptchaDTO;
import com.caixy.shortlink.manager.email.models.enums.EmailCaptchaBizEnum;
import com.caixy.shortlink.manager.email.models.captcha.EmailActiveUserDTO;
import com.caixy.shortlink.manager.UploadManager.annotation.FileUploadActionTarget;
import com.caixy.shortlink.manager.file.FileActionHelper;
import com.caixy.shortlink.manager.file.domain.UploadContext;
import com.caixy.shortlink.manager.redis.RedisManager;
import com.caixy.shortlink.mapper.UserMapper;
import com.caixy.shortlink.model.convertor.user.UserConvertor;
import com.caixy.shortlink.model.dto.file.FileUploadAfterActionResult;
import com.caixy.shortlink.model.dto.file.UploadFileDTO;
import com.caixy.shortlink.model.dto.file.UploadFileRequest;
import com.caixy.shortlink.model.dto.user.*;
import com.caixy.shortlink.model.entity.FileInfo;
import com.caixy.shortlink.model.entity.FileReference;
import com.caixy.shortlink.model.entity.User;
import com.caixy.shortlink.model.enums.*;
import com.caixy.shortlink.model.vo.user.RegistrationInfo;
import com.caixy.shortlink.model.vo.user.UserVO;
import com.caixy.shortlink.service.ApiKeyService;
import com.caixy.shortlink.service.EmailService;
import com.caixy.shortlink.service.UserService;
import com.caixy.shortlink.manager.file.strategy.FileActionStrategy;
import com.caixy.shortlink.utils.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.caixy.shortlink.utils.StringUtils;

import org.redisson.api.RBloomFilter;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 用户服务实现
 */
@Service
@Slf4j
@FileUploadActionTarget(FileActionBizEnum.USER_AVATAR)
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements InitializingBean, UserService,
        FileActionStrategy
{
    private static final UserConvertor userConvertor = UserConvertor.INSTANCE;
    private final RedisManager redisManager;
    private final RedissonClient redissonClient;
    private final EmailService emailService;
    private RBloomFilter<String> nickNameBloomFilter;
    private RBloomFilter<String> emailBloomFilter;
    private final ApiKeyService apiKeyService;
    private static final String DEFAULT_NICK_NAME_PREFIX = "用户";
    private static final String DEFAULT_SEPARATOR = "-";
    private static final String DEFAULT_AVATAR = "https://api.dicebear.com/7.x/identicon/svg?seed=%s";
    private static final String DEFAULT_USER_PROFILE = "这个用户很懒，什么都没写。";

    @Override
    public UserVO getUserVO(User user)
    {
        if (user == null)
        {
            return null;
        }
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        return userVO;
    }

    @Override
    public List<UserVO> getUserVO(List<User> userList)
    {
        if (CollUtil.isEmpty(userList))
        {
            return new ArrayList<>();
        }
        return userList.stream().map(this::getUserVO).collect(Collectors.toList());
    }

    @Override
    public QueryWrapper<User> getQueryWrapper(UserQueryRequest userQueryRequest)
    {
        if (userQueryRequest == null)
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }
        Long id = userQueryRequest.getId();
        String unionId = userQueryRequest.getUnionId();
        String mpOpenId = userQueryRequest.getMpOpenId();
        String userName = userQueryRequest.getUserName();
        String userProfile = userQueryRequest.getUserProfile();
        String userRole = userQueryRequest.getUserRole();
        String sortField = userQueryRequest.getSortField();
        String sortOrder = userQueryRequest.getSortOrder();
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(id != null, "id", id);
        queryWrapper.eq(StringUtils.isNotBlank(unionId), "unionId", unionId);
        queryWrapper.eq(StringUtils.isNotBlank(mpOpenId), "mpOpenId", mpOpenId);
        queryWrapper.eq(StringUtils.isNotBlank(userRole), "userRole", userRole);
        queryWrapper.like(StringUtils.isNotBlank(userProfile), "userProfile", userProfile);
        queryWrapper.like(StringUtils.isNotBlank(userName), "userName", userName);
        queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                             sortField);
        return queryWrapper;
    }


    // 重载的 makeRegister 方法，接收 User 对象
    @Override
    public Long doRegister(User user)
    {
        synchronized (user.getUserEmail().intern())
        {
            // 生成用户名
            String nickName = generateNickName(user.getUserEmail());
            user.setNickName(nickName);
            // 加密密码并设置
            String encryptPassword = EncryptionUtils.encryptPassword(user.getUserPassword());
            user.setUserPassword(encryptPassword);
            boolean saveResult = this.save(user);
            if (!saveResult)
            {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "注册失败，数据库错误");
            }
            return user.getId();
        }
    }

    /**
     * 随机生成密码
     *
     * @author CAIXYPROMISE
     * @version 1.0
     * @since 2024/4/26 下午9:42
     */
    @Override
    public String generatePassword()
    {
        // 定义字符集
        String lowerCaseLetters = "abcdefghijklmnopqrstuvwxyz";
        String upperCaseLetters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String numbers = "0123456789";
        String specialCharacters = "!@#$%&*.?";

        // 确保每种字符至少出现一次
        List<Character> passwordChars = new ArrayList<>();
        passwordChars.add(RandomUtil.randomChar(lowerCaseLetters));
        passwordChars.add(RandomUtil.randomChar(upperCaseLetters));
        passwordChars.add(RandomUtil.randomChar(numbers));
        passwordChars.add(RandomUtil.randomChar(specialCharacters));

        // 随机密码长度
        int length = RandomUtil.randomInt(8, 21);

        // 填充剩余的字符
        String allCharacters = lowerCaseLetters + upperCaseLetters + numbers + specialCharacters;
        for (int i = passwordChars.size(); i < length; i++)
        {
            passwordChars.add(RandomUtil.randomChar(allCharacters));
        }

        // 打乱字符顺序
        Collections.shuffle(passwordChars);

        // 构建最终的密码字符串
        StringBuilder password = new StringBuilder();
        for (Character ch : passwordChars)
        {
            password.append(ch);
        }

        return password.toString();
    }

    @Override
    public void sendModifyPasswordIdentifyCode(UserVO userVO)
    {
        User userInfo = getUserInfoByIdOrThrow(userVO.getId());
        emailService.sendEmail(userInfo.getUserEmail(), new CommonEmailCaptchaDTO("修改密码"),
                               EmailCaptchaBizEnum.RESET_PASSWORD);
    }


    @Override
    public Boolean modifyPassword(Long userId, UserModifyPasswordRequest userModifyPasswordRequest)
    {
        String userPassword = userModifyPasswordRequest.getNewPassword();
        validPassword(userPassword, userModifyPasswordRequest.getConfirmPassword());

        // 查询用户
        User currenUser = this.getById(userId);
        if (currenUser == null)
        {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "用户不存在");
        }

        // 校验邮箱验证码
        emailService.verifyCaptcha(EmailCaptchaBizEnum.RESET_PASSWORD, currenUser.getUserEmail(),
                                   userModifyPasswordRequest.getCaptchaCode());

        // 加密密码
        String encryptPassword = EncryptionUtils.encryptPassword(userPassword);
        currenUser.setUserPassword(encryptPassword);
        // 清空登录状态
        return this.updateById(currenUser);
    }


    /**
     * 校验用户信息
     *
     * @author CAIXYPROMISE
     * @version 1.0
     * @since 2024/5/21 下午3:56
     */
    @Override
    public void validUserInfo(User user, boolean add)
    {
        if (add)
        {
            String userPassword = user.getUserPassword();
            String userEmail = user.getUserEmail();
            // 检查密码是否合法
            ThrowUtils.throwIf(!RegexUtils.validatePassword(userPassword), ErrorCode.PARAMS_ERROR, "密码不符合要求");
            // 查询检查邮箱唯一性
            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(User::getUserEmail, userEmail);
            long count = baseMapper.selectCount(queryWrapper);
            // 账号已存在
            ThrowUtils.throwIf(count > 0, ErrorCode.PARAMS_ERROR, "账号已存在");
            user.setUserGender(UserGenderEnum.UNKNOWN.getValue());
        }
        Integer gender = user.getUserGender();
        if (gender != null && UserGenderEnum.getEnumByValue(gender) == null)
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "性别参数错误");
        }
    }

    /**
     * 根据用户id获取用户，不存在则抛出业务异常
     *
     * @author CAIXYPROMISE
     * @version 1.0
     * @version 2025/4/25 17:34
     */
    @Override
    public User getUserInfoByIdOrThrow(Long userId)
    {
        User user = this.getById(userId);
        ThrowUtils.throwIf(user == null, ErrorCode.NOT_FOUND_ERROR, "用户不存在");
        return user;
    }

    /**
     * 头像文件上传处理逻辑
     *
     * @author CAIXYPROMISE
     * @version 1.0
     * @since 2024/6/7 下午4:31
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public FileUploadAfterActionResult doAfterUploadAction(UploadContext uploadContext, FileActionHelper helper,
                                                           Path savePath, UploadFileRequest req,
                                                           HttpServletRequest servletReq)
    {
        UploadFileDTO dto = uploadContext.getUploadFileDTO();
        Long userId = dto.getUserId();
        User user = this.getById(userId);
        ThrowUtils.throwIf(user == null, ErrorCode.NOT_FOUND_ERROR, "更新用户不存在");
        String newUrl = dto.getFileSaveInfo().getFileURL();
        if (StringUtils.isBlank(newUrl)) {
            log.error("更新用户头像失败, 访问链接失败, 用户Id: {}, 新头像链接: {}, 上传", user.getId(), newUrl);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "文件上传失败");
        }

        return buildAvatarUpdateResult(user, newUrl, servletReq);
    }

    private FileUploadAfterActionResult buildAvatarUpdateResult(User user, String newUrl, HttpServletRequest request)
    {
        // 更新用户头像信息
        user.setUserAvatar(newUrl);
        boolean updated = this.updateById(user);
        if (!updated) {
            log.error("更新用户头像链接失败, 用户Id: {}, 新头像链接: {}", user.getId(), newUrl);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "更新用户头像失败");
        }
        // 更新session内的头像信息
        setUserInfoInSession(user, request);
        // 设置操作返回结果
        return FileUploadAfterActionResult.successBuilder().visitUrl(newUrl).bizId(user.getId())
                                          .accessLevelEnum(FileAccessLevelEnum.PUBLIC)
                                          .displayName(String.format("avatar_%s", user.getId())).build();
    }


    /**
     * 更新用户信息，同时更新Session内的信息
     *
     * @author CAIXYPROMISE
     * @version 1.0
     * @since 2024/9/30 上午2:01
     */
    @Override
    public Boolean updateUserAndSessionById(User user, HttpServletRequest request)
    {
        boolean result = this.updateById(user);
        if (result)
        {
            setUserInfoInSession(getById(user.getId()), request);
        }
        return result;
    }

    /**
     * 提交修改邮箱请求-步骤1：检查原邮箱-发送源优享验证码
     *
     * @author CAIXYPROMISE
     * @version 1.0
     * @version 2025/4/25 3:00
     */
    @Override
    public void submitModifyEmailCheckOriginEmail(UserVO loginInfo, String originEmail)
    {
        User userInfo = getUserInfoByIdOrThrow(loginInfo.getId());
        String userEmail = userInfo.getUserEmail();
        ThrowUtils.throwIf(!userEmail.equals(originEmail), ErrorCode.PARAMS_ERROR, "原邮箱错误");
        emailService.sendEmail(userInfo.getUserEmail(), new CommonEmailCaptchaDTO("修改绑定邮箱-校验原邮箱"),
                               EmailCaptchaBizEnum.RESET_EMAIL);
    }


    /**
     * 提交修改邮箱请求-步骤2-检查密码和步骤1验证码
     *
     * @author CAIXYPROMISE
     * @version 1.0
     * @version 2025/4/25 17:30
     */
    @Override
    public String submitModifyEmailCheckPasswordAndCode(UserVO loginUser, String password, String code)
    {
        User user = getUserInfoByIdOrThrow(loginUser.getId());
        String userEmail = user.getUserEmail();
        emailService.verifyCaptcha(EmailCaptchaBizEnum.RESET_EMAIL, userEmail, code);
        boolean matchPassword = EncryptionUtils.matchPassword(password, user.getUserPassword());
        if (!matchPassword)
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码错误");
        }
        // 生成一个token安全token给前端。后面需要都带上这个token来保持
        Map<String, Object> modifyPasswordInfoMap = new HashMap<>();
        modifyPasswordInfoMap.put("userEmail", userEmail);
        // 后续操作，如果Ip不对则返回错误
        String operatorIp = ServletUtils.getRemoteIp();
        if (!operatorIp.equals(loginUser.getLoginIp()))
        {
            throw new BusinessException(ErrorCode.FORBIDDEN_ERROR, "非法操作");
        }
        // 生成token
        String token = TokenUtils.createMD5TokenWithSalt(userEmail, operatorIp, loginUser.getId().toString());
        modifyPasswordInfoMap.put("operatorIp", operatorIp);
        redisManager.setHashMap(EmailCaptchaBizEnum.RESET_EMAIL, modifyPasswordInfoMap, token);
        return token;
    }

    @Override
    public void submitModifyEmailSendCodeToNewEmail(UserVO loginUser, String token, String newEmail)
    {
        // 校验token
        Map<String, Object> typedMap = redisManager.getHashMap(EmailCaptchaBizEnum.RESET_EMAIL, token);
        if (typedMap == null)
        {
            throw new BusinessException(ErrorCode.FORBIDDEN_ERROR, "无效请求");
        }
        String userEmail = (String) typedMap.get("userEmail");
        String operatorIp = (String) typedMap.get("operatorIp");
        if (!operatorIp.equals(loginUser.getLoginIp()))
        {
            throw new BusinessException(ErrorCode.FORBIDDEN_ERROR, "非法操作");
        }
        // 检查邮箱
        if (userEmail.equals(newEmail))
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "新旧邮箱不能一致");
        }
        // 校验邮箱是否已被使用
        boolean emailExist = emailExist(newEmail);
        if (emailExist)
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "邮箱已被绑定");
        }
        typedMap.put("newEmail", newEmail);
        // 发送新邮箱的验证码邮件
        emailService.sendEmail(newEmail, new CommonEmailCaptchaDTO("修改绑定邮箱-校验新邮箱"), EmailCaptchaBizEnum.RESET_EMAIL);
        // 将新邮箱写入缓存，后续只依赖这个
        redisManager.setHashMap(EmailCaptchaBizEnum.RESET_EMAIL, typedMap, token);
    }

    @Override
    public Boolean modifyEmail(Long id, String token, String code)
    {
        // 从redis内提取信息
        Map<String, Object> typedMap = redisManager.getHashMap(EmailCaptchaBizEnum.RESET_EMAIL, token);
        if (typedMap == null)
        {
            throw new BusinessException(ErrorCode.FORBIDDEN_ERROR, "无效请求");
        }
        String newEmail = (String) typedMap.get("newEmail");
        String operatorIp = (String) typedMap.get("operatorIp");
        if (!operatorIp.equals(ServletUtils.getRemoteIp()))
        {
            throw new BusinessException(ErrorCode.FORBIDDEN_ERROR, "非法操作");
        }
        // 获取用户信息
        User userInfo = getUserInfoByIdOrThrow(id);
        // 校验邮箱验证码
        emailService.verifyCaptcha(EmailCaptchaBizEnum.RESET_EMAIL, newEmail, code);
        // 更新邮箱
        userInfo.setUserEmail(newEmail);
        boolean updated = this.updateById(userInfo);
        if (!updated)
        {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "修改失败");
        }
        // 删除缓存
        redisManager.delete(EmailCaptchaBizEnum.RESET_EMAIL, newEmail);
        // 清除登录状态-需要重新登录
        ServletUtils.removeAttributeInSession(UserConstant.USER_LOGIN_STATE);
        return true;
    }

    /**
     * 用户预注册
     *
     * @author CAIXYPROMISE
     * @version 1.0
     * @version 2025/1/30 1:29
     */
    @Override
    public Boolean userPreRegistration(UserRegisterRequest userRegisterRequest)
    {
        String userName = userRegisterRequest.getUserName();
        String email = userRegisterRequest.getUserEmail();
        if (StringUtils.isAnyBlank(userName, email))
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        boolean validatedUserName = RegexUtils.validatedUserName(userName);
        if (!validatedUserName)
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "昵称格式错误，只能包含中文字符和英文大小写字母，最多15个字符");
        }
        if (emailExist(email))
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "邮箱已注册");
        }

        synchronized (email.intern())
        {
            // 检查邮箱验证码是否发送
            String sendEmail = redisManager.getString(RedisKeyEnum.ACTIVE_USER, email);
            if (StringUtils.isNotBlank(sendEmail))
            {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户注册邮件已发送，请前往邮箱里查看。");
            }
            // 发送激活邮件
            String accessToken = DigestUtil.md5Hex(String.format("%s%s%s%s", RandomUtil.randomString(5), userName,
                                                                 email, System.currentTimeMillis()));
            HashMap<String, Object> cacheMap = new HashMap<>();
            cacheMap.put("userEmail", email);
            cacheMap.put("userName", userName);
            EmailActiveUserDTO activeUserDTO = new EmailActiveUserDTO();
            activeUserDTO.setToken(accessToken);
            // 先发邮件再写缓存，尽量避免提前过期，而且sendEmail里还会多做校验邮箱合法性操作。
            emailService.sendEmail(email, activeUserDTO, EmailCaptchaBizEnum.ACTIVE_USER);
            redisManager.setHashMap(RedisKeyEnum.ACTIVE_USER, cacheMap, accessToken);
        }
        return true;
    }

    @Override
    public RegistrationInfo getRegistrationInfoByParams(String token)
    {
        HashMap<String, Object> cacheMap = redisManager.getHashMap(RedisKeyEnum.ACTIVE_USER, token);
        if (cacheMap == null || cacheMap.isEmpty())
        {
            throw new BusinessException(ErrorCode.FORBIDDEN_ERROR, "无效请求");
        }
        return RegistrationInfo.builder().email(MapUtils.safetyGetValueByKey(cacheMap, "userEmail", String.class))
                               .nickName(MapUtils.safetyGetValueByKey(cacheMap, "userName", String.class)).build();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean doActivateUser(String token, String code, UserActivationRequest userActivationRequest)
    {
        HashMap<String, Object> cacheMap = redisManager.getHashMap(RedisKeyEnum.ACTIVE_USER, token);
        if (cacheMap == null || cacheMap.isEmpty())
        {
            throw new BusinessException(ErrorCode.FORBIDDEN_ERROR, "无效请求或激活过期");
        }
        String email = MapUtils.safetyGetValueByKey(cacheMap, "userEmail", String.class);
        String userName = MapUtils.safetyGetValueByKey(cacheMap, "userName", String.class);
        if (StringUtils.isAnyBlank(email, userName))
        {
            throw new BusinessException(ErrorCode.FORBIDDEN_ERROR, "无效请求或激活过期");
        }
        // 验证邮箱验证码
        emailService.verifyCaptcha(EmailCaptchaBizEnum.ACTIVE_USER, email, code);
        // 校验密码安全性
        String password = userActivationRequest.getPassword();
        String confirmPassword = userActivationRequest.getConfirmPassword();
        validPassword(password, confirmPassword);
        // 创建用户
        User user = User.builder().userName(userName).userEmail(email).userGender(UserGenderEnum.UNKNOWN.getValue())
                        .userAvatar(String.format(DEFAULT_AVATAR, userName)).userRole(UserRoleEnum.USER.getValue())
                        .userProfile(DEFAULT_USER_PROFILE).userPassword(password) // doRegister内加密
                        .build()
                ;
        Long userId = doRegister(user);
        if (userId == null)
        {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "注册失败");
        }
        // 删除缓存
        redisManager.delete(RedisKeyEnum.ACTIVE_USER, token);
        redisManager.delete(RedisKeyEnum.ACTIVE_USER, email);
        Boolean initApiKeyByUser = apiKeyService.initApiKeyByUser(user.getId());
        if (!initApiKeyByUser)
        {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "注册失败: 初始化API密钥失败");
        }
        return true;
    }

    /**
     * 检查用户名是否存在(唯一性)
     *
     * @author CAIXYPROMISE
     * @version 1.0
     * @version 2024/11/15 0:58
     */
    private boolean nickNameExist(String nickName)
    {
        //如果布隆过滤器中存在，再进行数据库二次判断
        if (StringUtils.isNotBlank(nickName) && this.nickNameBloomFilter != null && this.nickNameBloomFilter.contains(nickName))
        {
            return this.baseMapper.findByNickname(nickName) != null;
        }
        return false;
    }

    private boolean emailExist(String email)
    {
        if (StringUtils.isNotBlank(email) && this.emailBloomFilter != null && this.emailBloomFilter.contains(email))
        {
            return this.baseMapper.findByEmail(email) != null;
        }
        return false;
    }


    private String generateNickName(String email)
    {
        String defaultNickName;
        do
        {
            // 理论邮箱最短字符为5个
            defaultNickName = String.format("%s%s%s%s%s", DEFAULT_NICK_NAME_PREFIX, DEFAULT_SEPARATOR,
                                            RandomUtil.randomNumbers(5), DEFAULT_SEPARATOR, email.substring(0, 5));
        }
        while (nickNameExist(defaultNickName));
        return defaultNickName;
    }

    private void setUserInfoInSession(User user, HttpServletRequest request)
    {
        request.getSession().setAttribute(UserConstant.USER_LOGIN_STATE, userConvertor.toVO(user));
    }


    private static void validPassword(String userPassword, String confirmPassword)
    {
        if (userPassword.length() < 8)
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户密码过短");
        }
        if (userPassword.length() > 20)
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户密码过长");
        }
        if (!userPassword.equals(confirmPassword))
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "两次输入密码不一致");
        }
        // 检查密码是否合法
        ThrowUtils.throwIf(!RegexUtils.validatePassword(userPassword), ErrorCode.PARAMS_ERROR,
                           "密码不符合要求：必须包含大小写字母和数字的组合，可以使用特殊字符，长度在8-20之间)");
    }


    @Override
    public void afterPropertiesSet() throws Exception
    {
        this.nickNameBloomFilter = redissonClient.getBloomFilter("nickName");
        if (nickNameBloomFilter != null && !nickNameBloomFilter.isExists())
        {
            this.nickNameBloomFilter.tryInit(100000L, 0.01);
        }
        this.emailBloomFilter = redissonClient.getBloomFilter("emailAccount");
        if (emailBloomFilter != null && !emailBloomFilter.isExists())
        {
            this.emailBloomFilter.tryInit(100000L, 0.01);
        }
    }
}
