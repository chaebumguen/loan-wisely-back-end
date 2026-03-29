package com.ccksy.loan.domain.admin.policy.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class PolicyDetailResponse {
    private Long id;
    private String name;
    private String version;
    private String status;
    private String description;
    private List<String> rules;
    private List<String> validationRules;
    private String approvedBy;
    private String approvedAt;
    private String createdAt;
}
