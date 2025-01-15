package com.caixy.shortlink.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.caixy.shortlink.common.ErrorCode;
import com.caixy.shortlink.constant.CommonConstant;
import com.caixy.shortlink.constant.UserConstant;
import com.caixy.shortlink.exception.BusinessException;
import com.caixy.shortlink.exception.ThrowUtils;
import com.caixy.shortlink.manager.Email.core.EmailSenderEnum;
import com.caixy.shortlink.manager.Email.models.captcha.EmailCaptchaConstant;
import com.caixy.shortlink.manager.UploadManager.annotation.FileUploadActionTarget;
import com.caixy.shortlink.manager.redis.RedisManager;
import com.caixy.shortlink.mapper.UserMapper;
import com.caixy.shortlink.model.convertor.user.UserConvertor;
import com.caixy.shortlink.model.dto.file.UploadFileDTO;
import com.caixy.shortlink.model.dto.file.UploadFileRequest;
import com.caixy.shortlink.model.dto.user.UserModifyPasswordRequest;
import com.caixy.shortlink.model.dto.user.UserQueryRequest;
import com.caixy.shortlink.model.dto.user.UserRegisterRequest;
import com.caixy.shortlink.model.dto.user.UserResetEmailRequest;
import com.caixy.shortlink.model.entity.User;
import com.caixy.shortlink.model.enums.FileActionBizEnum;
import com.caixy.shortlink.model.enums.UserGenderEnum;
import com.caixy.shortlink.model.vo.user.UserVO;
import com.caixy.shortlink.service.ApiKeyService;
import com.caixy.shortlink.service.UserService;
import com.caixy.shortlink.strategy.FileActionStrategy;
import com.caixy.shortlink.strategy.UploadFileMethodStrategy;
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
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements InitializingBean, UserService, FileActionStrategy
{
    private static final UserConvertor userConvertor = UserConvertor.INSTANCE;
    private final RedisManager redisManager;
    private final RedissonClient redissonClient;
    private RBloomFilter<String> nickNameBloomFilter;
    private final ApiKeyService apiKeyService;
    private final String DEFAULT_NICK_NAME_PREFIX = "用户_";

    @Override
    @Transactional(rollbackFor = Exception.class)
    public long userRegister( UserRegisterRequest userRegisterRequest)
    {
        String userPassword = userRegisterRequest.getUserPassword();
        User user = new User();
        BeanUtils.copyProperties(userRegisterRequest, user);
        // 1. 校验
        validUserInfo(user, true);
        // 2. 插入数据
        user.setUserPassword(userPassword);
        user.setUserRole(UserConstant.DEFAULT_ROLE);
        Long userId = doRegister(user);
        Boolean initApiKeyByUser = apiKeyService.initApiKeyByUser(userId);
        if (!initApiKeyByUser) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "注册失败: 初始化API密钥失败");
        }
        return userId;
    }

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
    public Long doRegister( User user)
    {
        synchronized (user.getUserAccount().intern())
        {
            // 检查账户是否重复
            checkUserAccount(user.getUserAccount());
            // 生成用户名
            generateNickName(user.getUserPhone());
            // 加密密码并设置
            String encryptPassword = EncryptionUtils.encryptPassword(user.getUserPassword());
            user.setUserPassword(encryptPassword);
            // 插入数据
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
    public Boolean modifyPassword(Long userId,  UserModifyPasswordRequest userModifyPasswordRequest)
    {
        String userPassword = userModifyPasswordRequest.getNewPassword();
        if (userPassword.length() < 8)
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户密码过短");
        }
        if (userPassword.length() > 20)
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户密码过长");
        }
        if (!userPassword.equals(userModifyPasswordRequest.getConfirmPassword()))
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "两次输入密码不一致");
        }
        // 检查密码是否合法
        ThrowUtils.throwIf(!RegexUtils.validatePassword(userPassword), ErrorCode.PARAMS_ERROR, "密码不符合要求");

        // 查询用户
        User currenUser = this.getById(userId);
        if (currenUser == null)
        {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "用户不存在");
        }

        // 校验邮箱验证码
        verifyEmailCaptcha(userModifyPasswordRequest.getCaptchaCode(), currenUser.getUserEmail(), EmailSenderEnum.RESET_PASSWORD);

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
    public void validUserInfo( User user, boolean add)
    {
        if (add)
        {
            String userAccount = user.getUserAccount();
            String userPassword = user.getUserPassword();
            String userEmail = user.getUserEmail();
            String userPhone = user.getUserPhone();
            // 检查密码是否合法
            ThrowUtils.throwIf(!RegexUtils.validatePassword(userPassword), ErrorCode.PARAMS_ERROR, "密码不符合要求");
            // 检查账号是否合法
            ThrowUtils.throwIf(!RegexUtils.validateAccount(userAccount), ErrorCode.PARAMS_ERROR, "用户账号格式错误");
            // 检查手机号是否合法
            ThrowUtils.throwIf(!RegexUtils.isMobilePhone(userPhone), ErrorCode.PARAMS_ERROR, "手机号格式错误");
            // 检查用户邮箱
            ThrowUtils.throwIf(!RegexUtils.isEmail(userEmail), ErrorCode.PARAMS_ERROR, "邮箱格式错误");

            // 查询账号是否存在，同时检查手机、邮箱唯一性
            QueryWrapper<User> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("userAccount", userAccount).or();
            queryWrapper.eq("userEmail", userEmail).or();
            queryWrapper.eq("userPhone", userPhone);
            long count = baseMapper.selectCount(queryWrapper);
            // 账号已存在
            ThrowUtils.throwIf(count > 0, ErrorCode.PARAMS_ERROR, "账号已存在");
        }
        Integer gender = user.getUserGender();
        if (gender != null && UserGenderEnum.getEnumByValue(gender) == null)
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "性别参数错误");
        }
    }

    /**
     * 头像文件上传处理逻辑
     *
     * @author CAIXYPROMISE
     * @version 1.0
     * @since 2024/6/7 下午4:31
     */
    @Override
    public Boolean doAfterUploadAction( UploadFileDTO uploadFileDTO, Path savePath,
                                       UploadFileRequest uploadFileRequest, HttpServletRequest request) throws IOException
    {
        Long userId = uploadFileDTO.getUserId();
        User user = this.getById(userId);
        if (user == null)
        {
            return false;
        }
        // 更新用户头像
        String oldUserAvatar = user.getUserAvatar();
        user.setUserAvatar(uploadFileDTO.getFileInfo().getFileURL());
        UploadFileMethodStrategy uploadManager = uploadFileDTO.getUploadManager();
        boolean updated = this.updateById(user);
        if (updated)
        {
            // 删除旧头像
            if (StringUtils.isNotBlank(oldUserAvatar))
            {
                FileActionBizEnum uploadBizEnum = uploadFileDTO.getFileActionBizEnum();

                String[] filename = oldUserAvatar.split("/");
                if (filename.length > 0)
                {
                    Path filepath = uploadBizEnum.buildFileAbsolutePathAndName(userId, filename[filename.length - 1]);
                    uploadManager.deleteFileAllowFail(filepath);
                    setUserInfoInSession(user, request);
                    return true;
                }
                return false;
            }
            // 可能初始化的时候没有设置头像，可以设置一个默认头像，但不允许删除默认头像
            return true;
        }
        return false;
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

    @Override
    public Boolean resetEmail(Long id, UserResetEmailRequest userResetEmailRequest, HttpServletRequest request)
    {
        // 从Session内获取新的邮箱值
        String newEmail = ServletUtils.getAttributeFromSession(EmailSenderEnum.RESET_EMAIL.getKey(), String.class)
                                      .orElseThrow(() -> new BusinessException(ErrorCode.FORBIDDEN_ERROR, "无效请求"));
        // 获取用户信息
        User userInfo = getById(id);
        if (userInfo == null)
        {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "用户不存在");
        }
        // 检查新旧邮箱是否一致
        if (Objects.equals(userInfo.getUserEmail(), newEmail))
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "新旧邮箱不能一致");
        }
        // 检查用户密码是否正确
        boolean matchPassword = EncryptionUtils.matchPassword(userResetEmailRequest.getPassword(),
                userInfo.getUserPassword());
        if (!matchPassword)
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码错误");
        }
        // 校验邮箱验证码
        verifyEmailCaptcha(userResetEmailRequest.getCode(), newEmail, EmailSenderEnum.RESET_EMAIL);
        // 更新邮箱
        userInfo.setUserEmail(newEmail);
        boolean updated = this.updateById(userInfo);
        if (updated)
        {
            // 删除缓存
            redisManager.delete(EmailSenderEnum.RESET_EMAIL, newEmail);
            // 清除登录状态-需要重新登录
            ServletUtils.removeAttributeInSession(UserConstant.USER_LOGIN_STATE);
            // 清除验证码签名
            ServletUtils.removeAttributeInSession(EmailSenderEnum.RESET_EMAIL.getKey());
            return true;
        }
        return false;
    }

    /**
     * 检查用户名是否存在(唯一性)
     *
     * @author CAIXYPROMISE
     * @version 1.0
     * @version 2024/11/15 0:58
     */
    public boolean nickNameExist(String nickName) {
        //如果布隆过滤器中存在，再进行数据库二次判断
        if (this.nickNameBloomFilter != null && this.nickNameBloomFilter.contains(nickName)) {
            return this.baseMapper.findByNickname(nickName) != null;
        }
        return false;
    }


    private void verifyEmailCaptcha(String code, String newEmail, EmailSenderEnum senderEnum)
    {
        // 从缓存获取信息
        HashMap<String, Object> captchaInCache = redisManager.getHashMap(senderEnum, newEmail);
        if (captchaInCache == null || captchaInCache.isEmpty())
        {
            throw new BusinessException(ErrorCode.FORBIDDEN_ERROR, "无效请求");
        }
        String captcha = MapUtils.safetyGetValueByKey(captchaInCache, EmailCaptchaConstant.CACHE_KEY_CODE,
                String.class);

        if (StringUtils.isBlank(captcha))
        {
            throw new BusinessException(ErrorCode.FORBIDDEN_ERROR, "无效请求");
        }

        // 验证码验证
        if (!captcha.equals(code))
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "验证码错误");
        }
    }

    // 私有方法，用于检查账户是否重复
    private void checkUserAccount(String userAccount)
    {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        long count = this.baseMapper.selectCount(queryWrapper);
        if (count > 0)
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号重复");
        }
    }
    private String generateNickName(String phone)
    {
        String defaultNickName;
        do {
            defaultNickName = DEFAULT_NICK_NAME_PREFIX + RandomUtil.randomNumbers(5) + phone.substring(0, 5);
        } while (nickNameExist(defaultNickName));
        return defaultNickName;
    }

    private void setUserInfoInSession(User user,  HttpServletRequest request)
    {
        request.getSession().setAttribute(UserConstant.USER_LOGIN_STATE, userConvertor.toVO(user));
    }

    @Override
    public void afterPropertiesSet() throws Exception
    {
        this.nickNameBloomFilter = redissonClient.getBloomFilter("nickName");
        if (nickNameBloomFilter != null && !nickNameBloomFilter.isExists()) {
            this.nickNameBloomFilter.tryInit(100000L, 0.01);
        }
    }
}
