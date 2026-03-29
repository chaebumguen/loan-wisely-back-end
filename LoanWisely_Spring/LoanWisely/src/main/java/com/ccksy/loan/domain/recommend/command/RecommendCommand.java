package com.ccksy.loan.domain.recommend.command;

import com.ccksy.loan.domain.recommend.dto.request.RecommendRequest;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class RecommendCommand {

    private Long userId;
    private Integer requestedInputLevel;
    private String requestTraceId;

    private LocalDateTime requestedAt;

    /**
     * 재현키
     * - traceId가 있으면 그것을 우선 사용(팀 단위 입력/추적에 유리)
     * - 없으면 UUID 기반(추후 스냅샷/정책/메타 기반 결정론 키로 개선)
     */
    private String reproduceKey;

    public static RecommendCommand from(RecommendRequest req) {
        String key;
        if (req.getRequestTraceId() != null && !req.getRequestTraceId().isBlank()) {
            key = "R-" + req.getUserId() + "-" + req.getRequestTraceId();
        } else {
            key = "R-" + req.getUserId() + "-" + UUID.randomUUID();
        }

        return RecommendCommand.builder()
                .userId(req.getUserId())
                .requestedInputLevel(req.getRequestedInputLevel())
                .requestTraceId(req.getRequestTraceId())
                .requestedAt(LocalDateTime.now())
                .reproduceKey(key)
                .build();
    }
}
