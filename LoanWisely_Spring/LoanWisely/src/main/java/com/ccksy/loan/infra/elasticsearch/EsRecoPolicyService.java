package com.ccksy.loan.infra.elasticsearch;

import com.ccksy.loan.domain.recommend.entity.RecoPolicy;
import com.ccksy.loan.infra.elasticsearch.dto.EsRecoPolicySearchItem;
import com.ccksy.loan.infra.elasticsearch.dto.EsSearchResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class EsRecoPolicyService {

    private final RestHighLevelClient client;
    private final EsProperties props;

    public void indexAfterCommit(RecoPolicy policy) {
        if (policy == null) return;
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    safeIndex(policy);
                }
            });
        } else {
            safeIndex(policy);
        }
    }

    public void indexNow(RecoPolicy policy) {
        if (policy == null) return;
        safeIndex(policy);
    }

    public int indexAll(List<RecoPolicy> policies) {
        if (policies == null || policies.isEmpty()) return 0;
        int count = 0;
        for (RecoPolicy policy : policies) {
            safeIndex(policy);
            count++;
        }
        return count;
    }

    private void safeIndex(RecoPolicy policy) {
        try {
            Map<String, Object> doc = new HashMap<>();
            doc.put("policy_id", policy.getPolicyId());
            doc.put("version", policy.getVersion());
            doc.put("status", policy.getStatus());
            doc.put("is_active", policy.getIsActive());
            doc.put("policy_key", policy.getPolicyKey());
            doc.put("policy_value", policy.getPolicyValue());
            doc.put("approved_at", toDateString(policy.getApprovedAt()));
            doc.put("created_at", toDateString(policy.getCreatedAt()));

            IndexRequest request = new IndexRequest(props.getIndices().getRecoPolicy())
                    .id(String.valueOf(policy.getPolicyId()))
                    .source(doc, XContentType.JSON)
                    .setRefreshPolicy("false");
            client.index(request, RequestOptions.DEFAULT);
        } catch (Exception ex) {
            log.warn("ES index reco_policy failed: {}", ex.getMessage());
        }
    }

    public EsSearchResponse<EsRecoPolicySearchItem> search(String version,
                                                           String status,
                                                           String isActive,
                                                           int page,
                                                           int size) {
        String index = props.getIndices().getRecoPolicy();
        try {
            SearchSourceBuilder source = new SearchSourceBuilder();
            BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();

            if (version != null && !version.isBlank()) {
                boolQuery.filter(QueryBuilders.termQuery("version", version));
            }
            if (status != null && !status.isBlank()) {
                boolQuery.filter(QueryBuilders.termQuery("status", status));
            }
            if (isActive != null && !isActive.isBlank()) {
                boolQuery.filter(QueryBuilders.termQuery("is_active", isActive));
            }

            source.query(boolQuery);
            source.from(Math.max(page, 0) * Math.max(size, 1));
            source.size(Math.max(size, 1));
            source.sort("created_at", org.elasticsearch.search.sort.SortOrder.DESC);

            SearchRequest request = new SearchRequest(index).source(source);
            var response = client.search(request, RequestOptions.DEFAULT);

            List<EsRecoPolicySearchItem> items = new ArrayList<>();
            for (SearchHit hit : response.getHits().getHits()) {
                Map<String, Object> s = hit.getSourceAsMap();
                items.add(new EsRecoPolicySearchItem(
                        toLong(s.get("policy_id")),
                        toStringSafe(s.get("version")),
                        toStringSafe(s.get("status")),
                        toStringSafe(s.get("is_active")),
                        toStringSafe(s.get("policy_key")),
                        toStringSafe(s.get("approved_at")),
                        toStringSafe(s.get("created_at"))
                ));
            }
            return new EsSearchResponse<>(response.getHits().getTotalHits().value, items);
        } catch (Exception ex) {
            log.warn("ES search reco_policy failed: {}", ex.getMessage());
            return new EsSearchResponse<>(0, List.of());
        }
    }

    private Long toLong(Object value) {
        if (value == null) return null;
        if (value instanceof Number n) return n.longValue();
        try {
            return Long.parseLong(value.toString());
        } catch (Exception ex) {
            return null;
        }
    }

    private String toStringSafe(Object value) {
        return value == null ? null : value.toString();
    }

    private String toDateString(java.time.LocalDateTime time) {
        return time == null ? null : time.toString();
    }
}
