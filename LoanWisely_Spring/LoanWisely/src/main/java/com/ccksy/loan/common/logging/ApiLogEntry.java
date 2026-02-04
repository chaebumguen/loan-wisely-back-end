package com.ccksy.loan.common.logging;

import java.time.Instant;

public class ApiLogEntry {
    private final Instant timestamp;
    private final String traceId;
    private final String method;
    private final String path;
    private final String query;
    private final int status;
    private final long durationMs;
    private final String clientIp;
    private final String userAgent;

    public ApiLogEntry(
            Instant timestamp,
            String traceId,
            String method,
            String path,
            String query,
            int status,
            long durationMs,
            String clientIp,
            String userAgent
    ) {
        this.timestamp = timestamp;
        this.traceId = traceId;
        this.method = method;
        this.path = path;
        this.query = query;
        this.status = status;
        this.durationMs = durationMs;
        this.clientIp = clientIp;
        this.userAgent = userAgent;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public String getTraceId() {
        return traceId;
    }

    public String getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public String getQuery() {
        return query;
    }

    public int getStatus() {
        return status;
    }

    public long getDurationMs() {
        return durationMs;
    }

    public String getClientIp() {
        return clientIp;
    }

    public String getUserAgent() {
        return userAgent;
    }
}
