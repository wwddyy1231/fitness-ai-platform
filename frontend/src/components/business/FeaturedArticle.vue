<script setup lang="ts">
import { ArrowRight } from '@lucide/vue'
import type { ArticleSummary } from '@/types/content'

defineProps<{ article: ArticleSummary; recommendations: ArticleSummary[] }>()
const formatDate = (value: string) =>
  new Intl.DateTimeFormat('zh-CN', { month: 'short', day: 'numeric' }).format(new Date(value))
</script>

<template>
  <section class="featured" aria-labelledby="featured-title">
    <article class="featured-primary">
      <img
        :src="article.image.src"
        :alt="article.image.alt"
        :width="article.image.width"
        :height="article.image.height"
        fetchpriority="high"
      />
      <div class="featured-overlay">
        <span class="article-category">{{ article.category }}</span>
        <h1 id="featured-title">{{ article.title }}</h1>
        <p>{{ article.summary }}</p>
        <div class="article-meta">
          <time :datetime="article.publishedAt">{{ formatDate(article.publishedAt) }}</time
          ><span>{{ article.readCount.toLocaleString('zh-CN') }} 阅读</span>
        </div>
      </div>
    </article>
    <div class="recommendation-list" aria-label="重点推荐">
      <div class="recommendation-heading"><span>EDITOR'S PICK</span><strong>重点推荐</strong></div>
      <article v-for="item in recommendations" :key="item.id" class="recommendation-item">
        <div>
          <span class="recommendation-category">{{ item.category }}</span>
          <h2>{{ item.title }}</h2>
          <time :datetime="item.publishedAt">{{ formatDate(item.publishedAt) }}</time>
        </div>
        <ArrowRight :size="18" aria-hidden="true" />
      </article>
    </div>
  </section>
</template>

<style scoped>
.featured {
  display: grid;
  grid-template-columns: minmax(0, 1.9fr) minmax(18rem, 0.8fr);
  min-height: 38rem;
  background: var(--color-surface-strong);
}
.featured-primary {
  position: relative;
  min-height: 38rem;
  overflow: hidden;
}
.featured-primary::after {
  position: absolute;
  inset: 0;
  background: linear-gradient(0deg, rgb(4 10 6 / 91%) 0%, rgb(4 10 6 / 22%) 68%, transparent 100%);
  content: '';
}
.featured-primary img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}
.featured-overlay {
  position: absolute;
  right: clamp(1.5rem, 4vw, 4rem);
  bottom: clamp(1.5rem, 4vw, 3.5rem);
  left: clamp(1.5rem, 4vw, 4rem);
  z-index: var(--z-base);
  max-width: 49rem;
  color: var(--color-text-inverse);
}
.article-category {
  display: inline-block;
  margin-bottom: var(--space-4);
  padding: var(--space-1) var(--space-3);
  background: var(--color-accent-500);
  font-size: var(--font-size-xs);
  font-weight: var(--font-weight-bold);
}
.featured-overlay h1 {
  margin-bottom: var(--space-4);
  font-family: var(--font-family-display);
  font-size: clamp(2rem, 4vw, 3.5rem);
  font-weight: var(--font-weight-black);
  line-height: var(--line-height-tight);
}
.featured-overlay p {
  max-width: 43rem;
  margin-bottom: var(--space-5);
  color: #d7e0da;
  font-size: var(--font-size-md);
}
.article-meta {
  display: flex;
  gap: var(--space-4);
  color: #b3c0b7;
  font-size: var(--font-size-xs);
}
.recommendation-list {
  display: grid;
  grid-template-rows: auto repeat(4, 1fr);
  padding: var(--space-6) var(--space-8);
  color: var(--color-text-inverse);
}
.recommendation-heading {
  display: flex;
  align-items: baseline;
  justify-content: space-between;
  padding-bottom: var(--space-4);
  border-bottom: 1px solid #354139;
}
.recommendation-heading span {
  color: #7fac90;
  font-size: var(--font-size-2xs);
  font-weight: var(--font-weight-bold);
}
.recommendation-heading strong {
  font-size: var(--font-size-sm);
}
.recommendation-item {
  display: grid;
  grid-template-columns: minmax(0, 1fr) auto;
  align-items: center;
  gap: var(--space-4);
  padding: var(--space-4) 0;
  border-bottom: 1px solid #354139;
}
.recommendation-item:last-child {
  border-bottom: 0;
}
.recommendation-category {
  color: #8bc9a3;
  font-size: var(--font-size-2xs);
  font-weight: var(--font-weight-semibold);
}
.recommendation-item h2 {
  margin: var(--space-1) 0 var(--space-2);
  font-size: var(--font-size-base);
  line-height: var(--line-height-heading);
}
.recommendation-item time {
  color: #849289;
  font-size: var(--font-size-2xs);
}
.recommendation-item > svg {
  color: #708077;
}

@media (max-width: 1000px) {
  .featured {
    grid-template-columns: minmax(0, 1.5fr) minmax(17rem, 0.75fr);
  }
  .featured-overlay p {
    font-size: var(--font-size-base);
  }
}

@media (max-width: 767px) {
  .featured {
    display: block;
    min-height: 0;
  }
  .featured-primary {
    min-height: 31rem;
  }
  .featured-primary img {
    object-position: 54% center;
  }
  .featured-overlay h1 {
    font-size: var(--font-size-3xl);
  }
  .featured-overlay p {
    display: -webkit-box;
    overflow: hidden;
    -webkit-box-orient: vertical;
    -webkit-line-clamp: 2;
  }
  .recommendation-list {
    display: block;
    padding: var(--space-6) var(--content-gutter);
  }
}
</style>
