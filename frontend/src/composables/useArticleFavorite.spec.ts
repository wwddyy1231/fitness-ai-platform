import { effectScope } from 'vue'
import { describe, expect, it, vi } from 'vitest'

import { ApiClientError } from '@/api/error'
import type { FavoriteStatusDto } from '@/api/favorite'

import { useArticleFavorite } from './useArticleFavorite'

describe('useArticleFavorite', () => {
  it('loads and toggles the favorite state without optimistic updates', async () => {
    const scope = effectScope()
    const create = vi.fn(async () => result(true))
    const remove = vi.fn(async () => result(false))
    const favorite = scope.run(() => useArticleFavorite(async () => result(false), create, remove))!

    await favorite.load('10')
    expect(favorite.status.value).toBe('not-favorited')
    await favorite.toggle('10')
    expect(favorite.status.value).toBe('favorited')
    expect(create).toHaveBeenCalledOnce()
    await favorite.toggle('10')
    expect(favorite.status.value).toBe('not-favorited')
    expect(remove).toHaveBeenCalledOnce()
    scope.stop()
  })

  it('keeps loading and blocks duplicate toggles until the request settles', async () => {
    let resolveRequest: ((value: FavoriteStatusDto) => void) | undefined
    const create = vi.fn(
      () => new Promise<FavoriteStatusDto>((resolve) => (resolveRequest = resolve)),
    )
    const scope = effectScope()
    const favorite = scope.run(() => useArticleFavorite(async () => result(false), create))!

    const first = favorite.toggle('10')
    const duplicate = favorite.toggle('10')
    expect(favorite.loading.value).toBe(true)
    expect(create).toHaveBeenCalledOnce()
    resolveRequest?.(result(true))
    await Promise.all([first, duplicate])
    expect(favorite.status.value).toBe('favorited')
    scope.stop()
  })

  it('exposes normalized request errors', async () => {
    const scope = effectScope()
    const favorite = scope.run(() =>
      useArticleFavorite(async () => {
        throw new ApiClientError('收藏状态加载失败', { status: 503 })
      }),
    )!

    await favorite.load('10')
    expect(favorite.status.value).toBe('error')
    expect(favorite.errorMessage.value).toBe('收藏状态加载失败')
    scope.stop()
  })
})

function result(favorited: boolean): FavoriteStatusDto {
  return { articleId: '10', favorited }
}
