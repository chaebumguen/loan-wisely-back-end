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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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

        if (result.getWarnings() != null && !result.getWarnings().isEmpty()
                && result.getItems() != null && !result.getItems().isEmpty()) {
            for (var item : result.getItems()) {
                for (Map.Entry<String, String> entry : result.getWarnings().entrySet()) {
                    RecoExclusionReason reason = RecoExclusionReason.builder()
                            .resultId(recoResultId)
                            .productId(item.getProductId())
                            .reasonCode(entry.getKey())
                            .reasonText(entry.getValue())
                            .createdAt(now)
                            .build();
                    recoExclusionReasonMapper.insert(reason);
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
        return "state=" + safe(result.getState()) +
                ";inputLevel=" + safe(result.getResolvedInputLevel()) +
                ";policyVersion=" + safe(result.getPolicyVersion()) +
                ";metaVersion=" + safe(result.getMetaVersion());
    }

    private String safe(Object value) {
        return value == null ? "" : value.toString();
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
