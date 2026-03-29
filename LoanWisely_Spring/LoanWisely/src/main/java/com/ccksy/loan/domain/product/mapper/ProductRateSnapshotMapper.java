package com.ccksy.loan.domain.product.mapper;

import com.ccksy.loan.domain.product.entity.ProductRateSnapshot;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ProductRateSnapshotMapper {

    ProductRateSnapshot selectLatestByProductId(@Param("productId") Long productId);

    int insert(ProductRateSnapshot snapshot);
}
