-- fitness-ai-platform MySQL 8 schema
-- MySQL 8.0.16+，默认字符集 utf8mb4，时间字段建议统一写入 UTC。

CREATE DATABASE IF NOT EXISTS fitness_ai
    DEFAULT CHARACTER SET utf8mb4
    DEFAULT COLLATE utf8mb4_0900_ai_ci;

USE fitness_ai;

-- 用户表。密码只保存 BCrypt/Argon2 哈希，不保存明文。
CREATE TABLE `user` (
    `id` BIGINT UNSIGNED NOT NULL COMMENT '用户ID，应用侧雪花ID',
    `username` VARCHAR(32) NOT NULL COMMENT '登录用户名',
    `password_hash` VARCHAR(100) NOT NULL COMMENT '密码哈希',
    `nickname` VARCHAR(50) NOT NULL COMMENT '用户昵称',
    `email` VARCHAR(128) DEFAULT NULL COMMENT '邮箱',
    `mobile` VARCHAR(32) DEFAULT NULL COMMENT '手机号，建议应用层加密',
    `avatar_url` VARCHAR(512) DEFAULT NULL COMMENT '头像地址',
    `gender` TINYINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '性别：0未知，1男，2女，3其他',
    `status` TINYINT UNSIGNED NOT NULL DEFAULT 1 COMMENT '状态：0禁用，1正常，2锁定',
    `last_login_at` DATETIME(3) DEFAULT NULL COMMENT '最后登录时间',
    `created_at` DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
    `updated_at` DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '更新时间',
    `deleted` TINYINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '逻辑删除：0否，1是',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_username` (`username`),
    UNIQUE KEY `uk_user_email` (`email`),
    UNIQUE KEY `uk_user_mobile` (`mobile`),
    KEY `idx_user_status_created` (`status`, `created_at`, `id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户账户';

CREATE TABLE `role` (
    `id` BIGINT UNSIGNED NOT NULL COMMENT '角色ID',
    `code` VARCHAR(32) NOT NULL COMMENT '角色编码：MEMBER、EDITOR、ADMIN',
    `name` VARCHAR(64) NOT NULL COMMENT '角色名称',
    `status` TINYINT UNSIGNED NOT NULL DEFAULT 1 COMMENT '状态：0停用，1启用',
    `created_at` DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
    `updated_at` DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_role_code` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='系统角色';

CREATE TABLE `permission` (
    `id` BIGINT UNSIGNED NOT NULL COMMENT '权限ID',
    `code` VARCHAR(100) NOT NULL COMMENT '权限编码，如article:create',
    `name` VARCHAR(100) NOT NULL COMMENT '权限名称',
    `created_at` DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_permission_code` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='系统权限';

CREATE TABLE `user_role_relation` (
    `user_id` BIGINT UNSIGNED NOT NULL COMMENT '用户ID',
    `role_id` BIGINT UNSIGNED NOT NULL COMMENT '角色ID',
    `created_at` DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '授权时间',
    PRIMARY KEY (`user_id`, `role_id`),
    KEY `idx_user_role_role` (`role_id`, `user_id`),
    CONSTRAINT `fk_user_role_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`),
    CONSTRAINT `fk_user_role_role` FOREIGN KEY (`role_id`) REFERENCES `role` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户角色关系';

CREATE TABLE `role_permission_relation` (
    `role_id` BIGINT UNSIGNED NOT NULL COMMENT '角色ID',
    `permission_id` BIGINT UNSIGNED NOT NULL COMMENT '权限ID',
    `created_at` DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '授权时间',
    PRIMARY KEY (`role_id`, `permission_id`),
    KEY `idx_role_permission_permission` (`permission_id`, `role_id`),
    CONSTRAINT `fk_role_permission_role` FOREIGN KEY (`role_id`) REFERENCES `role` (`id`),
    CONSTRAINT `fk_role_permission_permission` FOREIGN KEY (`permission_id`) REFERENCES `permission` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='角色权限关系';

-- 文章分类，使用邻接表支持多级分类。
CREATE TABLE `article_category` (
    `id` BIGINT UNSIGNED NOT NULL COMMENT '分类ID',
    `parent_id` BIGINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '父分类ID，0表示根分类',
    `name` VARCHAR(64) NOT NULL COMMENT '分类名称',
    `slug` VARCHAR(80) NOT NULL COMMENT 'URL友好标识',
    `description` VARCHAR(500) DEFAULT NULL COMMENT '分类说明',
    `icon_url` VARCHAR(512) DEFAULT NULL COMMENT '分类图标',
    `sort_order` INT NOT NULL DEFAULT 0 COMMENT '排序值，越小越靠前',
    `status` TINYINT UNSIGNED NOT NULL DEFAULT 1 COMMENT '状态：0停用，1启用',
    `created_at` DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
    `updated_at` DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '更新时间',
    `deleted` TINYINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_article_category_slug` (`slug`),
    KEY `idx_category_parent_status_sort` (`parent_id`, `status`, `sort_order`, `id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='文章分类';

CREATE TABLE `article_tag` (
    `id` BIGINT UNSIGNED NOT NULL COMMENT '标签ID',
    `name` VARCHAR(64) NOT NULL COMMENT '标签名称',
    `slug` VARCHAR(80) NOT NULL COMMENT 'URL友好标识',
    `usage_count` BIGINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '使用次数，异步维护',
    `created_at` DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
    `updated_at` DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '更新时间',
    `deleted` TINYINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_article_tag_name` (`name`),
    UNIQUE KEY `uk_article_tag_slug` (`slug`),
    KEY `idx_article_tag_usage` (`usage_count`, `id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='文章标签';

-- 正文使用 LONGTEXT；列表查询禁止 SELECT *，避免读取正文造成大页和回表开销。
CREATE TABLE `article` (
    `id` BIGINT UNSIGNED NOT NULL COMMENT '文章ID',
    `category_id` BIGINT UNSIGNED NOT NULL COMMENT '分类ID',
    `author_id` BIGINT UNSIGNED NOT NULL COMMENT '作者用户ID',
    `title` VARCHAR(200) NOT NULL COMMENT '文章标题',
    `summary` VARCHAR(600) DEFAULT NULL COMMENT '文章摘要',
    `content` LONGTEXT NOT NULL COMMENT '文章正文，HTML或Markdown',
    `cover_url` VARCHAR(512) DEFAULT NULL COMMENT '封面地址',
    `status` TINYINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '状态：0草稿，1待审核，2已发布，3已拒绝，4已下线',
    `is_recommended` TINYINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '是否推荐：0否，1是',
    `recommend_weight` INT NOT NULL DEFAULT 0 COMMENT '推荐权重',
    `view_count` BIGINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '浏览量，建议Redis累加后回写',
    `like_count` BIGINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '点赞数',
    `favorite_count` BIGINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '收藏数',
    `published_at` DATETIME(3) DEFAULT NULL COMMENT '发布时间',
    `created_at` DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
    `updated_at` DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '更新时间',
    `deleted` TINYINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (`id`),
    KEY `idx_article_category_publish` (`category_id`, `status`, `published_at` DESC, `id` DESC),
    KEY `idx_article_latest` (`status`, `published_at` DESC, `id` DESC),
    KEY `idx_article_hot` (`status`, `view_count` DESC, `id` DESC),
    KEY `idx_article_recommend` (`status`, `is_recommended`, `recommend_weight` DESC, `published_at` DESC, `id` DESC),
    KEY `idx_article_author_created` (`author_id`, `created_at` DESC, `id` DESC),
    CONSTRAINT `fk_article_category` FOREIGN KEY (`category_id`) REFERENCES `article_category` (`id`),
    CONSTRAINT `fk_article_author` FOREIGN KEY (`author_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='健身文章';

CREATE TABLE `article_tag_relation` (
    `article_id` BIGINT UNSIGNED NOT NULL COMMENT '文章ID',
    `tag_id` BIGINT UNSIGNED NOT NULL COMMENT '标签ID',
    `created_at` DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
    PRIMARY KEY (`article_id`, `tag_id`),
    KEY `idx_article_tag_relation_tag` (`tag_id`, `article_id`),
    CONSTRAINT `fk_tag_relation_article` FOREIGN KEY (`article_id`) REFERENCES `article` (`id`),
    CONSTRAINT `fk_tag_relation_tag` FOREIGN KEY (`tag_id`) REFERENCES `article_tag` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='文章标签关系';

CREATE TABLE `fitness_video` (
    `id` BIGINT UNSIGNED NOT NULL COMMENT '视频ID',
    `category_id` BIGINT UNSIGNED DEFAULT NULL COMMENT '关联文章分类ID',
    `author_id` BIGINT UNSIGNED NOT NULL COMMENT '发布者ID',
    `title` VARCHAR(200) NOT NULL COMMENT '视频标题',
    `summary` VARCHAR(600) DEFAULT NULL COMMENT '视频简介',
    `video_url` VARCHAR(512) NOT NULL COMMENT '视频播放地址或媒体资源键',
    `cover_url` VARCHAR(512) NOT NULL COMMENT '视频封面地址',
    `duration_seconds` INT UNSIGNED NOT NULL DEFAULT 0 COMMENT '时长，单位秒',
    `status` TINYINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '状态：0处理中，1待审核，2已发布，3失败，4下线',
    `is_recommended` TINYINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '是否推荐',
    `view_count` BIGINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '播放量',
    `published_at` DATETIME(3) DEFAULT NULL COMMENT '发布时间',
    `created_at` DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
    `updated_at` DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '更新时间',
    `deleted` TINYINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (`id`),
    KEY `idx_video_latest` (`status`, `published_at` DESC, `id` DESC),
    KEY `idx_video_category_publish` (`category_id`, `status`, `published_at` DESC, `id` DESC),
    KEY `idx_video_hot` (`status`, `view_count` DESC, `id` DESC),
    KEY `idx_video_recommend` (`status`, `is_recommended`, `published_at` DESC, `id` DESC),
    KEY `idx_video_author_created` (`author_id`, `created_at` DESC, `id` DESC)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='健身视频';

CREATE TABLE `training_plan` (
    `id` BIGINT UNSIGNED NOT NULL COMMENT '训练计划ID',
    `creator_id` BIGINT UNSIGNED NOT NULL COMMENT '创建者ID',
    `name` VARCHAR(160) NOT NULL COMMENT '计划名称',
    `description` TEXT COMMENT '计划说明',
    `cover_url` VARCHAR(512) DEFAULT NULL COMMENT '封面地址',
    `goal_type` VARCHAR(32) NOT NULL COMMENT '目标：增肌、减脂、塑形、体能等',
    `difficulty` TINYINT UNSIGNED NOT NULL DEFAULT 1 COMMENT '难度：1初级，2中级，3高级',
    `duration_weeks` SMALLINT UNSIGNED NOT NULL COMMENT '持续周数',
    `days_per_week` TINYINT UNSIGNED NOT NULL COMMENT '每周训练天数',
    `plan_content` JSON NOT NULL COMMENT '首期训练阶段与动作编排；稳定后拆分子表',
    `status` TINYINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '状态：0草稿，1发布，2下线',
    `is_recommended` TINYINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '是否推荐',
    `enroll_count` BIGINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '参与人数',
    `published_at` DATETIME(3) DEFAULT NULL COMMENT '发布时间',
    `created_at` DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
    `updated_at` DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '更新时间',
    `deleted` TINYINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (`id`),
    KEY `idx_training_goal` (`status`, `goal_type`, `difficulty`, `published_at` DESC, `id` DESC),
    KEY `idx_training_recommend` (`status`, `is_recommended`, `enroll_count` DESC, `id` DESC),
    KEY `idx_training_creator` (`creator_id`, `created_at` DESC, `id` DESC)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='健身训练计划';

CREATE TABLE `nutrition_plan` (
    `id` BIGINT UNSIGNED NOT NULL COMMENT '营养计划ID',
    `creator_id` BIGINT UNSIGNED NOT NULL COMMENT '创建者ID',
    `name` VARCHAR(160) NOT NULL COMMENT '计划名称',
    `description` TEXT COMMENT '计划说明',
    `goal_type` VARCHAR(32) NOT NULL COMMENT '目标：增肌、减脂、维持等',
    `daily_calories` INT UNSIGNED DEFAULT NULL COMMENT '每日参考热量，kcal',
    `protein_grams` DECIMAL(8,2) DEFAULT NULL COMMENT '每日蛋白质，克',
    `carbohydrate_grams` DECIMAL(8,2) DEFAULT NULL COMMENT '每日碳水，克',
    `fat_grams` DECIMAL(8,2) DEFAULT NULL COMMENT '每日脂肪，克',
    `plan_content` JSON NOT NULL COMMENT '餐次和食物编排；稳定后拆分子表',
    `status` TINYINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '状态：0草稿，1发布，2下线',
    `is_recommended` TINYINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '是否推荐',
    `published_at` DATETIME(3) DEFAULT NULL COMMENT '发布时间',
    `created_at` DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
    `updated_at` DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '更新时间',
    `deleted` TINYINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (`id`),
    KEY `idx_nutrition_goal` (`status`, `goal_type`, `published_at` DESC, `id` DESC),
    KEY `idx_nutrition_recommend` (`status`, `is_recommended`, `published_at` DESC, `id` DESC),
    KEY `idx_nutrition_creator` (`creator_id`, `created_at` DESC, `id` DESC)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='营养计划';

CREATE TABLE `fitness_equipment` (
    `id` BIGINT UNSIGNED NOT NULL COMMENT '器材ID',
    `name` VARCHAR(160) NOT NULL COMMENT '器材名称',
    `brand` VARCHAR(100) DEFAULT NULL COMMENT '品牌',
    `category_code` VARCHAR(50) NOT NULL COMMENT '器材分类编码',
    `summary` VARCHAR(600) DEFAULT NULL COMMENT '器材简介',
    `description` LONGTEXT COMMENT '详细说明与使用方法',
    `cover_url` VARCHAR(512) DEFAULT NULL COMMENT '封面地址',
    `target_muscles` JSON DEFAULT NULL COMMENT '适用肌群编码数组',
    `difficulty` TINYINT UNSIGNED NOT NULL DEFAULT 1 COMMENT '使用难度：1初级，2中级，3高级',
    `status` TINYINT UNSIGNED NOT NULL DEFAULT 1 COMMENT '状态：0停用，1启用',
    `is_recommended` TINYINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '是否推荐',
    `created_at` DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
    `updated_at` DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '更新时间',
    `deleted` TINYINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (`id`),
    KEY `idx_equipment_category` (`status`, `category_code`, `id` DESC),
    KEY `idx_equipment_brand` (`brand`, `status`, `id` DESC),
    KEY `idx_equipment_recommend` (`status`, `is_recommended`, `id` DESC)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='健身器材';

-- 一行保存一条消息，而不是一行保存整段对话，便于流式写入和游标分页。
CREATE TABLE `ai_chat_history` (
    `id` BIGINT UNSIGNED NOT NULL COMMENT '消息ID，建议雪花ID并按时间递增',
    `conversation_id` BIGINT UNSIGNED NOT NULL COMMENT '会话ID',
    `user_id` BIGINT UNSIGNED NOT NULL COMMENT '用户ID，也是未来分片键候选',
    `role` VARCHAR(16) NOT NULL COMMENT '消息角色：SYSTEM、USER、ASSISTANT、TOOL',
    `content` MEDIUMTEXT NOT NULL COMMENT '消息内容',
    `model_name` VARCHAR(80) DEFAULT NULL COMMENT '生成模型名称',
    `prompt_tokens` INT UNSIGNED NOT NULL DEFAULT 0 COMMENT '输入Token数',
    `completion_tokens` INT UNSIGNED NOT NULL DEFAULT 0 COMMENT '输出Token数',
    `latency_ms` INT UNSIGNED DEFAULT NULL COMMENT '模型调用耗时毫秒',
    `status` TINYINT UNSIGNED NOT NULL DEFAULT 1 COMMENT '状态：0生成中，1完成，2失败，3拦截',
    `metadata` JSON DEFAULT NULL COMMENT '引用、工具调用、安全标签等扩展数据',
    `created_at` DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_chat_conversation_cursor` (`conversation_id`, `id`),
    KEY `idx_chat_user_time` (`user_id`, `created_at` DESC, `id` DESC),
    KEY `idx_chat_created` (`created_at`, `id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='AI聊天消息历史';

CREATE TABLE `knowledge_document` (
    `id` BIGINT UNSIGNED NOT NULL COMMENT '知识文档ID',
    `knowledge_base_id` BIGINT UNSIGNED NOT NULL DEFAULT 1 COMMENT '知识库ID，未来支持多知识库',
    `source_type` VARCHAR(32) NOT NULL COMMENT '来源类型：ARTICLE、FILE、URL、MANUAL',
    `source_id` BIGINT UNSIGNED DEFAULT NULL COMMENT '站内来源业务ID',
    `title` VARCHAR(300) NOT NULL COMMENT '文档标题',
    `source_url` VARCHAR(1000) DEFAULT NULL COMMENT '来源地址',
    `content_hash` CHAR(64) NOT NULL COMMENT '规范化正文SHA-256，用于去重',
    `version` INT UNSIGNED NOT NULL DEFAULT 1 COMMENT '文档版本',
    `status` TINYINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '状态：0待处理，1处理中，2可用，3失败，4停用',
    `chunk_count` INT UNSIGNED NOT NULL DEFAULT 0 COMMENT '分块数量',
    `error_message` VARCHAR(1000) DEFAULT NULL COMMENT '处理失败原因',
    `metadata` JSON DEFAULT NULL COMMENT '作者、栏目、权限等元数据',
    `created_at` DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
    `updated_at` DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '更新时间',
    `deleted` TINYINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_knowledge_doc_hash_version` (`knowledge_base_id`, `content_hash`, `version`),
    KEY `idx_knowledge_doc_source` (`source_type`, `source_id`),
    KEY `idx_knowledge_doc_status` (`knowledge_base_id`, `status`, `updated_at`, `id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='RAG知识文档';

-- 向量本体存向量数据库；MySQL保存文本、定位信息及vector_id映射。
CREATE TABLE `knowledge_chunk` (
    `id` BIGINT UNSIGNED NOT NULL COMMENT '知识分块ID',
    `document_id` BIGINT UNSIGNED NOT NULL COMMENT '知识文档ID',
    `knowledge_base_id` BIGINT UNSIGNED NOT NULL DEFAULT 1 COMMENT '知识库ID，冗余以支持过滤和分片',
    `chunk_index` INT UNSIGNED NOT NULL COMMENT '文档内分块序号，从0开始',
    `content` MEDIUMTEXT NOT NULL COMMENT '分块文本',
    `content_hash` CHAR(64) NOT NULL COMMENT '分块文本SHA-256',
    `token_count` INT UNSIGNED NOT NULL DEFAULT 0 COMMENT '分块Token数',
    `vector_id` VARCHAR(128) DEFAULT NULL COMMENT '向量数据库记录ID',
    `embedding_model` VARCHAR(100) DEFAULT NULL COMMENT '向量模型名称',
    `embedding_dimension` SMALLINT UNSIGNED DEFAULT NULL COMMENT '向量维度',
    `status` TINYINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '状态：0待向量化，1可用，2失败，3停用',
    `metadata` JSON DEFAULT NULL COMMENT '标题路径、页码、权限等检索过滤信息',
    `created_at` DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
    `updated_at` DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_knowledge_chunk_position` (`document_id`, `chunk_index`),
    UNIQUE KEY `uk_knowledge_chunk_vector` (`vector_id`),
    KEY `idx_knowledge_chunk_ingestion` (`knowledge_base_id`, `status`, `id`),
    KEY `idx_knowledge_chunk_hash` (`content_hash`),
    CONSTRAINT `fk_knowledge_chunk_document` FOREIGN KEY (`document_id`) REFERENCES `knowledge_document` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='RAG知识分块';

-- 初始分类数据，可按环境决定是否执行。
INSERT INTO `article_category`
(`id`, `parent_id`, `name`, `slug`, `sort_order`, `status`)
VALUES
(101, 0, '健身文章', 'fitness-article', 10, 1),
(102, 0, '健身营养', 'fitness-nutrition', 20, 1),
(103, 0, '健身器材', 'fitness-equipment', 30, 1);
