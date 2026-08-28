# Fitness AI Platform Repository Guide

## Scope and source of truth

- This file applies to the entire repository rooted at `C:\Users\Administrator\Desktop\web`.
- The product goal is a fitness information portal, an AI fitness assistant, and a fitness knowledge base.
- Treat checked-in code, configuration, migrations, and tests as the source of truth. Inspect them before making decisions; do not assume a module, endpoint, table, field, or frontend feature exists merely because it appears in a plan or architecture diagram.
- The implemented backend is under `fitness-ai-platform/`. The planned frontend location is `frontend/`, but that directory does not currently exist.
- Files under `docs/` describe architecture and may include generated visualization artifacts. They are context, not proof that a described component has been implemented.
- Keep changes scoped to the user's request. Do not delete unrelated code, overwrite uncommitted work, or perform broad refactors without explicit approval.

## Current repository reality

The backend currently contains these code areas:

- `common`: the shared API envelope, pagination response, exceptions, global exception handling, and MyBatis Plus persistence support.
- `user`: registration, login, users, roles, password hashing, and JWT issuance.
- `security`: stateless Spring Security, JWT parsing, authentication filtering, and method-level authorization.
- `content`: article, category, and tag DTOs, entities, mappers, services, and REST endpoints.
- `home`: portal home aggregation endpoints.
- `file`: local image and video-cover upload plus public file resource mapping.
- `ai`: LangChain4j model configuration, chat history, fitness assistant service, and in-process RAG over published content.

Do not describe unimplemented entities from `database/fitness_ai_schema.sql` or architecture documents as working application modules. Verify corresponding Java code and Flyway migrations first.

## Backend technology and conventions

- Java 21, Spring Boot 3.4.x, and Maven.
- MyBatis Plus with MySQL 8; Redis is configured through Spring Data Redis.
- Flyway owns executable schema evolution.
- Spring Security is stateless and uses JWT bearer authentication.
- LangChain4j integrates with an OpenAI-compatible chat and embedding provider.
- HTTP interfaces are REST APIs.

Preserve clear Controller / Service / Mapper layering:

- Controllers handle HTTP binding, validation, status codes, authorization annotations, and delegation only. Do not put complex business logic in controllers.
- Services own business rules and transaction boundaries.
- Mappers and repository classes own persistence queries. Review index use, query counts, pagination, and N+1 risks when changing reads.
- Keep DTO, VO, and Entity types separate. Do not expose persistence entities as API contracts.
- Use constructor injection and follow the package conventions already present under `com.fitnessai.platform`.
- Use Jakarta Bean Validation on request DTOs and request parameters. Keep validation errors flowing through `GlobalExceptionHandler`.
- Use `BusinessException` for expected business failures and preserve the existing error-code approach unless a task explicitly changes the contract.

## API contracts and security

- Successful and failed JSON responses use `ApiResponse<T>` with `code`, `message`, and `data`. Do not introduce a competing response envelope.
- Paginated endpoints use `PageResponse<T>` with `records`, `total`, `page`, and `size`.
- Existing public business routes primarily use `/api/v1/**`; current AI routes use `/api/ai/**`. Do not silently rename or normalize them. Coordinate and test any contract change across backend and frontend.
- Keep authentication and authorization aligned with `SecurityConfig` and method annotations such as `@PreAuthorize`.
- Never weaken authorization to make a client flow easier. Do not trust client-supplied identity without comparing it to the authenticated principal.
- Passwords must remain one-way hashed. Never log passwords, JWTs, API keys, authorization headers, or other credentials.
- Preserve the current stateless security model unless the requested feature deliberately changes it.

## Database and persistence

- Add every schema or seed-data change as a new, ordered migration under `fitness-ai-platform/src/main/resources/db/migration/`. Never edit an already-applied migration to retrofit a new change.
- Do not bypass Flyway with runtime DDL, manual-only database instructions, or ad hoc schema initialization.
- The active migrations currently use tables such as `sys_user`, `sys_role`, `cms_article`, and `ai_chat_history`. `fitness-ai-platform/database/fitness_ai_schema.sql` uses a different broader schema and is not a substitute for the active Flyway history.
- Confirm table and column names against both the relevant migration and mapped entity/query before editing persistence code.
- Keep MyBatis Plus logical-delete and audit-field behavior consistent with `BaseEntity`, `AuditMetaObjectHandler`, and `application.yml`.
- Use transactions for multi-write operations. Avoid per-row queries in loops; batch or join when query volume can grow.
- When adding a query, review realistic filters, sort order, cardinality, and required indexes. Do not add indexes blindly.

## Configuration and secrets

- Secrets, passwords, tokens, and private endpoints must not be committed to source control.
- Sensitive `application.yml` values must use environment-variable placeholders. Do not add usable secret defaults.
- Do not commit private `.env` files. Update ignore rules or safe example files only when the task requires them.
- Keep storage paths, database settings, Redis settings, JWT settings, model names, API base URLs, and AI tuning externally configurable.
- Treat current development defaults as development conveniences, not production-safe values.

## LangChain4j and RAG

