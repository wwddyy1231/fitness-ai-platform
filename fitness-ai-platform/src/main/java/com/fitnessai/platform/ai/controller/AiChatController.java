package com.fitnessai.platform.ai.controller;

import com.fitnessai.platform.ai.dto.AiChatRequest;
import com.fitnessai.platform.ai.rag.FitnessKnowledgeRagService;
import com.fitnessai.platform.ai.service.AiFitnessAssistantService;
import com.fitnessai.platform.ai.vo.AiChatVO;
import com.fitnessai.platform.common.api.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ai")
public class AiChatController {
    private final AiFitnessAssistantService assistant;
    private final FitnessKnowledgeRagService rag;
    public AiChatController(AiFitnessAssistantService assistant, FitnessKnowledgeRagService rag) { this.assistant = assistant; this.rag = rag; }

    @PostMapping("/chat")
    public ApiResponse<AiChatVO> chat(@Valid @RequestBody AiChatRequest request) {
        return ApiResponse.success(assistant.chat(request));
    }

    @PostMapping("/knowledge/refresh")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Integer> refreshKnowledge() {
        return ApiResponse.success(rag.refresh());
    }
}
