package com.fitnessai.platform.content.dto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
public record CategoryRequest(Long parentId, @NotBlank @Size(max=50) String name,
 @NotBlank @Size(max=60) String slug, Integer sort, Boolean enabled) {}
