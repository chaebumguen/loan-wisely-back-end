package com.ccksy.loan.infra.elasticsearch;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "elasticsearch")
public class EsProperties {

    private String host = "localhost";
    private int port = 9200;
    private String scheme = "http";
    private Indices indices = new Indices();

    @Getter
    @Setter
    public static class Indices {
        private String recoPolicy = "reco_policy";
        private String recommendHistory = "recommend_history";
    }
}
