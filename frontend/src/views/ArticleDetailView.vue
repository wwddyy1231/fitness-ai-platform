<script setup lang="ts">
import { ArrowLeft, Bookmark, RefreshCw } from '@lucide/vue'
import { ElMessage } from 'element-plus'
import { computed, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'

import { useArticleDetail } from '@/composables/useArticleDetail'
import { useArticleFavorite } from '@/composables/useArticleFavorite'
import { useAuthStore } from '@/stores/auth'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()
const { status, data: article, errorMessage, errorKind, load, retry } = useArticleDetail()
const favorite = useArticleFavorite()

const articleId = computed(() => {
  const value = route.params.id
  return Array.isArray(value) ? (value[0] ?? '') : (value ?? '')
})

watch(articleId, (id) => id && void load(id), { immediate: true })
watch(
  [articleId, () => authStore.isAuthenticated, status],
  ([id, authenticated, articleStatus]) => {
    if (id && authenticated && (articleStatus === 'success' || articleStatus === 'empty')) {
      void favorite.load(id)
    } else {
      favorite.reset()
    }
  },
  { immediate: true },
)

async function toggleFavorite(): Promise<void> {
  if (!authStore.isAuthenticated) {
    await router.push({ name: 'login', query: { redirect: route.fullPath } })
    return
  }
  await favorite.toggle(articleId.value)
  if (favorite.status.value === 'error') ElMessage.error(favorite.errorMessage.value)
}

function formatDate(value: string | null): string {
  if (!value) return '发布时间待更新'
  const date = new Date(value)
  return Number.isNaN(date.getTime())
    ? '发布时间待更新'
    : new Intl.DateTimeFormat('zh-CN', { dateStyle: 'long' }).format(date)
}

const errorTitle = computed(() => {
  if (errorKind.value === 'not-found') return '文章不存在'
  if (errorKind.value === 'forbidden') return '暂时无法访问这篇文章'
  return '文章加载失败'
})
</script>

<template>
  <div class="article-detail-view">
    <div class="content-container">
      <nav class="article-breadcrumb" aria-label="面包屑导航">
        <el-breadcrumb separator="/">
          <el-breadcrumb-item :to="{ name: 'home' }">首页</el-breadcrumb-item>
          <el-breadcrumb-item>{{ article?.categoryName || '文章详情' }}</el-breadcrumb-item>
        </el-breadcrumb>
      </nav>

      <div v-if="status === 'idle' || status === 'loading'" class="detail-state" aria-busy="true">
        <span class="state-label">正在加载文章</span>
        <el-skeleton :rows="10" animated />
      </div>

      <article v-else-if="article && (status === 'success' || status === 'empty')" class="article">
        <header class="article-header">
          <span class="article-category">{{ article.categoryName }}</span>
          <h1>{{ article.title }}</h1>
          <div class="article-meta">
            <div class="article-meta-copy">
              <time :datetime="article.publishedAt || undefined">{{
                formatDate(article.publishedAt)
              }}</time>
              <span>{{ article.viewCount.toLocaleString('zh-CN') }} 阅读</span>
            </div>
            <el-button
              class="favorite-button"
              :type="favorite.favorited.value ? 'primary' : 'default'"
              :plain="favorite.favorited.value"
              :loading="favorite.loading.value"
              :disabled="favorite.loading.value"
              :aria-pressed="favorite.favorited.value"
              @click="toggleFavorite"
            >
              <Bookmark
                :size="17"
                :fill="favorite.favorited.value ? 'currentColor' : 'none'"
                aria-hidden="true"
              />
              {{ favorite.favorited.value ? '已收藏' : '收藏' }}
            </el-button>
          </div>
        </header>

        <figure class="article-cover">
          <img
            :src="article.coverUrl || ''"
            :alt="article.coverAlt"
            width="1600"
            height="1067"
            fetchpriority="high"
          />
        </figure>

        <p v-if="article.summary" class="article-summary">{{ article.summary }}</p>

        <div v-if="article.content" class="article-body">{{ article.content }}</div>
        <section v-else class="empty-content" aria-live="polite">
          <h2>正文正在整理中</h2>
          <p>这篇文章暂时没有可展示的正文内容。</p>
        </section>

        <footer v-if="article.tags.length" class="article-tags" aria-label="文章标签">
          <span v-for="tag in article.tags" :key="tag">{{ tag }}</span>
        </footer>
      </article>

      <section v-else class="detail-state error-state" aria-live="polite">
        <h1>{{ errorTitle }}</h1>
        <p>{{ errorMessage || '暂时无法获取文章内容。' }}</p>
        <div class="state-actions">
          <el-button tag="router-link" :to="{ name: 'home' }">
            <ArrowLeft :size="16" aria-hidden="true" />返回首页
          </el-button>
          <el-button v-if="errorKind === 'network'" type="primary" @click="retry">
            <RefreshCw :size="16" aria-hidden="true" />重试
          </el-button>
        </div>
      </section>
    </div>
  </div>
</template>

<style scoped>
.article-detail-view {
  padding: var(--space-8) 0 var(--space-20);
  background: var(--color-canvas);
}
.article-breadcrumb {
  margin-bottom: var(--space-10);
}
.article,
.detail-state {
  width: min(100%, 58rem);
  margin-inline: auto;
}
.article-header {
  margin-bottom: var(--space-8);
}
.article-category {
  display: inline-block;
  margin-bottom: var(--space-4);
  color: var(--color-primary-600);
  font-size: var(--font-size-sm);
  font-weight: var(--font-weight-bold);
}
.article-header h1 {
  margin-bottom: var(--space-5);
  color: var(--color-text-strong);
  font-family: var(--font-family-display);
  font-size: var(--font-size-4xl);
  font-weight: var(--font-weight-black);
  line-height: var(--line-height-tight);
}
.article-meta {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  justify-content: space-between;
  gap: var(--space-4) var(--space-6);
  color: var(--color-text-subtle);
  font-size: var(--font-size-sm);
}
.article-meta-copy {
  display: flex;
  flex-wrap: wrap;
  gap: var(--space-4);
}
.favorite-button :deep(span) {
  display: inline-flex;
  align-items: center;
  gap: var(--space-2);
}
.article-cover {
  overflow: hidden;
  aspect-ratio: 3 / 2;
  margin: 0 0 var(--space-10);
  background: var(--color-surface-muted);
  border-radius: var(--radius-md);
}
.article-cover img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}
.article-summary {
  margin-bottom: var(--space-8);
  padding-left: var(--space-6);
  border-left: 0.25rem solid var(--color-accent-500);
  color: var(--color-text);
  font-size: var(--font-size-md);
  font-weight: var(--font-weight-medium);
}
.article-body {
  color: var(--color-text);
  font-size: var(--font-size-md);
  line-height: 1.85;
  overflow-wrap: anywhere;
  white-space: pre-wrap;
}
.article-tags {
  display: flex;
  flex-wrap: wrap;
  gap: var(--space-2);
  margin-top: var(--space-12);
  padding-top: var(--space-6);
  border-top: var(--border-thin);
}
.article-tags span {
  padding: var(--space-1) var(--space-3);
  background: var(--color-primary-50);
  border-radius: var(--radius-sm);
  color: var(--color-primary-700);
  font-size: var(--font-size-xs);
  font-weight: var(--font-weight-semibold);
}
.detail-state {
  display: grid;
  min-height: 28rem;
  align-content: center;
  justify-items: start;
  gap: var(--space-4);
}
.detail-state :deep(.el-skeleton) {
  width: 100%;
}
.state-label,
.detail-state p {
  color: var(--color-text-muted);
}
.detail-state h1,
.empty-content h2,
.empty-content p {
  margin: 0;
}
.detail-state h1,
.empty-content h2 {
  color: var(--color-text-strong);
  font-family: var(--font-family-display);
}
.state-actions {
  display: flex;
  flex-wrap: wrap;
  gap: var(--space-3);
}
.state-actions :deep(.el-button span) {
  display: inline-flex;
  align-items: center;
  gap: var(--space-2);
}
.empty-content {
  padding: var(--space-10) 0;
  border-block: var(--border-thin);
}

@media (max-width: 767px) {
  .article-detail-view {
    padding-top: var(--space-6);
  }
  .article-breadcrumb {
    margin-bottom: var(--space-8);
  }
  .article-header h1 {
    font-size: var(--font-size-3xl);
  }
  .article-cover {
    width: calc(100% + (var(--content-gutter) * 2));
    margin-left: calc(var(--content-gutter) * -1);
    border-radius: 0;
  }
  .article-summary,
  .article-body {
    font-size: var(--font-size-base);
  }
}
</style>
