package com.fitnessai.platform.ai.rag;

import java.util.ArrayList;
import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class KnowledgeSourceRepository {
    private final JdbcTemplate jdbc;
    public KnowledgeSourceRepository(JdbcTemplate jdbc) { this.jdbc = jdbc; }

    public List<KnowledgeSource> findPublishedSources() {
        List<KnowledgeSource> result = new ArrayList<>();
        result.addAll(jdbc.query("""
                select id, title, concat_ws('\n', summary, content) body
                from cms_article where status = 'PUBLISHED' and deleted = 0
                """, (rs, n) -> new KnowledgeSource("ARTICLE", rs.getString("id"), rs.getString("title"), rs.getString("body"))));
        result.addAll(jdbc.query("""
                select id, name title, concat_ws('\n', description, plan_content) body
                from training_plan where status = 1 and deleted = 0
                """, (rs, n) -> new KnowledgeSource("TRAINING_PLAN", rs.getString("id"), rs.getString("title"), rs.getString("body"))));
        result.addAll(jdbc.query("""
                select id, name title, concat_ws('\n', description, plan_content) body
                from nutrition_plan where status = 1 and deleted = 0
                """, (rs, n) -> new KnowledgeSource("NUTRITION_PLAN", rs.getString("id"), rs.getString("title"), rs.getString("body"))));
        return result;
    }

    public record KnowledgeSource(String type, String id, String title, String content) {}
}
