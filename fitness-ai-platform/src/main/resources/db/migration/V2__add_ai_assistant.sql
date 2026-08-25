CREATE TABLE training_plan (
    id BIGINT PRIMARY KEY,
    name VARCHAR(160) NOT NULL,
    description TEXT,
    plan_content JSON NOT NULL,
    status TINYINT NOT NULL DEFAULT 0 COMMENT '0草稿，1发布，2下线',
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    deleted TINYINT NOT NULL DEFAULT 0,
    KEY idx_training_plan_rag(status, updated_at, id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='训练计划知识来源';

CREATE TABLE nutrition_plan (
    id BIGINT PRIMARY KEY,
    name VARCHAR(160) NOT NULL,
    description TEXT,
    plan_content JSON NOT NULL,
    status TINYINT NOT NULL DEFAULT 0 COMMENT '0草稿，1发布，2下线',
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    deleted TINYINT NOT NULL DEFAULT 0,
    KEY idx_nutrition_plan_rag(status, updated_at, id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='营养计划知识来源';

CREATE TABLE ai_chat_history (
    id BIGINT PRIMARY KEY COMMENT '消息ID',
    conversation_id BIGINT NOT NULL COMMENT '会话ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    role VARCHAR(16) NOT NULL COMMENT 'USER或ASSISTANT',
    content MEDIUMTEXT NOT NULL COMMENT '消息内容',
    model_name VARCHAR(100) DEFAULT NULL COMMENT '模型名称',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '1完成，2失败',
    created_at DATETIME(3) NOT NULL COMMENT '创建时间',
    KEY idx_ai_chat_conversation(conversation_id, id),
    KEY idx_ai_chat_user_time(user_id, created_at DESC, id DESC),
    CONSTRAINT fk_ai_chat_user FOREIGN KEY(user_id) REFERENCES sys_user(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI聊天消息记录';
