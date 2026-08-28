package com.fitnessai.platform.common.config;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fitnessai.platform.common.api.ApiResponse;
import org.junit.jupiter.api.Test;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

class JacksonConfigTest {
    private final ObjectMapper objectMapper = objectMapper();

    @Test
    void omitsNullDataFromResponseEnvelope() throws Exception {
        String json = objectMapper.writeValueAsString(ApiResponse.failure(40401, "文章不存在"));

        assertThat(json).isEqualTo("{\"code\":40401,\"message\":\"文章不存在\"}");
    }

    @Test
    void serializesBoxedLongAsStringAndPrimitiveLongAsNumber() throws Exception {
        String json = objectMapper.writeValueAsString(new IdContract(9223372036854775807L, 10L));

        assertThat(json).isEqualTo("{\"id\":\"9223372036854775807\",\"total\":10}");
    }

    @Test
    void acceptsStringEncodedLongInRequests() throws Exception {
        IdRequest request = objectMapper.readValue("{\"id\":\"9223372036854775807\"}", IdRequest.class);

        assertThat(request.id()).isEqualTo(Long.MAX_VALUE);
    }

    private record IdContract(Long id, long total) {}
    private record IdRequest(Long id) {}

    private ObjectMapper objectMapper() {
        Jackson2ObjectMapperBuilder builder = new Jackson2ObjectMapperBuilder()
                .serializationInclusion(JsonInclude.Include.NON_NULL);
        new JacksonConfig().longIdSerializer().customize(builder);
        return builder.build();
    }
}
