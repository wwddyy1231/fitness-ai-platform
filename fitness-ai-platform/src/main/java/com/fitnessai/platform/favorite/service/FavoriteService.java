package com.fitnessai.platform.favorite.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fitnessai.platform.common.exception.BusinessException;
import com.fitnessai.platform.content.entity.Article;
import com.fitnessai.platform.content.mapper.ArticleMapper;
import com.fitnessai.platform.favorite.entity.ArticleFavorite;
import com.fitnessai.platform.favorite.mapper.ArticleFavoriteMapper;
import com.fitnessai.platform.favorite.vo.FavoriteStatusVO;
import com.fitnessai.platform.user.entity.User;
import com.fitnessai.platform.user.mapper.UserMapper;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FavoriteService {
    private static final String PUBLISHED = "PUBLISHED";

    private final ArticleFavoriteMapper favoriteMapper;
    private final ArticleMapper articleMapper;
    private final UserMapper userMapper;

    public FavoriteService(ArticleFavoriteMapper favoriteMapper, ArticleMapper articleMapper, UserMapper userMapper) {
        this.favoriteMapper = favoriteMapper;
        this.articleMapper = articleMapper;
        this.userMapper = userMapper;
    }

    public FavoriteStatusVO status(Long articleId, Authentication authentication) {
        Long userId = requireUser(authentication).getId();
        requirePublishedArticle(articleId);
        return result(articleId, favoriteMapper.countByUserAndArticle(userId, articleId) > 0);
    }

    @Transactional
    public FavoriteStatusVO favorite(Long articleId, Authentication authentication) {
        Long userId = requireUser(authentication).getId();
        requirePublishedArticle(articleId);
        ArticleFavorite favorite = new ArticleFavorite();
        favorite.setUserId(userId);
        favorite.setArticleId(articleId);
        favoriteMapper.insertIgnore(favorite);
        return result(articleId, true);
    }

    @Transactional
    public FavoriteStatusVO unfavorite(Long articleId, Authentication authentication) {
        Long userId = requireUser(authentication).getId();
        requirePublishedArticle(articleId);
        favoriteMapper.deleteByUserAndArticle(userId, articleId);
        return result(articleId, false);
    }

    private User requireUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new BusinessException(401, "请先登录");
        }
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, authentication.getName())
                .eq(User::getStatus, 1));
        if (user == null) throw new BusinessException(401, "登录用户不存在或已禁用");
        return user;
    }

    private void requirePublishedArticle(Long articleId) {
        Long count = articleMapper.selectCount(new LambdaQueryWrapper<Article>()
                .eq(Article::getId, articleId)
                .eq(Article::getStatus, PUBLISHED));
        if (count == null || count == 0) throw new BusinessException(40401, "文章不存在");
    }

    private FavoriteStatusVO result(Long articleId, boolean favorited) {
        return new FavoriteStatusVO(articleId, favorited);
    }
}
