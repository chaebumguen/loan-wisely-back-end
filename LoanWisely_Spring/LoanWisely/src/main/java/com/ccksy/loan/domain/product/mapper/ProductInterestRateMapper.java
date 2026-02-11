package com.ccksy.loan.domain.product.mapper;

import com.ccksy.loan.domain.product.entity.ProductInterestRate;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ProductInterestRateMapper {

    ProductInterestRate selectLatestByProductId(@Param("productId") Long productId);
}
