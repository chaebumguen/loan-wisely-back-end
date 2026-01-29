package com.ccksy.loan.domain.product.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

/**
 * 대출 상품 조회 응답 DTO
 *
 * 역할:
 * - 대출 상품 목록/단건 조회 시 클라이언트로 반환되는 데이터 모델
 * - 조회 전용(Read Model)
 *
 * 설계 원칙:
 * - 비즈니스 로직 포함 금지
 * - Entity와 분리 (조회 API 최적화)
 * - MyBatis ResultMap과 1:1 매핑
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoanProductResponse {

    /** 상품 ID */
    private Long productId;

    /** 상품명 */
    private String productName;

    /** 금융기관명 */
    private String bankName;

    /** 금리 (예: 3.5%) */
    private BigDecimal interestRate;

    /** 최대 한도 */
    private Long maxLimit;

    /** 대출 유형 (예: 신용대출, 전세자금대출 등) */
    private String loanType;

    /** 상품 등록일 */
    private LocalDateTime createdAt;
}
