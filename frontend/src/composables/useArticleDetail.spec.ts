import { effectScope } from 'vue'
import { describe, expect, it } from 'vitest'

import type { ArticleDetailDto } from '@/api/article'
import { ApiClientError } from '@/api/error'

import { useArticleDetail } from './useArticleDetail'

describe('useArticleDetail', () => {
  it('moves from idle to success and empty according to content', async () => {
    const scope = effectScope()
    const detail = scope.run(() => useArticleDetail(async () => article()))!

    expect(detail.status.value).toBe('idle')
    await detail.load('101')
    expect(detail.status.value).toBe('success')

    const empty = scope.run(() =>
      useArticleDetail(async () => article({ content: null, summary: null })),
    )!
    await empty.load('102')
    expect(empty.status.value).toBe('empty')
    scope.stop()
  })

  it('classifies business errors and retries the last article', async () => {
    let attempt = 0
    const scope = effectScope()
    const detail = scope.run(() =>
      useArticleDetail(async () => {
        attempt += 1
        if (attempt === 1) throw new ApiClientError('文章不存在', { code: 40401 })
        return article()
      }),
    )!

    await detail.load('101')
    expect(detail.status.value).toBe('error')
    expect(detail.errorKind.value).toBe('not-found')
    expect(detail.errorMessage.value).toBe('文章不存在')

    await detail.retry()
    expect(detail.status.value).toBe('success')
    expect(attempt).toBe(2)
    scope.stop()
  })

  it('cancels an old request and only applies the latest result', async () => {
    const signals: AbortSignal[] = []
    const scope = effectScope()
    const detail = scope.run(() =>
      useArticleDetail((id, signal) => {
        signals.push(signal!)
        if (id === '2') return Promise.resolve(article({ id: '2', title: '第二篇' }))
        return new Promise<ArticleDetailDto>((_, reject) =>
          signal?.addEventListener('abort', () =>
            reject(new DOMException('Aborted', 'AbortError')),
          ),
        )
      }),
    )!

    const first = detail.load('1')
    const second = detail.load('2')
    await Promise.all([first, second])

    expect(signals[0]?.aborted).toBe(true)
    expect(detail.data.value?.id).toBe('2')
    expect(detail.status.value).toBe('success')
    scope.stop()
  })
})

function article(overrides: Partial<ArticleDetailDto> = {}): ArticleDetailDto {
  return {
    id: '101',
    categoryId: '10',
    categoryName: '力量训练',
    title: '深蹲训练基础',
    summary: '摘要',
    content: '正文',
    coverUrl: null,
    status: 'PUBLISHED',
    recommended: true,
    viewCount: 10,
    publishedAt: null,
    tags: [],
    ...overrides,
  }
}
