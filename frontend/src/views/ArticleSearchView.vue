<script setup lang="ts">
import { RefreshCw, Search } from '@lucide/vue'

import ArticleListItem from '@/components/business/ArticleListItem.vue'
import {
  normalizeKeyword,
  normalizeSearchPage,
  useArticleSearch,
} from '@/composables/useArticleSearch'

const route = useRoute()
const router = useRouter()
const inputKeyword = ref('')
const { status, keyword, page, pageData, errorMessage, load, retry } = useArticleSearch()

watch(
  [() => route.query.q, () => route.query.page],
  async ([rawKeyword, rawPage]) => {
    const normalizedKeyword = normalizeKeyword(
      Array.isArray(rawKeyword) ? rawKeyword[0] : rawKeyword,
    )
    const normalizedPage = normalizeSearchPage(Array.isArray(rawPage) ? rawPage[0] : rawPage)
    inputKeyword.value = normalizedKeyword

    const currentKeyword = Array.isArray(rawKeyword) ? rawKeyword[0] : rawKeyword
    const currentPage = Array.isArray(rawPage) ? rawPage[0] : rawPage
    const canonicalPage = normalizedPage === 1 ? undefined : String(normalizedPage)
    if (currentKeyword !== (normalizedKeyword || undefined) || currentPage !== canonicalPage) {
      await replaceSearch(normalizedKeyword, normalizedPage)
      return
    }

    await load(normalizedKeyword, normalizedPage)
    if (page.value !== normalizedPage) await replaceSearch(normalizedKeyword, page.value)
  },
  { immediate: true },
)

function submitSearch(): void {
  const normalized = normalizeKeyword(inputKeyword.value)
  if (!normalized) return
  void router.push({ name: 'article-search', query: { q: normalized } })
}

function changePage(nextPage: number): void {
  void router.push({
    name: 'article-search',
    query: nextPage === 1 ? { q: keyword.value } : { q: keyword.value, page: String(nextPage) },
  })
}

function replaceSearch(nextKeyword: string, nextPage: number): Promise<unknown> {
  return router.replace({
    name: 'article-search',
    query: nextKeyword
      ? nextPage === 1
        ? { q: nextKeyword }
        : { q: nextKeyword, page: String(nextPage) }
      : {},
  })
}
</script>

<template>
  <div class="search-view">
    <div class="content-container search-container">
      <header class="search-header">
        <span class="section-kicker">SEARCH</span>
        <h1>搜索健身内容</h1>
        <form class="search-box" role="search" @submit.prevent="submitSearch">
          <el-input
            v-model="inputKeyword"
            clearable
            maxlength="100"
            size="large"
            aria-label="搜索文章"
            placeholder="输入动作、训练、营养或器材关键词"
          />
          <el-button
            type="primary"
            size="large"
            native-type="submit"
            :disabled="!inputKeyword.trim()"
          >
            <Search :size="18" aria-hidden="true" />搜索
          </el-button>
        </form>
      </header>

      <section v-if="status === 'idle'" class="search-state" aria-live="polite">
        <h2>查找你需要的健身知识</h2>
        <p>输入关键词后，将从已发布文章的标题、摘要和正文中搜索。</p>
      </section>

      <section v-else-if="status === 'loading'" class="search-state" aria-busy="true">
        <span>正在搜索“{{ keyword }}”</span>
        <el-skeleton :rows="8" animated />
      </section>

      <section v-else-if="status === 'success' && pageData" aria-label="文章搜索结果">
        <div class="result-heading">
          <h2>“{{ keyword }}”的搜索结果</h2>
          <span>共 {{ pageData.total.toLocaleString('zh-CN') }} 篇</span>
        </div>
        <div class="article-list">
          <ArticleListItem
            v-for="article in pageData.records"
            :key="article.id"
            :article="article"
            show-tags
          />
        </div>
        <nav v-if="pageData.total > pageData.size" class="pagination" aria-label="搜索结果分页">
          <el-pagination
            background
            layout="prev, pager, next"
            :current-page="page"
            :page-size="pageData.size"
            :total="pageData.total"
            @current-change="changePage"
          />
        </nav>
      </section>

      <section v-else class="search-state" aria-live="polite">
        <template v-if="status === 'empty'">
          <h2>没有找到相关文章</h2>
          <p>可以尝试缩短关键词，或更换训练动作、营养和器材相关词语。</p>
        </template>
        <template v-else>
          <h2>搜索暂时不可用</h2>
          <p>{{ errorMessage || '网络请求失败，请稍后重试。' }}</p>
          <el-button type="primary" @click="retry">
            <RefreshCw :size="16" aria-hidden="true" />重试
          </el-button>
        </template>
      </section>
    </div>
  </div>
</template>

<style scoped>
.search-view {
  padding: var(--space-10) 0 var(--space-20);
  background: var(--color-canvas);
}
.search-container {
  width: min(100% - (var(--content-gutter) * 2), 68rem);
}
.search-header {
  margin-bottom: var(--space-10);
  padding-bottom: var(--space-10);
  border-bottom: var(--border-strong);
}
.search-header h1 {
  margin-bottom: var(--space-6);
  color: var(--color-text-strong);
  font-family: var(--font-family-display);
  font-size: var(--font-size-4xl);
  font-weight: var(--font-weight-black);
  line-height: var(--line-height-tight);
}
.search-box {
  display: grid;
  grid-template-columns: minmax(0, 1fr) auto;
  gap: var(--space-3);
}
.search-box :deep(.el-button span),
.search-state :deep(.el-button span) {
  display: inline-flex;
  align-items: center;
  gap: var(--space-2);
}
.result-heading {
  display: flex;
  align-items: baseline;
  justify-content: space-between;
  gap: var(--space-4);
  margin-bottom: var(--space-8);
}
.result-heading h2 {
  margin: 0;
  color: var(--color-text-strong);
  font-family: var(--font-family-display);
  font-size: var(--font-size-xl);
}
.result-heading span {
  flex: none;
  color: var(--color-text-muted);
  font-size: var(--font-size-sm);
}
.article-list {
  display: grid;
  gap: var(--space-6);
}
.pagination {
  display: flex;
  justify-content: center;
  margin-top: var(--space-12);
  overflow: hidden;
}
.search-state {
  display: grid;
  min-height: 24rem;
  align-content: center;
  justify-items: start;
  gap: var(--space-4);
}
.search-state h2,
.search-state p {
  margin: 0;
}
.search-state h2 {
  color: var(--color-text-strong);
  font-family: var(--font-family-display);
}
.search-state p,
.search-state > span {
  color: var(--color-text-muted);
}
.search-state :deep(.el-skeleton) {
  width: 100%;
}

@media (max-width: 767px) {
  .search-view {
    padding-top: var(--space-6);
  }
  .search-header {
    padding-bottom: var(--space-8);
  }
  .search-header h1 {
    font-size: var(--font-size-3xl);
  }
  .search-box {
    grid-template-columns: 1fr;
  }
  .search-box :deep(.el-button) {
    width: 100%;
  }
  .result-heading {
    display: grid;
  }
  .pagination :deep(.el-pager li) {
    display: none;
  }
}
</style>
