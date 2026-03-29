package com.ccksy.loan.infra.elasticsearch;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(EsProperties.class)
public class EsClientConfig {

    @Bean(destroyMethod = "close")
    public RestHighLevelClient restHighLevelClient(EsProperties props) {
        return new RestHighLevelClient(
                RestClient.builder(new HttpHost(props.getHost(), props.getPort(), props.getScheme()))
        );
    }
}
