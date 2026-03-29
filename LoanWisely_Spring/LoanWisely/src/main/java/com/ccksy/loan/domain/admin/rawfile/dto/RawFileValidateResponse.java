package com.ccksy.loan.domain.admin.rawfile.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class RawFileValidateResponse {
    private boolean ok;
    private List<String> errors;
}
