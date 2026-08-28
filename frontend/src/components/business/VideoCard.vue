<script setup lang="ts">
import { Play } from '@lucide/vue'
import type { FitnessVideo } from '@/types/content'
defineProps<{ video: FitnessVideo }>()
</script>

<template>
  <article class="video-card">
    <div class="video-image">
      <img
        :src="video.image.src"
        :alt="video.image.alt"
        :width="video.image.width"
        :height="video.image.height"
        loading="lazy"
        decoding="async"
      /><span class="play-icon" aria-hidden="true"><Play :size="20" fill="currentColor" /></span
      ><time>{{ video.duration }}</time>
    </div>
    <span class="category">{{ video.category }}</span>
    <h3>{{ video.title }}</h3>
  </article>
</template>

<style scoped>
.video-card {
  min-width: 0;
}
.video-image {
  position: relative;
  overflow: hidden;
  aspect-ratio: 16 / 9;
  margin-bottom: var(--space-4);
  background: #202a24;
  border-radius: var(--radius-sm);
}
.video-image::after {
  position: absolute;
  inset: 0;
  background: linear-gradient(0deg, rgb(7 15 10 / 55%), transparent 58%);
  content: '';
}
.video-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform var(--transition-slow);
}
.video-card:hover img {
  transform: scale(1.03);
}
.play-icon {
  position: absolute;
  top: 50%;
  left: 50%;
  z-index: var(--z-base);
  display: grid;
  width: 3rem;
  height: 3rem;
  place-items: center;
  transform: translate(-50%, -50%);
  background: color-mix(in srgb, var(--color-surface) 92%, transparent);
  border-radius: 50%;
  color: var(--color-primary-600);
}
.video-image time {
  position: absolute;
  right: var(--space-3);
  bottom: var(--space-3);
  z-index: var(--z-base);
  padding: var(--space-1) var(--space-2);
  background: rgb(7 15 10 / 78%);
  border-radius: var(--radius-xs);
  color: white;
  font-size: var(--font-size-2xs);
}
.category {
  color: var(--color-primary-300);
  font-size: var(--font-size-xs);
  font-weight: var(--font-weight-bold);
}
h3 {
  margin: var(--space-2) 0 0;
  color: var(--color-text-inverse);
  font-size: var(--font-size-base);
  line-height: var(--line-height-heading);
}
</style>
