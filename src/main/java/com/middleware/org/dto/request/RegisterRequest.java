package com.middleware.org.dto.request;

import lombok.Data;

@Data
public class RegisterRequest {
    private String account;
    private String email;
    private String phone;
    private String name;
    private String nickname;
    private String password;
    private String captchaId;
    private String captchaCode;
}