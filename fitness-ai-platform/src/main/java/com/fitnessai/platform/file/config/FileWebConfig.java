package com.fitnessai.platform.file.config;
import com.fitnessai.platform.file.service.FileStorageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
@Configuration
public class FileWebConfig implements WebMvcConfigurer {
 private final FileStorageService storage; private final String prefix;
 public FileWebConfig(FileStorageService s,@Value("${storage.local.public-prefix}") String p){storage=s;prefix=p;}
 @Override public void addResourceHandlers(ResourceHandlerRegistry registry){registry.addResourceHandler(prefix+"/**").addResourceLocations(storage.root().toUri().toString());}
}
