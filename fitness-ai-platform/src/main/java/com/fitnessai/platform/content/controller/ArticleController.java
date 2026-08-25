package com.fitnessai.platform.content.controller;
import com.fitnessai.platform.common.api.ApiResponse;
import com.fitnessai.platform.common.api.PageResponse;
import com.fitnessai.platform.content.dto.ArticleRequest;
import com.fitnessai.platform.content.service.ArticleService;
import com.fitnessai.platform.content.vo.ArticleVO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
@Validated @RestController @RequestMapping("/api/v1/articles")
public class ArticleController {
 private final ArticleService service; public ArticleController(ArticleService s){service=s;}
 @GetMapping public ApiResponse<PageResponse<ArticleVO>> page(@RequestParam(defaultValue="1") @Min(1) long page,@RequestParam(defaultValue="10") @Min(1) @Max(100) long size,@RequestParam(required=false) Long categoryId){return ApiResponse.success(service.page(page,size,categoryId));}
 @GetMapping("/{id}") public ApiResponse<ArticleVO> get(@PathVariable Long id){return ApiResponse.success(service.get(id));}
 @PostMapping @ResponseStatus(HttpStatus.CREATED) @PreAuthorize("hasAnyRole('EDITOR','ADMIN')") public ApiResponse<ArticleVO> create(@Valid @RequestBody ArticleRequest r){return ApiResponse.success(service.create(r));}
 @PutMapping("/{id}") @PreAuthorize("hasAnyRole('EDITOR','ADMIN')") public ApiResponse<ArticleVO> update(@PathVariable Long id,@Valid @RequestBody ArticleRequest r){return ApiResponse.success(service.update(id,r));}
 @DeleteMapping("/{id}") @PreAuthorize("hasRole('ADMIN')") public ApiResponse<Void> delete(@PathVariable Long id){service.delete(id);return ApiResponse.success();}
}
