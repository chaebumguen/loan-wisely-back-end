package com.ccksy.loan.common.response;

import java.util.List;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 페이징 응답 전용 래퍼
 *
 * - 목록 조회 API에서만 사용
 * - ApiResponse와 분리하여 책임을 명확히 한다
 * - Page<T> / Slice<T> / MyBatis 페이징 결과를 감싸는 용도
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
//생성자를 외부에서 직접 호출하지 못하도록 제한
//→ 반드시 정적 팩토리 메서드(of, success 등)만 사용하게 강제
@NoArgsConstructor(access = AccessLevel.PROTECTED)
//Jackson 역직렬화 및 프레임워크 내부 사용을 위한 기본 생성자
//→ 외부 new 호출은 차단
public class PageResponse<T> {

    /** 실제 데이터 목록 */
    private List<T> items;

    /** 전체 데이터 개수 */
    private long totalCount;

    /** 현재 페이지 번호 (0-based or 1-based는 API 기준에 따름) */
    private int page;

    /** 페이지 크기 */
    private int size;

    /** 전체 페이지 수 */
    private int totalPages;

    /* =========================
     * Factory Methods
     * ========================= */

    public static <T> PageResponse<T> of(
            List<T> items,
            long totalCount,
            int page,
            int size) {

        int totalPages = calculateTotalPages(totalCount, size);

        return new PageResponse<>(
                items,
                totalCount,
                page,
                size,
                totalPages
        );
    }

    /* =========================
     * Internal Utils
     * ========================= */

    private static int calculateTotalPages(long totalCount, int size) {
        if (size <= 0) {
            return 0;
        }
        return (int) Math.ceil((double) totalCount / size);
    }
}
