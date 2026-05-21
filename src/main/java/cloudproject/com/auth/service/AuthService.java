package cloudproject.com.auth.service;

import cloudproject.com.auth.domain.RefreshToken;
import cloudproject.com.auth.domain.User;
import cloudproject.com.auth.dto.LoginRequest;
import cloudproject.com.auth.dto.SignupRequest;
import cloudproject.com.auth.dto.TokenResponse;
import cloudproject.com.auth.repository.RefreshTokenRepository;
import cloudproject.com.auth.repository.UserRepository;
import cloudproject.com.global.common.code.ErrorCode;
import cloudproject.com.global.error.BusinessException;
import cloudproject.com.global.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public void signup(SignupRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new BusinessException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }
        User user = User.builder()
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .name(request.name())
                .role(request.role())
                .createdAt(LocalDateTime.now())
                .build();
        userRepository.save(user);
    }

    @Transactional
    public TokenResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new BusinessException(ErrorCode.INVALID_PASSWORD);
        }

        String accessToken = jwtTokenProvider.generateAccessToken(user.getUserId(), user.getRole());
        String newRefreshToken = jwtTokenProvider.generateRefreshToken(user.getUserId());

        String hashedToken = jwtTokenProvider.hashToken(newRefreshToken);
        refreshTokenRepository.findByUser(user).ifPresentOrElse(
                rt -> rt.updateToken(hashedToken),
                () -> refreshTokenRepository.save(RefreshToken.of(user, hashedToken))
        );

        return new TokenResponse(accessToken, newRefreshToken);
    }

    @Transactional
    public TokenResponse refresh(String refreshToken) {
        RefreshToken stored = refreshTokenRepository.findByToken(jwtTokenProvider.hashToken(refreshToken))
                .orElseThrow(() -> new BusinessException(ErrorCode.INVALID_TOKEN));

        User user = stored.getUser();
        String newAccessToken = jwtTokenProvider.generateAccessToken(user.getUserId(), user.getRole());
        String newRefreshToken = jwtTokenProvider.generateRefreshToken(user.getUserId());

        stored.updateToken(jwtTokenProvider.hashToken(newRefreshToken));

        return new TokenResponse(newAccessToken, newRefreshToken);
    }
}
