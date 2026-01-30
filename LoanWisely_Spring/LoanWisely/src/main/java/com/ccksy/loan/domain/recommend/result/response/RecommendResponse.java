package com.ccksy.loan.domain.recommend.result.response;

import java.util.ArrayList;
import java.util.List;

/**
 * 추천 결과 최종 응답 DTO
 *
 * <p>Controller 에서 직접 반환되는 객체</p>
 */
public class RecommendResponse {

    private List<RecommendItemResponse> items = new ArrayList<>();
    private int totalCount;

    /**
     * 설명/경고/정책 안내 메시지 (Decorator 결과 병합)
     */
    private List<String> messages = new ArrayList<>();

    // --- getter / setter ---

    public List<RecommendItemResponse> getItems() {
        return items;
    }

    public void setItems(List<RecommendItemResponse> items) {
        this.items = items;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public List<String> getMessages() {
        return messages;
    }

    public void setMessages(List<String> messages) {
        this.messages = messages;
    }
}
