package com.ccksy.loan.infra.elasticsearch;

import com.ccksy.loan.domain.recommend.entity.RecommendHistory;
import com.ccksy.loan.infra.elasticsearch.dto.EsRecommendHistorySearchItem;
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
import org.elasticsearch.index.query.RangeQueryBuilder;
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
public class EsRecommendHistoryService {

    private final RestHighLevelClient client;
    private final EsProperties props;

    public void indexAfterCommit(RecommendHistory history) {
        if (history == null) return;
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    safeIndex(history);
                }
            });
        } else {
            safeIndex(history);
        }
    }

    public void indexNow(RecommendHistory history) {
        if (history == null) return;
        safeIndex(history);
    }

    public int indexAll(List<RecommendHistory> histories) {
        if (histories == null || histories.isEmpty()) return 0;
        int count = 0;
        for (RecommendHistory history : histories) {
            safeIndex(history);
            count++;
        }
        return count;
    }

    private void safeIndex(RecommendHistory history) {
        try {
            Map<String, Object> doc = new HashMap<>();
            doc.put("recommend_id", history.getRecommendId());
            doc.put("user_id", history.getUserId());
            doc.put("policy_version", history.getPolicyVersion());
            doc.put("recommend_state", history.getRecommendState());
            doc.put("explain_summary", history.getExplainSummary());
            doc.put("created_at", toDateString(history.getCreatedAt()));
            doc.put("reco_request_id", history.getRecoRequestId());
            doc.put("reco_result_id", history.getRecoResultId());

            IndexRequest request = new IndexRequest(props.getIndices().getRecommendHistory())
                    .id(String.valueOf(history.getRecommendId()))
                    .source(doc, XContentType.JSON)
                    .setRefreshPolicy("false");
            client.index(request, RequestOptions.DEFAULT);
        } catch (Exception ex) {
            log.warn("ES index recommend_history failed: {}", ex.getMessage());
        }
    }

    public EsSearchResponse<EsRecommendHistorySearchItem> search(Long userId,
                                                                 String policyVersion,
                                                                 String keyword,
                                                                 String from,
                                                                 String to,
                                                                 int page,
                                                                 int size) {
        String index = props.getIndices().getRecommendHistory();
        try {
            SearchSourceBuilder source = new SearchSourceBuilder();
            BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();

            if (userId != null) {
                boolQuery.filter(QueryBuilders.termQuery("user_id", userId));
            }
            if (policyVersion != null && !policyVersion.isBlank()) {
                boolQuery.filter(QueryBuilders.termQuery("policy_version", policyVersion));
            }
            if (keyword != null && !keyword.isBlank()) {
                boolQuery.must(QueryBuilders.matchQuery("explain_summary", keyword));
            }
            if ((from != null && !from.isBlank()) || (to != null && !to.isBlank())) {
                RangeQueryBuilder range = QueryBuilders.rangeQuery("created_at");
                if (from != null && !from.isBlank()) range.gte(from);
                if (to != null && !to.isBlank()) range.lte(to);
                boolQuery.filter(range);
            }

            source.query(boolQuery);
            source.from(Math.max(page, 0) * Math.max(size, 1));
            source.size(Math.max(size, 1));
            source.sort("created_at", org.elasticsearch.search.sort.SortOrder.DESC);

            SearchRequest request = new SearchRequest(index).source(source);
            var response = client.search(request, RequestOptions.DEFAULT);

            List<EsRecommendHistorySearchItem> items = new ArrayList<>();
            for (SearchHit hit : response.getHits().getHits()) {
                Map<String, Object> s = hit.getSourceAsMap();
                items.add(new EsRecommendHistorySearchItem(
                        toLong(s.get("recommend_id")),
                        toLong(s.get("user_id")),
                        toStringSafe(s.get("policy_version")),
                        toStringSafe(s.get("recommend_state")),
                        toStringSafe(s.get("explain_summary")),
                        toStringSafe(s.get("created_at")),
                        toLong(s.get("reco_request_id")),
                        toLong(s.get("reco_result_id"))
                ));
            }
            return new EsSearchResponse<>(response.getHits().getTotalHits().value, items);
        } catch (Exception ex) {
            log.warn("ES search recommend_history failed: {}", ex.getMessage());
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
