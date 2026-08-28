<script setup lang="ts">
import { Apple, ArrowRight, BrainCircuit, Dumbbell, Sparkles } from '@lucide/vue'
import { ElMessage } from 'element-plus'
const startConsultation = () => ElMessage.info('AI 健身助手将在聊天模块接入后开放')
</script>

<template>
  <section id="ai-coach" class="ai-banner" aria-labelledby="ai-coach-title">
    <div class="ai-copy">
      <span class="ai-kicker"><Sparkles :size="17" aria-hidden="true" /> FITNESS AI COACH</span>
      <h2 id="ai-coach-title">把零散问题，整理成下一步行动</h2>
      <p>
        基于你的目标、训练经验和现实条件，获得结构清晰的训练与营养建议。重要健康决策仍应咨询专业人士。
      </p>
      <el-button type="primary" size="large" @click="startConsultation"
        >开始咨询 <ArrowRight :size="18" aria-hidden="true"
      /></el-button>
    </div>
    <ul class="ai-capabilities">
      <li>
        <span><Dumbbell :size="21" aria-hidden="true" /></span>
        <div><strong>AI 训练建议</strong><small>目标、频率与器械条件</small></div>
      </li>
      <li>
        <span><Apple :size="21" aria-hidden="true" /></span>
        <div><strong>AI 营养建议</strong><small>训练表现与日常饮食</small></div>
      </li>
      <li>
        <span><BrainCircuit :size="21" aria-hidden="true" /></span>
        <div><strong>健身知识问答</strong><small>从站内知识中寻找依据</small></div>
      </li>
    </ul>
  </section>
</template>

<style scoped>
.ai-banner {
  position: relative;
  display: grid;
  grid-template-columns: minmax(0, 1.25fr) minmax(20rem, 0.75fr);
  overflow: hidden;
  background: var(--color-surface-strong);
  border-radius: var(--radius-lg);
  color: var(--color-text-inverse);
}
.ai-banner::before {
  position: absolute;
  inset: 0 auto 0 0;
  width: 0.35rem;
  background: var(--color-accent-500);
  content: '';
}
.ai-copy {
  padding: clamp(2rem, 5vw, 4.5rem);
}
.ai-kicker {
  display: inline-flex;
  align-items: center;
  gap: var(--space-2);
  color: #88cda3;
  font-size: var(--font-size-xs);
  font-weight: var(--font-weight-bold);
}
h2 {
  max-width: 42rem;
  margin: var(--space-5) 0 var(--space-4);
  font-family: var(--font-family-display);
  font-size: var(--font-size-3xl);
  font-weight: var(--font-weight-black);
  line-height: var(--line-height-tight);
}
.ai-copy p {
  max-width: 40rem;
  margin-bottom: var(--space-6);
  color: #bdc9c1;
}
.ai-copy :deep(.el-button span) {
  display: inline-flex;
  align-items: center;
  gap: var(--space-2);
}
.ai-capabilities {
  display: grid;
  align-content: center;
  margin: 0;
  padding: var(--space-10);
  background: #17231c;
  list-style: none;
}
.ai-capabilities li {
  display: grid;
  grid-template-columns: auto minmax(0, 1fr);
  align-items: center;
  gap: var(--space-4);
  padding: var(--space-5) 0;
  border-bottom: 1px solid #344239;
}
.ai-capabilities li:last-child {
  border-bottom: 0;
}
.ai-capabilities li > span {
  display: grid;
  width: 2.75rem;
  height: 2.75rem;
  place-items: center;
  background: #20392b;
  border-radius: var(--radius-sm);
  color: #88cda3;
}
.ai-capabilities strong,
.ai-capabilities small {
  display: block;
}
.ai-capabilities strong {
  font-size: var(--font-size-sm);
}
.ai-capabilities small {
  margin-top: var(--space-1);
  color: #8e9c93;
  font-size: var(--font-size-xs);
}
@media (max-width: 767px) {
  .ai-banner {
    grid-template-columns: 1fr;
  }
  .ai-capabilities {
    padding: var(--space-5) var(--space-6);
  }
}
</style>
