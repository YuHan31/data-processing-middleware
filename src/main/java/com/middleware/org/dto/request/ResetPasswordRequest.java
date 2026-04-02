package com.middleware.org.dto.request;

import lombok.Data;

@Data
public class ResetPasswordRequest {
    private String account;
    private String phone;
    private String email;
    private String newPassword;
    private String captchaId;
    private String captchaCode;
}
