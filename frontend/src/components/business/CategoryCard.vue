<script setup lang="ts">
import { Apple, Bike, Dumbbell, Flame, House, TrendingUp } from '@lucide/vue'
import type { Component } from 'vue'
import type { FitnessCategory } from '@/types/content'

const props = defineProps<{ category: FitnessCategory }>()
const icons: Record<FitnessCategory['icon'], Component> = {
  muscle: TrendingUp,
  'fat-loss': Flame,
  strength: Dumbbell,
  home: House,
  nutrition: Apple,
  equipment: Bike,
}
const icon = computed(() => icons[props.category.icon])
</script>

<template>
  <a class="category-card" :href="`/#${category.id}`"
    ><span class="icon-wrap"><component :is="icon" :size="22" aria-hidden="true" /></span>
    <div>
      <h3>{{ category.name }}</h3>
      <p>{{ category.description }}</p>
    </div>
    <strong v-if="category.articleCount !== null"
      >{{ category.articleCount }}<small>篇</small></strong
    ><strong v-else><small>专题</small></strong></a
  >
</template>

<style scoped>
.category-card {
  display: grid;
  grid-template-columns: auto minmax(0, 1fr) auto;
  align-items: center;
  gap: var(--space-4);
  padding: var(--space-5);
  background: var(--color-surface);
  border: var(--border-thin);
  border-radius: var(--radius-md);
  transition:
    transform var(--transition-base),
    border-color var(--transition-base),
    box-shadow var(--transition-base);
}
.category-card:hover {
  transform: translateY(-2px);
  border-color: var(--color-primary-300);
  box-shadow: var(--shadow-md);
}
.icon-wrap {
  display: grid;
  width: 2.75rem;
  height: 2.75rem;
  place-items: center;
  background: var(--color-primary-50);
  border-radius: var(--radius-sm);
  color: var(--color-primary-600);
}
h3 {
  margin: 0 0 var(--space-1);
  color: var(--color-text-strong);
  font-size: var(--font-size-base);
}
p {
  margin: 0;
  color: var(--color-text-muted);
  font-size: var(--font-size-xs);
}
strong {
  color: var(--color-text-strong);
  font-family: var(--font-family-display);
  font-size: var(--font-size-lg);
}
small {
  margin-left: var(--space-1);
  color: var(--color-text-subtle);
  font-family: var(--font-family-sans);
  font-size: var(--font-size-2xs);
  font-weight: var(--font-weight-regular);
}
</style>
