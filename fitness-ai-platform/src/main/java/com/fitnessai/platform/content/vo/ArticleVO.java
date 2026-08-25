package com.fitnessai.platform.content.vo;
import java.time.LocalDateTime;
import java.util.List;
public record ArticleVO(Long id, Long categoryId, String title, String summary, String content,
 String coverUrl, String status, boolean recommended, long viewCount, LocalDateTime publishedAt, List<String> tags) {}
