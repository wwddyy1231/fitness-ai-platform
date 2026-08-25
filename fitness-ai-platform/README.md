# fitness-ai-platform

Java 21、Spring Boot 3.4、MyBatis Plus、MySQL 与 Redis 构建的健身资讯平台后端骨架。

## 本批次包含

- 用户注册、登录、JWT 与角色鉴权
- 文章分类、标签和文章 CRUD
- 最新、热门、推荐及首页聚合接口
- 图片与视频封面本地上传
- DTO/VO、统一响应、参数校验和全局异常处理
- Flyway 初始化数据库
- LangChain4j AI 健身助手、OpenAI 兼容模型和站内知识 RAG

## 本地启动

1. 创建 MySQL 数据库：`CREATE DATABASE fitness_ai CHARACTER SET utf8mb4;`
2. 启动 Redis。
3. 配置 `DB_USERNAME`、`DB_PASSWORD` 和至少 32 字节的 `JWT_SECRET`。
4. 执行：`mvn spring-boot:run`。

## AI 配置

至少设置：

```text
AI_API_KEY=你的模型密钥
AI_BASE_URL=https://api.openai.com/v1
AI_CHAT_MODEL=gpt-4o-mini
AI_EMBEDDING_MODEL=text-embedding-3-small
```

聊天接口为 `POST /api/ai/chat`，请求体示例：

```json
{"userId": 123, "message": "请为我制定一套减脂训练计划"}
```

首版 RAG 使用进程内向量索引；首次聊天时从已发布文章、训练计划和营养计划构建索引。管理员可调用
`POST /api/ai/knowledge/refresh` 刷新。生产环境应将 `EmbeddingStore` Bean 替换为 Qdrant、Milvus 等持久化向量库。

普通用户注册后默认获得 `MEMBER` 角色。首个 `ADMIN`/`EDITOR` 的创建应通过部署初始化脚本或后续管理员能力完成，不提供公开提权接口。
