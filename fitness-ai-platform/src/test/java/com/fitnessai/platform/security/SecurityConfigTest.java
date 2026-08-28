package com.fitnessai.platform.security;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.mock.web.MockServletContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

class SecurityConfigTest {
    private AnnotationConfigWebApplicationContext context;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        context = new AnnotationConfigWebApplicationContext();
        context.setServletContext(new MockServletContext());
        context.register(TestConfiguration.class);
        context.refresh();
        mockMvc = MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).build();
    }

    @AfterEach
    void closeContext() {
        context.close();
    }

    @Test
    void allowsAnonymousReadAndAuthenticationEndpoints() throws Exception {
        mockMvc.perform(get("/api/v1/articles/1")).andExpect(status().isOk());
        mockMvc.perform(get("/api/v1/home")).andExpect(status().isOk());
        mockMvc.perform(post("/api/v1/auth/login")).andExpect(status().isOk());
    }

    @Test
    void protectsAiChatAndContentWrites() throws Exception {
        mockMvc.perform(post("/api/ai/chat")).andExpect(status().isUnauthorized());
        mockMvc.perform(post("/api/v1/articles")).andExpect(status().isUnauthorized());
        mockMvc.perform(post("/api/v1/articles").with(user("member").roles("MEMBER")))
                .andExpect(status().isForbidden());
        mockMvc.perform(post("/api/v1/articles").with(user("editor").roles("EDITOR")))
                .andExpect(status().isOk());
        mockMvc.perform(post("/api/ai/chat").with(user("member").roles("MEMBER")))
                .andExpect(status().isOk());
    }

    @Test
    void reservesDeletesAndKnowledgeRefreshForAdmin() throws Exception {
        mockMvc.perform(delete("/api/v1/articles/1").with(user("editor").roles("EDITOR")))
                .andExpect(status().isForbidden());
        mockMvc.perform(delete("/api/v1/articles/1").with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk());
        mockMvc.perform(post("/api/ai/knowledge/refresh").with(user("editor").roles("EDITOR")))
                .andExpect(status().isForbidden());
        mockMvc.perform(post("/api/ai/knowledge/refresh").with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk());
    }

    @Configuration
    @EnableWebMvc
    @EnableWebSecurity
    @Import(SecurityConfig.class)
    static class TestConfiguration {
        @Bean
        ObjectMapper objectMapper() {
            return new ObjectMapper();
        }

        @Bean
        JwtAuthenticationFilter jwtAuthenticationFilter() {
            JwtProperties properties = new JwtProperties("test-secret-key-that-is-at-least-32-bytes", 3600);
            return new JwtAuthenticationFilter(new JwtService(properties));
        }

        @Bean
        TestController testController() {
            return new TestController();
        }
    }

    @RestController
    static class TestController {
        @GetMapping({"/api/v1/articles/1", "/api/v1/home"})
        Map<String, Boolean> read() {
            return Map.of("ok", true);
        }

        @PostMapping({"/api/v1/auth/login", "/api/v1/articles", "/api/ai/chat",
                "/api/ai/knowledge/refresh"})
        Map<String, Boolean> write() {
            return Map.of("ok", true);
        }

        @DeleteMapping("/api/v1/articles/1")
        Map<String, Boolean> deleteArticle() {
            return Map.of("ok", true);
        }
    }
}
