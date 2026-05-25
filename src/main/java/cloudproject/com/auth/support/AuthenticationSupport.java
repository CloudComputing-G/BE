package cloudproject.com.auth.support;

import cloudproject.com.auth.domain.Role;
import cloudproject.com.global.common.code.ErrorCode;
import cloudproject.com.global.error.BusinessException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

public final class AuthenticationSupport {

    private static final String ROLE_PREFIX = "ROLE_";

    private AuthenticationSupport() {
    }

    public static Role extractRole(Authentication authentication) {
        if (authentication == null) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }

        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .filter(authority -> authority.startsWith(ROLE_PREFIX))
                .map(authority -> authority.substring(ROLE_PREFIX.length()))
                .findFirst()
                .map(AuthenticationSupport::toRole)
                .orElseThrow(() -> new BusinessException(ErrorCode.FORBIDDEN));
    }

    private static Role toRole(String roleName) {
        try {
            return Role.valueOf(roleName);
        } catch (IllegalArgumentException e) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }
    }
}
