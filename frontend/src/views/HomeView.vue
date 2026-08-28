<script setup lang="ts">
import { ArrowRight } from '@lucide/vue'
import AiCoachBanner from '@/components/business/AiCoachBanner.vue'
import ArticleListItem from '@/components/business/ArticleListItem.vue'
import CategoryCard from '@/components/business/CategoryCard.vue'
import FeaturedArticle from '@/components/business/FeaturedArticle.vue'
import TrainingPlanCard from '@/components/business/TrainingPlanCard.vue'
import TrendingList from '@/components/business/TrendingList.vue'
import VideoCard from '@/components/business/VideoCard.vue'
import HomeRemoteState from '@/components/common/HomeRemoteState.vue'
import { useHomeContent } from '@/composables/useHomeContent'
import { fitnessVideos, trainingPlans } from '@/mocks/home'

const { status, data: home, errorMessage, load } = useHomeContent()
onMounted(load)
</script>

<template>
  <div class="home-view">
    <template v-if="status === 'success' && home">
      <div v-if="home.featuredArticle" class="content-container hero-wrap">
        <FeaturedArticle
          :article="home.featuredArticle"
          :recommendations="home.featuredRecommendations"
        />
      </div>

      <section
        v-if="home.trendingArticles.length"
        class="trending-section"
        aria-labelledby="trending-title"
      >
        <div class="content-container">
          <div class="section-heading">
            <div>
              <span class="section-kicker">24H RANKING</span>
              <h2 id="trending-title" class="section-title">今日热门</h2>
            </div>
            <a class="section-link" href="/#latest"
              >查看全部 <ArrowRight :size="16" aria-hidden="true"
            /></a>
          </div>
          <TrendingList :items="home.trendingArticles" />
        </div>
      </section>

      <section
        v-if="home.latestArticles.length || home.fitnessCategories.length"
        id="latest"
        class="latest-section"
        aria-labelledby="latest-title"
      >
        <div class="content-container latest-layout">
          <div v-if="home.latestArticles.length" class="latest-column">
            <div class="section-heading">
              <div>
                <span class="section-kicker">LATEST STORIES</span>
                <h2 id="latest-title" class="section-title">最新健身资讯</h2>
              </div>
              <a class="section-link" href="/#latest"
                >更多资讯 <ArrowRight :size="16" aria-hidden="true"
              /></a>
            </div>
            <div class="article-list">
              <ArticleListItem
                v-for="article in home.latestArticles"
                :key="article.id"
                :article="article"
              />
            </div>
          </div>
          <aside
            v-if="home.fitnessCategories.length"
            id="categories"
            class="category-column"
            aria-labelledby="category-title"
          >
            <span class="section-kicker">EXPLORE</span>
            <h2 id="category-title" class="section-title">找到你的训练方向</h2>
            <p class="category-intro">从目标、训练环境和知识主题出发，快速进入对应内容。</p>
            <div class="category-grid">
              <CategoryCard
                v-for="category in home.fitnessCategories"
                :key="category.id"
                :category="category"
              />
            </div>
          </aside>
        </div>
      </section>
    </template>

    <div v-else class="content-container">
      <HomeRemoteState :status="status" :message="errorMessage" @retry="load" />
    </div>

    <section id="plans" class="plans-section" aria-labelledby="plans-title">
      <div class="content-container">
        <div class="section-heading">
          <div>
            <span class="section-kicker">TRAINING PROGRAMS</span>
            <h2 id="plans-title" class="section-title">选择适合当前阶段的训练计划</h2>
          </div>
          <p class="section-note">从稳定执行开始，再逐步增加训练复杂度。</p>
        </div>
        <div class="plan-grid">
          <TrainingPlanCard
            v-for="(plan, index) in trainingPlans"
            :key="plan.id"
            :plan="plan"
            :index="index"
          />
        </div>
      </div>
    </section>

    <section id="videos" class="videos-section" aria-labelledby="videos-title">
      <div class="content-container">
        <div class="section-heading">
          <div>
            <span class="section-kicker">WATCH & PRACTICE</span>
            <h2 id="videos-title" class="section-title">健身视频</h2>
          </div>
          <a class="section-link" href="/#videos"
            >全部视频 <ArrowRight :size="16" aria-hidden="true"
          /></a>
        </div>
        <div class="video-grid">
          <VideoCard v-for="video in fitnessVideos" :key="video.id" :video="video" />
        </div>
      </div>
    </section>

    <div class="content-container ai-section"><AiCoachBanner /></div>
  </div>
