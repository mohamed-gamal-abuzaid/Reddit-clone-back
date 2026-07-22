package com.redditclone.reddit_backend.service;

import com.redditclone.reddit_backend.dtos.Auth.AuthResponse;
import com.redditclone.reddit_backend.dtos.Auth.LoginRequest;
import com.redditclone.reddit_backend.dtos.Auth.RegisterRequest;
import com.redditclone.reddit_backend.entity.*;
import com.redditclone.reddit_backend.repository.*;
import com.redditclone.reddit_backend.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Value("${jwt.refresh-token-expiration}")
    private long refreshTokenExpiration;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail()))
            throw new RuntimeException("Email already in use");

        if (userRepository.existsByUsername(request.getUsername()))
            throw new RuntimeException("Username already taken");

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .build();

        userRepository.save(user);

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = saveRefreshToken(user);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .username(user.getUsername())
                .email(user.getEmail())
                .build();
    }

    @Transactional
    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        refreshTokenRepository.revokeAllUserTokens(user);

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = saveRefreshToken(user);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .username(user.getUsername())
                .email(user.getEmail())
                .build();
    }

    @Transactional
    public AuthResponse refreshToken(String refreshToken) {
        RefreshToken token = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new RuntimeException("Refresh token not found"));

        if (token.isRevoked() || token.getExpiresAt().isBefore(LocalDateTime.now()))
            throw new RuntimeException("Refresh token expired or revoked");

        User user = token.getUser();
        refreshTokenRepository.revokeAllUserTokens(user);

        String newAccessToken = jwtService.generateAccessToken(user);
        String newRefreshToken = saveRefreshToken(user);

        return AuthResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .username(user.getUsername())
                .email(user.getEmail())
                .build();
    }

    public String saveRefreshToken(User user) {
        String tokenValue = UUID.randomUUID().toString();
        RefreshToken refreshToken = RefreshToken.builder()
                .token(tokenValue)
                .user(user)
                .expiresAt(LocalDateTime.now().plusSeconds(refreshTokenExpiration / 1000))
                .revoked(false)
                .build();
        refreshTokenRepository.save(refreshToken);
        return tokenValue;
    }
}
