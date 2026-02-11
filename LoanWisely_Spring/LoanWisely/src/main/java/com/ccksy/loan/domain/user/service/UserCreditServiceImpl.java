package com.ccksy.loan.domain.user.service;

import com.ccksy.loan.domain.user.dto.request.UserCreditLv1Request;
import com.ccksy.loan.domain.user.dto.request.UserCreditLv2Request;
import com.ccksy.loan.domain.user.dto.request.UserCreditLv3Request;
import com.ccksy.loan.domain.user.dto.response.UserCreditLv1Response;
import com.ccksy.loan.domain.user.dto.response.UserCreditLv2Response;
import com.ccksy.loan.domain.user.dto.response.UserCreditLv3Response;
import com.ccksy.loan.domain.user.entity.UserCreditLv1;
import com.ccksy.loan.domain.user.entity.UserCreditLv2;
import com.ccksy.loan.domain.user.entity.UserCreditLv3;
import com.ccksy.loan.domain.user.mapper.UserCreditLv1Mapper;
import com.ccksy.loan.domain.user.mapper.UserCreditLv2Mapper;
import com.ccksy.loan.domain.user.mapper.UserCreditLv3Mapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserCreditServiceImpl implements UserCreditService {

    private final UserCreditLv1Mapper userCreditLv1Mapper;
    private final UserCreditLv2Mapper userCreditLv2Mapper;
    private final UserCreditLv3Mapper userCreditLv3Mapper;

    @Override
    @Transactional
    public UserCreditLv1Response upsertLv1(UserCreditLv1Request request) {
        LocalDateTime now = LocalDateTime.now();
        Long nextId = userCreditLv1Mapper.selectNextId();
        UserCreditLv1 entity = UserCreditLv1.builder()
                .lv1Id(nextId)
                .userId(request.getUserId())
                .age(request.getAge())
                .incomeYear(request.getIncomeYear())
                .gender(request.getGender())
                .isActive("Y")
                .createdAt(now)
                .build();

        userCreditLv1Mapper.deactivateActiveByUserId(request.getUserId());
        userCreditLv1Mapper.insert(entity);
        return UserCreditLv1Response.from(entity);
    }

    @Override
    @Transactional
    public UserCreditLv2Response upsertLv2(UserCreditLv2Request request) {
        LocalDateTime now = LocalDateTime.now();
        Long nextId = userCreditLv2Mapper.selectNextId();
        UserCreditLv2 entity = UserCreditLv2.builder()
                .lv2Id(nextId)
                .userId(request.getUserId())
                .employmentType(request.getEmploymentType())
                .residenceType(request.getResidenceType())
                .isActive("Y")
                .createdAt(now)
                .build();

        userCreditLv2Mapper.deactivateActiveByUserId(request.getUserId());
        userCreditLv2Mapper.insert(entity);
        return UserCreditLv2Response.from(entity);
    }

    @Override
    @Transactional
    public UserCreditLv3Response upsertLv3(UserCreditLv3Request request) {
        LocalDateTime now = LocalDateTime.now();
        Long nextId = userCreditLv3Mapper.selectNextId();
        UserCreditLv3 entity = UserCreditLv3.builder()
                .lv3Id(nextId)
                .userId(request.getUserId())
                .loanPurpose(request.getLoanPurpose())
                .totalDebt(request.getTotalDebt())
                .existingLoanCount(request.getExistingLoanCount())
                .isActive("Y")
                .createdAt(now)
                .build();

        userCreditLv3Mapper.deactivateActiveByUserId(request.getUserId());
        userCreditLv3Mapper.insert(entity);
        return UserCreditLv3Response.from(entity);
    }
}
