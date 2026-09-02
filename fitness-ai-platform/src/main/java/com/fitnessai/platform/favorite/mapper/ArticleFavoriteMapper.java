package com.fitnessai.platform.favorite.mapper;

import com.fitnessai.platform.favorite.entity.ArticleFavorite;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

public interface ArticleFavoriteMapper {
    @Select("""
            select count(*) from cms_article_favorite
            where user_id = #{userId} and article_id = #{articleId}
            """)
    int countByUserAndArticle(@Param("userId") Long userId, @Param("articleId") Long articleId);

    @Insert("""
            insert ignore into cms_article_favorite(user_id, article_id, created_at)
            values(#{userId}, #{articleId}, UTC_TIMESTAMP())
            """)
    int insertIgnore(ArticleFavorite favorite);

    @Delete("""
            delete from cms_article_favorite
            where user_id = #{userId} and article_id = #{articleId}
            """)
    int deleteByUserAndArticle(@Param("userId") Long userId, @Param("articleId") Long articleId);
}
