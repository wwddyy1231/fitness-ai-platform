package com.fitnessai.platform.content.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fitnessai.platform.common.exception.BusinessException;
import com.fitnessai.platform.content.entity.Article;
import com.fitnessai.platform.content.mapper.ArticleMapper;
import com.fitnessai.platform.content.mapper.CategoryMapper;
import com.fitnessai.platform.content.mapper.TagMapper;
import com.fitnessai.platform.content.mapper.TagMapper.ArticleTagRow;
import java.util.List;
import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

@ExtendWith(MockitoExtension.class)
class ArticleServiceTest {
    @Mock private ArticleMapper articleMapper;
    @Mock private CategoryMapper categoryMapper;
    @Mock private TagMapper tagMapper;
    private final JdbcTemplate jdbcTemplate = new JdbcTemplate();

    @BeforeAll
    static void initializeMybatisMetadata() {
        TableInfoHelper.initTableInfo(
                new MapperBuilderAssistant(new MybatisConfiguration(), "article-service-test"), Article.class);
    }

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void anonymousUserCannotReadUnpublishedArticle() {
        Article draft = article(1L, "DRAFT");
        when(articleMapper.selectById(1L)).thenReturn(draft);

        assertThatThrownBy(() -> service().get(1L))
                .isInstanceOfSatisfying(BusinessException.class,
                        exception -> assertThat(exception.getCode()).isEqualTo(40401));
    }

    @Test
    void anonymousUserCanReadPublishedArticle() {
        Article published = article(1L, "PUBLISHED");
        when(articleMapper.selectById(1L)).thenReturn(published);
        when(tagMapper.selectNamesByArticleIds(List.of(1L))).thenReturn(List.of());

        assertThat(service().get(1L)).satisfies(article -> {
            assertThat(article.id()).isEqualTo(1L);
            assertThat(article.status()).isEqualTo("PUBLISHED");
        });
    }

    @Test
    void missingArticleIsNotExposed() {
        when(articleMapper.selectById(404L)).thenReturn(null);

        assertThatThrownBy(() -> service().get(404L))
                .isInstanceOfSatisfying(BusinessException.class, exception -> {
                    assertThat(exception.getCode()).isEqualTo(40401);
                    assertThat(exception.getMessage()).isEqualTo("文章不存在");
                });
    }

    @Test
    void memberCannotReadUnpublishedArticle() {
        Article draft = article(1L, "DRAFT");
        when(articleMapper.selectById(1L)).thenReturn(draft);
        authenticate("ROLE_MEMBER");

        assertThatThrownBy(() -> service().get(1L)).isInstanceOf(BusinessException.class);
    }

    @Test
    void editorCanReadUnpublishedArticle() {
        Article draft = article(1L, "DRAFT");
        when(articleMapper.selectById(1L)).thenReturn(draft);
        when(tagMapper.selectNamesByArticleIds(List.of(1L))).thenReturn(List.of());
        authenticate("ROLE_EDITOR");

        assertThat(service().get(1L).status()).isEqualTo("DRAFT");
    }

    @Test
    void adminCanReadUnpublishedArticle() {
        Article draft = article(1L, "DRAFT");
        when(articleMapper.selectById(1L)).thenReturn(draft);
        when(tagMapper.selectNamesByArticleIds(List.of(1L))).thenReturn(List.of());
        authenticate("ROLE_ADMIN");

        assertThat(service().get(1L).status()).isEqualTo("DRAFT");
    }

    @Test
    void loadsTagsOnceForAnArticleCollection() {
        Article first = article(1L, "PUBLISHED");
        Article second = article(2L, "PUBLISHED");
        when(articleMapper.selectList(any())).thenReturn(List.of(first, second));
        when(tagMapper.selectNamesByArticleIds(List.of(1L, 2L)))
                .thenReturn(List.of(new ArticleTagRow(1L, "力量"), new ArticleTagRow(2L, "营养")));

        var result = service().latest(10);

        assertThat(result.get(0).tags()).containsExactly("力量");
        assertThat(result.get(1).tags()).containsExactly("营养");
        verify(tagMapper).selectNamesByArticleIds(List.of(1L, 2L));
    }

    @Test
    void homeQueriesUsePublishedStatusAndExpectedOrdering() {
        when(articleMapper.selectList(any())).thenReturn(List.of());

        service().latest(10);
        service().hot(10);
        service().recommended(10);

        @SuppressWarnings("unchecked")
        ArgumentCaptor<LambdaQueryWrapper<Article>> captor = ArgumentCaptor.forClass(LambdaQueryWrapper.class);
        verify(articleMapper, org.mockito.Mockito.times(3)).selectList(captor.capture());
        var queries = captor.getAllValues();
        assertPublished(queries.get(0));
        assertPublished(queries.get(1));
        assertPublished(queries.get(2));
        assertThat(queries.get(0).getSqlSegment()).contains("published_at DESC");
        assertThat(queries.get(1).getSqlSegment()).contains("view_count DESC");
        assertThat(queries.get(2).getSqlSegment()).contains("recommended", "published_at DESC");
        assertThat(queries.get(2).getParamNameValuePairs()).containsValue(1);
    }

    @Test
    void searchUsesPublishedStatusAndGroupedLikeConditions() {
        Page<Article> emptyPage = Page.of(1, 10);
        emptyPage.setRecords(List.of());
        emptyPage.setTotal(0);
        when(articleMapper.selectPage(any(), any())).thenReturn(emptyPage);

        service().page(1, 10, null, "  深蹲  ");

        @SuppressWarnings("unchecked")
        ArgumentCaptor<LambdaQueryWrapper<Article>> queryCaptor =
                ArgumentCaptor.forClass(LambdaQueryWrapper.class);
        verify(articleMapper).selectPage(any(), queryCaptor.capture());
        LambdaQueryWrapper<Article> query = queryCaptor.getValue();
        assertPublished(query);
        assertThat(query.getSqlSegment()).contains("title", "summary", "content", "published_at DESC");
        assertThat(query.getParamNameValuePairs()).containsValue("%深蹲%");
    }

    private ArticleService service() {
        return new ArticleService(articleMapper, categoryMapper, tagMapper, jdbcTemplate);
    }

    private Article article(Long id, String status) {
        Article article = new Article();
        article.setId(id);
        article.setCategoryId(100L);
        article.setTitle("title");
        article.setContent("content");
        article.setStatus(status);
        article.setRecommended(0);
        article.setViewCount(0L);
        return article;
    }

    private void authenticate(String authority) {
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(
                "editor", "n/a", List.of(new SimpleGrantedAuthority(authority))));
    }

    private void assertPublished(LambdaQueryWrapper<Article> query) {
        assertThat(query.getSqlSegment()).contains("status");
        assertThat(query.getParamNameValuePairs()).containsValue("PUBLISHED");
    }
}
