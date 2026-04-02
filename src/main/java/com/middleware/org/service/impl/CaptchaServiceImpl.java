package com.middleware.org.service.impl;

import com.middleware.org.dto.response.CaptchaResponse;
import com.middleware.org.service.ICaptchaService;
import com.middleware.org.util.CaptchaUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class CaptchaServiceImpl implements ICaptchaService {

    private final Map<String, CaptchaData> captchaStore = new ConcurrentHashMap<>();

    @Override
    public CaptchaResponse generate() {
        CaptchaUtil.CaptchaResult result = CaptchaUtil.generate();
        String captchaId = UUID.randomUUID().toString();

        captchaStore.put(captchaId, new CaptchaData(result.getCode(), System.currentTimeMillis()));

        return CaptchaResponse.builder()
                .captchaId(captchaId)
                .captchaImage(result.getImage())
                .build();
    }

    @Override
    public boolean verify(String captchaId, String code) {
        log.info("验证码校验 - captchaId: {}, code: {}", captchaId, code);

        if (captchaId == null || code == null) {
            log.warn("验证码ID或验证码为空");
            return false;
        }

        CaptchaData data = captchaStore.remove(captchaId);
        if (data == null) {
            log.warn("验证码不存在或已使用: {}", captchaId);
            return false;
        }

        if (System.currentTimeMillis() - data.createTime > 300000) {
            log.warn("验证码已过期");
            return false;
        }

        boolean result = data.code.equalsIgnoreCase(code);
        log.info("验证码校验结果: {}, 期望: {}, 实际: {}", result, data.code, code);
        return result;
    }

    private static class CaptchaData {
        String code;
        long createTime;

        CaptchaData(String code, long createTime) {
            this.code = code;
            this.createTime = createTime;
        }
    }
}