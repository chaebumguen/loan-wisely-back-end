// FILE: domain/recommend/mapper/RecommendHistoryMapper.java
package com.ccksy.loan.domain.recommend.mapper;

import com.ccksy.loan.domain.recommend.entity.RecommendHistory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * 추천 이력 SQL 전용 Mapper (MyBatis 인터페이스만).
 *
 * v1 규칙:
 * - Mapper는 "SQL 접근"만 담당 (판단/정책/설명 로직 금지)
 * - RecommendHistory는 Evidence/재현의 루트 엔티티
 *
 * NOTE:
 * - 실제 SQL은 resources/mybatis/mapper/recommend/RecommendHistoryMapper.xml 에 위치
 * - XML에서 resultMap/parameterMap으로 RecommendHistory 매핑을 수행한다.
 */
@Mapper
public interface RecommendHistoryMapper {

    /**
     * 추천 이력 1건 생성(불변 이력).
     * - 생성 시 status는 REQUESTED 또는 상위 프로세스가 결정한 상태로 저장
     */
    int insertRecommendHistory(@Param("h") RecommendHistory history);

    /**
     * 추천 상태 업데이트 (예: REQUESTED -> VALID/INVALID/BLOCKED).
     * - Explain 저장 경로(explanationJsonPath)는 설명 생성 성공 시 함께 업데이트 가능
     */
    int updateStatusAndExplanation(
            @Param("recommendationId") Long recommendationId,
            @Param("status") RecommendHistory.Status status,
            @Param("explanationJsonPath") String explanationJsonPath,
            @Param("updatedAt") LocalDateTime updatedAt
    );

    /**
     * PK로 조회.
     */
    Optional<RecommendHistory> findById(@Param("recommendationId") Long recommendationId);

    /**
     * 결정론/재현 키로 조회.
     * - 동일 입력+동일 버전+동일 상품스냅샷이면 동일 determinismKey가 생성되어야 함
     */
    Optional<RecommendHistory> findByDeterminismKey(@Param("determinismKey") String determinismKey);
}
