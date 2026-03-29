package com.ccksy.loan.domain.product.dto.request;

import com.ccksy.loan.common.exception.BusinessException;
import com.ccksy.loan.common.exception.ErrorCode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

/**
 * 상품 등록/수정 요청
 * - 코드값은 CODE_VALUE 기반으로 관리된다는 전제(문자열 코드 식별자)
 */
@Getter
@Setter
@NoArgsConstructor
public class LoanProductRequest {

    // 수정 시 사용
    private Long productId;

    @NotNull
    private Long providerId;

    @NotBlank
    private String productName;

    @NotBlank
    private String productTypeCodeValueId;

    @NotBlank
    private String loanTypeCodeValueId;

    @NotBlank
    private String repaymentTypeCodeValueId;

    private String collateralTypeCodeValueId;
    private String rateTypeCodeValueId;

    private String note;

    /**
     * 판매 종료일(옵션)
     */
    private LocalDate endDate;

    public void assertRequiredFields() {
        if (providerId == null) {
            throw new BusinessException(ErrorCode.VALIDATION_FAILED, "providerId는 필수입니다.");
        }
        if (productName == null || productName.trim().isEmpty()) {
            throw new BusinessException(ErrorCode.VALIDATION_FAILED, "productName은 필수입니다.");
        }
        if (productTypeCodeValueId == null || productTypeCodeValueId.trim().isEmpty()) {
            throw new BusinessException(ErrorCode.VALIDATION_FAILED, "productTypeCodeValueId는 필수입니다.");
        }
        if (loanTypeCodeValueId == null || loanTypeCodeValueId.trim().isEmpty()) {
            throw new BusinessException(ErrorCode.VALIDATION_FAILED, "loanTypeCodeValueId는 필수입니다.");
        }
        if (repaymentTypeCodeValueId == null || repaymentTypeCodeValueId.trim().isEmpty()) {
            throw new BusinessException(ErrorCode.VALIDATION_FAILED, "repaymentTypeCodeValueId는 필수입니다.");
        }
    }
}
