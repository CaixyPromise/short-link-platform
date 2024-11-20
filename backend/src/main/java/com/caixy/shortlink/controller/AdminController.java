package com.caixy.shortlink.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.caixy.shortlink.annotation.AuthCheck;
import com.caixy.shortlink.common.Result;
import com.caixy.shortlink.common.ResultUtils;
import com.caixy.shortlink.manager.Authorization.AuthManager;
import com.caixy.shortlink.model.enums.UserRoleEnum;
import com.caixy.shortlink.model.vo.user.UserVO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 管理员接口控制器
 *
 * @Author CAIXYPROMISE
 * @name com.caixy.shortlink.controller.AdminController
 * @since 2024/10/29 00:40
 */
@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/admin")
public class AdminController
{
    private final AuthManager authManager;

    /**
     * 获取在线用户列表，支持分页
     */
    @GetMapping("/onlineUsers")
    @AuthCheck(mustRole = UserRoleEnum.ADMIN)
    public Result<Page<UserVO>> getOnlineUsers(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size)
    {
        Page<UserVO> onlineUsers = authManager.getOnlineUsers(page, size);
        return ResultUtils.success(onlineUsers);
    }

    /**
     * 强制下线指定用户
     */
    @PostMapping("/forceLogout")
    @AuthCheck(mustRole = UserRoleEnum.ADMIN)
    public Result<Boolean> forceLogout(@RequestParam("userId") Long userId)
    {
        authManager.forceLogout(userId);
        return ResultUtils.success(true);
    }
}
