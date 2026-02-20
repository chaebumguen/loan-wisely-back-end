package com.ccksy.loan.infra.elasticsearch;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class EsIndexService {

    private final RestHighLevelClient client;
    private final EsProperties props;

    @PostConstruct
    public void ensureIndices() {
        ensureRecommendHistoryIndex();
        ensureRecoPolicyIndex();
    }

    public void resetIndices() {
        deleteIndexIfExists(props.getIndices().getRecommendHistory());
        deleteIndexIfExists(props.getIndices().getRecoPolicy());
        ensureIndices();
    }

    private void deleteIndexIfExists(String index) {
        try {
            if (!client.indices().exists(new GetIndexRequest(index), RequestOptions.DEFAULT)) {
                return;
            }
            client.indices().delete(new DeleteIndexRequest(index), RequestOptions.DEFAULT);
            log.info("Elasticsearch index deleted: {}", index);
        } catch (Exception ex) {
            log.warn("Failed to delete index {}: {}", index, ex.getMessage());
        }
    }

    private void ensureRecommendHistoryIndex() {
        String index = props.getIndices().getRecommendHistory();
        try {
            if (client.indices().exists(new GetIndexRequest(index), RequestOptions.DEFAULT)) {
                return;
            }
            CreateIndexRequest request = new CreateIndexRequest(index);
            request.settings(Settings.builder()
                    .put("index.number_of_shards", 1)
                    .put("index.number_of_replicas", 0)
            );
            XContentBuilder mapping = XContentFactory.jsonBuilder()
                    .startObject()
                    .startObject("properties")
                    .startObject("recommend_id").field("type", "long").endObject()
                    .startObject("user_id").field("type", "long").endObject()
                    .startObject("policy_version").field("type", "keyword").endObject()
                    .startObject("recommend_state").field("type", "keyword").endObject()
                    .startObject("explain_summary").field("type", "text")
                        .startObject("fields")
                        .startObject("keyword").field("type", "keyword").field("ignore_above", 256).endObject()
                        .endObject()
                    .endObject()
                    .startObject("created_at").field("type", "date").endObject()
                    .startObject("reco_request_id").field("type", "long").endObject()
                    .startObject("reco_result_id").field("type", "long").endObject()
                    .endObject()
                    .endObject();
            request.mapping(mapping);
            client.indices().create(request, RequestOptions.DEFAULT);
            log.info("Elasticsearch index created: {}", index);
        } catch (Exception ex) {
            log.warn("Failed to ensure index {}: {}", index, ex.getMessage());
        }
    }

    private void ensureRecoPolicyIndex() {
        String index = props.getIndices().getRecoPolicy();
        try {
            if (client.indices().exists(new GetIndexRequest(index), RequestOptions.DEFAULT)) {
                return;
            }
            CreateIndexRequest request = new CreateIndexRequest(index);
            request.settings(Settings.builder()
                    .put("index.number_of_shards", 1)
                    .put("index.number_of_replicas", 0)
            );
            XContentBuilder mapping = XContentFactory.jsonBuilder()
                    .startObject()
                    .startObject("properties")
                    .startObject("policy_id").field("type", "long").endObject()
                    .startObject("version").field("type", "keyword").endObject()
                    .startObject("status").field("type", "keyword").endObject()
                    .startObject("is_active").field("type", "keyword").endObject()
                    .startObject("policy_key").field("type", "keyword").endObject()
                    .startObject("policy_value").field("type", "text").endObject()
                    .startObject("approved_at").field("type", "date").endObject()
                    .startObject("created_at").field("type", "date").endObject()
                    .endObject()
                    .endObject();
            request.mapping(mapping);
            client.indices().create(request, RequestOptions.DEFAULT);
            log.info("Elasticsearch index created: {}", index);
        } catch (Exception ex) {
            log.warn("Failed to ensure index {}: {}", index, ex.getMessage());
        }
    }
}
