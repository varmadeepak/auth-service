package com.expensetracker.authservice.service;


import com.expensetracker.authservice.entity.RefreshToken;
import com.expensetracker.authservice.entity.UserInfo;
import com.expensetracker.authservice.repository.RefreshTokenRepository;
import com.expensetracker.authservice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class RefreshTokenService {

    @Autowired
    RefreshTokenRepository refreshTokenRepository;
    @Autowired
    UserRepository userRepository;

    public RefreshToken createRefreshToken(String userName){
        UserInfo extractUser = userRepository.findByUsername(userName);
        RefreshToken refreshToken = RefreshToken.builder()
                .userInfo(extractUser)
                .token(UUID.randomUUID().toString())
                .expiryDate(Instant.now().plusMillis(600000))
                .build();

        return refreshTokenRepository.save(refreshToken);
    }

    public RefreshToken verifyExpiration(RefreshToken refreshToken){
        if(refreshToken.getExpiryDate().compareTo(Instant.now()) < 0){
            refreshTokenRepository.delete(refreshToken);
            throw new RuntimeException(refreshToken.getToken() + "Has been Expired");
        }
        return refreshToken;
    }

    public Optional<RefreshToken> findByToken(String token){
        return refreshTokenRepository.findByToken(token);
    }
}
