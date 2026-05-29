package cloudproject.com.auth.dto;

public record TokenResponse(
        String accessToken,
        String refreshToken,
        String username
) {}
