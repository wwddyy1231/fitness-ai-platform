import { computed, onScopeDispose, ref } from 'vue'

import {
  favoriteArticle,
  getFavoriteStatus,
  type FavoriteStatusDto,
  unfavoriteArticle,
} from '@/api/favorite'
import { mapFavoriteStatusDto } from '@/api/favorite/mapper'
import { toApiClientError } from '@/api/error'

export type ArticleFavoriteStatus = 'idle' | 'loading' | 'favorited' | 'not-favorited' | 'error'
export type FavoriteFetcher = (
  articleId: string,
  signal?: AbortSignal,
) => Promise<FavoriteStatusDto>

export function useArticleFavorite(
  fetchStatus: FavoriteFetcher = getFavoriteStatus,
  createFavorite: FavoriteFetcher = favoriteArticle,
  removeFavorite: FavoriteFetcher = unfavoriteArticle,
) {
  const status = ref<ArticleFavoriteStatus>('idle')
  const favorited = ref(false)
  const errorMessage = ref('')
  const loading = computed(() => status.value === 'loading')
  let activeController: AbortController | null = null
  let requestSequence = 0

  async function load(articleId: string): Promise<void> {
    await run(articleId, fetchStatus)
  }

  async function toggle(articleId: string): Promise<void> {
    if (loading.value) return
    await run(articleId, favorited.value ? removeFavorite : createFavorite)
  }

  async function run(articleId: string, fetcher: FavoriteFetcher): Promise<void> {
    activeController?.abort()
    const controller = new AbortController()
    activeController = controller
    const requestId = ++requestSequence
    status.value = 'loading'
    errorMessage.value = ''

    try {
      const model = mapFavoriteStatusDto(await fetcher(articleId, controller.signal))
      if (requestId !== requestSequence) return
      favorited.value = model.favorited
      status.value = model.favorited ? 'favorited' : 'not-favorited'
    } catch (error: unknown) {
      if (requestId !== requestSequence || controller.signal.aborted) return
      errorMessage.value = toApiClientError(error).message
      status.value = 'error'
    } finally {
      if (activeController === controller) activeController = null
    }
  }

  function reset(): void {
    activeController?.abort()
    activeController = null
    requestSequence += 1
    status.value = 'idle'
    favorited.value = false
    errorMessage.value = ''
  }

  onScopeDispose(reset)

  return { status, favorited, loading, errorMessage, load, toggle, reset }
}
