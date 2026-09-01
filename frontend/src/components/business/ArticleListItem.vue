<script setup lang="ts">
import type { ArticleSummary } from '@/types/content'
defineProps<{ article: ArticleSummary }>()
const formatDate = (value: string) => {
  const date = new Date(value)
  return Number.isNaN(date.getTime())
    ? '时间待更新'
    : new Intl.DateTimeFormat('zh-CN', { month: 'short', day: 'numeric' }).format(date)
}
</script>

<template>
  <article class="article-list-item">
    <RouterLink class="article-image" :to="{ name: 'article-detail', params: { id: article.id } }">
      <img
        :src="article.image.src"
        :alt="article.image.alt"
        :width="article.image.width"
        :height="article.image.height"
        loading="lazy"
        decoding="async"
      />
    </RouterLink>
    <div class="article-content">
      <span class="category">{{ article.category }}</span>
      <h3>
        <RouterLink :to="{ name: 'article-detail', params: { id: article.id } }">{{
          article.title
        }}</RouterLink>
      </h3>
      <p>{{ article.summary }}</p>
      <div class="meta">
        <time :datetime="article.publishedAt">{{ formatDate(article.publishedAt) }}</time
        ><span>{{ article.readCount.toLocaleString('zh-CN') }} 阅读</span>
      </div>
    </div>
  </article>
</template>

<style scoped>
.article-list-item {
  display: grid;
  grid-template-columns: minmax(12rem, 15rem) minmax(0, 1fr);
  gap: var(--space-6);
  padding: 0 0 var(--space-6);
  border-bottom: var(--border-thin);
}
.article-image {
  overflow: hidden;
  aspect-ratio: 4 / 3;
  background: var(--color-surface-muted);
  border-radius: var(--radius-sm);
}
.article-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform var(--transition-slow);
}
.article-list-item:hover img {
  transform: scale(1.025);
}
.category {
  color: var(--color-primary-600);
  font-size: var(--font-size-xs);
  font-weight: var(--font-weight-bold);
}
h3 {
  margin: var(--space-2) 0 var(--space-3);
  color: var(--color-text-strong);
  font-family: var(--font-family-display);
  font-size: var(--font-size-lg);
  font-weight: var(--font-weight-bold);
  line-height: var(--line-height-heading);
}
p {
  display: -webkit-box;
  overflow: hidden;
  margin-bottom: var(--space-4);
  color: var(--color-text-muted);
  font-size: var(--font-size-sm);
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 2;
}
.meta {
  display: flex;
  gap: var(--space-4);
  color: var(--color-text-subtle);
  font-size: var(--font-size-xs);
}
@media (max-width: 600px) {
  .article-list-item {
    grid-template-columns: 7.5rem minmax(0, 1fr);
    gap: var(--space-4);
  }
  h3 {
    display: -webkit-box;
    overflow: hidden;
    margin-top: var(--space-1);
    font-size: var(--font-size-base);
    -webkit-box-orient: vertical;
    -webkit-line-clamp: 2;
  }
  p {
    display: none;
  }
  .meta {
    gap: var(--space-2);
    font-size: var(--font-size-2xs);
  }
}
</style>
