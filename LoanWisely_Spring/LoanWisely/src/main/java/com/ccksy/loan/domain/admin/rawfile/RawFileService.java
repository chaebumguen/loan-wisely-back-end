package com.ccksy.loan.domain.admin.rawfile;

import com.ccksy.loan.common.exception.BusinessException;
import com.ccksy.loan.common.exception.ErrorCode;
import com.ccksy.loan.domain.admin.rawfile.dto.RawFileIngestResponse;
import com.ccksy.loan.domain.admin.rawfile.dto.RawFileListItem;
import com.ccksy.loan.domain.admin.rawfile.dto.RawFileNormalizeResponse;
import com.ccksy.loan.domain.admin.rawfile.dto.RawFileUploadResponse;
import com.ccksy.loan.domain.admin.rawfile.dto.RawFileValidateResponse;
import com.ccksy.loan.domain.admin.rawfile.dto.RawFileEdaResponse;
import com.ccksy.loan.domain.admin.rawfile.entity.EdaMetric;
import com.ccksy.loan.domain.admin.rawfile.entity.EdaOutlierResult;
import com.ccksy.loan.domain.admin.rawfile.entity.EdaRun;
import com.ccksy.loan.domain.admin.rawfile.entity.EdaStatResult;
import com.ccksy.loan.domain.admin.rawfile.entity.RawFileCell;
import com.ccksy.loan.domain.admin.rawfile.entity.RawFileNormalized;
import com.ccksy.loan.domain.admin.rawfile.entity.RawFileRow;
import com.ccksy.loan.domain.admin.rawfile.entity.RawFileUpload;
import com.ccksy.loan.domain.admin.rawfile.entity.QualityIssue;
import com.ccksy.loan.domain.admin.rawfile.mapper.EdaMetricMapper;
import com.ccksy.loan.domain.admin.rawfile.mapper.EdaOutlierResultMapper;
import com.ccksy.loan.domain.admin.rawfile.mapper.EdaRunMapper;
import com.ccksy.loan.domain.admin.rawfile.mapper.EdaStatResultMapper;
import com.ccksy.loan.domain.admin.rawfile.mapper.RawFileCellMapper;
import com.ccksy.loan.domain.admin.rawfile.mapper.RawFileNormalizedMapper;
import com.ccksy.loan.domain.admin.rawfile.mapper.RawFileRowMapper;
import com.ccksy.loan.domain.admin.rawfile.mapper.RawFileUploadMapper;
import com.ccksy.loan.domain.admin.rawfile.mapper.QualityIssueMapper;
import com.ccksy.loan.domain.product.entity.ProductRateSnapshot;
import com.ccksy.loan.domain.product.mapper.ProductRateSnapshotMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HexFormat;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class RawFileService {
    private static final Logger log = LoggerFactory.getLogger(RawFileService.class);

    private static final DateTimeFormatter LIST_TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final RawFileUploadMapper rawFileUploadMapper;
    private final RawFileRowMapper rawFileRowMapper;
    private final RawFileCellMapper rawFileCellMapper;
    private final RawFileNormalizedMapper rawFileNormalizedMapper;
    private final EdaRunMapper edaRunMapper;
    private final EdaMetricMapper edaMetricMapper;
    private final EdaStatResultMapper edaStatResultMapper;
    private final EdaOutlierResultMapper edaOutlierResultMapper;
    private final QualityIssueMapper qualityIssueMapper;
    private final ProductRateSnapshotMapper productRateSnapshotMapper;
    private final Path storageDir;
    private final Path schemaPath;
    private final com.fasterxml.jackson.databind.ObjectMapper objectMapper;

    public RawFileService(RawFileUploadMapper rawFileUploadMapper,
                          RawFileRowMapper rawFileRowMapper,
                          RawFileCellMapper rawFileCellMapper,
                          RawFileNormalizedMapper rawFileNormalizedMapper,
                          EdaRunMapper edaRunMapper,
                          EdaMetricMapper edaMetricMapper,
                          EdaStatResultMapper edaStatResultMapper,
                          EdaOutlierResultMapper edaOutlierResultMapper,
                          QualityIssueMapper qualityIssueMapper,
                          ProductRateSnapshotMapper productRateSnapshotMapper,
                          @Value("${storage.raw-files-dir}") String storageDir,
                          @Value("${storage.raw-files-schema-path}") String schemaPath,
                          com.fasterxml.jackson.databind.ObjectMapper objectMapper) {
        this.rawFileUploadMapper = rawFileUploadMapper;
        this.rawFileRowMapper = rawFileRowMapper;
        this.rawFileCellMapper = rawFileCellMapper;
        this.rawFileNormalizedMapper = rawFileNormalizedMapper;
        this.edaRunMapper = edaRunMapper;
        this.edaMetricMapper = edaMetricMapper;
        this.edaStatResultMapper = edaStatResultMapper;
        this.edaOutlierResultMapper = edaOutlierResultMapper;
        this.qualityIssueMapper = qualityIssueMapper;
        this.productRateSnapshotMapper = productRateSnapshotMapper;
        this.storageDir = Paths.get(storageDir);
        this.schemaPath = Paths.get(schemaPath);
        this.objectMapper = objectMapper;
    }

    @Transactional(readOnly = true)
    public List<RawFileListItem> listRawFiles() {
        return rawFileUploadMapper.selectAll().stream()
                .map(item -> new RawFileListItem(
                        item.getUploadId(),
                        item.getFileName(),
                        item.getStatus(),
                        item.getUploadedAt() == null ? null : item.getUploadedAt().format(LIST_TIME_FORMAT)
                ))
                .toList();
    }

    @Transactional
    public RawFileUploadResponse upload(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST, "Empty file");
        }
        try {
            Files.createDirectories(storageDir);
            String originalName = file.getOriginalFilename() == null ? "upload.bin" : file.getOriginalFilename();
            String storedName = UUID.randomUUID() + "-" + originalName;
            Path storedPath = storageDir.resolve(storedName);

            try (InputStream in = file.getInputStream()) {
                Files.copy(in, storedPath);
            }

            String hash = sha256(storedPath);
            Long nextId = rawFileUploadMapper.selectNextId();
            RawFileUpload upload = RawFileUpload.builder()
                    .uploadId(nextId)
                    .fileName(originalName)
                    .fileHash(hash)
                    .storedPath(storedPath.toString())
                    .status("UPLOADED")
                    .uploadedAt(LocalDateTime.now())
                    .build();
            rawFileUploadMapper.insert(upload);
            return new RawFileUploadResponse(nextId, "UPLOADED");
        } catch (IOException ex) {
            throw new BusinessException(ErrorCode.INTERNAL_ERROR, "File upload failed");
        }
    }

    @Transactional
    public RawFileValidateResponse validate(Long uploadId) {
        RawFileUpload upload = rawFileUploadMapper.selectById(uploadId);
        if (upload == null) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST, "Upload not found");
        }
        if (!"INGESTED".equals(upload.getStatus()) && !"NORMALIZED".equals(upload.getStatus()) && !"EDA_DONE".equals(upload.getStatus())) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST, "Ingest required before normalize");
        }
        List<String> errors = validateFile(upload);
        if (errors.isEmpty()) {
            rawFileUploadMapper.updateStatus(uploadId, "VALIDATED");
            return new RawFileValidateResponse(true, List.of());
        }
        rawFileUploadMapper.updateStatus(uploadId, "FAILED");
        return new RawFileValidateResponse(false, errors);
    }

        @Transactional
    public RawFileIngestResponse ingest(Long uploadId) {
        RawFileUpload upload = rawFileUploadMapper.selectById(uploadId);
        if (upload == null) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST, "Upload not found");
        }
        if ("INGESTED".equals(upload.getStatus()) || "NORMALIZED".equals(upload.getStatus()) || "EDA_DONE".equals(upload.getStatus())) {
            return new RawFileIngestResponse(0, 0);
        }
        int inserted = ingestFile(upload);
        rawFileUploadMapper.updateStatus(uploadId, "INGESTED");
        return new RawFileIngestResponse(inserted, 0);
    }
    @Transactional
    public RawFileNormalizeResponse normalize(Long uploadId) {
        RawFileUpload upload = rawFileUploadMapper.selectById(uploadId);
        if (upload == null) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST, "Upload not found");
        }
        if (!"INGESTED".equals(upload.getStatus()) && !"NORMALIZED".equals(upload.getStatus()) && !"EDA_DONE".equals(upload.getStatus())) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST, "Ingest required before normalize");
        }
        List<RawFileCell> cells = rawFileCellMapper.selectByUploadId(uploadId);
        rawFileNormalizedMapper.deleteByUploadId(uploadId);
        if (cells == null || cells.isEmpty()) {
            return new RawFileNormalizeResponse(0, upload.getStatus());
        }
        final int batchSize = 500;
        List<RawFileNormalized> batch = new ArrayList<>();
        int total = 0;
        LocalDateTime now = LocalDateTime.now();
        for (RawFileCell cell : cells) {
            RawFileNormalized norm = RawFileNormalized.builder()
                    .uploadId(cell.getUploadId())
                    .rowNum(cell.getRowNum())
                    .columnName(cell.getColumnName())
                    .columnValue(cell.getColumnValue())
                    .createdAt(now)
                    .build();
            batch.add(norm);
            if (batch.size() >= batchSize) {
                rawFileNormalizedMapper.insertBatch(batch);
                total += batch.size();
                batch.clear();
            }
        }
        if (!batch.isEmpty()) {
            rawFileNormalizedMapper.insertBatch(batch);
            total += batch.size();
        }
        createRateSnapshotsFromNormalized(uploadId);
        rawFileUploadMapper.updateStatus(uploadId, "NORMALIZED");
        return new RawFileNormalizeResponse(total, "NORMALIZED");
    }
    @Transactional
    public RawFileEdaResponse runEda(Long uploadId) {
        try {
            RawFileUpload upload = rawFileUploadMapper.selectById(uploadId);
            if (upload == null) {
                throw new BusinessException(ErrorCode.INVALID_REQUEST, "Upload not found");
            }
            if (!"NORMALIZED".equals(upload.getStatus()) && !"EDA_DONE".equals(upload.getStatus())) {
                throw new BusinessException(ErrorCode.INVALID_REQUEST, "Normalize required before EDA");
            }
            List<RawFileCell> cells = rawFileCellMapper.selectByUploadId(uploadId);
            if (cells == null || cells.isEmpty()) {
                throw new BusinessException(ErrorCode.INVALID_REQUEST, "No data to analyze");
            }

            Long edaRunId = edaRunMapper.selectNextId();
            EdaRun run = EdaRun.builder()
                    .edaRunId(edaRunId)
                    .userId(upload.getUploaderId())
                    .snapshotId(null)
                    .versionId(null)
                    .createdAt(LocalDateTime.now())
                    .build();
            edaRunMapper.insert(run);

            Map<String, List<BigDecimal>> numericColumns = new HashMap<>();
            Map<String, Integer> missingCounts = new HashMap<>();
            Map<String, Integer> totalCounts = new HashMap<>();
            Map<String, List<ColumnValue>> columnValues = new HashMap<>();

            for (RawFileCell cell : cells) {
                String column = cell.getColumnName();
                totalCounts.put(column, totalCounts.getOrDefault(column, 0) + 1);
                if (cell.getColumnValue() == null || cell.getColumnValue().isBlank()) {
                    missingCounts.put(column, missingCounts.getOrDefault(column, 0) + 1);
                } else {
                    columnValues.computeIfAbsent(column, key -> new ArrayList<>())
                            .add(new ColumnValue(cell.getRowNum(), cell.getColumnValue()));
                    BigDecimal value = parseDecimal(cell.getColumnValue());
                    if (value != null) {
                        numericColumns.computeIfAbsent(column, key -> new ArrayList<>()).add(value);
                    }
                }
            }

            int metricCount = 0;
            int issueCount = 0;
            int totalRows = rawFileRowMapper.countByUploadId(uploadId);

            for (Map.Entry<String, List<BigDecimal>> entry : numericColumns.entrySet()) {
                String column = entry.getKey();
                List<BigDecimal> values = entry.getValue();
                if (values.isEmpty()) {
                    continue;
                }
                EdaStatResult stat = calculateStats(
                        edaRunId,
                        column,
                        values,
                        totalCounts.getOrDefault(column, 0),
                        missingCounts.getOrDefault(column, 0)
                );
                edaStatResultMapper.insert(stat);
                metricCount++;

                List<NumericPoint> points = buildNumericPoints(columnValues.getOrDefault(column, List.of()));
                issueCount += createOutlierResults(edaRunId, column, points, stat.getQ1(), stat.getQ3());
                issueCount += createQualityIssue(uploadId, column, stat.getMissingRate(), totalRows, missingCounts.getOrDefault(column, 0));
            }

            String summaryPath = writeEdaSummary(uploadId, edaRunId, totalRows, columnValues.size());
            EdaMetric metric = EdaMetric.builder()
                    .metricId(edaMetricMapper.selectNextId())
                    .edaRunId(edaRunId)
                    .metricName("summary")
                    .metricType("EDA")
                    .metricKey("SUMMARY")
                    .metricValuePath(summaryPath)
                    .createdAt(LocalDateTime.now())
                    .build();
            edaMetricMapper.insert(metric);

            rawFileUploadMapper.updateStatus(uploadId, "EDA_DONE");
            return new RawFileEdaResponse(edaRunId, metricCount, issueCount);
        } catch (Exception ex) {
            log.error("EDA failed for uploadId={}", uploadId, ex);
            throw ex;
        }
    }
