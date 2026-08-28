<script setup lang="ts">
import { ArrowRight, CalendarDays, Dumbbell } from '@lucide/vue'
import type { TrainingPlan } from '@/types/content'
defineProps<{ plan: TrainingPlan; index: number }>()
</script>

<template>
  <article class="plan-card">
    <div class="plan-topline">
      <span>{{ plan.level }}</span
      ><strong>{{ String(index + 1).padStart(2, '0') }}</strong>
    </div>
    <h3>{{ plan.title }}</h3>
    <p>{{ plan.focus }}</p>
    <dl>
      <div>
        <dt><CalendarDays :size="16" aria-hidden="true" />周期</dt>
        <dd>{{ plan.duration }}</dd>
      </div>
      <div>
        <dt><Dumbbell :size="16" aria-hidden="true" />频率</dt>
        <dd>{{ plan.frequency }}</dd>
      </div>
    </dl>
    <a href="/#plans">查看计划结构 <ArrowRight :size="16" aria-hidden="true" /></a>
  </article>
</template>

<style scoped>
.plan-card {
  position: relative;
  overflow: hidden;
  padding: var(--space-8);
  background: var(--color-surface);
  border: var(--border-thin);
  border-radius: var(--radius-md);
}
.plan-card::before {
  position: absolute;
  top: 0;
  right: 0;
  left: 0;
  height: 0.25rem;
  background: var(--color-primary-500);
  content: '';
}
.plan-card:nth-child(2)::before {
  background: var(--color-accent-500);
}
.plan-card:nth-child(3)::before {
  background: var(--color-text-strong);
}
.plan-topline {
  display: flex;
  align-items: center;
  justify-content: space-between;
}
.plan-topline span {
  color: var(--color-primary-600);
  font-size: var(--font-size-xs);
  font-weight: var(--font-weight-bold);
}
.plan-topline strong {
  color: var(--color-surface-muted);
  font-family: var(--font-family-display);
  font-size: var(--font-size-3xl);
  line-height: 1;
}
h3 {
  margin: var(--space-5) 0 var(--space-3);
  color: var(--color-text-strong);
  font-family: var(--font-family-display);
  font-size: var(--font-size-xl);
  line-height: var(--line-height-heading);
}
p {
  min-height: 3.25rem;
  margin-bottom: var(--space-6);
  color: var(--color-text-muted);
  font-size: var(--font-size-sm);
}
dl {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: var(--space-4);
  margin: 0 0 var(--space-6);
  padding: var(--space-4) 0;
  border-top: var(--border-thin);
  border-bottom: var(--border-thin);
}
dt {
  display: flex;
  align-items: center;
  gap: var(--space-2);
  color: var(--color-text-subtle);
  font-size: var(--font-size-xs);
}
dd {
  margin: var(--space-1) 0 0;
  color: var(--color-text-strong);
  font-size: var(--font-size-sm);
  font-weight: var(--font-weight-semibold);
}
a {
  display: inline-flex;
  align-items: center;
  gap: var(--space-2);
  color: var(--color-primary-600);
  font-size: var(--font-size-sm);
  font-weight: var(--font-weight-semibold);
}
</style>
