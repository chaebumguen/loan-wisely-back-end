package com.ccksy.loan.infra.elasticsearch;

import com.ccksy.loan.domain.recommend.entity.RecoPolicy;
import com.ccksy.loan.domain.recommend.entity.RecommendHistory;
import com.ccksy.loan.domain.recommend.mapper.RecoPolicyMapper;
import com.ccksy.loan.domain.recommend.mapper.RecommendHistoryMapper;
import com.ccksy.loan.infra.elasticsearch.dto.EsReindexResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class EsReindexService {

    private final EsIndexService esIndexService;
    private final EsRecoPolicyService esRecoPolicyService;
    private final EsRecommendHistoryService esRecommendHistoryService;
    private final RecoPolicyMapper recoPolicyMapper;
    private final RecommendHistoryMapper recommendHistoryMapper;

    public EsReindexResponse rebuildAll() {
        esIndexService.resetIndices();

        List<RecoPolicy> policies = recoPolicyMapper.selectAll();
        List<RecommendHistory> histories = recommendHistoryMapper.selectAll();

        int policyCount = esRecoPolicyService.indexAll(policies);
        int historyCount = esRecommendHistoryService.indexAll(histories);

        log.info("ES reindex completed. policies={}, histories={}", policyCount, historyCount);
        return new EsReindexResponse(policyCount, historyCount);
    }
}
