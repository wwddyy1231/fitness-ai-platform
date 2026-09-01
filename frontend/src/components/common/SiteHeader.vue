<script setup lang="ts">
import { Menu, Search, Sparkles, X } from '@lucide/vue'
import { ElMessage } from 'element-plus'

const mobileMenuOpen = ref(false)
const desktopSearchOpen = ref(false)
const searchKeyword = ref('')
const router = useRouter()

const navigation = [
  { label: '首页', href: '/' },
  { label: '肌肉健身', href: '/#categories' },
  { label: '健身视频', href: '/#videos' },
  { label: '健身计划', href: '/#plans' },
  { label: '健身营养', href: '/#latest' },
  { label: '健身器材', href: '/#categories' },
  { label: '健身资讯', href: '/#latest' },
] as const

function submitSearch() {
  const keyword = searchKeyword.value.trim()
  if (!keyword) {
    ElMessage.info('请输入搜索关键词')
    return
  }
  desktopSearchOpen.value = false
  mobileMenuOpen.value = false
  void router.push({ name: 'article-search', query: { q: keyword } })
}
</script>

<template>
  <header class="site-header">
    <div class="content-container header-inner">
      <RouterLink class="brand" to="/" aria-label="Fitness AI Platform 首页">
        <span class="brand-mark" aria-hidden="true">F</span>
        <span class="brand-copy">
          <strong>FITNESS AI</strong>
          <small>训练 · 营养 · 知识</small>
        </span>
      </RouterLink>

      <nav class="desktop-nav" aria-label="主导航">
        <a v-for="item in navigation" :key="item.label" :href="item.href">{{ item.label }}</a>
      </nav>

      <div class="header-actions">
        <el-button
          class="search-trigger"
          text
          circle
          :aria-label="desktopSearchOpen ? '关闭搜索' : '打开搜索'"
          :aria-expanded="desktopSearchOpen"
          @click="desktopSearchOpen = !desktopSearchOpen"
        >
          <X v-if="desktopSearchOpen" :size="19" aria-hidden="true" />
          <Search v-else :size="19" aria-hidden="true" />
        </el-button>
        <el-button
          class="ai-entry"
          type="primary"
          tag="a"
          href="/#ai-coach"
          aria-label="打开 AI 健身助手入口"
        >
          <Sparkles :size="17" aria-hidden="true" />
          <span>AI 健身助手</span>
        </el-button>
        <el-button
          class="mobile-menu-trigger"
          text
          circle
          aria-label="打开导航菜单"
          @click="mobileMenuOpen = true"
        >
          <Menu :size="22" aria-hidden="true" />
        </el-button>
      </div>
    </div>

    <div v-if="desktopSearchOpen" class="desktop-search-panel">
      <form class="content-container search-form" role="search" @submit.prevent="submitSearch">
        <el-input
          v-model="searchKeyword"
          clearable
          autofocus
          size="large"
          aria-label="搜索健身内容"
          placeholder="搜索动作、计划、营养或器材"
        />
        <el-button native-type="submit" type="primary" size="large">搜索</el-button>
      </form>
    </div>

    <el-drawer
      v-model="mobileMenuOpen"
      class="mobile-nav-drawer"
      direction="rtl"
      size="min(88vw, 24rem)"
      title="网站导航"
    >
      <form class="mobile-search" role="search" @submit.prevent="submitSearch">
        <el-input
          v-model="searchKeyword"
          clearable
          aria-label="搜索健身内容"
          placeholder="搜索健身内容"
        />
        <el-button type="primary" native-type="submit" aria-label="提交搜索"
          ><Search :size="18" aria-hidden="true"
        /></el-button>
      </form>
      <nav class="mobile-nav" aria-label="移动端主导航">
        <a
          v-for="item in navigation"
          :key="item.label"
          :href="item.href"
          @click="mobileMenuOpen = false"
          >{{ item.label }}</a
        >
      </nav>
      <el-button
        class="mobile-ai-entry"
        type="primary"
        tag="a"
        href="/#ai-coach"
        @click="mobileMenuOpen = false"
      >
        <Sparkles :size="18" aria-hidden="true" /> AI 健身助手
      </el-button>
    </el-drawer>
  </header>
