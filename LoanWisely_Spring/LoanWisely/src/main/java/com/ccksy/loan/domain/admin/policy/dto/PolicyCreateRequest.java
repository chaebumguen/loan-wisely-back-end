package com.ccksy.loan.domain.admin.policy.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class PolicyCreateRequest {
    private String name;
    private String description;
    private List<String> rules;
    private List<String> validationRules;
}
