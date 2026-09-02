package com.fitnessai.platform.favorite.controller;

import com.fitnessai.platform.common.api.ApiResponse;
import com.fitnessai.platform.favorite.service.FavoriteService;
import com.fitnessai.platform.favorite.vo.FavoriteStatusVO;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/favorites/articles")
public class FavoriteController {
    private final FavoriteService favoriteService;

    public FavoriteController(FavoriteService favoriteService) {
        this.favoriteService = favoriteService;
    }

    @GetMapping("/{articleId}")
    public ApiResponse<FavoriteStatusVO> status(@PathVariable Long articleId, Authentication authentication) {
        return ApiResponse.success(favoriteService.status(articleId, authentication));
    }

    @PutMapping("/{articleId}")
    public ApiResponse<FavoriteStatusVO> favorite(@PathVariable Long articleId, Authentication authentication) {
        return ApiResponse.success(favoriteService.favorite(articleId, authentication));
    }

    @DeleteMapping("/{articleId}")
    public ApiResponse<FavoriteStatusVO> unfavorite(@PathVariable Long articleId, Authentication authentication) {
        return ApiResponse.success(favoriteService.unfavorite(articleId, authentication));
    }
}
