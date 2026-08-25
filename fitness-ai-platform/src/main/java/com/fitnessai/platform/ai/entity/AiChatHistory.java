package com.fitnessai.platform.ai.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;

@TableName("ai_chat_history")
public class AiChatHistory {
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    private Long conversationId;
    private Long userId;
    private String role;
    private String content;
    private String modelName;
    private Integer status;
    private LocalDateTime createdAt;
    public Long getId(){return id;} public void setId(Long v){id=v;}
    public Long getConversationId(){return conversationId;} public void setConversationId(Long v){conversationId=v;}
    public Long getUserId(){return userId;} public void setUserId(Long v){userId=v;}
    public String getRole(){return role;} public void setRole(String v){role=v;}
    public String getContent(){return content;} public void setContent(String v){content=v;}
    public String getModelName(){return modelName;} public void setModelName(String v){modelName=v;}
    public Integer getStatus(){return status;} public void setStatus(Integer v){status=v;}
    public LocalDateTime getCreatedAt(){return createdAt;} public void setCreatedAt(LocalDateTime v){createdAt=v;}
}
