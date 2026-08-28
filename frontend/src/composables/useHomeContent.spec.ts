import { effectScope } from 'vue'
import { describe, expect, it } from 'vitest'

import { ApiClientError } from '@/api/error'
import type { HomeDto } from '@/api/home'

import { useHomeContent } from './useHomeContent'

describe('useHomeContent', () => {
  it('moves from idle to empty for an empty response', async () => {
    const scope = effectScope()
    const home = scope.run(() => useHomeContent(async () => emptyHomeDto()))!

    expect(home.status.value).toBe('idle')
    await home.load()
    expect(home.status.value).toBe('empty')
    scope.stop()
  })

  it('exposes normalized errors and supports retry', async () => {
    let attempt = 0
    const scope = effectScope()
    const home = scope.run(() =>
      useHomeContent(async () => {
        attempt += 1
        if (attempt === 1) throw new ApiClientError('后端暂时不可用', { status: 503 })
        return populatedHomeDto()
      }),
    )!

    await home.load()
    expect(home.status.value).toBe('error')
    expect(home.errorMessage.value).toBe('后端暂时不可用')

    await home.load()
    expect(home.status.value).toBe('success')
    scope.stop()
  })

  it('cancels the previous request before starting a duplicate request', async () => {
    const signals: AbortSignal[] = []
    let call = 0
    const scope = effectScope()
    const home = scope.run(() =>
      useHomeContent((signal) => {
        signals.push(signal!)
        call += 1
        if (call === 2) return Promise.resolve(populatedHomeDto())
        return new Promise<HomeDto>((_, reject) =>
          signal?.addEventListener('abort', () =>
            reject(new DOMException('Aborted', 'AbortError')),
          ),
        )
      }),
    )!

    const first = home.load()
    const second = home.load()
    await Promise.all([first, second])

    expect(signals[0]?.aborted).toBe(true)
    expect(home.status.value).toBe('success')
    scope.stop()
  })
})

function emptyHomeDto(): HomeDto {
  return {
    latestArticles: [],
    hotArticles: [],
    recommendedContent: [],
    categoryNavigation: [],
  }
}

function populatedHomeDto(): HomeDto {
  return {
    ...emptyHomeDto(),
    latestArticles: [
      {
        id: '1',
        categoryId: '10',
        title: '文章',
        summary: null,
        content: null,
        coverUrl: null,
        status: 'PUBLISHED',
        recommended: false,
        viewCount: 0,
        publishedAt: null,
        tags: [],
      },
    ],
  }
}
