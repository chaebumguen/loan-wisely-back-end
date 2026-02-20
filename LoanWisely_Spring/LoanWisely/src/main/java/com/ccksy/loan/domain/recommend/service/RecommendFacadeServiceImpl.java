package com.ccksy.loan.domain.recommend.service;

import com.ccksy.loan.common.exception.BusinessException;
import com.ccksy.loan.common.exception.ErrorCode;
import com.ccksy.loan.domain.recommend.command.RecommendCommand;
import com.ccksy.loan.domain.recommend.command.RecommendCommandHandler;
import com.ccksy.loan.domain.recommend.dto.request.RecommendRequest;
import com.ccksy.loan.domain.recommend.entity.RecoEventLog;
import com.ccksy.loan.domain.recommend.entity.RecoEstimationDetail;
import com.ccksy.loan.domain.recommend.entity.RecoExclusionReason;
import com.ccksy.loan.domain.recommend.entity.RecoItem;
import com.ccksy.loan.domain.recommend.entity.RecoRejectLog;
import com.ccksy.loan.domain.recommend.entity.RecoRequest;
import com.ccksy.loan.domain.recommend.entity.RecoResult;
import com.ccksy.loan.domain.recommend.entity.RecommendHistory;
import com.ccksy.loan.domain.recommend.filter.model.ExclusionReason;
import com.ccksy.loan.domain.recommend.mapper.RecoEventLogMapper;
import com.ccksy.loan.domain.recommend.mapper.RecoEstimationDetailMapper;
import com.ccksy.loan.domain.recommend.mapper.RecoExclusionReasonMapper;
import com.ccksy.loan.domain.recommend.mapper.RecoItemMapper;
import com.ccksy.loan.domain.recommend.mapper.RecoRejectLogMapper;
import com.ccksy.loan.domain.recommend.mapper.RecoRequestMapper;
import com.ccksy.loan.domain.recommend.mapper.RecoResultMapper;
import com.ccksy.loan.domain.recommend.mapper.RecommendHistoryMapper;
import com.ccksy.loan.domain.recommend.result.core.RecommendResult;
import com.ccksy.loan.domain.recommend.result.response.RecommendResponse;
import com.ccksy.loan.domain.product.service.ProductRateQuote;
import com.ccksy.loan.domain.product.service.ProductRateService;
import com.ccksy.loan.domain.user.entity.UserCreditLv1;
import com.ccksy.loan.domain.user.mapper.UserCreditLv1Mapper;
import com.ccksy.loan.infra.elasticsearch.EsRecommendHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class RecommendFacadeServiceImpl implements RecommendFacadeService {

    private final RecommendCommandHandler recommendCommandHandler;
    private final RecommendHistoryMapper recommendHistoryMapper;
    private final ExplainStorageService explainStorageService;
    private final RecoRequestMapper recoRequestMapper;
    private final RecoResultMapper recoResultMapper;
    private final RecoItemMapper recoItemMapper;
    private final RecoEstimationDetailMapper recoEstimationDetailMapper;
    private final RecoExclusionReasonMapper recoExclusionReasonMapper;
    private final RecoRejectLogMapper recoRejectLogMapper;
    private final RecoEventLogMapper recoEventLogMapper;
    private final ProductRateService productRateService;
    private final UserCreditLv1Mapper userCreditLv1Mapper;
    private final EsRecommendHistoryService esRecommendHistoryService;

    @Override
    @Transactional
    public RecommendResponse recommend(RecommendRequest request) {
        request.assertRequiredFields();

        UserCreditLv1 lv1 = userCreditLv1Mapper.selectLatestActiveByUserId(request.getUserId());
        if (lv1 == null) {
            throw new BusinessException(ErrorCode.VALIDATION_FAILED, "LV1 data is required");
        }
        if (request.getRequestedInputLevel() == null) {
            request.setRequestedInputLevel(1);
        }

        RecommendCommand command = RecommendCommand.from(request);
        RecommendResult result = recommendCommandHandler.handle(command);
        String explainSummary = buildExplainSummary(result);
        String explainFilePath = result.getExplainFilePath();
        if (explainFilePath == null || explainFilePath.isBlank()) {
            explainFilePath = explainStorageService.storeExplain(result, explainSummary);
            result = result.toBuilder().explainFilePath(explainFilePath).build();
        }

        LocalDateTime now = LocalDateTime.now();

        Long recoRequestId = recoRequestMapper.selectNextId();
        RecoRequest recoRequest = RecoRequest.builder()
                .requestId(recoRequestId)
                .userId(command.getUserId())
                .versionId(null)
                .requestedAt(now)
                .build();
        recoRequestMapper.insertWithId(recoRequest);

        Long recoResultId = recoResultMapper.selectNextId();
        RecoResult recoResult = RecoResult.builder()
                .resultId(recoResultId)
                .requestId(recoRequestId)
                .overallScore(null)
                .policyVersion(result.getPolicyVersion())
                .confidenceLevelCodeValueId(null)
                .explanationJsonPath(explainFilePath)
                .createdAt(now)
                .build();
        recoResultMapper.insertWithId(recoResult);

        if (result.getItems() != null) {
            int rank = 1;
            for (var item : result.getItems()) {
                ProductRateQuote rateQuote = productRateService.getRateQuote(item.getProductId());
                BigDecimal estimatedRate = item.getMinRate();
                if (estimatedRate == null && rateQuote != null) {
                    estimatedRate = rateQuote.getRateMin();
                }
                BigDecimal estimatedLimit = productRateService.estimateLimit(request.getUserId(), rateQuote);

                RecoItem recoItem = RecoItem.builder()
                        .resultId(recoResultId)
                        .productId(item.getProductId())
                        .matchingScore(toBigDecimal(item.getScore()))
                        .estimatedRate(estimatedRate)
                        .estimatedLimit(estimatedLimit)
                        .stabilityScore(toBigDecimal(item.getScore()))
                        .reasonJsonPath(item.getBriefReason())
                        .rank(rank++)
                        .createdAt(now)
                        .build();
                recoItemMapper.insert(recoItem);

                Long itemId = recoItemMapper.selectCurrentId();
                if (itemId != null) {
                    insertEstimationDetails(itemId, item, rateQuote, estimatedRate, estimatedLimit, now);
                }
            }
        }

        if (result.getItems() != null && !result.getItems().isEmpty()) {
            for (var item : result.getItems()) {
                Map<String, ExclusionReason> deduped = new LinkedHashMap<>();
                if (item.getExclusionReasons() != null) {
                    for (ExclusionReason reason : item.getExclusionReasons()) {
                        if (reason == null || reason.getMessage() == null) {
                            continue;
                        }
                        String key = reason.getMessage().trim();
                        if (key.isBlank()) {
                            continue;
                        }
                        deduped.putIfAbsent(key, reason);
                    }
                }
                if (deduped.isEmpty() && result.getWarnings() != null) {
                    for (Map.Entry<String, String> entry : result.getWarnings().entrySet()) {
                        String msg = entry.getValue() == null ? "" : entry.getValue().trim();
                        if (!msg.isBlank()) {
                            deduped.putIfAbsent(msg, ExclusionReason.of(entry.getKey(), msg));
                        }
                    }
                }
                for (ExclusionReason reason : deduped.values()) {
                    RecoExclusionReason entity = RecoExclusionReason.builder()
                            .resultId(recoResultId)
                            .productId(item.getProductId())
                            .reasonCode(reason.getCode())
                            .reasonText(reason.getMessage())
                            .createdAt(now)
                            .build();
                    recoExclusionReasonMapper.insert(entity);
                }
            }
        }

        if (!"READY".equalsIgnoreCase(result.getState())
                && result.getItems() != null
                && !result.getItems().isEmpty()) {
            for (var item : result.getItems()) {
                RecoRejectLog log = RecoRejectLog.builder()
                        .requestId(recoRequestId)
                        .productId(item.getProductId())
                        .rejectCode(result.getState())
                        .rejectReason("Rejected by state: " + result.getState())
                        .createdAt(now)
                        .build();
                recoRejectLogMapper.insert(log);
            }
        }

        if (result.getItems() != null && !result.getItems().isEmpty()) {
            for (var item : result.getItems()) {
                RecoEventLog log = RecoEventLog.builder()
                        .maskedUserId("U" + command.getUserId())
                        .productId(item.getProductId())
                        .eventTypeCodeValueId("RECO_CREATED")
                        .occurredAt(now)
                        .build();
                recoEventLogMapper.insert(log);
            }
        }

        Long recommendId = recommendHistoryMapper.selectNextId();
        RecommendHistory history = RecommendHistory.builder()
                .recommendId(recommendId)
                .userId(command.getUserId())
                .reproduceKey(result.getReproduceKey())
                .policyVersion(result.getPolicyVersion())
                .metaVersion(result.getMetaVersion())
                .evidenceFilePath(result.getEvidenceFilePath())
                .explainFilePath(result.getExplainFilePath())
                .explainSummary(explainSummary)
                .recoRequestId(recoRequestId)
                .recoResultId(recoResultId)
                .recommendState(result.getState())
                .createdAt(now)
                .build();

        recommendHistoryMapper.insertRecommendHistory(history);
        esRecommendHistoryService.indexAfterCommit(history);

        return RecommendResponse.from(result, recommendId);
    }

    @Override
    @Transactional(readOnly = true)
    public RecommendResponse reproduce(String reproduceKey) {
        if (reproduceKey == null || reproduceKey.trim().isEmpty()) {
            throw new BusinessException(ErrorCode.VALIDATION_FAILED, "reproduceKey is required");
        }

        RecommendHistory history = recommendHistoryMapper.selectByReproduceKey(reproduceKey);
        if (history == null) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST, "reproduceKey not found");
        }

        RecommendResult result = RecommendResult.builder()
                .state(history.getRecommendState())
                .reproduceKey(history.getReproduceKey())
                .resolvedInputLevel(null)
                .policyVersion(history.getPolicyVersion())
                .metaVersion(history.getMetaVersion())
                .evidenceFilePath(history.getEvidenceFilePath())
                .explainFilePath(history.getExplainFilePath())
                .build();

        return RecommendResponse.from(result, history.getRecommendId());
    }

    private String buildExplainSummary(RecommendResult result) {
        if (result == null) return null;
        int itemCount = result.getItems() == null ? 0 : result.getItems().size();
        int warningCount = result.getWarnings() == null ? 0 : result.getWarnings().size();

        BigDecimal minRate = null;
        BigDecimal maxRate = null;
        BigDecimal scoreSum = BigDecimal.ZERO;
        int scoreCount = 0;
        int exclusionCount = 0;

        if (result.getItems() != null) {
            for (var item : result.getItems()) {
                if (item == null) continue;
                BigDecimal candidateMin = item.getMinRate();
                BigDecimal candidateMax = item.getMinRate();
                if (item.getProductId() != null) {
                    ProductRateQuote quote = productRateService.getRateQuote(item.getProductId());
                    if (quote != null) {
                        if (quote.getRateMin() != null) {
                            candidateMin = quote.getRateMin();
                        }
                        if (quote.getRateMax() != null) {
                            candidateMax = quote.getRateMax();
                        }
                    }
                }
                if (candidateMin != null) {
                    minRate = minRate == null ? candidateMin : minRate.min(candidateMin);
                }
                if (candidateMax != null) {
                    maxRate = maxRate == null ? candidateMax : maxRate.max(candidateMax);
                }
                if (item.getScore() != null) {
                    scoreSum = scoreSum.add(item.getScore());
                    scoreCount++;
                }
                if (item.getExclusionReasons() != null) {
                    exclusionCount += item.getExclusionReasons().size();
                }
            }
        }

        StringBuilder sb = new StringBuilder();
        String policyVersion = safe(result.getPolicyVersion());
        if (!policyVersion.isBlank()) {
            sb.append("정책 ").append(policyVersion).append(" 기준으로 ");
        }

        if (itemCount > 0) {
            sb.append(itemCount).append("개 상품이 추천되었습니다.");
        } else {
            sb.append("추천 결과가 없습니다.");
        }

        if (minRate != null && maxRate != null) {
            sb.append(" 금리 범위는 ")
              .append(formatDecimal(minRate)).append("%~")
              .append(formatDecimal(maxRate)).append("%입니다.");
        }

        if (scoreCount > 0) {
            BigDecimal avgScore = scoreSum.divide(BigDecimal.valueOf(scoreCount), 4, java.math.RoundingMode.HALF_UP);
            sb.append(" 평균 점수는 ").append(formatDecimal(avgScore)).append("입니다.");
        }

        if (warningCount > 0) {
            sb.append(" 경고 ").append(warningCount).append("건이 있습니다.");
        }
        if (exclusionCount > 0) {
            sb.append(" 제외 사유 ").append(exclusionCount).append("건이 있습니다.");
        }

        String state = safe(result.getState());
        if (!state.isBlank() && !"READY".equalsIgnoreCase(state)) {
            sb.append(" 상태는 ").append(state).append("입니다.");
        }

        return sb.toString().trim();
    }

    private String safe(Object value) {
        return value == null ? "" : value.toString();
    }

    private String formatDecimal(BigDecimal value) {
        if (value == null) return "";
        return value.stripTrailingZeros().toPlainString();
    }

    private BigDecimal toBigDecimal(BigDecimal value) {
        return value;
    }

    private void insertEstimationDetails(Long itemId,
                                         com.ccksy.loan.domain.recommend.result.core.RecommendItem item,
                                         ProductRateQuote quote,
                                         BigDecimal estimatedRate,
                                         BigDecimal estimatedLimit,
                                         LocalDateTime now) {
        BigDecimal score = toBigDecimal(item.getScore());
        if (score != null) {
            recoEstimationDetailMapper.insert(RecoEstimationDetail.builder()
                    .itemId(itemId)
                    .factorCode("SCORE")
                    .factorName("Matching Score")
                    .factorValue(score.toPlainString())
                    .contribution(score)
                    .createdAt(now)
                    .build());
        }
        if (estimatedRate != null) {
            recoEstimationDetailMapper.insert(RecoEstimationDetail.builder()
                    .itemId(itemId)
                    .factorCode("RATE_MIN")
                    .factorName("Minimum Rate")
                    .factorValue(estimatedRate.toPlainString())
                    .contribution(null)
                    .createdAt(now)
                    .build());
        }
        if (quote != null && quote.getRateMax() != null) {
            recoEstimationDetailMapper.insert(RecoEstimationDetail.builder()
                    .itemId(itemId)
                    .factorCode("RATE_MAX")
                    .factorName("Maximum Rate")
                    .factorValue(quote.getRateMax().toPlainString())
                    .contribution(null)
                    .createdAt(now)
                    .build());
        }
        if (estimatedLimit != null) {
            recoEstimationDetailMapper.insert(RecoEstimationDetail.builder()
                    .itemId(itemId)
                    .factorCode("LIMIT_EST")
                    .factorName("Estimated Limit")
                    .factorValue(estimatedLimit.toPlainString())
                    .contribution(null)
                    .createdAt(now)
                    .build());
        }
    }
}
