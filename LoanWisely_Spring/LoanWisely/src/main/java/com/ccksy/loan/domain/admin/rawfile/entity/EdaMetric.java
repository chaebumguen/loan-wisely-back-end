package com.ccksy.loan.domain.admin.rawfile.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class EdaMetric {
    private Long metricId;
    private Long edaRunId;
    private String metricName;
    private String metricType;
    private String metricKey;
    private String metricValuePath;
    private LocalDateTime createdAt;
}
