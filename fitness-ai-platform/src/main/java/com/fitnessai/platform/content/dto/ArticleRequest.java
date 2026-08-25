package com.fitnessai.platform.content.dto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;
public record ArticleRequest(@NotNull Long categoryId, @NotBlank @Size(max=200) String title,
 @Size(max=500) String summary, @NotBlank String content, @Size(max=500) String coverUrl,
 Boolean recommended, List<Long> tagIds) {}
