package com.fitnessai.platform.home.vo;
import com.fitnessai.platform.content.vo.ArticleVO;
import com.fitnessai.platform.content.vo.CategoryVO;
import java.util.List;
public record HomeVO(List<ArticleVO> latestArticles,List<ArticleVO> hotArticles,List<ArticleVO> recommendedContent,List<CategoryVO> categoryNavigation) {}
