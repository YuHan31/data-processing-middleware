package com.middleware.org.dto.request;

import lombok.Data;

@Data
public class LoginRequest {
    private String loginId;
    private String password;
    private String captchaId;
    private String captchaCode;
}