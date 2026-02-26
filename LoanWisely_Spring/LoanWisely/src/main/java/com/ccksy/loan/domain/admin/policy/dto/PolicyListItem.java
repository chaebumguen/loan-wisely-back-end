package com.ccksy.loan.domain.admin.policy.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class PolicyListItem {
    private Long id;
    private String name;
    private String version;
    private String status;
    private String author;
    private String updatedAt;
}
