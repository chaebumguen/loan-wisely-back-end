package com.ccksy.loan.domain.product.service;

import java.util.concurrent.atomic.AtomicBoolean;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ExternalProductSyncScheduler {

    private static final String ZONE_SEOUL = "Asia/Seoul";
    private final ExternalProductSyncService externalProductSyncService;
    private final AtomicBoolean running = new AtomicBoolean(false);

    @Scheduled(cron = "0 0 3 * * *", zone = ZONE_SEOUL)
    public void syncDaily() {
        if (!running.compareAndSet(false, true)) {
            log.warn("External product sync already running. Skipping.");
            return;
        }
        try {
            int total = externalProductSyncService.syncAll();
            log.info("External product daily sync completed. total={}", total);
        } catch (Exception ex) {
            log.error("External product daily sync failed.", ex);
        } finally {
            running.set(false);
        }
    }
}
