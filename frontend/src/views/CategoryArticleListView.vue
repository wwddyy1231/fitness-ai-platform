<script setup lang="ts">
import { ArrowLeft, RefreshCw } from '@lucide/vue'

import ArticleListItem from '@/components/business/ArticleListItem.vue'
import { normalizePage, useCategoryArticles } from '@/composables/useCategoryArticles'

const route = useRoute()
const router = useRouter()
const { status, category, pageData, page, errorMessage, load, retry } = useCategoryArticles()

const slug = computed(() => {
  const value = route.params.slug
  return Array.isArray(value) ? (value[0] ?? '') : (value ?? '')
})

watch(
  [slug, () => route.query.page],
  async ([currentSlug, rawPage]) => {
    const normalized = normalizePage(rawPage)
    const canonicalPage = normalized === 1 ? undefined : String(normalized)
    const currentPage = Array.isArray(rawPage) ? rawPage[0] : rawPage
    if (currentPage !== canonicalPage) {
      await router.replace({
        name: 'category-articles',
        params: { slug: currentSlug },
        query: canonicalPage ? { page: canonicalPage } : {},
      })
      return
    }
    await load(currentSlug, normalized)
    if (page.value !== normalized) await changePage(page.value, true)
  },
  { immediate: true },
)

async function changePage(nextPage: number, replace = false): Promise<void> {
  const target = {
    name: 'category-articles' as const,
    params: { slug: slug.value },
    query: nextPage === 1 ? {} : { page: String(nextPage) },
  }
  await (replace ? router.replace(target) : router.push(target))
}
</script>

<template>
  <div class="category-view">
    <div class="content-container category-container">
      <RouterLink class="back-link" :to="{ name: 'home' }">
        <ArrowLeft :size="16" aria-hidden="true" />返回首页
      </RouterLink>

      <header v-if="category" class="category-header">
        <span class="section-kicker">CATEGORY</span>
        <h1>{{ category.name }}</h1>
        <p>{{ category.description }}</p>
      </header>

      <section
        v-if="status === 'idle' || status === 'loading'"
        class="category-state"
        aria-busy="true"
      >
        <span>正在加载分类文章</span>
        <el-skeleton :rows="8" animated />
      </section>

      <section v-else-if="status === 'success' && pageData" aria-label="分类文章列表">
        <div class="article-list">
          <ArticleListItem
            v-for="article in pageData.records"
            :key="article.id"
            :article="article"
            show-tags
          />
        </div>
        <nav v-if="pageData.total > pageData.size" class="pagination" aria-label="文章分页">
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

      <section v-else class="category-state" aria-live="polite">
        <template v-if="status === 'not-found'">
          <h1>分类不存在</h1>
          <p>该分类可能已停用或地址有误。</p>
        </template>
        <template v-else-if="status === 'empty'">
          <h2>暂无文章</h2>
          <p>该分类暂时没有已发布内容。</p>
        </template>
        <template v-else>
          <h1>分类内容加载失败</h1>
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
.category-view {
  padding: var(--space-8) 0 var(--space-20);
  background: var(--color-canvas);
}
.category-container {
  width: min(100% - (var(--content-gutter) * 2), 68rem);
}
.back-link {
  display: inline-flex;
  align-items: center;
  gap: var(--space-2);
  margin-bottom: var(--space-10);
  color: var(--color-primary-600);
  font-size: var(--font-size-sm);
  font-weight: var(--font-weight-semibold);
}
.category-header {
  margin-bottom: var(--space-12);
  padding-bottom: var(--space-8);
  border-bottom: var(--border-strong);
}
.category-header h1 {
  margin-bottom: var(--space-3);
  color: var(--color-text-strong);
  font-family: var(--font-family-display);
  font-size: var(--font-size-4xl);
  font-weight: var(--font-weight-black);
  line-height: var(--line-height-tight);
}
.category-header p {
  max-width: 42rem;
  margin: 0;
  color: var(--color-text-muted);
  font-size: var(--font-size-md);
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
.category-state {
  display: grid;
  min-height: 25rem;
  align-content: center;
  justify-items: start;
  gap: var(--space-4);
}
.category-state h1,
.category-state h2,
.category-state p {
  margin: 0;
}
.category-state h1,
.category-state h2 {
  color: var(--color-text-strong);
  font-family: var(--font-family-display);
}
.category-state p,
.category-state > span {
  color: var(--color-text-muted);
}
.category-state :deep(.el-skeleton) {
  width: 100%;
}
.category-state :deep(.el-button span) {
  display: inline-flex;
  align-items: center;
  gap: var(--space-2);
}

@media (max-width: 767px) {
  .category-view {
    padding-top: var(--space-6);
  }
  .back-link {
    margin-bottom: var(--space-8);
  }
  .category-header {
    margin-bottom: var(--space-8);
  }
  .category-header h1 {
    font-size: var(--font-size-3xl);
  }
  .category-header p {
    font-size: var(--font-size-base);
  }
  .pagination :deep(.el-pager li) {
    display: none;
  }
}
</style>
