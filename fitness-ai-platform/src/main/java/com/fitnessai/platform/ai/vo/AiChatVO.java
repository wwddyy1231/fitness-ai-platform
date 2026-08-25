package com.fitnessai.platform.ai.vo;

import java.util.List;

public record AiChatVO(Long conversationId, String answer, List<SourceVO> sources) {
    public record SourceVO(String type, String id, String title) {}
}
