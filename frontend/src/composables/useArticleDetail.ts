import { onScopeDispose, ref, shallowRef } from 'vue'

import { getArticleDetail, type ArticleDetail, type ArticleDetailDto } from '@/api/article'
import { mapArticleDetailDto } from '@/api/article/mapper'
import { ApiClientError, toApiClientError } from '@/api/error'

export type ArticleDetailStatus = 'idle' | 'loading' | 'success' | 'empty' | 'error'
export type ArticleDetailErrorKind = 'not-found' | 'forbidden' | 'network'
export type ArticleDetailFetcher = (id: string, signal?: AbortSignal) => Promise<ArticleDetailDto>

export function useArticleDetail(fetchArticle: ArticleDetailFetcher = getArticleDetail) {
  const status = ref<ArticleDetailStatus>('idle')
  const data = shallowRef<ArticleDetail | null>(null)
  const errorMessage = ref('')
  const errorKind = ref<ArticleDetailErrorKind | null>(null)
  let activeController: AbortController | null = null
  let requestSequence = 0
  let lastId = ''

  async function load(id: string): Promise<void> {
    lastId = id
    activeController?.abort()
    const controller = new AbortController()
    activeController = controller
    const requestId = ++requestSequence
    status.value = 'loading'
    errorMessage.value = ''
    errorKind.value = null

    try {
      const model = mapArticleDetailDto(await fetchArticle(id, controller.signal))
      if (requestId !== requestSequence) return
      data.value = model
      status.value = model.content ? 'success' : 'empty'
    } catch (error: unknown) {
      if (requestId !== requestSequence || controller.signal.aborted) return
      const normalized = toApiClientError(error)
      data.value = null
      errorMessage.value = normalized.message
      errorKind.value = classifyError(normalized)
      status.value = 'error'
    } finally {
      if (activeController === controller) activeController = null
    }
  }

  function retry(): Promise<void> {
    return lastId ? load(lastId) : Promise.resolve()
  }

  function cancel(): void {
    activeController?.abort()
    activeController = null
  }

  onScopeDispose(cancel)

  return { status, data, errorMessage, errorKind, load, retry, cancel }
}

function classifyError(error: ApiClientError): ArticleDetailErrorKind {
  if (error.status === 403 || error.code === 403) return 'forbidden'
  if (error.status === 404 || error.code === 404 || error.code === 40401) return 'not-found'
  return 'network'
}
