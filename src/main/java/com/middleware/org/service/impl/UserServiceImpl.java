package com.middleware.org.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.middleware.org.dto.request.RegisterRequest;
import com.middleware.org.dto.request.ResetPasswordRequest;
import com.middleware.org.entity.User;
import com.middleware.org.repository.UserRepository;
import com.middleware.org.service.IUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Slf4j
public class UserServiceImpl implements IUserService {

    @Autowired
    private UserRepository userRepository;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public User register(RegisterRequest request) {
        if (request.getAccount() == null || request.getAccount().trim().isEmpty()) {
            throw new IllegalArgumentException("账号不能为空");
        }
        if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("邮箱不能为空");
        }
        if (request.getPhone() == null || request.getPhone().trim().isEmpty()) {
            throw new IllegalArgumentException("手机号不能为空");
        }
        if (request.getName() == null || request.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("姓名不能为空");
        }
        if (request.getPassword() == null || request.getPassword().length() < 6) {
            throw new IllegalArgumentException("密码长度至少6位");
        }

        if (userRepository.selectCount(new LambdaQueryWrapper<User>().eq(User::getAccount, request.getAccount())) > 0) {
            throw new IllegalArgumentException("账号已存在");
        }
        if (userRepository.selectCount(new LambdaQueryWrapper<User>().eq(User::getEmail, request.getEmail())) > 0) {
            throw new IllegalArgumentException("邮箱已存在");
        }
        if (userRepository.selectCount(new LambdaQueryWrapper<User>().eq(User::getPhone, request.getPhone())) > 0) {
            throw new IllegalArgumentException("手机号已存在");
        }

        User user = new User();
        user.setAccount(request.getAccount());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setName(request.getName());
        user.setNickname(request.getNickname());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setCreateTime(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.insert(user);

        log.info("用户注册成功: {}", request.getAccount());
        return user;
    }

    @Override
    public User login(String loginId, String password) {
        User user = userRepository.selectOne(
            new LambdaQueryWrapper<User>()
                .and(wrapper -> wrapper
                    .eq(User::getAccount, loginId)
                    .or()
                    .eq(User::getEmail, loginId)
                    .or()
                    .eq(User::getPhone, loginId)
                )
        );

        if (user == null) {
            throw new IllegalArgumentException("用户不存在");
        }
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new IllegalArgumentException("密码错误");
        }

        log.info("用户登录成功: {}", loginId);
        return user;
    }

    @Override
    public boolean checkUnique(String field, String value) {
        if (value == null || value.trim().isEmpty()) {
            return false;
        }

        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        switch (field) {
            case "account":
                wrapper.eq(User::getAccount, value);
                break;
            case "email":
                wrapper.eq(User::getEmail, value);
                break;
            case "phone":
                wrapper.eq(User::getPhone, value);
                break;
            default:
                return false;
        }

        return userRepository.selectCount(wrapper) == 0;
    }

    @Override
    public User getUserById(Long userId) {
        return userRepository.selectById(userId);
    }

    @Override
    public void resetPassword(ResetPasswordRequest request) {
        if (request.getAccount() == null || request.getAccount().trim().isEmpty()) {
            throw new IllegalArgumentException("账号不能为空");
        }
        if (request.getPhone() == null || request.getPhone().trim().isEmpty()) {
            throw new IllegalArgumentException("手机号不能为空");
        }
        if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("邮箱不能为空");
        }
        if (request.getNewPassword() == null || request.getNewPassword().length() < 6) {
            throw new IllegalArgumentException("密码长度至少6位");
        }

        User user = userRepository.selectOne(
            new LambdaQueryWrapper<User>()
                .eq(User::getAccount, request.getAccount())
        );

        if (user == null) {
            throw new IllegalArgumentException("账号不存在");
        }
        if (!request.getPhone().equals(user.getPhone())) {
            throw new IllegalArgumentException("手机号不匹配");
        }
        if (!request.getEmail().equalsIgnoreCase(user.getEmail())) {
            throw new IllegalArgumentException("邮箱不匹配");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.updateById(user);

        log.info("用户重置密码成功: {}", request.getAccount());
    }
}
