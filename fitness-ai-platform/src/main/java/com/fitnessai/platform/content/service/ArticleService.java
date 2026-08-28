package com.fitnessai.platform.content.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fitnessai.platform.common.api.PageResponse;
import com.fitnessai.platform.common.exception.BusinessException;
import com.fitnessai.platform.content.dto.ArticleRequest;
import com.fitnessai.platform.content.entity.Article;
import com.fitnessai.platform.content.entity.Category;
import com.fitnessai.platform.content.mapper.ArticleMapper;
import com.fitnessai.platform.content.mapper.CategoryMapper;
import com.fitnessai.platform.content.mapper.TagMapper;
import com.fitnessai.platform.content.mapper.TagMapper.ArticleTagRow;
import com.fitnessai.platform.content.vo.ArticleVO;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ArticleService {
    private static final String PUBLISHED = "PUBLISHED";

    private final ArticleMapper articleMapper;
    private final CategoryMapper categoryMapper;
    private final TagMapper tagMapper;
    private final JdbcTemplate jdbc;

    public ArticleService(ArticleMapper articleMapper, CategoryMapper categoryMapper, TagMapper tagMapper,
                          JdbcTemplate jdbc) {
        this.articleMapper = articleMapper;
        this.categoryMapper = categoryMapper;
        this.tagMapper = tagMapper;
        this.jdbc = jdbc;
    }

    public PageResponse<ArticleVO> page(long page, long size, Long categoryId) {
        var query = new LambdaQueryWrapper<Article>()
                .eq(Article::getStatus, PUBLISHED)
                .eq(categoryId != null, Article::getCategoryId, categoryId)
                .orderByDesc(Article::getPublishedAt);
        Page<Article> result = articleMapper.selectPage(Page.of(page, size), query);
        return new PageResponse<>(toVOs(result.getRecords()), result.getTotal(), page, size);
    }

    public ArticleVO get(Long id) {
        Article article = require(id);
        if (!PUBLISHED.equals(article.getStatus()) && !canManageContent()) throw articleNotFound();
        return toVOs(List.of(article)).getFirst();
    }

    @Transactional
    public ArticleVO create(ArticleRequest request) {
        validateCategory(request.categoryId());
        Article article = fill(new Article(), request);
        article.setStatus(PUBLISHED);
        article.setPublishedAt(LocalDateTime.now());
        article.setViewCount(0L);
        articleMapper.insert(article);
        replaceTags(article.getId(), request.tagIds());
        return toVOs(List.of(article)).getFirst();
    }

    @Transactional
    public ArticleVO update(Long id, ArticleRequest request) {
        validateCategory(request.categoryId());
        Article article = fill(require(id), request);
        articleMapper.updateById(article);
        replaceTags(id, request.tagIds());
        return toVOs(List.of(article)).getFirst();
    }

    @Transactional
    public void delete(Long id) {
        require(id);
        articleMapper.deleteById(id);
        jdbc.update("delete from cms_article_tag where article_id=?", id);
    }

    public List<ArticleVO> latest(int limit) {
        return list(new LambdaQueryWrapper<Article>().eq(Article::getStatus, PUBLISHED)
                .orderByDesc(Article::getPublishedAt).last("limit " + limit));
    }

    public List<ArticleVO> hot(int limit) {
        return list(new LambdaQueryWrapper<Article>().eq(Article::getStatus, PUBLISHED)
                .orderByDesc(Article::getViewCount).last("limit " + limit));
    }

    public List<ArticleVO> recommended(int limit) {
        return list(new LambdaQueryWrapper<Article>().eq(Article::getStatus, PUBLISHED)
                .eq(Article::getRecommended, 1).orderByDesc(Article::getPublishedAt).last("limit " + limit));
    }

    private List<ArticleVO> list(LambdaQueryWrapper<Article> query) {
        return toVOs(articleMapper.selectList(query));
    }

    private List<ArticleVO> toVOs(List<Article> articles) {
        if (articles.isEmpty()) return List.of();

        List<Long> articleIds = articles.stream().map(Article::getId).toList();
        Map<Long, List<String>> tagsByArticleId = new HashMap<>();
        for (ArticleTagRow row : tagMapper.selectNamesByArticleIds(articleIds)) {
            tagsByArticleId.computeIfAbsent(row.articleId(), ignored -> new ArrayList<>()).add(row.tagName());
        }
        return articles.stream()
                .map(article -> toVO(article, tagsByArticleId.getOrDefault(article.getId(), List.of())))
                .toList();
    }

    private ArticleVO toVO(Article article, List<String> tags) {
        return new ArticleVO(article.getId(), article.getCategoryId(), article.getTitle(), article.getSummary(),
                article.getContent(), article.getCoverUrl(), article.getStatus(),
                Integer.valueOf(1).equals(article.getRecommended()),
                article.getViewCount() == null ? 0 : article.getViewCount(), article.getPublishedAt(), tags);
    }

    private Article require(Long id) {
        Article article = articleMapper.selectById(id);
        if (article == null) throw articleNotFound();
        return article;
    }

    private BusinessException articleNotFound() {
        return new BusinessException(40401, "文章不存在");
    }

    private boolean canManageContent() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) return false;
        return authentication.getAuthorities().stream().anyMatch(authority ->
                "ROLE_EDITOR".equals(authority.getAuthority()) || "ROLE_ADMIN".equals(authority.getAuthority()));
    }

    private void validateCategory(Long id) {
        Category category = categoryMapper.selectById(id);
        if (category == null || !Integer.valueOf(1).equals(category.getEnabled())) {
            throw new BusinessException(40001, "文章分类无效");
        }
    }

    private Article fill(Article article, ArticleRequest request) {
        article.setCategoryId(request.categoryId());
        article.setTitle(request.title());
        article.setSummary(request.summary());
        article.setContent(request.content());
        article.setCoverUrl(request.coverUrl());
        article.setRecommended(Boolean.TRUE.equals(request.recommended()) ? 1 : 0);
        return article;
    }

    private void replaceTags(Long articleId, List<Long> ids) {
        jdbc.update("delete from cms_article_tag where article_id=?", articleId);
        if (ids == null) return;
        for (Long id : ids) {
            if (tagMapper.selectById(id) == null) throw new BusinessException(40002, "标签不存在: " + id);
            jdbc.update("insert into cms_article_tag(article_id,tag_id) values(?,?)", articleId, id);
        }
    }
}
