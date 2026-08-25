package com.fitnessai.platform.file.controller;
import com.fitnessai.platform.common.api.ApiResponse;
import com.fitnessai.platform.file.service.FileStorageService;
import com.fitnessai.platform.file.vo.FileVO;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
@RestController @RequestMapping("/api/v1/files") @PreAuthorize("hasAnyRole('EDITOR','ADMIN')")
public class FileController {
 private final FileStorageService storage; public FileController(FileStorageService s){storage=s;}
 @PostMapping("/images") public ApiResponse<FileVO> image(@RequestPart("file") MultipartFile file){return ApiResponse.success(storage.storeImage(file,"images"));}
 @PostMapping("/video-covers") public ApiResponse<FileVO> cover(@RequestPart("file") MultipartFile file){return ApiResponse.success(storage.storeImage(file,"video-covers"));}
}
