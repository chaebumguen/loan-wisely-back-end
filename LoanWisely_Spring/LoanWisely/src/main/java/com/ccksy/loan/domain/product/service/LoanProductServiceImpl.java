package com.ccksy.loan.domain.product.service;

import com.ccksy.loan.common.exception.BusinessException;
import com.ccksy.loan.common.exception.ErrorCode;
import com.ccksy.loan.domain.product.dto.request.LoanProductRequest;
import com.ccksy.loan.domain.product.dto.response.LoanProductResponse;
import com.ccksy.loan.domain.product.entity.LoanProduct;
import com.ccksy.loan.domain.product.mapper.LoanProductMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LoanProductServiceImpl implements LoanProductService {

    private final LoanProductMapper loanProductMapper;

    @Override
    @Transactional(readOnly = true)
    public LoanProductResponse getById(Long productId) {
        if (productId == null) {
            throw new BusinessException(ErrorCode.VALIDATION_FAILED, "productId는 필수입니다.");
        }

        LoanProduct entity = loanProductMapper.selectById(productId);
        if (entity == null) {
            // v1(B안): NOT_FOUND 없이 INVALID_REQUEST로 처리
            throw new BusinessException(ErrorCode.INVALID_REQUEST, "해당 상품이 존재하지 않습니다.");
        }
        return LoanProductResponse.from(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LoanProductResponse> search(Long providerId,
                                           String productTypeCodeValueId,
                                           String loanTypeCodeValueId,
                                           String repaymentTypeCodeValueId) {
        List<LoanProduct> list = loanProductMapper.selectList(providerId, productTypeCodeValueId, loanTypeCodeValueId, repaymentTypeCodeValueId);
        return list.stream().map(LoanProductResponse::from).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public LoanProductResponse upsert(LoanProductRequest request) {
        request.assertRequiredFields();

        LocalDateTime now = LocalDateTime.now();
        LoanProduct entity = LoanProduct.from(request).toBuilder()
                .updatedAt(now)
                .build();

        if (entity.getProductId() == null) {
            entity = entity.toBuilder().addDate(now).build();
            int inserted = loanProductMapper.insert(entity);
            if (inserted != 1) {
                throw new BusinessException(ErrorCode.INTERNAL_ERROR, "상품 등록에 실패했습니다.");
            }
        } else {
            int updated = loanProductMapper.update(entity);
            if (updated != 1) {
                throw new BusinessException(ErrorCode.INVALID_REQUEST, "상품 수정 대상이 없거나 수정에 실패했습니다.");
            }
        }

        // upsert 후 단건 재조회(정합성 확인)
        Long id = entity.getProductId();
        if (id == null) {
            // insert 시 product_id를 DB에서 생성하는 경우, XML에서 selectKey로 주입하도록 설계
            // v1에서는 강제하지 않음. (원하면 V2/XML 작성 단계에서 selectKey를 적용)
            throw new BusinessException(ErrorCode.INTERNAL_ERROR, "등록 후 productId 확인이 필요합니다(Mapper selectKey 설정).");
        }

        LoanProduct saved = loanProductMapper.selectById(id);
        if (saved == null) {
            throw new BusinessException(ErrorCode.INTERNAL_ERROR, "상품 저장 후 조회에 실패했습니다.");
        }
        return LoanProductResponse.from(saved);
    }
}
