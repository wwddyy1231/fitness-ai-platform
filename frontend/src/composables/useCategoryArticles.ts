import { onScopeDispose, ref, shallowRef } from 'vue'

import {
  getCategories,
  getCategoryArticlePage,
  type ArticlePageDto,
  type CategoryDto,
} from '@/api/category'
import { mapArticlePageDto, mapCategoryDto, type CategoryArticlePage } from '@/api/category/mapper'
import { toApiClientError } from '@/api/error'

export const CATEGORY_PAGE_SIZE = 10

export type CategoryArticlesStatus =
  'idle' | 'loading' | 'success' | 'empty' | 'not-found' | 'error'
export type CategoryFetcher = (signal?: AbortSignal) => Promise<CategoryDto[]>
export type ArticlePageFetcher = (params: {
  categoryId: string
  page: number
  size: number
  signal?: AbortSignal
}) => Promise<ArticlePageDto>

export function useCategoryArticles(
  fetchCategories: CategoryFetcher = getCategories,
  fetchArticles: ArticlePageFetcher = getCategoryArticlePage,
) {
  const status = ref<CategoryArticlesStatus>('idle')
  const category = shallowRef<ReturnType<typeof mapCategoryDto> | null>(null)
  const pageData = shallowRef<CategoryArticlePage | null>(null)
  const page = ref(1)
  const errorMessage = ref('')
  let activeController: AbortController | null = null
  let requestSequence = 0
  let lastSlug = ''

  async function load(slug: string, requestedPage: number): Promise<void> {
    lastSlug = slug
    page.value = normalizePage(requestedPage)
    activeController?.abort()
    const controller = new AbortController()
    activeController = controller
    const requestId = ++requestSequence
    status.value = 'loading'
    category.value = null
    pageData.value = null
    errorMessage.value = ''

    try {
      const categories = (await fetchCategories(controller.signal)).map(mapCategoryDto)
      const selected = categories.find((item) => item.slug === slug)
      if (requestId !== requestSequence) return
      if (!selected) {
        status.value = 'not-found'
        return
      }

      category.value = selected
      let response = await fetchArticles({
        categoryId: selected.id,
        page: page.value,
        size: CATEGORY_PAGE_SIZE,
        signal: controller.signal,
      })
      if (requestId !== requestSequence) return

      const lastPage = Math.max(1, Math.ceil(response.total / CATEGORY_PAGE_SIZE))
      if (page.value > lastPage) {
        page.value = lastPage
        response = await fetchArticles({
          categoryId: selected.id,
          page: page.value,
          size: CATEGORY_PAGE_SIZE,
          signal: controller.signal,
        })
        if (requestId !== requestSequence) return
      }

      const mapped = mapArticlePageDto(response, selected)
      pageData.value = mapped
      status.value = mapped.records.length ? 'success' : 'empty'
    } catch (error: unknown) {
      if (requestId !== requestSequence || controller.signal.aborted) return
      errorMessage.value = toApiClientError(error).message
      status.value = 'error'
    } finally {
      if (activeController === controller) activeController = null
    }
  }

  function retry(): Promise<void> {
    return lastSlug ? load(lastSlug, page.value) : Promise.resolve()
  }

  function cancel(): void {
    activeController?.abort()
    activeController = null
  }

  onScopeDispose(cancel)

  return { status, category, pageData, page, errorMessage, load, retry, cancel }
}

export function normalizePage(value: unknown): number {
  const parsed = typeof value === 'string' && value.trim() ? Number(value) : value
  return typeof parsed === 'number' && Number.isSafeInteger(parsed) && parsed > 0 ? parsed : 1
}
