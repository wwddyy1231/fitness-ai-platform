<script setup lang="ts">
import { RefreshCw, WifiOff } from '@lucide/vue'
import type { HomeRequestStatus } from '@/composables/useHomeContent'

defineProps<{
  status: Exclude<HomeRequestStatus, 'success'>
  message?: string
}>()
defineEmits<{ retry: [] }>()
</script>

<template>
  <section class="remote-state" aria-live="polite" :aria-busy="status === 'loading'">
    <template v-if="status === 'idle' || status === 'loading'">
      <span class="state-label">正在获取最新内容</span>
      <el-skeleton :rows="6" animated />
    </template>
    <template v-else-if="status === 'empty'">
      <h1>内容正在准备中</h1>
      <p>当前还没有已发布的首页内容，请稍后再来查看。</p>
      <el-button @click="$emit('retry')"
        ><RefreshCw :size="16" aria-hidden="true" />重新加载</el-button
      >
    </template>
    <template v-else>
      <WifiOff :size="28" aria-hidden="true" />
      <h1>首页内容暂时无法加载</h1>
      <p>{{ message || '网络请求失败，请稍后重试。' }}</p>
      <el-button type="primary" @click="$emit('retry')"
        ><RefreshCw :size="16" aria-hidden="true" />重试</el-button
      >
    </template>
  </section>
</template>

<style scoped>
.remote-state {
  display: grid;
  min-height: 24rem;
  align-content: center;
  justify-items: start;
  gap: var(--space-4);
  padding: var(--space-12) 0;
}
.remote-state h1,
.remote-state p {
  margin: 0;
}
.remote-state h1 {
  color: var(--color-text-strong);
  font-family: var(--font-family-display);
  font-size: var(--font-size-2xl);
}
.remote-state p,
.state-label {
  color: var(--color-text-muted);
}
.remote-state :deep(.el-skeleton) {
  width: min(100%, 60rem);
}
.remote-state :deep(.el-button span) {
  display: inline-flex;
  align-items: center;
  gap: var(--space-2);
}
</style>
