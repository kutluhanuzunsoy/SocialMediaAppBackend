package com.project.socialmediaapp.services;

import com.project.socialmediaapp.entities.RefreshToken;
import com.project.socialmediaapp.entities.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.project.socialmediaapp.repos.RefreshTokenRepository;

import java.time.Instant;
import java.util.Date;

@Service
public class RefreshTokenService {
    @Value("${refresh.token.expires.in}")
    Long expireSeconds;

    private final RefreshTokenRepository refreshTokenRepository;

    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
    }

    public String createRefreshToken(User user) {
        RefreshToken refreshToken = refreshTokenRepository.findByUserId(user.getId());
        if (refreshToken == null) {
            refreshToken = new RefreshToken();
            refreshToken.setUser(user);
        }

        refreshToken.setToken(java.util.UUID.randomUUID().toString());
        refreshToken.setExpiryDate(Date.from(Instant.now().plusSeconds(expireSeconds)));
        refreshTokenRepository.save(refreshToken);

        return refreshToken.getToken();
    }

    public boolean isRefreshTokenExpired(RefreshToken refreshToken) {
        return refreshToken.getExpiryDate().before(new Date());
    }

    public RefreshToken getRefreshTokenByUserId(Long userId) {
        return refreshTokenRepository.findByUserId(userId);
    }
}
