package com.fitnessai.platform.devseed;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.startsWith;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fitnessai.platform.content.entity.Article;
import com.fitnessai.platform.content.entity.Category;
import com.fitnessai.platform.content.entity.Tag;
import com.fitnessai.platform.content.mapper.ArticleMapper;
import com.fitnessai.platform.content.mapper.CategoryMapper;
import com.fitnessai.platform.content.mapper.TagMapper;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;

@ExtendWith(MockitoExtension.class)
class DevHomeContentSeedServiceTest {
    @Mock private ArticleMapper articleMapper;
    @Mock private CategoryMapper categoryMapper;
    @Mock private TagMapper tagMapper;
    @Mock private JdbcTemplate jdbc;

    @Test
    void seedsExpectedContentAndDoesNotDuplicateEntitiesOnSecondRun() {
        List<Tag> insertedTags = new ArrayList<>();
        List<Article> insertedArticles = new ArrayList<>();
        AtomicInteger tagSelectCalls = new AtomicInteger();
        stubCategories();
        when(tagMapper.selectOne(any())).thenAnswer(invocation -> {
            int call = tagSelectCalls.getAndIncrement();
            return call < 6 ? null : insertedTags.get(call % 6);
        });
        doAnswer(invocation -> {
            Tag tag = invocation.getArgument(0);
            tag.setId(1_000L + insertedTags.size());
            insertedTags.add(tag);
            return 1;
        }).when(tagMapper).insert(any(Tag.class));
        when(articleMapper.selectList(any())).thenAnswer(invocation -> List.copyOf(insertedArticles));
        doAnswer(invocation -> {
            Article article = invocation.getArgument(0);
            article.setId(2_000L + insertedArticles.size());
            insertedArticles.add(article);
            return 1;
        }).when(articleMapper).insert(any(Article.class));

        var first = service().seed();
        var second = service().seed();

        assertThat(first).isEqualTo(new DevHomeContentSeedService.SeedResult(6, 6, 10));
        assertThat(second).isEqualTo(new DevHomeContentSeedService.SeedResult(0, 0, 10));
        assertThat(insertedArticles).hasSize(6);
        assertThat(insertedArticles).filteredOn(article -> "PUBLISHED".equals(article.getStatus())).hasSize(5);
        assertThat(insertedArticles).filteredOn(article -> "DRAFT".equals(article.getStatus())).hasSize(1);
        assertThat(insertedArticles).anyMatch(article -> article.getSummary() == null);
        assertThat(insertedArticles).anyMatch(article -> article.getCoverUrl() == null);
        assertThat(insertedArticles).anyMatch(article -> article.getCoverUrl() != null);
        assertThat(insertedArticles).allMatch(article ->
                article.getContent().startsWith(DevHomeContentSeedService.MARKER_PREFIX));
        verify(articleMapper, times(6)).insert(any(Article.class));
        verify(tagMapper, times(6)).insert(any(Tag.class));
        verify(jdbc, times(20)).update(startsWith("INSERT IGNORE"), anyLong(), anyLong());
    }

    @Test
    void cleanupTargetsOnlyMarkedArticlesAndUnreferencedDevTags() {
        when(jdbc.update(startsWith("DELETE article_tag"), any(String.class))).thenReturn(12);
        when(jdbc.update(startsWith("DELETE FROM cms_article"), any(String.class))).thenReturn(6);
        when(jdbc.update(startsWith("DELETE tag"), any(String.class))).thenReturn(6);

        var result = service().cleanup();

        assertThat(result).isEqualTo(new DevHomeContentSeedService.CleanupResult(6, 6));
        verify(jdbc).update(startsWith("DELETE FROM cms_article"),
                org.mockito.ArgumentMatchers.eq(DevHomeContentSeedService.MARKER_PREFIX + "%"));
        verify(jdbc).update(startsWith("DELETE tag"),
                org.mockito.ArgumentMatchers.eq(DevHomeContentSeedService.TAG_SLUG_PREFIX + "%"));
    }

    private DevHomeContentSeedService service() {
        return new DevHomeContentSeedService(articleMapper, categoryMapper, tagMapper, jdbc);
    }

    private void stubCategories() {
        when(categoryMapper.selectById(anyLong())).thenAnswer(invocation -> {
            long id = invocation.getArgument(0);
            Category category = new Category();
            category.setId(id);
            category.setEnabled(1);
            category.setSlug(switch ((int) id) {
                case 101 -> "fitness";
                case 102 -> "nutrition";
                case 103 -> "equipment";
                default -> "unknown";
            });
            return category;
        });
    }
}
