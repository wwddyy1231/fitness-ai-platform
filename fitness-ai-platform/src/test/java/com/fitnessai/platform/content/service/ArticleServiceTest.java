package com.fitnessai.platform.content.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fitnessai.platform.common.exception.BusinessException;
import com.fitnessai.platform.content.entity.Article;
import com.fitnessai.platform.content.mapper.ArticleMapper;
import com.fitnessai.platform.content.mapper.CategoryMapper;
import com.fitnessai.platform.content.mapper.TagMapper;
import com.fitnessai.platform.content.mapper.TagMapper.ArticleTagRow;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
}
