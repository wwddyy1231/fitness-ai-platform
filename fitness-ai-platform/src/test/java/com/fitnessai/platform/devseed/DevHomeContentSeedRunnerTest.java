package com.fitnessai.platform.devseed;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.fitnessai.platform.content.mapper.ArticleMapper;
import com.fitnessai.platform.content.mapper.CategoryMapper;
import com.fitnessai.platform.content.mapper.TagMapper;
import org.junit.jupiter.api.Test;
import org.springframework.boot.DefaultApplicationArguments;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.jdbc.core.JdbcTemplate;

class DevHomeContentSeedRunnerTest {
    private final DevHomeContentSeedService seedService = mock(DevHomeContentSeedService.class);

    @Test
    void runnerExistsOnlyInDevProfile() {
        new ApplicationContextRunner().withUserConfiguration(DevHomeContentSeedRunner.class)
                .run(context -> assertThat(context).doesNotHaveBean(DevHomeContentSeedRunner.class));

        contextRunner().withInitializer(context -> context.getEnvironment().setActiveProfiles("dev"))
                .run(context -> assertThat(context).hasSingleBean(DevHomeContentSeedRunner.class));
    }

    @Test
    void disabledRunnerDoesNotTouchDatabase() {
        new DevHomeContentSeedRunner(seedService, new DevHomeSeedProperties(false, false))
                .run(new DefaultApplicationArguments());

        verifyNoInteractions(seedService);
    }

    @Test
    void enabledRunnerSeedsContent() {
        when(seedService.seed()).thenReturn(new DevHomeContentSeedService.SeedResult(6, 6, 10));

        new DevHomeContentSeedRunner(seedService, new DevHomeSeedProperties(true, false))
                .run(new DefaultApplicationArguments());

        verify(seedService).seed();
        verify(seedService, never()).cleanup();
    }

    @Test
    void cleanupModeDoesNotReseed() {
        when(seedService.cleanup()).thenReturn(new DevHomeContentSeedService.CleanupResult(6, 6));

        new DevHomeContentSeedRunner(seedService, new DevHomeSeedProperties(true, true))
                .run(new DefaultApplicationArguments());

        verify(seedService).cleanup();
        verify(seedService, never()).seed();
    }

    private ApplicationContextRunner contextRunner() {
        return new ApplicationContextRunner()
                .withUserConfiguration(DevHomeContentSeedRunner.class, DevHomeContentSeedService.class)
                .withBean(ArticleMapper.class, () -> mock(ArticleMapper.class))
                .withBean(CategoryMapper.class, () -> mock(CategoryMapper.class))
                .withBean(TagMapper.class, () -> mock(TagMapper.class))
                .withBean(JdbcTemplate.class, () -> mock(JdbcTemplate.class))
                .withPropertyValues("app.seed.home.enabled=true", "app.seed.home.cleanup=false");
    }
}
