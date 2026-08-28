package com.fitnessai.platform.content.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fitnessai.platform.content.entity.Tag;
import java.util.Collection;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

public interface TagMapper extends BaseMapper<Tag> {
    @Select("""
            <script>
            SELECT article_tag.article_id, tag.name AS tag_name
            FROM cms_article_tag article_tag
            JOIN cms_tag tag ON tag.id = article_tag.tag_id
            WHERE tag.deleted = 0
              AND article_tag.article_id IN
              <foreach collection="articleIds" item="articleId" open="(" separator="," close=")">
                #{articleId}
              </foreach>
            ORDER BY article_tag.article_id, tag.name
            </script>
            """)
    List<ArticleTagRow> selectNamesByArticleIds(@Param("articleIds") Collection<Long> articleIds);

    record ArticleTagRow(Long articleId, String tagName) {}
}
