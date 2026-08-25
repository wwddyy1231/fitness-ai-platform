package com.fitnessai.platform.ai.service;

import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.fitnessai.platform.ai.config.AiProperties;
import com.fitnessai.platform.ai.dto.AiChatRequest;
import com.fitnessai.platform.ai.entity.AiChatHistory;
import com.fitnessai.platform.ai.mapper.AiChatHistoryMapper;
import com.fitnessai.platform.ai.rag.FitnessKnowledgeRagService;
import com.fitnessai.platform.ai.vo.AiChatVO;
import com.fitnessai.platform.common.exception.BusinessException;
import com.fitnessai.platform.user.entity.User;
import com.fitnessai.platform.user.mapper.UserMapper;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.rag.content.Content;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class AiFitnessAssistantService {
    private final ChatModel chatModel;
    private final FitnessKnowledgeRagService ragService;
    private final AiChatHistoryMapper historyMapper;
    private final UserMapper userMapper;
    private final AiProperties properties;
    private final String promptTemplate;

    public AiFitnessAssistantService(ChatModel chatModel, FitnessKnowledgeRagService ragService,
                                     AiChatHistoryMapper historyMapper, UserMapper userMapper,
                                     AiProperties properties, ResourceLoader resourceLoader) {
        this.chatModel = chatModel; this.ragService = ragService; this.historyMapper = historyMapper;
        this.userMapper = userMapper; this.properties = properties;
        Resource resource = resourceLoader.getResource("classpath:prompts/fitness-assistant.txt");
        try { this.promptTemplate = resource.getContentAsString(StandardCharsets.UTF_8); }
        catch (IOException e) { throw new IllegalStateException("无法加载AI Prompt模板", e); }
    }

    public AiChatVO chat(AiChatRequest request) {
        verifyCurrentUser(request.userId());
        if (properties.openai().apiKey() == null || properties.openai().apiKey().isBlank())
            throw new BusinessException(50301, "AI模型尚未配置，请设置AI_API_KEY");
        Long conversationId = IdWorker.getId();
        save(conversationId, request.userId(), "USER", request.message(), null, 1);
        try {
            List<Content> contents = ragService.retrieve(request.message());
            String knowledge = contents.isEmpty() ? "没有召回到可靠的站内知识。" : contents.stream()
                    .map(content -> "[" + title(content) + "]\n" + content.textSegment().text())
                    .reduce((a, b) -> a + "\n\n" + b).orElse("");
            String prompt = promptTemplate.replace("{{knowledge}}", knowledge).replace("{{question}}", request.message());
            String answer = chatModel.chat(prompt);
            save(conversationId, request.userId(), "ASSISTANT", answer, properties.openai().chatModel(), 1);
            return new AiChatVO(conversationId, answer, toSources(contents));
        } catch (RuntimeException ex) {
            save(conversationId, request.userId(), "ASSISTANT", "模型调用失败", properties.openai().chatModel(), 2);
            throw new BusinessException(50201, "AI服务暂时不可用，请稍后重试");
        }
    }

    private void verifyCurrentUser(Long requestedUserId) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userMapper.selectById(requestedUserId);
        if (user == null || !user.getUsername().equals(username)) throw new BusinessException(40302, "不能以其他用户身份发起对话");
    }

    private List<AiChatVO.SourceVO> toSources(List<Content> contents) {
        var unique = new LinkedHashMap<String, AiChatVO.SourceVO>();
        for (Content content : contents) {
            var metadata = content.textSegment().metadata();
            String type = metadata.getString("sourceType");
            String id = metadata.getString("sourceId");
            unique.put(type + ":" + id, new AiChatVO.SourceVO(type, id, metadata.getString("title")));
        }
        return List.copyOf(unique.values());
    }

    private String title(Content content) { return content.textSegment().metadata().getString("title"); }

    private void save(Long conversationId, Long userId, String role, String content, String model, int status) {
        AiChatHistory history = new AiChatHistory();
        history.setConversationId(conversationId); history.setUserId(userId); history.setRole(role);
        history.setContent(content); history.setModelName(model); history.setStatus(status); history.setCreatedAt(LocalDateTime.now());
        historyMapper.insert(history);
    }
}
