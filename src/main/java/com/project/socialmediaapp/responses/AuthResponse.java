package com.project.socialmediaapp.responses;

import lombok.Data;

@Data
public class AuthResponse {
    String message;
    Long userId;
    String accessToken;
    String refreshToken;
}
