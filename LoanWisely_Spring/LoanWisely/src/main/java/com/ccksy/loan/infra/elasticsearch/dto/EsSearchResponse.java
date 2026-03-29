package com.ccksy.loan.infra.elasticsearch.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class EsSearchResponse<T> {
    private long total;
    private List<T> items;
}
