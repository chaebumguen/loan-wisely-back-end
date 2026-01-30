package com.ccksy.loan.domain.product.service;

import com.ccksy.loan.domain.product.dto.request.LoanProductRequest;
import com.ccksy.loan.domain.product.dto.response.LoanProductResponse;
import com.ccksy.loan.domain.product.entity.LoanProduct;
import com.ccksy.loan.domain.product.mapper.LoanProductMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Service
public class LoanProductServiceImpl implements LoanProductService {

    private static final Logger log = LoggerFactory.getLogger(LoanProductServiceImpl.class);

    private final LoanProductMapper mapper;

    public LoanProductServiceImpl(LoanProductMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public List<LoanProductResponse> listProducts(LoanProductRequest req) {
        normalizeAndValidate(req);

        int offset = (req.getPage() - 1) * req.getSize();
        List<LoanProduct> rows = mapper.selectProducts(req, offset, req.getSize());

        List<LoanProductResponse> out = new ArrayList<>();
        for (LoanProduct r : rows) {
            out.add(toResponse(r));
        }

        // “판단에 사용된 자원”을 짧게 출력(요청 단위 관리 목적)
        log.info("RESOURCE_USED: domain/product:listProducts spec={} dbTables=PRODUCT,PRODUCT_INTEREST_RATE mapper=LoanProductMapper.xml",
                "SPEC-2026-01-29-v1");

        return out;
    }

    @Override
    public int countProducts(LoanProductRequest req) {
        normalizeAndValidate(req);
        return mapper.countProducts(req);
    }

    @Override
    public LoanProductResponse getProductDetail(long productId) {
        if (productId <= 0) {
            throw new IllegalArgumentException("productId must be positive.");
        }
        LoanProduct row = mapper.selectProductDetail(productId);
        if (row == null) {
            return null;
        }

        log.info("RESOURCE_USED: domain/product:getProductDetail spec={} dbTables=PRODUCT,PRODUCT_INTEREST_RATE mapper=LoanProductMapper.xml productId={}",
                "SPEC-2026-01-29-v1", productId);

        return toResponse(row);
    }

    @Override
    public void upsertProduct(LoanProduct product) {
        if (product == null) {
            throw new IllegalArgumentException("product must not be null.");
        }
        if (product.getProductId() == null || product.getProductId() <= 0) {
            throw new IllegalArgumentException("product.productId must be positive.");
        }
        if (!StringUtils.hasText(product.getProductName())) {
            throw new IllegalArgumentException("product.productName is required.");
        }
        if (product.getProviderId() == null || product.getProviderId() <= 0) {
            throw new IllegalArgumentException("product.providerId must be positive.");
        }

        mapper.upsertProduct(product);

        // 금리 스냅샷이 넘어오면 최신 스냅샷 insert
        if (product.getAsOfDate() != null && (product.getRateMin() != null || product.getRateMax() != null || product.getRateBase() != null)) {
            mapper.insertLatestRate(product);
        }

        log.info("RESOURCE_USED: domain/product:upsertProduct spec={} dbTables=PRODUCT,PRODUCT_INTEREST_RATE mapper=LoanProductMapper.xml productId={}",
                "SPEC-2026-01-29-v1", product.getProductId());
    }

    private void normalizeAndValidate(LoanProductRequest req) {
        if (req == null) {
            throw new IllegalArgumentException("request must not be null.");
        }
        if (req.getPage() == null || req.getPage() < 1) {
            req.setPage(1);
        }
        if (req.getSize() == null || req.getSize() < 1) {
            req.setSize(20);
        }
        if (req.getSize() > 100) {
            req.setSize(100);
        }

        // sortDir normalize
        String dir = req.getSortDir();
        if (!"ASC".equalsIgnoreCase(dir) && !"DESC".equalsIgnoreCase(dir)) {
            req.setSortDir("ASC");
        } else {
            req.setSortDir(dir.toUpperCase());
        }

        // sortBy whitelist normalize
        String sortBy = req.getSortBy();
        if (!StringUtils.hasText(sortBy)) {
            req.setSortBy("rateMin");
        } else {
            req.setSortBy(sortBy.trim());
        }
    }

    private LoanProductResponse toResponse(LoanProduct r) {
        LoanProductResponse o = new LoanProductResponse();
        o.setProductId(r.getProductId());
        o.setProviderId(r.getProviderId());
        o.setProviderName(r.getProviderName());
        o.setProductName(r.getProductName());

        o.setProductTypeCodeValueId(r.getProductTypeCodeValueId());
        o.setLoanTypeCodeValueId(r.getLoanTypeCodeValueId());
        o.setRepaymentTypeCodeValueId(r.getRepaymentTypeCodeValueId());
        o.setCollateralTypeCodeValueId(r.getCollateralTypeCodeValueId());
        o.setRateTypeCodeValueId(r.getRateTypeCodeValueId());

        o.setRateBase(r.getRateBase());
        o.setRateMin(r.getRateMin());
        o.setRateMax(r.getRateMax());
        o.setAsOfDate(r.getAsOfDate());

        o.setNote(r.getNote());
        o.setEndDate(r.getEndDate());
        return o;
    }
}
