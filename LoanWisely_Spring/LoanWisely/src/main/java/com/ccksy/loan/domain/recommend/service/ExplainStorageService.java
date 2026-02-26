package com.ccksy.loan.domain.recommend.service;

import com.ccksy.loan.domain.recommend.result.core.RecommendResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class ExplainStorageService {

    private final Path storageDir;
    private final ObjectMapper objectMapper;

    public ExplainStorageService(@Value("${storage.explain-dir}") String storageDir,
                                 ObjectMapper objectMapper) {
        this.storageDir = Paths.get(storageDir);
        this.objectMapper = objectMapper;
    }

    public String storeExplain(RecommendResult result, String summary) {
        if (result == null) return null;
        try {
            Files.createDirectories(storageDir);
            String name = result.getReproduceKey() + "-" + System.currentTimeMillis() + ".json";
            Path file = storageDir.resolve(name);

            Map<String, Object> payload = new HashMap<>();
            payload.put("summary", summary);
            payload.put("state", result.getState());
            payload.put("policyVersion", result.getPolicyVersion());
            payload.put("metaVersion", result.getMetaVersion());
            payload.put("resolvedInputLevel", result.getResolvedInputLevel());
            payload.put("warnings", result.getWarnings());
            payload.put("items", result.getItems());
            payload.put("createdAt", LocalDateTime.now().toString());

            objectMapper.writeValue(file.toFile(), payload);
            return file.toString();
        } catch (Exception ex) {
            return null;
        }
    }
}
