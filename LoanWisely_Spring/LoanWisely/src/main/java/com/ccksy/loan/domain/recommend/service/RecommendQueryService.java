package com.ccksy.loan.domain.recommend.service;

import com.ccksy.loan.common.exception.BusinessException;
import com.ccksy.loan.common.exception.ErrorCode;
import com.ccksy.loan.domain.product.entity.LoanProduct;
import com.ccksy.loan.domain.product.mapper.LoanProductMapper;
import com.ccksy.loan.domain.recommend.entity.RecoEventLog;
import com.ccksy.loan.domain.recommend.entity.RecoExclusionReason;
import com.ccksy.loan.domain.recommend.entity.RecoItem;
import com.ccksy.loan.domain.recommend.entity.RecoRejectLog;
import com.ccksy.loan.domain.recommend.entity.RecommendHistory;
import com.ccksy.loan.domain.recommend.mapper.RecoEventLogMapper;
import com.ccksy.loan.domain.recommend.mapper.RecoEstimationDetailMapper;
import com.ccksy.loan.domain.recommend.mapper.RecoExclusionReasonMapper;
import com.ccksy.loan.domain.recommend.mapper.RecoItemMapper;
import com.ccksy.loan.domain.recommend.mapper.RecoRejectLogMapper;
import com.ccksy.loan.domain.recommend.mapper.RecommendHistoryMapper;
import com.ccksy.loan.domain.recommend.result.response.RecommendDetailInfoResponse;
import com.ccksy.loan.domain.recommend.result.response.RecommendDetailResponse;
import com.ccksy.loan.domain.recommend.result.response.RecommendEstimationDetailResponse;
import com.ccksy.loan.domain.recommend.result.response.RecommendExplainResponse;
import com.ccksy.loan.domain.recommend.result.response.RecommendExplainSummaryResponse;
import com.ccksy.loan.domain.recommend.result.response.RecommendProductResponse;
import com.ccksy.loan.domain.recommend.result.response.RecommendationListItemResponse;
import com.ccksy.loan.domain.recommend.result.response.RecommendationListResponse;
import com.ccksy.loan.domain.recommend.result.response.RecommendationProductSummaryResponse;
import com.ccksy.loan.domain.product.service.ProductRateQuote;
import com.ccksy.loan.domain.product.service.ProductRateService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class RecommendQueryService {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    private static final int LIST_PRODUCT_LIMIT = 3;

    private final RecommendHistoryMapper recommendHistoryMapper;
    private final LoanProductMapper loanProductMapper;
    private final RecoItemMapper recoItemMapper;
    private final RecoEstimationDetailMapper recoEstimationDetailMapper;
    private final RecoExclusionReasonMapper recoExclusionReasonMapper;
    private final RecoEventLogMapper recoEventLogMapper;
    private final RecoRejectLogMapper recoRejectLogMapper;
    private final ObjectMapper objectMapper;
    private final ProductRateService productRateService;

    public RecommendationListResponse getRecommendations(Long userId, int page, int size) {
        if (page < 0 || size <= 0) {
            throw new BusinessException(ErrorCode.VALIDATION_FAILED, "Invalid page or size");
        }

        List<RecommendHistory> all = recommendHistoryMapper.selectHistoryByUserId(userId);
        int total = all == null ? 0 : all.size();
        if (total == 0) {
            return RecommendationListResponse.builder()
                    .items(Collections.emptyList())
                    .page(page)
                    .size(size)
                    .total(0)
                    .build();
        }

        int from = Math.min(page * size, total);
        int to = Math.min(from + size, total);
        List<RecommendHistory> slice = all.subList(from, to);

        List<RecommendationListItemResponse> items = new ArrayList<>();
        for (RecommendHistory history : slice) {
            List<RecommendationProductSummaryResponse> productSummaries =
                    buildProductSummaries(history, history.getUserId());
            items.add(RecommendationListItemResponse.builder()
                    .id(String.valueOf(history.getRecommendId()))
                    .title("Recommendation " + history.getRecommendId())
                    .createdAt(history.getCreatedAt() == null ? null : history.getCreatedAt().format(DATE_FORMAT))
                    .products(productSummaries)
                    .build());
        }

        return RecommendationListResponse.builder()
                .items(items)
                .page(page)
                .size(size)
                .total(total)
                .build();
    }

    public RecommendDetailResponse getRecommendationDetail(Long userId, String recommendationId) {
        RecommendHistory history = getHistoryForUser(userId, recommendationId);
        Map<String, Object> payload = readExplainPayload(history.getExplainFilePath());

        RecommendExplainSummaryResponse explain = buildExplainSummary(history, payload);
        List<RecommendProductResponse> products = buildProducts(history, payload, history.getUserId());
        RecommendDetailInfoResponse detail = RecommendDetailInfoResponse.builder()
                .description(explain.getSummary() == null ? "상품 상세 정보가 표시됩니다." : explain.getSummary())
                .monthlyPaymentExample("월 상환액 예시가 표시됩니다.")
                .riskWarning("고위험 조건 경고 및 승인 보장 아님 고지가 표시됩니다.")
                .build();

        return RecommendDetailResponse.builder()
                .explain(explain)
                .products(products)
                .detail(detail)
                .build();
    }

    public RecommendDetailResponse getRecommendationDetailForAdmin(String recommendationId) {
        Long id = parseId(recommendationId);
        RecommendHistory history = recommendHistoryMapper.selectById(id);
        if (history == null) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST, "recommendationId not found");
        }
        Map<String, Object> payload = readExplainPayload(history.getExplainFilePath());

        RecommendExplainSummaryResponse explain = buildExplainSummary(history, payload);
        List<RecommendProductResponse> products = buildProducts(history, payload, history.getUserId());
        RecommendDetailInfoResponse detail = RecommendDetailInfoResponse.builder()
                .description(explain.getSummary() == null ? "상품 상세 정보가 표시됩니다." : explain.getSummary())
                .monthlyPaymentExample("월 상환액 예시가 표시됩니다.")
                .riskWarning("고위험 조건 경고 및 승인 보장 아님 고지가 표시됩니다.")
                .build();

        return RecommendDetailResponse.builder()
                .explain(explain)
                .products(products)
                .detail(detail)
                .build();
    }

    public List<RecoEventLog> getEventLogs(Long productId) {
        if (productId == null) {
            throw new BusinessException(ErrorCode.VALIDATION_FAILED, "productId is required");
        }
        List<RecoEventLog> logs = recoEventLogMapper.selectByProductId(productId);
        return logs == null ? Collections.emptyList() : logs;
    }

    public List<RecoRejectLog> getRejectLogs(Long requestId) {
        if (requestId == null) {
            throw new BusinessException(ErrorCode.VALIDATION_FAILED, "requestId is required");
        }
        List<RecoRejectLog> logs = recoRejectLogMapper.selectByRequestId(requestId);
        return logs == null ? Collections.emptyList() : logs;
    }

    public List<RecoExclusionReason> getExclusionReasons(Long resultId) {
        if (resultId == null) {
            throw new BusinessException(ErrorCode.VALIDATION_FAILED, "resultId is required");
        }
        List<RecoExclusionReason> reasons = recoExclusionReasonMapper.selectByResultId(resultId);
        return reasons == null ? Collections.emptyList() : reasons;
    }
    public RecommendExplainResponse getRecommendationExplain(Long userId, String recommendationId) {
        RecommendHistory history = getHistoryForUser(userId, recommendationId);
        Map<String, Object> payload = readExplainPayload(history.getExplainFilePath());

        RecommendExplainSummaryResponse summary = buildExplainSummary(history, payload);
        List<String> reasons = buildReasons(history, payload);
        List<String> riskNotes = buildRiskNotes(history);

        return RecommendExplainResponse.builder()
                .summary(summary.getSummary())
                .levelUsed(summary.getLevelUsed())
                .levelStatus(summary.getLevelStatus())
                .reasons(reasons)
                .riskNotes(riskNotes)
                .build();
    }

    private RecommendHistory getHistoryForUser(Long userId, String recommendationId) {
        Long id = parseId(recommendationId);
        RecommendHistory history = recommendHistoryMapper.selectById(id);
        if (history == null) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST, "recommendationId not found");
        }
        if (!userId.equals(history.getUserId())) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "No access to recommendation");
        }
        return history;
    }

    private Long parseId(String recommendationId) {
        if (recommendationId == null || recommendationId.trim().isEmpty()) {
            throw new BusinessException(ErrorCode.VALIDATION_FAILED, "recommendationId is required");
        }
        try {
            return Long.parseLong(recommendationId.trim());
        } catch (NumberFormatException ex) {
            throw new BusinessException(ErrorCode.VALIDATION_FAILED, "recommendationId must be numeric");
        }
    }

    private Map<String, Object> readExplainPayload(String explainFilePath) {
        if (explainFilePath == null || explainFilePath.isBlank()) {
            return null;
        }
        try {
            Path path = Path.of(explainFilePath);
            if (!Files.exists(path)) {
                return null;
            }
            return objectMapper.readValue(path.toFile(), new TypeReference<Map<String, Object>>() {});
        } catch (IOException ex) {
            return null;
        }
    }

    private RecommendExplainSummaryResponse buildExplainSummary(RecommendHistory history, Map<String, Object> payload) {
        String summary = history.getExplainSummary();
        Integer inputLevel = null;
        if (payload != null) {
            Object summaryValue = payload.get("summary");
            if (summaryValue instanceof String s && !s.isBlank()) {
                summary = s;
            }
            Object levelValue = payload.get("resolvedInputLevel");
            inputLevel = asInteger(levelValue);
        }

        String levelUsed = toLevelUsed(inputLevel);
        String levelStatus = toLevelStatus(inputLevel);

        return RecommendExplainSummaryResponse.builder()
                .summary(summary == null ? "" : summary)
                .levelUsed(levelUsed)
                .levelStatus(levelStatus)
                .build();
    }

    private List<RecommendProductResponse> buildProducts(RecommendHistory history, Map<String, Object> payload, Long userId) {
        List<RecoItem> recoItems = fetchRecoItems(history);
        if (recoItems.isEmpty()) {
            return buildProductsFromPayload(payload, userId);
        }

        List<RecommendProductResponse> products = new ArrayList<>();
        for (RecoItem item : recoItems) {
            Long productId = item.getProductId();
            String productName = "대출 상품";
            String lenderName = "Provider";

            if (productId != null) {
                LoanProduct product = loanProductMapper.selectById(productId);
                if (product != null) {
                    productName = product.getProductName() == null ? productName : product.getProductName();
                    lenderName = product.getProviderId() == null
                            ? lenderName
                            : "Provider " + product.getProviderId();
                } else {
                    productName = "Product " + productId;
                    lenderName = "Provider";
                }
            }

            ProductRateQuote quote = productRateService.getRateQuote(productId);
            String rate = formatRate(resolveRateMin(item, quote), resolveRateMax(quote));
            String reason = item.getReasonJsonPath();
            Integer score = item.getMatchingScore() == null ? null : item.getMatchingScore().intValue();
            String limit = formatLimit(resolveLimit(item, quote, userId));
            String riskNote = item.getStabilityScore() == null ? "" : "안정성 점수 " + item.getStabilityScore();
            List<RecommendEstimationDetailResponse> estimationDetails = buildEstimationDetails(item);

            products.add(RecommendProductResponse.builder()
                    .id(productId == null ? "" : String.valueOf(productId))
                    .lenderName(lenderName)
                    .productName(productName)
                    .rate(rate)
                    .limit(limit)
                    .reason(reason)
                    .suitabilityScore(score)
                    .riskNote(riskNote)
                    .estimationDetails(estimationDetails)
                    .build());
        }

        return products;
    }

    private List<RecommendationProductSummaryResponse> buildProductSummaries(RecommendHistory history, Long userId) {
        List<RecoItem> recoItems = fetchRecoItems(history);
        if (recoItems.isEmpty()) {
            return Collections.emptyList();
        }
        recoItems.sort(Comparator.comparing(
                RecoItem::getMatchingScore,
                Comparator.nullsLast(Comparator.reverseOrder())
        ));
        int countLimit = Math.min(LIST_PRODUCT_LIMIT, recoItems.size());
        List<RecommendationProductSummaryResponse> summaries = new ArrayList<>();
        for (int i = 0; i < countLimit; i++) {
            RecoItem item = recoItems.get(i);
            Long productId = item.getProductId();
            LoanProduct product = productId == null ? null : loanProductMapper.selectById(productId);
            String productName = product == null || product.getProductName() == null
                    ? "대출 상품"
                    : product.getProductName();
            ProductRateQuote quote = productRateService.getRateQuote(productId);
            String rate = formatRate(resolveRateMin(item, quote), resolveRateMax(quote));
            String limit = formatLimit(resolveLimit(item, quote, userId));
            String repaymentMethod = resolveRepaymentMethod(product);
            summaries.add(RecommendationProductSummaryResponse.builder()
                    .productName(productName)
                    .rate(rate)
                    .limit(limit)
                    .repaymentMethod(repaymentMethod)
                    .build());
        }
        return summaries;
    }

    private List<String> buildReasons(RecommendHistory history, Map<String, Object> payload) {
        List<RecoExclusionReason> reasons = fetchExclusionReasons(history);
        if (!reasons.isEmpty()) {
            List<String> result = new ArrayList<>();
            for (RecoExclusionReason reason : reasons) {
                if (reason.getReasonText() != null && !reason.getReasonText().isBlank()) {
                    result.add(reason.getReasonText());
                }
            }
            if (!result.isEmpty()) {
                return result;
            }
        }
        if (payload == null || payload.get("warnings") == null) {
            return Collections.emptyList();
        }
        Map<String, String> warnings = objectMapper.convertValue(
                payload.get("warnings"),
                new TypeReference<Map<String, String>>() {}
        );
        if (warnings == null || warnings.isEmpty()) {
            return Collections.emptyList();
        }
        return new ArrayList<>(warnings.values());
    }

    private String toLevelUsed(Integer inputLevel) {
        if (inputLevel == null || inputLevel <= 1) {
            return "LV1";
        }
        if (inputLevel == 2) {
            return "LV2";
        }
        return "LV3";
    }

    private String toLevelStatus(Integer inputLevel) {
        if (inputLevel == null || inputLevel <= 0) {
            return "empty";
        }
        if (inputLevel >= 3) {
            return "full";
        }
        return "partial";
    }

    private Integer asInteger(Object value) {
        if (value instanceof Integer i) {
            return i;
        }
        if (value instanceof Number n) {
            return n.intValue();
        }
        if (value instanceof String s) {
            try {
                return Integer.parseInt(s);
            } catch (NumberFormatException ex) {
                return null;
            }
        }
        return null;
    }

    private Long asLong(Object value) {
        if (value instanceof Long l) {
            return l;
        }
        if (value instanceof Number n) {
            return n.longValue();
        }
        if (value instanceof String s) {
            try {
                return Long.parseLong(s);
            } catch (NumberFormatException ex) {
                return null;
            }
        }
        return null;
    }

    private String formatRate(java.math.BigDecimal minRate, java.math.BigDecimal maxRate) {
        if (minRate == null && maxRate == null) {
            return "금리 정보 없음";
        }
        if (minRate != null && maxRate != null) {
            return "금리 " + minRate + " ~ " + maxRate;
        }
        if (minRate != null) {
            return "금리 " + minRate;
        }
        return "금리 " + maxRate;
    }

    private String formatLimit(java.math.BigDecimal limit) {
        if (limit == null) {
            return "한도 정보 없음";
        }
        return limit.toString();
    }

    private String resolveRepaymentMethod(LoanProduct product) {
        if (product == null) {
            return "정보 없음";
        }
        String code = product.getRepaymentTypeCodeValueId();
        if (code == null || code.isBlank() || "UNKNOWN".equalsIgnoreCase(code)) {
            return "정보 없음";
        }
        if (code.startsWith("RPAY_TYPE_")) {
            String key = code.substring("RPAY_TYPE_".length());
            return switch (key) {
                case "S" -> "만기일시상환";
                case "D" -> "분할상환";
                case "M" -> "혼합상환";
                default -> "기타";
            };
        }
        return code;
    }

    private java.math.BigDecimal resolveRateMin(RecoItem item, ProductRateQuote quote) {
        if (item.getEstimatedRate() != null) {
            return item.getEstimatedRate();
        }
        return quote == null ? null : quote.getRateMin();
    }

    private java.math.BigDecimal resolveRateMax(ProductRateQuote quote) {
        return quote == null ? null : quote.getRateMax();
    }

    private java.math.BigDecimal resolveLimit(RecoItem item, ProductRateQuote quote, Long userId) {
        if (item.getEstimatedLimit() != null) {
            return item.getEstimatedLimit();
        }
        return productRateService.estimateLimit(userId, quote);
    }

    private List<RecommendEstimationDetailResponse> buildEstimationDetails(RecoItem item) {
        if (item == null || item.getItemId() == null) {
            return Collections.emptyList();
        }
        var details = recoEstimationDetailMapper.selectByItemId(item.getItemId());
        if (details == null || details.isEmpty()) {
            return Collections.emptyList();
        }
        List<RecommendEstimationDetailResponse> responses = new ArrayList<>();
        for (var detail : details) {
            responses.add(RecommendEstimationDetailResponse.builder()
                    .factorCode(detail.getFactorCode())
                    .factorName(detail.getFactorName())
                    .factorValue(detail.getFactorValue())
                    .contribution(detail.getContribution() == null ? null : detail.getContribution().toString())
                    .build());
        }
        return responses;
    }

    private java.math.BigDecimal asBigDecimal(Object value) {
        if (value instanceof java.math.BigDecimal bd) {
            return bd;
        }
        if (value instanceof Number n) {
            return java.math.BigDecimal.valueOf(n.doubleValue());
        }
        if (value instanceof String s) {
            try {
                return new java.math.BigDecimal(s);
            } catch (NumberFormatException ex) {
                return null;
            }
        }
        return null;
    }

    private List<RecoItem> fetchRecoItems(RecommendHistory history) {
        if (history.getRecoResultId() == null) {
            return Collections.emptyList();
        }
        List<RecoItem> items = recoItemMapper.selectByResultId(history.getRecoResultId());
        return items == null ? Collections.emptyList() : items;
    }

    private List<RecoExclusionReason> fetchExclusionReasons(RecommendHistory history) {
        if (history.getRecoResultId() == null) {
            return Collections.emptyList();
        }
        List<RecoExclusionReason> reasons = recoExclusionReasonMapper.selectByResultId(history.getRecoResultId());
        return reasons == null ? Collections.emptyList() : reasons;
    }

    private List<String> buildRiskNotes(RecommendHistory history) {
        List<RecoItem> items = fetchRecoItems(history);
        if (items.isEmpty()) {
            return Collections.emptyList();
        }
        List<String> notes = new ArrayList<>();
        for (RecoItem item : items) {
            if (item.getStabilityScore() != null) {
                notes.add("상품 " + item.getProductId() + " 안정성 점수 " + item.getStabilityScore());
            }
        }
        return notes;
    }

    private List<RecommendProductResponse> buildProductsFromPayload(Map<String, Object> payload, Long userId) {
        if (payload == null || payload.get("items") == null) {
            return Collections.emptyList();
        }
        List<Map<String, Object>> items = objectMapper.convertValue(
                payload.get("items"),
                new TypeReference<List<Map<String, Object>>>() {}
        );

        List<RecommendProductResponse> products = new ArrayList<>();
        for (Map<String, Object> item : items) {
            Long productId = asLong(item.get("productId"));
            String productName = "대출 상품";
            String lenderName = "Provider";

            if (productId != null) {
                LoanProduct product = loanProductMapper.selectById(productId);
                if (product != null) {
                    productName = product.getProductName() == null ? productName : product.getProductName();
                    lenderName = product.getProviderId() == null
                            ? lenderName
                            : "Provider " + product.getProviderId();
                } else {
                    productName = "Product " + productId;
                    lenderName = "Provider";
                }
            }

            ProductRateQuote quote = productRateService.getRateQuote(productId);
            String rate = formatRate(asBigDecimal(item.get("minRate")), resolveRateMax(quote));
            String reason = item.get("briefReason") instanceof String s ? s : "";
            Integer score = asInteger(item.get("score"));

            products.add(RecommendProductResponse.builder()
                    .id(productId == null ? "" : String.valueOf(productId))
                    .lenderName(lenderName)
                    .productName(productName)
                    .rate(rate)
                    .limit(formatLimit(productRateService.estimateLimit(userId, quote)))
                    .reason(reason)
                    .suitabilityScore(score)
                    .riskNote("")
                    .estimationDetails(Collections.emptyList())
                    .build());
        }

        return products;
    }
}




