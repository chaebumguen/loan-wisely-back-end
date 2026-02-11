package com.ccksy.loan.domain.recommend.command;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 짧은 호출 로그(관리용)
 * - DB 저장은 선택이며, v1에서는 콘솔 로그/파일 로그로도 충분
 */
@Getter
@Builder
public class RecommendCommandLog {

    private String reproduceKey;
    private Long userId;
    private LocalDateTime requestedAt;
    private String message;
}