</template>

<style scoped>
.site-header {
  position: sticky;
  top: 0;
  z-index: var(--z-header);
  background: color-mix(in srgb, var(--color-surface) 94%, transparent);
  border-bottom: var(--border-thin);
  backdrop-filter: blur(16px);
}
.header-inner {
  display: flex;
  height: var(--header-height);
  align-items: center;
  gap: var(--space-8);
}
.brand {
  display: inline-flex;
  flex: none;
  align-items: center;
  gap: var(--space-3);
}
.brand-mark {
  display: grid;
  width: 2.25rem;
  height: 2.25rem;
  place-items: center;
  transform: skew(-8deg);
  background: var(--color-primary-600);
  border-left: 0.25rem solid var(--color-accent-500);
  color: var(--color-text-inverse);
  font-family: var(--font-family-display);
  font-size: var(--font-size-lg);
  font-weight: var(--font-weight-black);
}
.brand-copy {
  display: grid;
  line-height: 1;
}
.brand-copy strong {
  color: var(--color-text-strong);
  font-family: var(--font-family-display);
  font-size: var(--font-size-sm);
  font-weight: var(--font-weight-black);
}
.brand-copy small {
  margin-top: 0.35rem;
  color: var(--color-text-muted);
  font-size: var(--font-size-2xs);
}
.desktop-nav {
  display: flex;
  min-width: 0;
  flex: 1;
  align-items: stretch;
  justify-content: center;
  gap: clamp(0.75rem, 1.35vw, 1.5rem);
  align-self: stretch;
}
.desktop-nav a {
  position: relative;
  display: inline-flex;
  align-items: center;
  color: var(--color-text);
  font-size: var(--font-size-sm);
  font-weight: var(--font-weight-medium);
  white-space: nowrap;
}
.desktop-nav a::after {
  position: absolute;
  right: 0;
  bottom: -1px;
  left: 0;
  height: 3px;
  transform: scaleX(0);
  background: var(--color-accent-500);
  content: '';
  transition: transform var(--transition-fast);
}
.desktop-nav a:hover::after,
.desktop-nav a:focus-visible::after {
  transform: scaleX(1);
}
.header-actions {
  display: flex;
  flex: none;
  align-items: center;
  gap: var(--space-2);
}
.header-actions :deep(.el-button + .el-button) {
  margin-left: 0;
}
.ai-entry :deep(span),
.mobile-ai-entry :deep(span) {
  display: inline-flex;
  align-items: center;
  gap: var(--space-2);
}
.mobile-menu-trigger {
  display: none;
}
.desktop-search-panel {
  position: absolute;
  right: 0;
  left: 0;
  padding: var(--space-4) 0;
  background: var(--color-surface);
  border-bottom: var(--border-thin);
  box-shadow: var(--shadow-md);
}
.search-form {
  display: grid;
  grid-template-columns: minmax(0, 1fr) auto;
  gap: var(--space-3);
  max-width: 52rem;
}
.mobile-search {
  display: grid;
  grid-template-columns: minmax(0, 1fr) auto;
  gap: var(--space-2);
}
.mobile-nav {
  display: grid;
  margin: var(--space-6) 0;
  border-top: var(--border-thin);
}
.mobile-nav a {
  padding: var(--space-4) 0;
  border-bottom: var(--border-thin);
  color: var(--color-text-strong);
  font-weight: var(--font-weight-semibold);
}
.mobile-ai-entry {
  width: 100%;
}

@media (max-width: 1199px) {
  .header-inner {
    gap: var(--space-4);
  }
  .brand-copy small,
  .desktop-nav a:nth-child(6) {
    display: none;
  }
  .desktop-nav {
    gap: var(--space-4);
  }
}

@media (max-width: 900px) {
  .desktop-nav,
  .search-trigger {
    display: none;
  }
  .header-actions {
    margin-left: auto;
  }
  .mobile-menu-trigger {
    display: inline-flex;
  }
}

@media (max-width: 520px) {
  .ai-entry span {
    display: none;
  }
  .brand-copy small {
    display: block;
  }
}
</style>
