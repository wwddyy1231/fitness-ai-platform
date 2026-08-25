package com.fitnessai.platform.ai.config;

import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiEmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(AiProperties.class)
public class LangChain4jConfig {
    @Bean
    ChatModel chatModel(AiProperties properties) {
        var p = properties.openai();
        return OpenAiChatModel.builder().baseUrl(p.baseUrl()).apiKey(p.apiKey())
                .modelName(p.chatModel()).temperature(p.temperature()).build();
    }

    @Bean
    EmbeddingModel embeddingModel(AiProperties properties) {
        var p = properties.openai();
        return OpenAiEmbeddingModel.builder().baseUrl(p.baseUrl()).apiKey(p.apiKey())
                .modelName(p.embeddingModel()).build();
    }

    @Bean
    EmbeddingStore<TextSegment> embeddingStore() {
        return new InMemoryEmbeddingStore<>();
    }
}
