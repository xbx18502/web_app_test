package com.example.controller;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;

import com.auth0.jwt.JWT;
import com.example.common.Result;
import com.example.common.enums.ResultCodeEnum;
import com.example.common.enums.RoleEnum;
import com.example.entity.Account;
import com.example.service.AdminService;
import com.example.service.UserService;
import com.example.utils.TokenUtils;

import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

/**
 * 基础前端接口
 */
@RestController
public class WebController {

    @Resource
    private AdminService adminService;
    @Resource
    private UserService userService;

    @GetMapping("/")
    public Result hello() {
        return Result.success("访问成功");
    }

    /**
     * 登录
     */
    // @PostMapping("/login")
    // public Result login(@RequestBody Account account) {
    // if (ObjectUtil.isEmpty(account.getUsername()) ||
    // ObjectUtil.isEmpty(account.getPassword())
    // || ObjectUtil.isEmpty(account.getRole())) {
    // return Result.error(ResultCodeEnum.PARAM_LOST_ERROR);
    // }
    // if (RoleEnum.ADMIN.name().equals(account.getRole())) {
    // account = adminService.login(account);
    // } else if (RoleEnum.USER.name().equals(account.getRole())) {
    // account = userService.login(account);
    // } else {
    // return Result.error(ResultCodeEnum.PARAM_ERROR);
    // }
    // return Result.success(account);
    // }
    @PostMapping("/login")
    public Result login(@RequestBody Account account, HttpServletResponse response) {
        if (ObjectUtil.isEmpty(account.getUsername()) || ObjectUtil.isEmpty(account.getPassword())
                || ObjectUtil.isEmpty(account.getRole())) {
            return Result.error(ResultCodeEnum.PARAM_LOST_ERROR);
        }

        Account loginAccount;
        if (RoleEnum.ADMIN.name().equals(account.getRole())) {
            loginAccount = adminService.login(account);
        } else if (RoleEnum.USER.name().equals(account.getRole())) {
            loginAccount = userService.login(account);
        } else {
            return Result.error(ResultCodeEnum.PARAM_ERROR);
        }

        // Set refresh token in HTTP-only cookie
        Cookie refreshTokenCookie = new Cookie("refreshToken", loginAccount.getRefreshToken());
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setMaxAge(24 * 60 * 60); // 24 hours
        response.addCookie(refreshTokenCookie);

        // Remove refresh token from response body
        loginAccount.setRefreshToken(null);
        return Result.success(loginAccount);
    }

    /**
     * 注册
     */
    @PostMapping("/register")
    public Result register(@RequestBody Account account) {
        if (StrUtil.isBlank(account.getUsername()) || StrUtil.isBlank(account.getPassword())
                || ObjectUtil.isEmpty(account.getRole())) {
            return Result.error(ResultCodeEnum.PARAM_LOST_ERROR);
        }
        if (RoleEnum.USER.name().equals(account.getRole())) {
            userService.register(account);
        } else {
            return Result.error(ResultCodeEnum.PARAM_ERROR);
        }
        return Result.success();
    }

    /**
     * 修改密码
     */
    @PutMapping("/updatePassword")
    public Result updatePassword(@RequestBody Account account) {
        if (StrUtil.isBlank(account.getUsername()) || StrUtil.isBlank(account.getPassword())
                || ObjectUtil.isEmpty(account.getNewPassword())) {
            return Result.error(ResultCodeEnum.PARAM_LOST_ERROR);
        }
        if (RoleEnum.ADMIN.name().equals(account.getRole())) {
            adminService.updatePassword(account);
        } else if (RoleEnum.USER.name().equals(account.getRole())) {
            userService.updatePassword(account);
        }
        return Result.success();
    }

    @PostMapping("/refreshToken")
    public Result refreshToken(@RequestBody Map<String, String> body) {
        String refreshToken = body.get("refreshToken");
        if (StrUtil.isBlank(refreshToken)) {
            return Result.error(ResultCodeEnum.PARAM_LOST_ERROR);
        }

        try {
            // Decode refresh token
            String userRole = JWT.decode(refreshToken).getAudience().get(0);
            String userId = userRole.split("-")[0];
            String role = userRole.split("-")[1];

            // Get user
            Account account = RoleEnum.ADMIN.name().equals(role) ? adminService.selectById(Integer.valueOf(userId))
                    : userService.selectById(Integer.valueOf(userId));

            if (ObjectUtil.isNull(account)) {
                return Result.error(ResultCodeEnum.USER_NOT_EXIST_ERROR);
            }

            // Generate new tokens
            String tokenData = account.getId() + "-" + role;
            String newToken = TokenUtils.createToken(tokenData, account.getPassword());
            String newRefreshToken = TokenUtils.createRefreshToken(tokenData, account.getPassword());

            // Return new token pair
            Map<String, String> tokens = new HashMap<>();
            tokens.put("token", newToken);
            tokens.put("refreshToken", newRefreshToken);

            return Result.success(tokens);
        } catch (Exception e) {
            return Result.error(ResultCodeEnum.TOKEN_CHECK_ERROR);
        }
    }

}
