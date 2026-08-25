package com.fitnessai.platform.content.entity;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fitnessai.platform.common.persistence.BaseEntity;
@TableName("cms_tag")
public class Tag extends BaseEntity {
    private String name; private String slug;
    public String getName(){return name;} public void setName(String v){name=v;}
    public String getSlug(){return slug;} public void setSlug(String v){slug=v;}
}
