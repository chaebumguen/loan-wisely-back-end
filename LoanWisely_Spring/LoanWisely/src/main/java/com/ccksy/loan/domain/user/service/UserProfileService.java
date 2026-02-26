package com.ccksy.loan.domain.user.service;

import com.ccksy.loan.domain.user.dto.request.UserProfileRequest;
import com.ccksy.loan.domain.user.dto.response.UserProfileResponse;

import java.util.List;

public interface UserProfileService {

    UserProfileResponse upsertProfile(UserProfileRequest request);

    UserProfileResponse getLatestProfile(Long userId);

    List<UserProfileResponse> getProfileHistory(Long userId);
}
