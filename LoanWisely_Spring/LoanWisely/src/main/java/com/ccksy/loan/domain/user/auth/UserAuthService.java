package com.ccksy.loan.domain.user.auth;

import com.ccksy.loan.domain.user.auth.dto.UserLoginRequest;
import com.ccksy.loan.domain.user.auth.dto.UserLoginResponse;
import com.ccksy.loan.domain.user.auth.dto.UserRegisterRequest;
import com.ccksy.loan.domain.user.auth.dto.UserRegisterResponse;
import com.ccksy.loan.domain.user.auth.dto.UserVerifyResponse;

public interface UserAuthService {
    UserLoginResponse login(UserLoginRequest request);

    UserRegisterResponse register(UserRegisterRequest request);

    UserVerifyResponse verify(String token);
}
