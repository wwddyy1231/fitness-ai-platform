package com.fitnessai.platform.devseed;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("app.seed.home")
public record DevHomeSeedProperties(boolean enabled, boolean cleanup) {}
