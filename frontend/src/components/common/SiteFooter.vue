<script setup lang="ts">
import { ArrowUpRight, Sparkles } from '@lucide/vue'
</script>

<template>
  <footer class="site-footer">
    <div class="content-container footer-main">
      <div class="footer-brand">
        <span class="footer-mark" aria-hidden="true">F</span>
        <div>
          <strong>FITNESS AI</strong>
          <p>让可靠知识、清晰计划和智能建议共同服务每一次训练。</p>
        </div>
      </div>
      <nav aria-label="网站栏目">
        <h2>网站栏目</h2>
        <a href="/">首页</a><a href="/#latest">健身资讯</a><a href="/#videos">健身视频</a
        ><a href="/#plans">训练计划</a>
      </nav>
      <nav aria-label="健身内容分类">
        <h2>内容分类</h2>
        <a href="/#categories">增肌与力量</a><a href="/#categories">减脂与体能</a
        ><a href="/#latest">营养与恢复</a><a href="/#categories">器材指南</a>
      </nav>
      <nav aria-label="关于 Fitness AI">
        <h2>关于</h2>
        <a href="/#about">内容原则</a><a href="/#about">联系我们</a><a href="/#about">隐私说明</a>
      </nav>
      <div class="footer-ai">
        <Sparkles :size="22" aria-hidden="true" />
        <h2>AI 健身助手</h2>
        <p>围绕目标、训练经验与日常条件，获得更清晰的行动建议。</p>
        <a href="/#ai-coach">开始咨询 <ArrowUpRight :size="16" aria-hidden="true" /></a>
      </div>
    </div>
    <div class="content-container footer-bottom">
      <p>© 2026 Fitness AI Platform. 内容仅供健身知识参考。</p>
      <p>科学训练 · 合理营养 · 长期进步</p>
    </div>
  </footer>
</template>

<style scoped>
.site-footer {
  padding: var(--space-16) 0 var(--space-6);
  background: var(--color-surface-inverse);
  color: var(--color-text-inverse);
}
.footer-main {
  display: grid;
  grid-template-columns: minmax(16rem, 1.6fr) repeat(3, minmax(8rem, 0.75fr)) minmax(15rem, 1fr);
  gap: var(--space-10);
}
.footer-brand {
  display: flex;
  align-items: flex-start;
  gap: var(--space-4);
}
.footer-mark {
  display: grid;
  width: 2.75rem;
  height: 2.75rem;
  flex: none;
  place-items: center;
  transform: skew(-8deg);
  background: var(--color-primary-500);
  border-left: 0.25rem solid var(--color-accent-500);
  font-size: var(--font-size-xl);
  font-weight: var(--font-weight-black);
}
.footer-brand strong {
  font-family: var(--font-family-display);
  font-size: var(--font-size-lg);
  font-weight: var(--font-weight-black);
}
.footer-brand p,
.footer-ai p {
  margin: var(--space-3) 0 0;
  color: #aebbb2;
  font-size: var(--font-size-sm);
}
.footer-main nav,
.footer-ai {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  gap: var(--space-2);
}
.footer-main h2 {
  margin: 0 0 var(--space-3);
  color: var(--color-text-inverse);
  font-size: var(--font-size-sm);
  font-weight: var(--font-weight-bold);
}
.footer-main nav a {
  color: #aebbb2;
  font-size: var(--font-size-sm);
  transition: color var(--transition-fast);
}
.footer-main nav a:hover {
  color: var(--color-text-inverse);
}
.footer-ai {
  padding-left: var(--space-6);
  border-left: 1px solid #344239;
}
.footer-ai > svg {
  color: #82c39c;
}
.footer-ai a {
  display: inline-flex;
  align-items: center;
  gap: var(--space-2);
  margin-top: var(--space-3);
  color: #9ad4b1;
  font-size: var(--font-size-sm);
  font-weight: var(--font-weight-semibold);
}
.footer-bottom {
  display: flex;
  justify-content: space-between;
  gap: var(--space-6);
  margin-top: var(--space-12);
  padding-top: var(--space-6);
  border-top: 1px solid #344239;
  color: #87978c;
  font-size: var(--font-size-xs);
}
.footer-bottom p {
  margin: 0;
}

@media (max-width: 1199px) {
  .footer-main {
    grid-template-columns: 1.5fr repeat(3, 0.75fr);
  }
  .footer-ai {
    grid-column: 1 / -1;
    flex-direction: row;
    align-items: center;
    padding: var(--space-5) 0 0;
    border-top: 1px solid #344239;
    border-left: 0;
  }
  .footer-ai h2,
  .footer-ai p,
  .footer-ai a {
    margin: 0;
  }
  .footer-ai p {
    flex: 1;
  }
}

@media (max-width: 767px) {
  .footer-main {
    grid-template-columns: repeat(2, minmax(0, 1fr));
    gap: var(--space-8) var(--space-6);
  }
  .footer-brand,
  .footer-ai {
    grid-column: 1 / -1;
  }
  .footer-main nav:nth-of-type(3) {
    display: none;
  }
  .footer-ai {
    display: grid;
    grid-template-columns: auto 1fr;
  }
  .footer-ai p,
  .footer-ai a {
    grid-column: 1 / -1;
  }
  .footer-bottom {
    display: grid;
  }
}
</style>
