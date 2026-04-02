package com.middleware.org.service;

import com.middleware.org.dto.request.RegisterRequest;
import com.middleware.org.dto.request.ResetPasswordRequest;
import com.middleware.org.entity.User;

public interface IUserService {
    User register(RegisterRequest request);
    User login(String loginId, String password);
    boolean checkUnique(String field, String value);
    User getUserById(Long userId);
    void resetPassword(ResetPasswordRequest request);
}
