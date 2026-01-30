package com.ccksy.loan.domain.product.mapper;

import com.ccksy.loan.domain.product.dto.request.LoanProductRequest;
import com.ccksy.loan.domain.product.entity.LoanProduct;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface LoanProductMapper {

    List<LoanProduct> selectProducts(@Param("req") LoanProductRequest req,
                                     @Param("offset") int offset,
                                     @Param("limit") int limit);

    int countProducts(@Param("req") LoanProductRequest req);

    LoanProduct selectProductDetail(@Param("productId") long productId);

    // 내부관리(적재 파이프라인)에서 호출할 수 있도록 최소 upsert 제공
    int upsertProduct(@Param("p") LoanProduct product);

    int insertLatestRate(@Param("p") LoanProduct product);
}
