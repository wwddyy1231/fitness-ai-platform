package com.fitnessai.platform.home.controller;
import com.fitnessai.platform.common.api.ApiResponse;
import com.fitnessai.platform.content.service.ArticleService;
import com.fitnessai.platform.content.service.TaxonomyService;
import com.fitnessai.platform.content.vo.ArticleVO;
import com.fitnessai.platform.content.vo.CategoryVO;
import com.fitnessai.platform.home.vo.HomeVO;
import java.util.List;
import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/api/v1/home")
public class HomeController {
 private final ArticleService articles; private final TaxonomyService taxonomy;
 public HomeController(ArticleService a,TaxonomyService t){articles=a;taxonomy=t;}
 @GetMapping public ApiResponse<HomeVO> home(){return ApiResponse.success(new HomeVO(articles.latest(10),articles.hot(10),articles.recommended(10),taxonomy.categories()));}
 @GetMapping("/latest-articles") public ApiResponse<List<ArticleVO>> latest(){return ApiResponse.success(articles.latest(10));}
 @GetMapping("/hot-articles") public ApiResponse<List<ArticleVO>> hot(){return ApiResponse.success(articles.hot(10));}
 @GetMapping("/recommendations") public ApiResponse<List<ArticleVO>> recommended(){return ApiResponse.success(articles.recommended(10));}
 @GetMapping("/categories") public ApiResponse<List<CategoryVO>> categories(){return ApiResponse.success(taxonomy.categories());}
}
