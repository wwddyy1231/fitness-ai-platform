package com.fitnessai.platform;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan("com.fitnessai.platform.**.mapper")
@SpringBootApplication
public class FitnessAiPlatformApplication {
    public static void main(String[] args) {
        SpringApplication.run(FitnessAiPlatformApplication.class, args);
    }
}
