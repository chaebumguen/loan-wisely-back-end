package com.ccksy.loan.domain.recommend.filter.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class FilterContext {

    private final Long userId;
    private final Integer inputLv;          // LV1~LV3
    private final Integer creditScore;      // 점수화/로딩 단계에서 채워 넣는 값
    private final BigDecimal dsr;           // 점수화/Feature 단계에서 채워 넣는 값
    private final String loanPurposeCode;   // 목적 코드
    private final LocalDateTime asOf;       // 판단 시점

    // 확장 필드 (향후 ML/정책/메타 추가시 사용)
    private final Map<String, Object> attributes = new HashMap<>();

    public FilterContext(Long userId,
                         Integer inputLv,
                         Integer creditScore,
                         BigDecimal dsr,
                         String loanPurposeCode,
                         LocalDateTime asOf) {
        this.userId = userId;
        this.inputLv = inputLv;
        this.creditScore = creditScore;
        this.dsr = dsr;
        this.loanPurposeCode = loanPurposeCode;
        this.asOf = asOf;
    }

    public Long getUserId() { return userId; }
    public Integer getInputLv() { return inputLv; }
    public Integer getCreditScore() { return creditScore; }
    public BigDecimal getDsr() { return dsr; }
    public String getLoanPurposeCode() { return loanPurposeCode; }
    public LocalDateTime getAsOf() { return asOf; }

    public Map<String, Object> getAttributes() { return attributes; }
    public FilterContext putAttr(String key, Object value) {
        attributes.put(key, value);
        return this;
    }
    public Object getAttr(String key) { return attributes.get(key); }
}
