package com.fitnessai.platform.content.service;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fitnessai.platform.common.exception.BusinessException;
import com.fitnessai.platform.content.dto.CategoryRequest;
import com.fitnessai.platform.content.dto.TagRequest;
import com.fitnessai.platform.content.entity.Category;
import com.fitnessai.platform.content.entity.Tag;
import com.fitnessai.platform.content.mapper.CategoryMapper;
import com.fitnessai.platform.content.mapper.TagMapper;
import com.fitnessai.platform.content.vo.CategoryVO;
import com.fitnessai.platform.content.vo.TagVO;
import java.util.List;
import org.springframework.stereotype.Service;
@Service
public class TaxonomyService {
 private final CategoryMapper categories; private final TagMapper tags;
 public TaxonomyService(CategoryMapper c,TagMapper t){categories=c;tags=t;}
 public List<CategoryVO> categories(){return categories.selectList(new LambdaQueryWrapper<Category>().eq(Category::getEnabled,1).orderByAsc(Category::getSort)).stream().map(this::vo).toList();}
 public CategoryVO createCategory(CategoryRequest r){Category c=new Category();c.setParentId(r.parentId()==null?0:r.parentId());c.setName(r.name());c.setSlug(r.slug());c.setSort(r.sort()==null?0:r.sort());c.setEnabled(!Boolean.FALSE.equals(r.enabled())?1:0);categories.insert(c);return vo(c);}
 public void deleteCategory(Long id){if(categories.selectById(id)==null)throw new BusinessException(40402,"分类不存在");categories.deleteById(id);}
 public List<TagVO> tags(){return tags.selectList(new LambdaQueryWrapper<Tag>().orderByAsc(Tag::getName)).stream().map(t->new TagVO(t.getId(),t.getName(),t.getSlug())).toList();}
 public TagVO createTag(TagRequest r){Tag t=new Tag();t.setName(r.name());t.setSlug(r.slug());tags.insert(t);return new TagVO(t.getId(),t.getName(),t.getSlug());}
 public void deleteTag(Long id){if(tags.selectById(id)==null)throw new BusinessException(40403,"标签不存在");tags.deleteById(id);}
 private CategoryVO vo(Category c){return new CategoryVO(c.getId(),c.getParentId(),c.getName(),c.getSlug(),c.getSort());}
}
