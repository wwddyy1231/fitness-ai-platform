package com.fitnessai.platform.content.entity;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fitnessai.platform.common.persistence.BaseEntity;
@TableName("cms_category")
public class Category extends BaseEntity {
    private Long parentId; private String name; private String slug; private Integer sort; private Integer enabled;
    public Long getParentId(){return parentId;} public void setParentId(Long v){parentId=v;}
    public String getName(){return name;} public void setName(String v){name=v;}
    public String getSlug(){return slug;} public void setSlug(String v){slug=v;}
    public Integer getSort(){return sort;} public void setSort(Integer v){sort=v;}
    public Integer getEnabled(){return enabled;} public void setEnabled(Integer v){enabled=v;}
}