- Keep AI concerns decoupled from ordinary content, user, and portal business logic.
- Keep substantial prompts in `fitness-ai-platform/src/main/resources/prompts/`; do not hard-code large prompts in Java classes.
- Keep the LLM provider behind injected LangChain4j interfaces/configuration rather than coupling business services to a vendor SDK.
- Configure bounded connection/request timeouts for external model calls and handle provider failures without exposing sensitive details.
- Provide a deliberate degraded behavior when the model or embedding service is unavailable; preserve useful non-AI platform behavior.
- Keep retrieval/indexing separate from answer generation. Preserve source metadata so generated answers can expose traceable knowledge references.
- The current embedding store is in-memory and lazily indexed from published articles, training plans, and nutrition plans. Do not describe it as persistent or distributed.
- Consider concurrency, refresh behavior, data volume, model cost, prompt injection, unsafe fitness advice, and health disclaimers when extending AI flows.
- Never commit an API key or place one in a prompt, test fixture, log, or example response.

## Frontend plan and required skill

The planned frontend stack is Vue 3, TypeScript, Vite, Element Plus, Vue Router, Pinia, and Axios under `frontend/`.

For every frontend implementation, review, configuration, or troubleshooting task, first read and follow:

`C:\Users\Administrator\.agents\skills\element-plus-vue3-skill\SKILL.md`

Then inspect the actual `frontend/` files and installed versions. Follow the lockfile and established project conventions; do not assume the planned stack has already been scaffolded.

Frontend rules:

- Use the Composition API and `<script setup lang="ts">`. Do not add Vue 2 Options API patterns.
- Define TypeScript types for request parameters, API envelopes, pagination, domain data, and component-facing models. Avoid `any` unless a documented boundary makes it unavoidable.
- Separate route-level pages, domain components, and reusable base components. Split oversized single-file components by cohesive responsibility.
- Centralize Axios creation, base URL, authentication headers, response-envelope handling, error normalization, and cancellation behavior. Do not scatter direct Axios calls through Vue components.
- Manage routes centrally with Vue Router. Use Pinia for state shared across pages; keep page-local state local.
- Match the backend's real `ApiResponse<T>` and `PageResponse<T>` contracts. Do not modify backend APIs merely to simplify frontend implementation.
- Represent loading, empty, error, permission-denied, disabled, and retry states where applicable.
- Use lazy loading and appropriately sized assets for non-critical images. Protect Core Web Vitals by controlling layout dimensions, bundle size, and initial data work.
- Implement keyboard access, visible focus, semantic structure, labels, useful alternative text, and sufficient contrast.
- Design desktop-first while verifying tablet and mobile layouts. Prevent overflow, overlap, and unreadable control labels.
- Avoid large dependencies without a demonstrated need; prefer existing project and browser capabilities.

## UI and UX direction

- This is a modern, content-first fitness portal, not a conventional ERP admin dashboard.
- The visual language should communicate fitness, strength, and movement while remaining information-dense and easy to scan.
- The home page prioritizes content discovery. The AI fitness assistant must have a prominent, coherent entry point.
- Use Element Plus primarily for robust interaction behavior. Do not let its default admin-template appearance define the site's brand or page composition.
- Establish shared design tokens for color, typography, spacing, radii, shadows, motion, and responsive breakpoints.
- Keep navigation and content hierarchy predictable across portal, knowledge, account, and AI experiences as those areas are implemented.
- Avoid decorative UI that competes with article discovery, source credibility, training information, or assistant responses.

## Tests and validation

- Before editing, run `git status --short` and preserve existing user changes.
- Read the relevant implementation, adjacent types, configuration, migrations, and tests before changing behavior. Search for an existing module or abstraction before creating one.
- For substantial tasks, present an implementation plan before editing.
- After backend changes, run the narrowest relevant tests and normally run `mvn test` from `fitness-ai-platform/`. Use additional targeted tests for security, persistence, validation, and API contract changes.
- The current test suite is only a skeleton and does not start a database-backed Spring context. Add focused coverage in proportion to the risk; do not claim integration coverage that does not exist.
- After frontend creation, use the package manager selected by its lockfile and run the available typecheck, unit tests, lint, and production build relevant to the change.
- If validation cannot run or fails, report the exact command, failure, and likely cause. Do not hide failures or imply success from compilation alone.

## Working and Git rules

For each task:

1. Read relevant code and understand the existing implementation.
2. Check whether the corresponding module, helper, endpoint, migration, or component already exists.
3. For a large task, state an implementation plan before editing.
4. Modify only files needed for the task.
5. Do not remove unrelated code or perform an unsolicited large refactor.
6. Run appropriate build, test, typecheck, or lint commands after changes.
7. Report failed or unavailable validation with its reason.
8. Finish with a list of changed files and validation results.

Additional Git constraints:

- Never force-push or rewrite history without explicit user authorization.
- Do not automatically commit or push unless the user requests it.
- Keep commits single-purpose when commits are requested.
- Never overwrite or revert the user's uncommitted changes.
- Never commit passwords, tokens, API keys, private `.env` files, local uploads, logs, IDE metadata, or build output.
