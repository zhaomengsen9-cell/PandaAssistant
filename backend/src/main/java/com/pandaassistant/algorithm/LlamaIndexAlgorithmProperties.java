package com.pandaassistant.algorithm;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@ConfigurationProperties(prefix = "app.algorithm.llama-index")
public record LlamaIndexAlgorithmProperties(String baseUrl, Duration timeout) {
    public LlamaIndexAlgorithmProperties {
        if (baseUrl == null || baseUrl.isBlank()) {
            baseUrl = "http://localhost:8001";
        }
        if (timeout == null) {
            timeout = Duration.ofSeconds(60);
        }
    }
}
