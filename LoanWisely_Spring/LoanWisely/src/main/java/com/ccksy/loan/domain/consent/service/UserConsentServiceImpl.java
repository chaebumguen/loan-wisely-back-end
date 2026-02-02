//package com.ccksy.loan.domain.consent.service;
//
//import com.ccksy.loan.domain.consent.dto.request.UserConsentRequest;
//import com.ccksy.loan.domain.consent.dto.response.UserConsentResponse;
//import com.ccksy.loan.domain.consent.entity.UserConsent;
//import com.ccksy.loan.domain.consent.mapper.UserConsentMapper;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.io.IOException;
//import java.nio.file.*;
//import java.time.Instant;
//import java.time.temporal.ChronoUnit;
//import java.util.List;
//import java.util.stream.Collectors;
//
///**
// * 동의는 DATA-1 요구사항(동의/이력/재현) 기준에 맞춰 append-only로 저장한다.
// * - DB에는 목적태그 JSON을 직접 저장하지 않고 파일 경로만 저장(요청사항 반영)
// */
//@Service
//public class UserConsentServiceImpl implements UserConsentService {
//
//    private final UserConsentMapper userConsentMapper;
//    private final ObjectMapper objectMapper;
//
//    /**
//     * 예: /var/app/storage (환경별 설정)
//     * - 동의 목적태그 JSON 파일 저장 루트
//     */
//    private final String consentStorageBasePath;
//
//    public UserConsentServiceImpl(
//            UserConsentMapper userConsentMapper,
//            ObjectMapper objectMapper,
//            @Value("${storage.consent.base-path:./storage/consent}") String consentStorageBasePath
//    ) {
//        this.userConsentMapper = userConsentMapper;
//        this.objectMapper = objectMapper;
//        this.consentStorageBasePath = consentStorageBasePath;
//    }
//
//    @Transactional
//    @Override
//    public UserConsentResponse createConsent(Long userId, UserConsentRequest request) {
//        request.validate();
//
//        // 목적태그는 파일로 저장 후 경로만 DB에 저장
//        String purposeTagsPath = null;
//        if (request.getPurposeTags() != null && !request.getPurposeTags().isEmpty()) {
//            purposeTagsPath = writePurposeTagsFile(userId, request.getLevel(), request.getPurposeTags());
//        }
//
//        UserConsent entity = new UserConsent();
//        entity.setUserId(userId);
//        entity.setConsentTypeCodeValueId(request.getLevel()); // level을 code_value_id로 취급
//        entity.setAgreedYn(request.isAgreedYn() ? "Y" : "N");
//        entity.setAgreedAt(Instant.now());
//        entity.setExpiredAt(resolveExpiry(request.getExpireDays()));
//        entity.setPurposeTagsPath(purposeTagsPath);
//        entity.setIsActive("Y");
//
//        userConsentMapper.insert(entity);
//
//        return UserConsentResponse.from(entity);
//    }
//
//    @Override
//    public List<UserConsentResponse> getActiveConsents(Long userId) {
//        return userConsentMapper.findActiveByUserId(userId).stream()
//                .map(UserConsentResponse::from)
//                .collect(Collectors.toList());
//    }
//
//    private Instant resolveExpiry(Integer expireDays) {
//        int days = (expireDays == null || expireDays <= 0) ? 365 : expireDays; // 기본 1년
//        return Instant.now().plus(days, ChronoUnit.DAYS);
//    }
//
//    private String writePurposeTagsFile(Long userId, String level, List<String> purposeTags) {
//        try {
//            Path base = Paths.get(consentStorageBasePath, "user-" + userId, "consents");
//            Files.createDirectories(base);
//
//            // 파일명: consent_purposeTags_{level}_{epoch}.json
//            String filename = "consent_purposeTags_" + level + "_" + Instant.now().toEpochMilli() + ".json";
//            Path target = base.resolve(filename);
//
//            String json = objectMapper.writeValueAsString(purposeTags);
//            Files.writeString(target, json, StandardOpenOption.CREATE_NEW);
//
//            return target.toString();
//        } catch (IOException e) {
//            // Evidence 저장 실패는 결과 무효 처리 대상(정책상 fail-fast)
//            throw new IllegalStateException("Failed to persist purposeTags file for consent", e);
//        }
//    }
//}

// FILE: domain/consent/service/UserConsentServiceImpl.java
package com.ccksy.loan.domain.consent.service;

import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * UserConsentService 구현체 (v1)
 *
 * Bean 규칙:
 * - UserConsentService 타입의 유일한 Spring Bean
 * - Controller에서는 @Qualifier 없이 주입 가능
 */
@Service
public class UserConsentServiceImpl implements UserConsentService {

    public UserConsentServiceImpl() {
        // 실제 구현에서는 UserConsentMapper / Repository 주입
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasConsent(Long userId, String consentType) {
        Objects.requireNonNull(userId, "userId must not be null");
        Objects.requireNonNull(consentType, "consentType must not be null");

        // TODO: 실제 조회 로직으로 교체
        // v1 기본값: 동의 없음
        return false;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveConsent(Long userId, String consentType, boolean agreed) {
        Objects.requireNonNull(userId, "userId must not be null");
        Objects.requireNonNull(consentType, "consentType must not be null");

        // TODO: 실제 저장/갱신 로직으로 교체
        // v1: 구조/트랜잭션 경계만 제공
    }
}

