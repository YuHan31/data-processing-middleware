package com.middleware.org.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserResponse {
    private Long userId;
    private String account;
    private String email;
    private String phone;
    private String name;
    private String nickname;
    private String role;
}
