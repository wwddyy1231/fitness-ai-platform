<script setup lang="ts">
import type { TrendingArticle } from '@/types/content'
defineProps<{ items: TrendingArticle[] }>()
</script>

<template>
  <ol class="trending-list">
    <li v-for="(item, index) in items" :key="item.id">
      <span class="rank">{{ String(index + 1).padStart(2, '0') }}</span>
      <div>
        <span class="category">{{ item.category }}</span>
        <h3>{{ item.title }}</h3>
      </div>
    </li>
  </ol>
</template>

<style scoped>
.trending-list {
  display: grid;
  grid-template-columns: repeat(5, minmax(0, 1fr));
  margin: 0;
  padding: 0;
  list-style: none;
}
.trending-list li {
  display: grid;
  grid-template-columns: auto minmax(0, 1fr);
  gap: var(--space-4);
  padding: 0 var(--space-5);
  border-left: var(--border-thin);
}
.trending-list li:first-child {
  padding-left: 0;
  border-left: 0;
}
.rank {
  color: var(--color-accent-500);
  font-family: var(--font-family-display);
  font-size: var(--font-size-xl);
  font-weight: var(--font-weight-black);
  line-height: 1;
}
.category {
  color: var(--color-text-muted);
  font-size: var(--font-size-2xs);
  font-weight: var(--font-weight-semibold);
}
h3 {
  margin: var(--space-2) 0 0;
  color: var(--color-text-strong);
  font-size: var(--font-size-sm);
  line-height: var(--line-height-heading);
}
@media (max-width: 1199px) {
  .trending-list {
    grid-template-columns: repeat(2, minmax(0, 1fr));
    gap: var(--space-6) 0;
  }
  .trending-list li:nth-child(odd) {
    padding-left: 0;
    border-left: 0;
  }
  .trending-list li:last-child {
    grid-column: 1 / -1;
  }
}
@media (max-width: 600px) {
  .trending-list {
    grid-template-columns: 1fr;
  }
  .trending-list li,
  .trending-list li:nth-child(odd) {
    padding: 0 0 var(--space-5);
    border-bottom: var(--border-thin);
    border-left: 0;
  }
  .trending-list li:last-child {
    grid-column: auto;
  }
}
</style>
