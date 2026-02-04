package com.ccksy.loan.common.logging;

import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;
import org.springframework.stereotype.Component;

@Component
public class ApiLogCollector {
    private static final int MAX_ENTRIES = 1000;
    private final Deque<ApiLogEntry> entries = new ConcurrentLinkedDeque<>();

    public void add(ApiLogEntry entry) {
        entries.addLast(entry);
        while (entries.size() > MAX_ENTRIES) {
            entries.pollFirst();
        }
    }

    public List<ApiLogEntry> snapshot() {
        return new ArrayList<>(entries);
    }
}
