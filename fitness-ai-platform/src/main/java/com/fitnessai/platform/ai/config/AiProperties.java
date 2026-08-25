package com.fitnessai.platform.ai.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "ai")
public record AiProperties(Openai openai, Rag rag) {
    public record Openai(String baseUrl, String apiKey, String chatModel, String embeddingModel, Double temperature) {}
    public record Rag(Integer maxResults, Double minScore) {}
}
