package com.fitnessai.platform.devseed;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("dev")
@EnableConfigurationProperties(DevHomeSeedProperties.class)
public class DevHomeContentSeedRunner implements ApplicationRunner {
    private static final Logger log = LoggerFactory.getLogger(DevHomeContentSeedRunner.class);

    private final DevHomeContentSeedService seedService;
    private final DevHomeSeedProperties properties;

    public DevHomeContentSeedRunner(DevHomeContentSeedService seedService, DevHomeSeedProperties properties) {
        this.seedService = seedService;
        this.properties = properties;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (properties.cleanup()) {
            DevHomeContentSeedService.CleanupResult result = seedService.cleanup();
            log.info("Development home seed cleanup completed: {} articles, {} tags",
                    result.articlesDeleted(), result.tagsDeleted());
            return;
        }
        if (!properties.enabled()) return;

        DevHomeContentSeedService.SeedResult result = seedService.seed();
        log.info("Development home seed completed: {} articles created, {} tags created, {} relations ensured",
                result.articlesCreated(), result.tagsCreated(), result.relationsEnsured());
    }
}
