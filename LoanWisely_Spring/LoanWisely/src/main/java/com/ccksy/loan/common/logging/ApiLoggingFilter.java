package com.ccksy.loan.common.logging;

import java.io.IOException;
import java.time.Instant;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@Order(Ordered.LOWEST_PRECEDENCE)
public class ApiLoggingFilter extends OncePerRequestFilter {

    private final ApiLogCollector apiLogCollector;

    public ApiLoggingFilter(ApiLogCollector apiLogCollector) {
        this.apiLogCollector = apiLogCollector;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        long start = System.currentTimeMillis();
        int status = 500;
        try {
            filterChain.doFilter(request, response);
            status = response.getStatus();
        } finally {
            long durationMs = System.currentTimeMillis() - start;
            ApiLogEntry entry = new ApiLogEntry(
                    Instant.now(),
                    MDC.get(TraceIdFilter.TRACE_ID_KEY),
                    request.getMethod(),
                    request.getRequestURI(),
                    request.getQueryString(),
                    status,
                    durationMs,
                    clientIp(request),
                    request.getHeader("User-Agent")
            );
            apiLogCollector.add(entry);
        }
    }

    private String clientIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            int comma = forwarded.indexOf(',');
            return comma > 0 ? forwarded.substring(0, comma).trim() : forwarded.trim();
        }
        return request.getRemoteAddr();
    }
}
