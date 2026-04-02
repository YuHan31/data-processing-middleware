package com.middleware.org.service;

import com.middleware.org.dto.response.CaptchaResponse;

public interface ICaptchaService {
    CaptchaResponse generate();
    boolean verify(String captchaId, String code);
}