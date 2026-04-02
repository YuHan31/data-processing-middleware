package com.middleware.org.controller;

import com.middleware.org.common.Result;
import com.middleware.org.dto.request.LoginRequest;
import com.middleware.org.dto.request.RegisterRequest;
import com.middleware.org.dto.request.ResetPasswordRequest;
import com.middleware.org.dto.response.CaptchaResponse;
import com.middleware.org.dto.response.UserResponse;
import com.middleware.org.entity.User;
import com.middleware.org.service.ICaptchaService;
import com.middleware.org.service.IUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
@Tag(name = "用户管理", description = "用户登录注册接口")
public class UserController {

    @Autowired
    private IUserService userService;

    @Autowired
    private ICaptchaService captchaService;

    @GetMapping("/captcha")
    @Operation(summary = "获取验证码")
    public Result<CaptchaResponse> getCaptcha() {
        CaptchaResponse captcha = captchaService.generate();
        return Result.success(captcha);
    }

    @PostMapping("/register")
    @Operation(summary = "用户注册")
    public Result<UserResponse> register(@RequestBody RegisterRequest request) {
        try {
            if (!captchaService.verify(request.getCaptchaId(), request.getCaptchaCode())) {
                return Result.fail("验证码错误或已过期");
            }

            User user = userService.register(request);

            UserResponse response = UserResponse.builder()
                    .userId(user.getId())
                    .account(user.getAccount())
                    .email(user.getEmail())
                    .phone(user.getPhone())
                    .name(user.getName())
                    .nickname(user.getNickname())
                    .build();
            return Result.success("注册成功", response);
        } catch (Exception e) {
            return Result.fail(e.getMessage());
        }
    }

    @PostMapping("/login")
    @Operation(summary = "用户登录")
    public Result<UserResponse> login(@RequestBody LoginRequest request, HttpSession session) {
        try {
            if (!captchaService.verify(request.getCaptchaId(), request.getCaptchaCode())) {
                return Result.fail("验证码错误或已过期");
            }

            User user = userService.login(request.getLoginId(), request.getPassword());

            session.setAttribute("userId", user.getId());

            UserResponse response = UserResponse.builder()
                    .userId(user.getId())
                    .account(user.getAccount())
                    .email(user.getEmail())
                    .phone(user.getPhone())
                    .name(user.getName())
                    .nickname(user.getNickname())
                    .build();
            return Result.success("登录成功", response);
        } catch (Exception e) {
            return Result.fail(e.getMessage());
        }
    }

    @GetMapping("/check-unique")
    @Operation(summary = "检查字段唯一性")
    public Result<Map<String, Boolean>> checkUnique(@RequestParam String field, @RequestParam String value) {
        boolean unique = userService.checkUnique(field, value);
        Map<String, Boolean> data = new HashMap<>();
        data.put("unique", unique);
        return Result.success(data);
    }

    @GetMapping("/profile")
    @Operation(summary = "获取个人信息")
    public Result<UserResponse> getProfile(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return Result.fail("未登录");
        }

        User user = userService.getUserById(userId);
        if (user == null) {
            return Result.fail("用户不存在");
        }

        UserResponse response = UserResponse.builder()
                .userId(user.getId())
                .account(user.getAccount())
                .email(user.getEmail())
                .phone(user.getPhone())
                .name(user.getName())
                .nickname(user.getNickname())
                .build();
        return Result.success(response);
    }

    @PostMapping("/reset-password")
    @Operation(summary = "重置密码")
    public Result<String> resetPassword(@RequestBody ResetPasswordRequest request) {
        try {
            if (!captchaService.verify(request.getCaptchaId(), request.getCaptchaCode())) {
                return Result.fail("验证码错误或已过期");
            }

            userService.resetPassword(request);
            return Result.success("密码重置成功");
        } catch (Exception e) {
            return Result.fail(e.getMessage());
        }
    }
}