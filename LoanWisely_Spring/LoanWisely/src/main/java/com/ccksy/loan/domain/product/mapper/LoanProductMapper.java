package com.ccksy.loan.domain.product.mapper;

import com.ccksy.loan.domain.product.entity.LoanProduct;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface LoanProductMapper {

    LoanProduct selectById(@Param("productId") Long productId);

    List<LoanProduct> selectList(@Param("providerId") Long providerId,
                                @Param("productTypeCodeValueId") String productTypeCodeValueId,
                                @Param("loanTypeCodeValueId") String loanTypeCodeValueId,
                                @Param("repaymentTypeCodeValueId") String repaymentTypeCodeValueId);

    int insert(LoanProduct product);

    int update(LoanProduct product);
}