</template>

<style scoped>
.home-view {
  background: var(--color-canvas);
}
.hero-wrap {
  padding-top: var(--space-6);
}
.trending-section,
.latest-section,
.plans-section,
.videos-section {
  padding: var(--space-20) 0;
}
.trending-section {
  background: var(--color-surface);
  border-bottom: var(--border-thin);
}
.latest-layout {
  display: grid;
  grid-template-columns: minmax(0, 1.65fr) minmax(21rem, 0.85fr);
  gap: clamp(3rem, 6vw, 6rem);
  align-items: start;
}
.article-list {
  display: grid;
  gap: var(--space-6);
}
.category-column {
  position: sticky;
  top: calc(var(--header-height) + var(--space-8));
}
.category-intro {
  margin: var(--space-4) 0 var(--space-6);
  color: var(--color-text-muted);
  font-size: var(--font-size-sm);
}
.category-grid {
  display: grid;
  gap: var(--space-3);
}
.plans-section {
  background: var(--color-surface-muted);
}
.section-note {
  max-width: 26rem;
  margin: 0;
  color: var(--color-text-muted);
  font-size: var(--font-size-sm);
  text-align: right;
}
.plan-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: var(--space-5);
}
.videos-section {
  background: var(--color-surface-strong);
}
.videos-section :deep(.section-kicker) {
  color: #8fc9a5;
}
.videos-section :deep(.section-title) {
  color: var(--color-text-inverse);
}
.videos-section :deep(.section-link) {
  color: #9ed2b2;
}
.video-grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: var(--space-5);
}
.ai-section {
  padding: var(--space-20) 0;
}

@media (max-width: 1199px) {
  .latest-layout {
    grid-template-columns: minmax(0, 1.35fr) minmax(19rem, 0.75fr);
    gap: var(--space-10);
  }
  .plan-grid {
    gap: var(--space-4);
  }
  .video-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
    gap: var(--space-8) var(--space-5);
  }
}

@media (max-width: 767px) {
  .hero-wrap {
    width: 100%;
    padding-top: 0;
  }
  .trending-section,
  .latest-section,
  .plans-section,
  .videos-section {
    padding: var(--space-16) 0;
  }
  .latest-layout {
    grid-template-columns: 1fr;
    gap: var(--space-16);
  }
  .category-column {
    position: static;
  }
  .category-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
  .category-grid :deep(.category-card) {
    grid-template-columns: auto minmax(0, 1fr);
  }
  .category-grid :deep(.category-card > strong) {
    display: none;
  }
  .section-note {
    display: none;
  }
  .plan-grid {
    grid-template-columns: 1fr;
  }
  .video-grid {
    display: flex;
    width: calc(100% + var(--content-gutter));
    gap: var(--space-4);
    overflow-x: auto;
    padding-right: var(--content-gutter);
    padding-bottom: var(--space-3);
    scroll-snap-type: x mandatory;
  }
  .video-grid :deep(.video-card) {
    width: min(78vw, 20rem);
    flex: none;
    scroll-snap-align: start;
  }
  .ai-section {
    width: 100%;
    padding: 0;
  }
}

@media (max-width: 520px) {
  .category-grid {
    grid-template-columns: 1fr;
  }
}
</style>
