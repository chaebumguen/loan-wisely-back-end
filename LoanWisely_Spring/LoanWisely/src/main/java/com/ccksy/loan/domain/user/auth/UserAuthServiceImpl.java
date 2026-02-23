package com.ccksy.loan.domain.user.auth;

import com.ccksy.loan.common.exception.BusinessException;
import com.ccksy.loan.common.exception.ErrorCode;
import com.ccksy.loan.common.security.UserJwtService;
import com.ccksy.loan.common.security.UserTokenClaims;
import com.ccksy.loan.domain.user.auth.dto.UserLoginRequest;
import com.ccksy.loan.domain.user.auth.dto.UserLoginResponse;
import com.ccksy.loan.domain.user.auth.dto.UserRegisterRequest;
import com.ccksy.loan.domain.user.auth.dto.UserRegisterResponse;
import com.ccksy.loan.domain.user.auth.dto.UserVerifyResponse;
import com.ccksy.loan.domain.user.auth.entity.UserAuth;
import com.ccksy.loan.domain.user.auth.mapper.UserAuthMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserAuthServiceImpl implements UserAuthService {

    private final UserAuthMapper userAuthMapper;
    private final UserJwtService userJwtService;
    private final PasswordEncoder passwordEncoder;
    private final long ttlSeconds;
    private final int maxFailAttempts;

    public UserAuthServiceImpl(UserAuthMapper userAuthMapper,
                               UserJwtService userJwtService,
                               PasswordEncoder passwordEncoder,
                               @Value("${security.user-jwt-ttl-secs}") long ttlSeconds,
                               @Value("${security.user-login-max-failures:5}") int maxFailAttempts) {
        this.userAuthMapper = userAuthMapper;
        this.userJwtService = userJwtService;
        this.passwordEncoder = passwordEncoder;
        this.ttlSeconds = ttlSeconds;
        this.maxFailAttempts = maxFailAttempts;
    }

    @Override
    @Transactional(noRollbackFor = BusinessException.class)
    public UserLoginResponse login(UserLoginRequest request) {
        request.assertRequiredFields();
        String username = request.getUsername().trim();

        UserAuth auth = userAuthMapper.selectByUsername(username);
        if (auth == null || !"ACTIVE".equalsIgnoreCase(auth.getStatus())) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "Invalid credentials");
        }
        if ("Y".equalsIgnoreCase(auth.getIsLocked())) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "Account locked");
        }
        if (!matchesPassword(auth.getPasswordHash(), request.getPassword())) {
            int nextCount = (auth.getFailLoginCount() == null ? 0 : auth.getFailLoginCount()) + 1;
            userAuthMapper.incrementFailLoginCount(auth.getUserId());
            if (nextCount >= maxFailAttempts) {
                userAuthMapper.lockUser(auth.getUserId());
            }
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "Invalid credentials");
        }

        userAuthMapper.resetFailLoginCount(auth.getUserId());
        userAuthMapper.updateLastLoginAt(auth.getUserId());
        String token = userJwtService.issueToken(auth.getUserId(), auth.getUsername());
        return new UserLoginResponse(auth.getUserId(), auth.getUsername(), token, ttlSeconds);
    }

    @Override
    @Transactional
    public UserRegisterResponse register(UserRegisterRequest request) {
        request.assertRequiredFields();

        UserAuth exists = userAuthMapper.selectByUsername(request.getUsername());
        if (exists != null) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST, "Username already exists");
        }

        Long nextId = userAuthMapper.selectNextId();
        UserAuth auth = UserAuth.builder()
                .userId(nextId)
                .username(request.getUsername().trim())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .status("ACTIVE")
                .failLoginCount(0)
                .isLocked("N")
                .build();

        userAuthMapper.insertUserAuth(auth);
        return new UserRegisterResponse(auth.getUserId(), auth.getUsername());
    }

    @Override
    public UserVerifyResponse verify(String token) {
        if (token == null || token.isBlank()) {
            throw new BusinessException(ErrorCode.VALIDATION_FAILED, "token is required");
        }
        UserTokenClaims claims = userJwtService.parseToken(token);
        return new UserVerifyResponse(claims.userId(), claims.username());
    }

    private boolean matchesPassword(String storedHash, String rawPassword) {
        if (storedHash == null) return false;
        if (storedHash.startsWith("plain:")) {
            return storedHash.equals("plain:" + rawPassword);
        }
        return passwordEncoder.matches(rawPassword, storedHash);
    }
}
