package com.ccksy.loan.domain.metadata.dto.admin;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class CreditDictionaryItemRequest {
    private String columnCode;
    private String columnName;
    private String columnDesc;
    private String dataType;
    private Boolean isRequired;
}
