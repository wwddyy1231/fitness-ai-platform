package com.fitnessai.platform.favorite.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.fitnessai.platform.common.exception.BusinessException;
import com.fitnessai.platform.content.entity.Article;
import com.fitnessai.platform.content.mapper.ArticleMapper;
import com.fitnessai.platform.favorite.entity.ArticleFavorite;
import com.fitnessai.platform.favorite.mapper.ArticleFavoriteMapper;
import com.fitnessai.platform.user.entity.User;
import com.fitnessai.platform.user.mapper.UserMapper;
import java.util.List;
import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

class FavoriteServiceTest {
    private ArticleFavoriteMapper favoriteMapper;
    private ArticleMapper articleMapper;
    private UserMapper userMapper;
    private FavoriteService service;

    @BeforeAll
    static void initializeMybatisMetadata() {
        var configuration = new MybatisConfiguration();
        TableInfoHelper.initTableInfo(new MapperBuilderAssistant(configuration, "favorite-article"), Article.class);
        TableInfoHelper.initTableInfo(new MapperBuilderAssistant(configuration, "favorite-user"), User.class);
    }

    @BeforeEach
    void setUp() {
        favoriteMapper = mock(ArticleFavoriteMapper.class);
        articleMapper = mock(ArticleMapper.class);
        userMapper = mock(UserMapper.class);
        service = new FavoriteService(favoriteMapper, articleMapper, userMapper);
    }

    @Test
    void rejectsAnonymousUsers() {
        assertThatThrownBy(() -> service.favorite(10L, null))
                .isInstanceOfSatisfying(BusinessException.class,
                        exception -> assertThat(exception.getCode()).isEqualTo(401));
    }

    @Test
    void favoritesPublishedArticleIdempotently() {
        when(userMapper.selectOne(any())).thenReturn(user(1L, "member"));
        when(articleMapper.selectCount(any())).thenReturn(1L);
        var authentication = authentication("member");

        assertThat(service.favorite(10L, authentication).favorited()).isTrue();
        assertThat(service.favorite(10L, authentication).favorited()).isTrue();

        ArgumentCaptor<ArticleFavorite> captor = ArgumentCaptor.forClass(ArticleFavorite.class);
        verify(favoriteMapper, times(2)).insertIgnore(captor.capture());
        assertThat(captor.getAllValues()).allSatisfy(favorite -> {
            assertThat(favorite.getUserId()).isEqualTo(1L);
            assertThat(favorite.getArticleId()).isEqualTo(10L);
        });
    }

    @Test
    void cancelsFavoriteIdempotently() {
        when(userMapper.selectOne(any())).thenReturn(user(1L, "member"));
        when(articleMapper.selectCount(any())).thenReturn(1L);

        assertThat(service.unfavorite(10L, authentication("member")).favorited()).isFalse();
        assertThat(service.unfavorite(10L, authentication("member")).favorited()).isFalse();

        verify(favoriteMapper, times(2)).deleteByUserAndArticle(1L, 10L);
    }

    @Test
    void doesNotExposeDraftOrMissingArticle() {
        when(userMapper.selectOne(any())).thenReturn(user(1L, "member"));
        when(articleMapper.selectCount(any())).thenReturn(0L);

        assertThatThrownBy(() -> service.status(10L, authentication("member")))
                .isInstanceOfSatisfying(BusinessException.class,
                        exception -> assertThat(exception.getCode()).isEqualTo(40401));
    }

    @Test
    void isolatesFavoritesByAuthenticatedUser() {
        when(userMapper.selectOne(any()))
                .thenReturn(user(1L, "first"))
                .thenReturn(user(2L, "second"));
        when(articleMapper.selectCount(any())).thenReturn(1L);
        when(favoriteMapper.countByUserAndArticle(1L, 10L)).thenReturn(1);
        when(favoriteMapper.countByUserAndArticle(2L, 10L)).thenReturn(0);

        assertThat(service.status(10L, authentication("first")).favorited()).isTrue();
        assertThat(service.status(10L, authentication("second")).favorited()).isFalse();
    }

    private static User user(Long id, String username) {
        User user = new User();
        user.setId(id);
        user.setUsername(username);
        user.setStatus(1);
        return user;
    }

    private static UsernamePasswordAuthenticationToken authentication(String username) {
        return new UsernamePasswordAuthenticationToken(username, null, List.of());
    }
}
