package com.fitnessai.platform.devseed;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fitnessai.platform.content.entity.Article;
import com.fitnessai.platform.content.entity.Category;
import com.fitnessai.platform.content.entity.Tag;
import com.fitnessai.platform.content.mapper.ArticleMapper;
import com.fitnessai.platform.content.mapper.CategoryMapper;
import com.fitnessai.platform.content.mapper.TagMapper;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Profile("dev")
public class DevHomeContentSeedService {
    static final String MARKER_PREFIX = "<!-- DEV_SEED:fitness-ai-home:v1:";
    static final String TAG_SLUG_PREFIX = "dev-seed-";

    private static final List<CategorySpec> REQUIRED_CATEGORIES = List.of(
            new CategorySpec(101L, "fitness"),
            new CategorySpec(102L, "nutrition"),
            new CategorySpec(103L, "equipment"));

    private static final List<TagSpec> TAGS = List.of(
            new TagSpec("squat", "[DEV] 深蹲"),
            new TagSpec("muscle", "[DEV] 增肌"),
            new TagSpec("fat-loss", "[DEV] 减脂"),
            new TagSpec("high-protein", "[DEV] 高蛋白"),
            new TagSpec("home-training", "[DEV] 居家训练"),
            new TagSpec("equipment", "[DEV] 器材"));

    private static final List<ArticleSpec> ARTICLES = List.of(
            new ArticleSpec("squat-basics", 101L, "深蹲训练基础：从稳定站姿开始",
                    "掌握站距、核心稳定与膝髋协同，建立可持续进阶的深蹲动作。",
                    "深蹲训练应先建立稳定站姿，再逐步增加负重。训练时保持足底三点支撑，并让膝盖方向与脚尖一致。",
                    "https://images.unsplash.com/photo-1534438327276-14e5300c3a48?auto=format&fit=crop&w=1600&q=80",
                    "PUBLISHED", true, 12_840L, LocalDateTime.of(2026, 8, 30, 9, 0),
                    List.of("squat", "muscle")),
            new ArticleSpec("beginner-muscle", 101L, "新手增肌计划：先稳定执行六周",
                    "用每周三次全身训练建立动作基础，并通过训练日志记录渐进超负荷。",
                    "新手阶段不需要复杂分化。选择深蹲、推、拉和髋主导动作，保证恢复后再增加重量或次数。",
                    null, "PUBLISHED", true, 8_420L, LocalDateTime.of(2026, 8, 29, 10, 0),
                    List.of("muscle")),
            new ArticleSpec("high-protein", 102L, "高蛋白饮食建议：把总量分配到每一餐",
                    "优先保证全天蛋白质总量，再根据训练时间安排容易执行的餐次。",
                    "从鸡蛋、奶制品、瘦肉、鱼类和豆制品中组合蛋白质来源，并同时保证蔬菜、主食和饮水。",
                    "https://images.unsplash.com/photo-1490645935967-10de6ba17061?auto=format&fit=crop&w=1600&q=80",
                    "PUBLISHED", true, 10_600L, LocalDateTime.of(2026, 8, 28, 11, 0),
                    List.of("high-protein", "muscle")),
            new ArticleSpec("fat-loss-myths", 102L, "减脂期饮食误区：不是吃得越少越好",
                    null,
                    "减脂需要稳定且适度的能量缺口。长期极低热量饮食会影响训练表现、恢复和计划执行。",
                    null, "PUBLISHED", false, 15_400L, LocalDateTime.of(2026, 8, 27, 8, 30),
                    List.of("fat-loss", "high-protein")),
            new ArticleSpec("home-kettlebell", 101L, "家庭壶铃训练：一只壶铃完成全身循环",
                    "以硬拉、杯式深蹲、划船和推举组成适合居家执行的训练循环。",
                    "先使用能够稳定控制的重量，每个动作保留两到三次余力，并在动作质量下降前结束一组。",
                    "https://images.unsplash.com/photo-1517836357463-d25dfeac3438?auto=format&fit=crop&w=1600&q=80",
                    "PUBLISHED", false, 9_200L, LocalDateTime.of(2026, 8, 26, 18, 0),
                    List.of("home-training", "equipment")),
            new ArticleSpec("equipment-guide-draft", 103L, "健身器材选择指南：先匹配训练场景",
                    "根据空间、预算和训练目标选择器材，避免为低频功能增加不必要成本。",
                    "器材选择应先确认常用动作、可用空间和维护成本，再比较承重、调节范围与售后服务。",
                    null, "DRAFT", true, 30_000L, LocalDateTime.of(2026, 8, 31, 9, 0),
                    List.of("equipment")));

