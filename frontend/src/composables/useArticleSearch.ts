import { onScopeDispose, ref, shallowRef } from 'vue'

import { searchArticles, type SearchPageDto } from '@/api/search'
import { mapSearchPageDto, type SearchResultPage } from '@/api/search/mapper'
import { toApiClientError } from '@/api/error'

export const SEARCH_PAGE_SIZE = 10

export type ArticleSearchStatus = 'idle' | 'loading' | 'success' | 'empty' | 'error'
export type ArticleSearchFetcher = (params: {
  keyword: string
  page: number
  size: number
  signal?: AbortSignal
}) => Promise<SearchPageDto>

export function useArticleSearch(fetchSearch: ArticleSearchFetcher = searchArticles) {
  const status = ref<ArticleSearchStatus>('idle')
  const keyword = ref('')
  const page = ref(1)
  const pageData = shallowRef<SearchResultPage | null>(null)
  const errorMessage = ref('')
  let activeController: AbortController | null = null
  let requestSequence = 0

  async function load(rawKeyword: string, requestedPage: number): Promise<void> {
    keyword.value = normalizeKeyword(rawKeyword)
    page.value = normalizeSearchPage(requestedPage)
    activeController?.abort()
    const requestId = ++requestSequence
    pageData.value = null
    errorMessage.value = ''

    if (!keyword.value) {
      activeController = null
      status.value = 'idle'
      return
    }

    const controller = new AbortController()
    activeController = controller
    status.value = 'loading'

    try {
      let response = await fetchSearch({
        keyword: keyword.value,
        page: page.value,
        size: SEARCH_PAGE_SIZE,
        signal: controller.signal,
      })
      if (requestId !== requestSequence) return

      const lastPage = Math.max(1, Math.ceil(response.total / SEARCH_PAGE_SIZE))
      if (page.value > lastPage) {
        page.value = lastPage
        response = await fetchSearch({
          keyword: keyword.value,
          page: page.value,
          size: SEARCH_PAGE_SIZE,
          signal: controller.signal,
        })
        if (requestId !== requestSequence) return
      }

      const mapped = mapSearchPageDto(response)
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
    return load(keyword.value, page.value)
  }

  function cancel(): void {
    activeController?.abort()
    activeController = null
  }

  onScopeDispose(cancel)

  return { status, keyword, page, pageData, errorMessage, load, retry, cancel }
}

export function normalizeKeyword(value: unknown): string {
  return typeof value === 'string' ? value.trim().slice(0, 100) : ''
}

export function normalizeSearchPage(value: unknown): number {
  const parsed = typeof value === 'string' && value.trim() ? Number(value) : value
  return typeof parsed === 'number' && Number.isSafeInteger(parsed) && parsed > 0 ? parsed : 1
}
