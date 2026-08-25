package com.fitnessai.platform.content.controller;
import com.fitnessai.platform.common.api.ApiResponse;
import com.fitnessai.platform.content.dto.CategoryRequest;
import com.fitnessai.platform.content.dto.TagRequest;
import com.fitnessai.platform.content.service.TaxonomyService;
import com.fitnessai.platform.content.vo.CategoryVO;
import com.fitnessai.platform.content.vo.TagVO;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
@RestController
public class TaxonomyController {
 private final TaxonomyService service; public TaxonomyController(TaxonomyService s){service=s;}
 @GetMapping("/api/v1/categories") public ApiResponse<List<CategoryVO>> categories(){return ApiResponse.success(service.categories());}
 @PostMapping("/api/v1/categories") @ResponseStatus(HttpStatus.CREATED) @PreAuthorize("hasAnyRole('EDITOR','ADMIN')") public ApiResponse<CategoryVO> addCategory(@Valid @RequestBody CategoryRequest r){return ApiResponse.success(service.createCategory(r));}
 @DeleteMapping("/api/v1/categories/{id}") @PreAuthorize("hasRole('ADMIN')") public ApiResponse<Void> deleteCategory(@PathVariable Long id){service.deleteCategory(id);return ApiResponse.success();}
 @GetMapping("/api/v1/tags") public ApiResponse<List<TagVO>> tags(){return ApiResponse.success(service.tags());}
 @PostMapping("/api/v1/tags") @ResponseStatus(HttpStatus.CREATED) @PreAuthorize("hasAnyRole('EDITOR','ADMIN')") public ApiResponse<TagVO> addTag(@Valid @RequestBody TagRequest r){return ApiResponse.success(service.createTag(r));}
 @DeleteMapping("/api/v1/tags/{id}") @PreAuthorize("hasRole('ADMIN')") public ApiResponse<Void> deleteTag(@PathVariable Long id){service.deleteTag(id);return ApiResponse.success();}
}
