package com.fitnessai.platform.content.entity;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fitnessai.platform.common.persistence.BaseEntity;
import java.time.LocalDateTime;
@TableName("cms_article")
public class Article extends BaseEntity {
    private Long categoryId; private String title; private String summary; private String content; private String coverUrl;
    private String status; private Integer recommended; private Long viewCount; private LocalDateTime publishedAt;
    public Long getCategoryId(){return categoryId;} public void setCategoryId(Long v){categoryId=v;}
    public String getTitle(){return title;} public void setTitle(String v){title=v;}
    public String getSummary(){return summary;} public void setSummary(String v){summary=v;}
    public String getContent(){return content;} public void setContent(String v){content=v;}
    public String getCoverUrl(){return coverUrl;} public void setCoverUrl(String v){coverUrl=v;}
    public String getStatus(){return status;} public void setStatus(String v){status=v;}
    public Integer getRecommended(){return recommended;} public void setRecommended(Integer v){recommended=v;}
    public Long getViewCount(){return viewCount;} public void setViewCount(Long v){viewCount=v;}
    public LocalDateTime getPublishedAt(){return publishedAt;} public void setPublishedAt(LocalDateTime v){publishedAt=v;}
}