private String sha256(Path filePath) throws IOException {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] data = Files.readAllBytes(filePath);
            byte[] hash = digest.digest(data);
            return HexFormat.of().formatHex(hash);
        } catch (Exception ex) {
            throw new IOException(ex);
        }
    }

    private List<String> validateFile(RawFileUpload upload) {
        Path path = Paths.get(upload.getStoredPath());
        if (!Files.exists(path)) {
            return List.of("Stored file not found");
        }
        try {
            long size = Files.size(path);
            if (size <= 0) {
                return List.of("Empty file");
            }
        } catch (IOException ex) {
            return List.of("Unable to read file size");
        }

        String name = upload.getFileName() == null ? "" : upload.getFileName().toLowerCase();
        if (name.endsWith(".csv")) {
            try (java.io.BufferedReader br = Files.newBufferedReader(path)) {
                String header = br.readLine();
                if (header == null || header.trim().isEmpty()) {
                    return List.of("CSV header is empty");
                }
                String[] columns = header.split(",");
                if (columns.length == 0) {
                    return List.of("CSV header has no columns");
                }
                List<String> schemaErrors = validateAgainstSchema(name, columns);
                if (!schemaErrors.isEmpty()) {
                    return schemaErrors;
                }
            } catch (IOException ex) {
                return List.of("CSV read failed");
            }
        }

        return List.of();
    }

    private int ingestFile(RawFileUpload upload) {
        Path path = Paths.get(upload.getStoredPath());
        if (!Files.exists(path)) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST, "Stored file not found");
        }
        String name = upload.getFileName() == null ? "" : upload.getFileName().toLowerCase();
        if (name.endsWith(".csv")) {
            try (java.io.BufferedReader br = Files.newBufferedReader(path)) {
                int count = 0;
                String line = br.readLine(); // header
                if (line == null) {
                    return 0;
                }
                String[] headers = line.split(",");
                long rowNum = 0;
                final int cellBatchSize = 100;
                List<RawFileCell> cellBatch = new java.util.ArrayList<>();
                while ((line = br.readLine()) != null) {
                    if (line.trim().isEmpty()) {
                        continue;
                    }
                    rowNum++;
                    String[] values = line.split(",", -1);
                    java.util.Map<String, String> rowMap = new java.util.HashMap<>();
                    for (int i = 0; i < headers.length; i++) {
                        String key = headers[i].trim();
                        String value = i < values.length ? values[i].trim() : "";
                        rowMap.put(key, value);
                    }
                    String rowJson = objectMapper.writeValueAsString(rowMap);
                    Long nextId = rawFileRowMapper.selectNextId();
                    RawFileRow row = RawFileRow.builder()
                            .rowId(nextId)
                            .uploadId(upload.getUploadId())
                            .rowNum(rowNum)
                            .rowJson(rowJson)
                            .createdAt(LocalDateTime.now())
                            .build();
                    rawFileRowMapper.insertOne(row);

                    for (int i = 0; i < headers.length; i++) {
                        String key = headers[i].trim();
                        if (key.isEmpty()) {
                            continue;
                        }
                        String value = rowMap.getOrDefault(key, "");
                        RawFileCell cell = RawFileCell.builder()
                                .uploadId(upload.getUploadId())
                                .rowNum(rowNum)
                                .columnName(key)
                                .columnValue(value)
                                .createdAt(LocalDateTime.now())
                                .build();
                        cellBatch.add(cell);
                        if (cellBatch.size() >= cellBatchSize) {
                            rawFileCellMapper.insertBatch(cellBatch);
                            cellBatch.clear();
                        }
                    }
                    count++;
                }
                if (!cellBatch.isEmpty()) {
                    rawFileCellMapper.insertBatch(cellBatch);
                }
                return count;
            } catch (IOException ex) {
                throw new BusinessException(ErrorCode.INTERNAL_ERROR, "CSV ingest failed");
            } catch (Exception ex) {
                throw new BusinessException(ErrorCode.INTERNAL_ERROR, "Row insert failed: " + ex.getMessage());
            }
        }
        return 0;
    }

    private BigDecimal average(List<BigDecimal> values) {
        if (values.isEmpty()) return null;
        BigDecimal sum = BigDecimal.ZERO;
        for (BigDecimal v : values) {
            sum = sum.add(v);
        }
        return sum.divide(new BigDecimal(values.size()), 6, java.math.RoundingMode.HALF_UP);
    }

    private EdaStatResult calculateStats(Long edaRunId,
                                         String column,
                                         List<BigDecimal> values,
                                         int totalCount,
                                         int missingCount) {
        List<BigDecimal> sorted = new ArrayList<>(values);
        sorted.sort(BigDecimal::compareTo);

        BigDecimal mean = average(sorted);
        BigDecimal med = median(sorted);
        BigDecimal min = sorted.isEmpty() ? null : sorted.get(0);
        BigDecimal max = sorted.isEmpty() ? null : sorted.get(sorted.size() - 1);
        BigDecimal q1 = quartile(sorted, 0.25d);
        BigDecimal q3 = quartile(sorted, 0.75d);
        BigDecimal std = stddev(sorted, mean);
        BigDecimal missingRate = totalCount == 0
                ? BigDecimal.ZERO
                : new BigDecimal(missingCount)
                .divide(new BigDecimal(totalCount), 6, java.math.RoundingMode.HALF_UP);

        return EdaStatResult.builder()
                .statId(edaStatResultMapper.selectNextId())
                .edaRunId(edaRunId)
                .rowId(null)
                .columnCode(column)
                .mean(mean)
                .median(med)
                .std(std)
                .min(min)
                .max(max)
                .q1(q1)
                .q3(q3)
                .skewness(null)
                .kurtosis(null)
                .missingRate(missingRate)
                .dataType("NUMBER")
                .build();
    }

    private BigDecimal median(List<BigDecimal> values) {
        if (values.isEmpty()) return null;
        List<BigDecimal> sorted = new ArrayList<>(values);
        sorted.sort(BigDecimal::compareTo);
        int n = sorted.size();
        if (n % 2 == 1) {
            return sorted.get(n / 2);
        }
        BigDecimal a = sorted.get((n / 2) - 1);
        BigDecimal b = sorted.get(n / 2);
        return a.add(b).divide(new BigDecimal(2), 6, java.math.RoundingMode.HALF_UP);
    }

    private BigDecimal quartile(List<BigDecimal> values, double q) {
        if (values.isEmpty()) return null;
        List<BigDecimal> sorted = new ArrayList<>(values);
        sorted.sort(BigDecimal::compareTo);
        int n = sorted.size();
        if (n == 1) {
            return sorted.get(0);
        }
        double pos = q * (n - 1);
        int idx = (int) Math.floor(pos);
        int next = Math.min(idx + 1, n - 1);
        double frac = pos - idx;
        BigDecimal lower = sorted.get(idx);
        BigDecimal upper = sorted.get(next);
        if (frac == 0) {
            return lower;
        }
        BigDecimal diff = upper.subtract(lower);
        return lower.add(diff.multiply(new BigDecimal(frac))).setScale(6, java.math.RoundingMode.HALF_UP);
    }

    private BigDecimal stddev(List<BigDecimal> values, BigDecimal mean) {
        if (values.isEmpty() || mean == null) return null;
        double m = mean.doubleValue();
        double sum = 0.0;
        for (BigDecimal v : values) {
            double d = v.doubleValue() - m;
            sum += d * d;
        }
        double variance = sum / values.size();
        return new BigDecimal(Math.sqrt(variance)).setScale(6, java.math.RoundingMode.HALF_UP);
    }

    private int createOutlierResults(Long edaRunId,
                                     String column,
                                     List<NumericPoint> points,
                                     BigDecimal q1,
                                     BigDecimal q3) {
        if (q1 == null || q3 == null) {
            return 0;
        }
        if (points.isEmpty()) return 0;
        BigDecimal iqr = q3.subtract(q1);
        BigDecimal lower = q1.subtract(iqr.multiply(new BigDecimal("1.5")));
        BigDecimal upper = q3.add(iqr.multiply(new BigDecimal("1.5")));
        List<EdaOutlierResult> batch = new ArrayList<>();
        int inserted = 0;
        for (NumericPoint point : points) {
            if (point.value().compareTo(lower) < 0 || point.value().compareTo(upper) > 0) {
                EdaOutlierResult outlier = EdaOutlierResult.builder()
                        .edaRunId(edaRunId)
                        .rowId(point.rowNum())
                        .columnCode(column)
                        .methodCodeValueId("IQR")
                        .flag("Y")
                        .reason("IQR outlier")
                        .build();
                batch.add(outlier);
                if (batch.size() >= 200) {
                    edaOutlierResultMapper.insertBatch(batch);
                    inserted += batch.size();
                    batch.clear();
                }
            }
        }
        if (batch.isEmpty()) {
            return inserted;
        }
        edaOutlierResultMapper.insertBatch(batch);
        inserted += batch.size();
        return inserted;
    }

    private static final class ColumnValue {
        private final long rowNum;
        private final String value;

        private ColumnValue(long rowNum, String value) {
            this.rowNum = rowNum;
            this.value = value;
        }

        private long rowNum() {
            return rowNum;
        }

        private String value() {
            return value;
        }
    }

    private List<NumericPoint> buildNumericPoints(List<ColumnValue> values) {
        if (values == null || values.isEmpty()) {
            return List.of();
        }
        List<NumericPoint> points = new ArrayList<>();
        for (ColumnValue value : values) {
            BigDecimal parsed = parseDecimal(value.value());
            if (parsed != null) {
                points.add(new NumericPoint(value.rowNum(), parsed));
            }
        }
        return points;
    }

    private static final class NumericPoint {
        private final long rowNum;
        private final BigDecimal value;

        private NumericPoint(long rowNum, BigDecimal value) {
            this.rowNum = rowNum;
            this.value = value;
        }

        private long rowNum() {
            return rowNum;
        }

        private BigDecimal value() {
            return value;
        }
    }

    private int createQualityIssue(Long uploadId,
                                   String column,
                                   BigDecimal missingRate,
                                   int totalRows,
                                   int missing) {
        try {
            Path issueDir = storageDir.resolve("quality");
            Files.createDirectories(issueDir);
            Map<String, Object> payload = new HashMap<>();
            payload.put("column", column);
            payload.put("missingRate", missingRate);
            payload.put("totalRows", totalRows);
            payload.put("missingCount", missing);
            String name = "issue-" + uploadId + "-" + column + "-" + System.currentTimeMillis() + ".json";
            Path file = issueDir.resolve(name);
            objectMapper.writeValue(file.toFile(), payload);

            QualityIssue issue = QualityIssue.builder()
                    .issueId(qualityIssueMapper.selectNextId())
                    .uploadId(uploadId)
                    .issueTypeCodeValueId("MISSING_RATE")
                    .columnCode(column)
                    .detailJsonPath(file.toString())
                    .statusCodeValueId("OPEN")
                    .resolvedByUserId(null)
                    .resolvedAt(null)
                    .detectedStageCodeValueId("EDA")
                    .issuedAt(LocalDateTime.now())
                    .build();
            qualityIssueMapper.insert(issue);
            return 1;
        } catch (Exception ex) {
            return 0;
        }
    }

    private String writeEdaSummary(Long uploadId, Long edaRunId, int totalRows, int totalColumns) {
        try {
            Path edaDir = storageDir.resolve("eda");
            Files.createDirectories(edaDir);
            Map<String, Object> payload = new HashMap<>();
            payload.put("uploadId", uploadId);
            payload.put("edaRunId", edaRunId);
            payload.put("totalRows", totalRows);
            payload.put("totalColumns", totalColumns);
            String name = "eda-summary-" + uploadId + "-" + edaRunId + ".json";
            Path file = edaDir.resolve(name);
            objectMapper.writeValue(file.toFile(), payload);
            return file.toString();
        } catch (Exception ex) {
            return null;
        }
    }

    private List<String> validateAgainstSchema(String fileName, String[] headerColumns) {
        if (!Files.exists(schemaPath)) {
            return List.of();
        }
        try {
            String json = Files.readString(schemaPath);
            if (json == null || json.isBlank()) {
                return List.of();
            }
            SchemaConfig config = objectMapper.readValue(json, SchemaConfig.class);
            if (config == null || config.rules == null) {
                return List.of();
            }
            List<String> header = java.util.Arrays.stream(headerColumns)
                    .map(String::trim)
                    .filter(s -> !s.isBlank())
                    .toList();
            for (SchemaRule rule : config.rules) {
                if (rule == null || rule.match == null) {
                    continue;
                }
                if (fileName.contains(rule.match.toLowerCase())) {
                    List<String> missing = new java.util.ArrayList<>();
                    for (String req : rule.required) {
                        if (!header.contains(req)) {
                            missing.add(req);
                        }
                    }
                    if (!missing.isEmpty()) {
                        return List.of("Missing required columns: " + String.join(",", missing));
                    }
                }
            }
            return List.of();
        } catch (Exception ex) {
            return List.of("Schema validation failed");
        }
    }

    private static class SchemaConfig {
        public java.util.List<SchemaRule> rules;
    }

    private static class SchemaRule {
        public String match;
        public java.util.List<String> required;
    }

    private void createRateSnapshotsFromNormalized(Long uploadId) {
        List<RawFileNormalized> rows = rawFileNormalizedMapper.selectByUploadId(uploadId);
        if (rows == null || rows.isEmpty()) {
            return;
        }
        Map<Long, Map<String, String>> grouped = new HashMap<>();
        for (RawFileNormalized row : rows) {
            grouped.computeIfAbsent(row.getRowNum(), key -> new HashMap<>())
                    .put(normalizeKey(row.getColumnName()), row.getColumnValue());
        }

        LocalDate today = LocalDate.now();
        LocalDateTime now = LocalDateTime.now();
        for (Map<String, String> map : grouped.values()) {
            Long productId = parseLong(pickValue(map, "product_id", "productid", "product"));
            if (productId == null) {
                continue;
            }
            BigDecimal rateMin = parseDecimal(pickValue(map, "rate_min", "min_rate", "interest_min", "minrate"));
            BigDecimal rateMax = parseDecimal(pickValue(map, "rate_max", "max_rate", "interest_max", "maxrate"));
            BigDecimal scoreBase = parseDecimal(pickValue(map, "score_base", "base_score", "credit_score_base"));
            String rateType = pickValue(map, "rate_type", "ratetype", "rate_kind");
            LocalDate asOfDate = parseDate(pickValue(map, "as_of_date", "asofdate", "date", "base_date"));
            if (asOfDate == null) {
                asOfDate = today;
            }

            if (rateMin == null && rateMax == null && scoreBase == null) {
                continue;
            }

            ProductRateSnapshot snapshot = ProductRateSnapshot.builder()
                    .productId(productId)
                    .rateMin(rateMin)
                    .rateMax(rateMax)
                    .scoreBase(scoreBase)
                    .rateType(rateType)
                    .asOfDate(asOfDate)
                    .createdAt(now)
                    .build();
            productRateSnapshotMapper.insert(snapshot);
        }
    }

    private String normalizeKey(String key) {
        if (key == null) return "";
        return key.trim().toLowerCase().replaceAll("[\\s\\-]", "_");
    }

    private String pickValue(Map<String, String> map, String... keys) {
        for (String key : keys) {
            String val = map.get(key);
            if (val != null && !val.isBlank()) {
                return val.trim();
            }
        }
        return null;
    }

    private Long parseLong(String value) {
        if (value == null || value.isBlank()) return null;
        try {
            return Long.parseLong(value.trim());
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private BigDecimal parseDecimal(String value) {
        if (value == null || value.isBlank()) return null;
        try {
            return new BigDecimal(value.trim());
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private LocalDate parseDate(String value) {
        if (value == null || value.isBlank()) return null;
        String v = value.trim();
        try {
            if (v.contains("/")) {
                return LocalDate.parse(v, DateTimeFormatter.ofPattern("yyyy/MM/dd"));
            }
            return LocalDate.parse(v);
        } catch (Exception ex) {
            return null;
        }
    }
}






