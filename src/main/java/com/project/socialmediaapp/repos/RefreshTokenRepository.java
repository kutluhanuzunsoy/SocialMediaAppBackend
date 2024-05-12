package com.project.socialmediaapp.repos;

import com.project.socialmediaapp.entities.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long>{
    RefreshToken findByUserId(Long userId);
}
