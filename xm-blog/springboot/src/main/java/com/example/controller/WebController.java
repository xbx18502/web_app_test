package com.example.controller;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.example.common.Constants;
import com.example.common.Result;
import com.example.common.config.JwtInterceptor;
import com.example.common.enums.ResultCodeEnum;
import com.example.common.enums.RoleEnum;
import com.example.entity.Account;
import com.example.exception.CustomException;
import com.example.service.AdminService;
import com.example.service.UserService;
import com.example.utils.TokenUtils;

import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
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

    public boolean validateAccessToken(HttpServletRequest request) {
        String token = request.getHeader(Constants.TOKEN);
        if (ObjectUtil.isEmpty(token)) {
            token = request.getParameter(Constants.TOKEN);
        }
        if (ObjectUtil.isEmpty(token)) {
            throw new CustomException(ResultCodeEnum.TOKEN_INVALID_ERROR);
        }

        Account account = null;
        try {
            String userRole = JWT.decode(token).getAudience().get(0);
            String userId = userRole.split("-")[0];
            String role = userRole.split("-")[1];

            account = RoleEnum.ADMIN.name().equals(role) ? adminService.selectById(Integer.valueOf(userId))
                    : userService.selectById(Integer.valueOf(userId));

            if (ObjectUtil.isNull(account)) {
                throw new CustomException(ResultCodeEnum.USER_NOT_EXIST_ERROR);
            }

            JWTVerifier jwtVerifier = JWT.require(Algorithm.HMAC256(account.getPassword())).build();
            jwtVerifier.verify(token);
            return true;
        } catch (Exception e) {
            throw new CustomException(ResultCodeEnum.TOKEN_CHECK_ERROR);
        }
    }

    @PostMapping("/refreshToken")
    public Result refreshToken(HttpServletRequest request, HttpServletResponse response) {
        // 1. Get refresh token from cookie
        Cookie[] cookies = request.getCookies();
        String refreshToken = null;
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("refreshToken".equals(cookie.getName())) {
                    refreshToken = cookie.getValue();
                    break;
                }
            }
        }

        if (StrUtil.isBlank(refreshToken)) {
            return Result.error(ResultCodeEnum.REFRESH_TOKEN_INVALID_ERROR);
        }

        try {
            // 2. Validate refresh token
            String userRole = JWT.decode(refreshToken).getAudience().get(0);
            String[] parts = userRole.split("-");
            if (parts.length != 2) {
                return Result.error(ResultCodeEnum.TOKEN_CHECK_ERROR);
            }

            String userId = parts[0];
            String role = parts[1];

            // 3. Get user and validate
            Account account = RoleEnum.ADMIN.name().equals(role) ? adminService.selectById(Integer.valueOf(userId))
                    : userService.selectById(Integer.valueOf(userId));

            if (ObjectUtil.isNull(account)) {
                return Result.error(ResultCodeEnum.USER_NOT_EXIST_ERROR);
            }
            // if (!validateAccessToken(request)) {
            //     return Result.error(ResultCodeEnum.TOKEN_CHECK_ERROR);
            // }
            // 4. Verify token signature
            JWTVerifier verifier = JWT.require(Algorithm.HMAC256(account.getPassword())).build();
            verifier.verify(refreshToken);

            // 5. Generate new tokens
            String tokenData = account.getId() + "-" + role;
            String newToken = TokenUtils.createToken(tokenData, account.getPassword());
            // String newRefreshToken = TokenUtils.createRefreshToken(tokenData, account.getPassword());

            // 6. Set new refresh token in cookie
            // Cookie newRefreshTokenCookie = new Cookie("refreshToken", newRefreshToken);
            // newRefreshTokenCookie.setHttpOnly(true);
            // newRefreshTokenCookie.setSecure(true); // for HTTPS
            // newRefreshTokenCookie.setPath("/");
            // newRefreshTokenCookie.setMaxAge(24 * 60 * 60); // 24 hours
            // response.addCookie(newRefreshTokenCookie);

            // 7. Return new access token
            return Result.success(Collections.singletonMap("token", newToken));

        } catch (JWTVerificationException e) {
            return Result.error(ResultCodeEnum.TOKEN_CHECK_ERROR);
        } catch (Exception e) {
            return Result.error(ResultCodeEnum.SYSTEM_ERROR);
        }
    }

}