    private final ArticleMapper articleMapper;
    private final CategoryMapper categoryMapper;
    private final TagMapper tagMapper;
    private final JdbcTemplate jdbc;

    public DevHomeContentSeedService(ArticleMapper articleMapper, CategoryMapper categoryMapper,
                                     TagMapper tagMapper, JdbcTemplate jdbc) {
        this.articleMapper = articleMapper;
        this.categoryMapper = categoryMapper;
        this.tagMapper = tagMapper;
        this.jdbc = jdbc;
    }

    @Transactional
    public SeedResult seed() {
        verifyCategories();
        Map<String, Long> tagIds = new HashMap<>();
        int tagsCreated = 0;
        for (TagSpec spec : TAGS) {
            Tag tag = tagMapper.selectOne(new LambdaQueryWrapper<Tag>().eq(Tag::getSlug, spec.slug()));
            if (tag == null) {
                tag = new Tag();
                tag.setName(spec.name());
                tag.setSlug(spec.slug());
                tagMapper.insert(tag);
                tagsCreated++;
            }
            tagIds.put(spec.key(), tag.getId());
        }

        Map<String, Article> existingByMarker = articleMapper.selectList(
                        new LambdaQueryWrapper<Article>().likeRight(Article::getContent, MARKER_PREFIX))
                .stream().collect(Collectors.toMap(this::extractMarker, article -> article));

        int articlesCreated = 0;
        int relationsEnsured = 0;
        for (ArticleSpec spec : ARTICLES) {
            Article article = existingByMarker.get(spec.marker());
            if (article == null) {
                article = toArticle(spec);
                articleMapper.insert(article);
                articlesCreated++;
            }
            for (String tagKey : spec.tagKeys()) {
                jdbc.update("INSERT IGNORE INTO cms_article_tag(article_id, tag_id) VALUES (?, ?)",
                        article.getId(), tagIds.get(tagKey));
                relationsEnsured++;
            }
        }
        return new SeedResult(articlesCreated, tagsCreated, relationsEnsured);
    }

    @Transactional
    public CleanupResult cleanup() {
        String articlePattern = MARKER_PREFIX + "%";
        jdbc.update("""
                DELETE article_tag FROM cms_article_tag article_tag
                JOIN cms_article article ON article.id = article_tag.article_id
                WHERE article.content LIKE ?
                """, articlePattern);
        int articlesDeleted = jdbc.update("DELETE FROM cms_article WHERE content LIKE ?", articlePattern);
        int tagsDeleted = jdbc.update("""
                DELETE tag FROM cms_tag tag
                WHERE tag.slug LIKE ?
                  AND NOT EXISTS (SELECT 1 FROM cms_article_tag article_tag WHERE article_tag.tag_id = tag.id)
                """, TAG_SLUG_PREFIX + "%");
        return new CleanupResult(articlesDeleted, tagsDeleted);
    }

    private void verifyCategories() {
        for (CategorySpec required : REQUIRED_CATEGORIES) {
            Category category = categoryMapper.selectById(required.id());
            if (category == null || !required.slug().equals(category.getSlug())
                    || !Integer.valueOf(1).equals(category.getEnabled())) {
                throw new IllegalStateException("Development seed requires enabled category " + required.id()
                        + " with slug " + required.slug());
            }
        }
    }

    private Article toArticle(ArticleSpec spec) {
        Article article = new Article();
        article.setCategoryId(spec.categoryId());
        article.setTitle(spec.title());
        article.setSummary(spec.summary());
        article.setContent(marker(spec.marker()) + System.lineSeparator() + spec.content());
        article.setCoverUrl(spec.coverUrl());
        article.setStatus(spec.status());
        article.setRecommended(spec.recommended() ? 1 : 0);
        article.setViewCount(spec.viewCount());
        article.setPublishedAt(spec.publishedAt());
        return article;
    }

    private String extractMarker(Article article) {
        int end = article.getContent().indexOf(" -->");
        return end < 0 ? "" : article.getContent().substring(MARKER_PREFIX.length(), end);
    }

    private String marker(String key) {
        return MARKER_PREFIX + key + " -->";
    }

    public record SeedResult(int articlesCreated, int tagsCreated, int relationsEnsured) {}
    public record CleanupResult(int articlesDeleted, int tagsDeleted) {}
    private record CategorySpec(Long id, String slug) {}
    private record TagSpec(String key, String name) {
        String slug() { return TAG_SLUG_PREFIX + key; }
    }
    private record ArticleSpec(String marker, Long categoryId, String title, String summary, String content,
                               String coverUrl, String status, boolean recommended, long viewCount,
                               LocalDateTime publishedAt, List<String> tagKeys) {}
}
