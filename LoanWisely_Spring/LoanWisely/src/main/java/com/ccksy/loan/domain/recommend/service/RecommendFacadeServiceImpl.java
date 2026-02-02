// FILE: domain/recommend/service/RecommendFacadeServiceImpl.java
package com.ccksy.loan.domain.recommend.service;

import java.lang.reflect.Constructor;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ccksy.loan.domain.recommend.command.RecommendCommand;
import com.ccksy.loan.domain.recommend.command.RecommendCommandHandler;
import com.ccksy.loan.domain.recommend.dto.request.RecommendRequest;
import com.ccksy.loan.domain.recommend.entity.RecommendHistory;
import com.ccksy.loan.domain.recommend.mapper.RecommendHistoryMapper;
import com.ccksy.loan.domain.recommend.result.core.RecommendItem;
import com.ccksy.loan.domain.recommend.result.response.RecommendResponse;

/**
 * RecommendFacadeService 구현체 (v1)
 *
 * Controller 컴파일 오류 해소 포인트:
 * - recommend(request) 메서드 제공
 * - explain(recommendationId) 메서드 제공
 *
 * v1 책임:
 * - Controller는 전달만, Service는 오케스트레이션만
 * - 판단/정책/점수 로직은 process/template 구현체가 수행
 */
@Service
public class RecommendFacadeServiceImpl implements RecommendFacadeService {

    private static final Logger log = LoggerFactory.getLogger(RecommendFacadeServiceImpl.class);

    private final RecommendCommandHandler recommendCommandHandler;
    private final RecommendHistoryMapper recommendHistoryMapper;

    public RecommendFacadeServiceImpl(
            RecommendCommandHandler recommendCommandHandler,
            RecommendHistoryMapper recommendHistoryMapper
    ) {
        this.recommendCommandHandler = recommendCommandHandler;
        this.recommendHistoryMapper = recommendHistoryMapper;
    }

    /**
     * 추천 실행
     *
     * @return RecommendResponse (프로젝트에 이미 존재하는 응답 DTO로 조립)
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public RecommendResponse recommend(RecommendRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("RecommendRequest must not be null.");
        }

        Long userId = resolveUserId();
        Map<String, Object> options = request.getOptions() == null ? Collections.emptyMap() : request.getOptions();

        RecommendCommand command = new RecommendCommand(
                userId,
                request.getPolicyVersion(),
                request.getCreditMetaVersion(),
                request.getFinancialMetaVersion(),
                options
        );

        // v1: 후보군은 외부에서 준비되어 주입되는 것이 원칙이나,
        // service 폴더만 생성하는 범위에서 추가 컴포넌트/파일을 만들지 않기 위해
        // options["candidates"]에 List<RecommendItem>이 들어온 경우만 사용한다.
        List<RecommendItem> candidates = extractCandidates(options);

        List<RecommendItem> recommended = recommendCommandHandler.handle(command, candidates);

        // RecommendResponse의 생성자/팩토리 메서드 시그니처를 확정할 정보가 없으므로
        // 컴파일 안정성을 위해 리플렉션 기반으로 조립한다(런타임 Fail-Fast).
        return buildRecommendResponse(recommended);
    }

    /**
     * Explain 조회
     *
     * @return v1에서는 Object로 반환(Controller 시그니처 정합).
     */
    @Override
    @Transactional(readOnly = true)
    public Object explain(Long recommendationId) {
        if (recommendationId == null) {
            throw new IllegalArgumentException("recommendationId must not be null.");
        }

        Optional<RecommendHistory> found = recommendHistoryMapper.findById(recommendationId);
        return found.orElseThrow(() ->
                new IllegalStateException("Recommendation not found. id=" + recommendationId)
        );
    }

    private Long resolveUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getPrincipal() == null) {
            throw new IllegalStateException("Unauthenticated request (no authentication principal).");
        }

        Object principal = auth.getPrincipal();
        if (principal instanceof Number) {
            return ((Number) principal).longValue();
        }

        String s = String.valueOf(principal).trim();
        try {
            return Long.parseLong(s);
        } catch (Exception e) {
            throw new IllegalStateException("Cannot resolve userId from principal: " + principal);
        }
    }

    @SuppressWarnings("unchecked")
    private List<RecommendItem> extractCandidates(Map<String, Object> options) {
        Object v = options.get("candidates");
        if (v == null) return List.of();
        if (v instanceof List) {
            try {
                return (List<RecommendItem>) v;
            } catch (ClassCastException e) {
                log.warn("options.candidates exists but not List<RecommendItem>. type={}", v.getClass().getName());
                return List.of();
            }
        }
        return List.of();
    }

    private RecommendResponse buildRecommendResponse(List<RecommendItem> items) {
        List<RecommendItem> safe = (items == null) ? List.of() : items;

        // 1) public RecommendResponse(List<RecommendItem>)
        try {
            Constructor<RecommendResponse> c = RecommendResponse.class.getConstructor(List.class);
            return c.newInstance(safe);
        } catch (NoSuchMethodException ignore) {
            // next
        } catch (Exception e) {
            throw new IllegalStateException("Failed to instantiate RecommendResponse via (List) constructor.", e);
        }

        // 2) static of(List)
        try {
            return (RecommendResponse) RecommendResponse.class
                    .getMethod("of", List.class)
                    .invoke(null, safe);
        } catch (NoSuchMethodException ignore) {
            // next
        } catch (Exception e) {
            throw new IllegalStateException("Failed to build RecommendResponse via static of(List).", e);
        }

        // 3) static from(List)
        try {
            return (RecommendResponse) RecommendResponse.class
                    .getMethod("from", List.class)
                    .invoke(null, safe);
        } catch (NoSuchMethodException ignore) {
            // final
        } catch (Exception e) {
            throw new IllegalStateException("Failed to build RecommendResponse via static from(List).", e);
        }

        throw new IllegalStateException(
                "RecommendResponse builder not found. Provide either: "
                        + "public RecommendResponse(List), static of(List), or static from(List)."
        );
    }
}
