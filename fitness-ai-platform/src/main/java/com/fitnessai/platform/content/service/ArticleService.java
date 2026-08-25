package com.fitnessai.platform.content.service;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fitnessai.platform.common.api.PageResponse;
import com.fitnessai.platform.common.exception.BusinessException;
import com.fitnessai.platform.content.dto.ArticleRequest;
import com.fitnessai.platform.content.entity.Article;
import com.fitnessai.platform.content.entity.Category;
import com.fitnessai.platform.content.entity.Tag;
import com.fitnessai.platform.content.mapper.ArticleMapper;
import com.fitnessai.platform.content.mapper.CategoryMapper;
import com.fitnessai.platform.content.mapper.TagMapper;
import com.fitnessai.platform.content.vo.ArticleVO;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ArticleService {
 private final ArticleMapper articleMapper; private final CategoryMapper categoryMapper; private final TagMapper tagMapper; private final JdbcTemplate jdbc;
 public ArticleService(ArticleMapper a, CategoryMapper c, TagMapper t, JdbcTemplate j){articleMapper=a;categoryMapper=c;tagMapper=t;jdbc=j;}
 public PageResponse<ArticleVO> page(long page,long size,Long categoryId){
  var q=new LambdaQueryWrapper<Article>().eq(Article::getStatus,"PUBLISHED").eq(categoryId!=null,Article::getCategoryId,categoryId).orderByDesc(Article::getPublishedAt);
  Page<Article> result=articleMapper.selectPage(Page.of(page,size),q);
  return new PageResponse<>(result.getRecords().stream().map(this::toVO).toList(),result.getTotal(),page,size);
 }
 public ArticleVO get(Long id){Article a=require(id); return toVO(a);}
 @Transactional public ArticleVO create(ArticleRequest r){validateCategory(r.categoryId()); Article a=fill(new Article(),r);a.setStatus("PUBLISHED");a.setPublishedAt(LocalDateTime.now());a.setViewCount(0L);articleMapper.insert(a);replaceTags(a.getId(),r.tagIds());return toVO(a);}
 @Transactional public ArticleVO update(Long id,ArticleRequest r){validateCategory(r.categoryId());Article a=fill(require(id),r);articleMapper.updateById(a);replaceTags(id,r.tagIds());return toVO(a);}
 @Transactional public void delete(Long id){require(id);articleMapper.deleteById(id);jdbc.update("delete from cms_article_tag where article_id=?",id);}
 public List<ArticleVO> latest(int limit){return list(new LambdaQueryWrapper<Article>().eq(Article::getStatus,"PUBLISHED").orderByDesc(Article::getPublishedAt).last("limit "+limit));}
 public List<ArticleVO> hot(int limit){return list(new LambdaQueryWrapper<Article>().eq(Article::getStatus,"PUBLISHED").orderByDesc(Article::getViewCount).last("limit "+limit));}
 public List<ArticleVO> recommended(int limit){return list(new LambdaQueryWrapper<Article>().eq(Article::getStatus,"PUBLISHED").eq(Article::getRecommended,1).orderByDesc(Article::getPublishedAt).last("limit "+limit));}
 private List<ArticleVO> list(LambdaQueryWrapper<Article> q){return articleMapper.selectList(q).stream().map(this::toVO).toList();}
 private Article require(Long id){Article a=articleMapper.selectById(id);if(a==null)throw new BusinessException(40401,"文章不存在");return a;}
 private void validateCategory(Long id){Category c=categoryMapper.selectById(id);if(c==null||!Integer.valueOf(1).equals(c.getEnabled()))throw new BusinessException(40001,"文章分类无效");}
 private Article fill(Article a,ArticleRequest r){a.setCategoryId(r.categoryId());a.setTitle(r.title());a.setSummary(r.summary());a.setContent(r.content());a.setCoverUrl(r.coverUrl());a.setRecommended(Boolean.TRUE.equals(r.recommended())?1:0);return a;}
 private void replaceTags(Long articleId,List<Long> ids){jdbc.update("delete from cms_article_tag where article_id=?",articleId);if(ids!=null)for(Long id:ids){if(tagMapper.selectById(id)==null)throw new BusinessException(40002,"标签不存在: "+id);jdbc.update("insert into cms_article_tag(article_id,tag_id) values(?,?)",articleId,id);}}
 private ArticleVO toVO(Article a){List<String> tags=jdbc.queryForList("select t.name from cms_tag t join cms_article_tag at on at.tag_id=t.id where at.article_id=? and t.deleted=0",String.class,a.getId());return new ArticleVO(a.getId(),a.getCategoryId(),a.getTitle(),a.getSummary(),a.getContent(),a.getCoverUrl(),a.getStatus(),Integer.valueOf(1).equals(a.getRecommended()),a.getViewCount()==null?0:a.getViewCount(),a.getPublishedAt(),tags);}
}
