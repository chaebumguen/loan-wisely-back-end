package com.ccksy.loan.common.response;

import java.util.List;

/**
 * 페이징 전용 응답 객체
 *
 * 책임:
 * - 목록 + 페이지 메타데이터 전달
 * - 정렬 기준, 검색 로직, 필터 조건은 포함하지 않음
 */
public class PageResponse<T> {

    private final List<T> items;
    private final int page;
    private final int size;
    private final long totalCount;

    public PageResponse(List<T> items, int page, int size, long totalCount) {
        this.items = items;
        this.page = page;
        this.size = size;
        this.totalCount = totalCount;
    }

    public List<T> getItems() {
        return items;
    }

    public int getPage() {
        return page;
    }

    public int getSize() {
        return size;
    }

    public long getTotalCount() {
        return totalCount;
    }
}
