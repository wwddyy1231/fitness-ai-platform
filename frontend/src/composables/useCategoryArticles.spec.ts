import { effectScope } from 'vue'
import { describe, expect, it, vi } from 'vitest'

import type { ArticlePageDto, CategoryDto } from '@/api/category'
import { ApiClientError } from '@/api/error'

import { normalizePage, useCategoryArticles } from './useCategoryArticles'

describe('useCategoryArticles', () => {
  it('normalizes invalid page values', () => {
    expect(normalizePage(undefined)).toBe(1)
    expect(normalizePage('')).toBe(1)
    expect(normalizePage('-2')).toBe(1)
    expect(normalizePage('1.5')).toBe(1)
    expect(normalizePage('2')).toBe(2)
  })

  it('reports a missing category without requesting articles', async () => {
    const fetchArticles = vi.fn()
    const scope = effectScope()
    const result = scope.run(() => useCategoryArticles(async () => [category()], fetchArticles))!

    await result.load('missing', 1)

    expect(result.status.value).toBe('not-found')
    expect(fetchArticles).not.toHaveBeenCalled()
    scope.stop()
  })

  it('reports an empty category page', async () => {
    const scope = effectScope()
    const result = scope.run(() =>
      useCategoryArticles(
        async () => [category()],
        async () => pageDto(),
      ),
    )!

    await result.load('fitness', 1)

    expect(result.status.value).toBe('empty')
    expect(result.category.value?.id).toBe('9223372036854775807')
    expect(result.pageData.value?.records).toEqual([])
    scope.stop()
  })

  it('exposes errors and retries the last request', async () => {
    let attempt = 0
    const scope = effectScope()
    const result = scope.run(() =>
      useCategoryArticles(
        async () => {
          attempt += 1
          if (attempt === 1) throw new ApiClientError('分类服务不可用', { status: 503 })
          return [category()]
        },
        async () => pageDto({ records: [article()] }),
      ),
    )!

    await result.load('fitness', 1)
    expect(result.status.value).toBe('error')
    expect(result.errorMessage.value).toBe('分类服务不可用')

    await result.retry()
    expect(result.status.value).toBe('success')
    expect(attempt).toBe(2)
    scope.stop()
  })

  it('cancels an old request and prevents it from overwriting the latest page', async () => {
    const signals: AbortSignal[] = []
    const requests: Array<ReturnType<typeof deferred<ArticlePageDto>>> = []
    const scope = effectScope()
    const result = scope.run(() =>
      useCategoryArticles(
        async () => [category()],
        ({ signal }) => {
          signals.push(signal!)
          const request = deferred<ArticlePageDto>()
          requests.push(request)
          return request.promise
        },
      ),
    )!

    const first = result.load('fitness', 1)
    await Promise.resolve()
    const second = result.load('fitness', 2)
    await Promise.resolve()
    requests[1]!.resolve(
      pageDto({ records: [article({ id: '2', title: '第二页' })], total: 20, page: 2 }),
    )
    await second
    requests[0]!.resolve(pageDto({ records: [article({ id: '1', title: '第一页' })] }))
    await first

    expect(signals[0]?.aborted).toBe(true)
    expect(result.pageData.value?.records[0]?.id).toBe('2')
    expect(result.page.value).toBe(2)
    scope.stop()
  })
})

function category(): CategoryDto {
  return {
    id: '9223372036854775807',
    parentId: '0',
    name: '健身文章',
    slug: 'fitness',
    sort: 1,
  }
}

function article(overrides: Partial<ArticlePageDto['records'][number]> = {}) {
  return {
    id: '1',
    categoryId: '9223372036854775807',
    categoryName: '健身文章',
    title: '文章',
    summary: null,
    content: null,
    coverUrl: null,
    status: 'PUBLISHED',
    recommended: false,
    viewCount: 0,
    publishedAt: null,
    tags: [],
    ...overrides,
  }
}

function pageDto(overrides: Partial<ArticlePageDto> = {}): ArticlePageDto {
  return { records: [], total: 0, page: 1, size: 10, ...overrides }
}

function deferred<T>() {
  let resolve!: (value: T) => void
  const promise = new Promise<T>((done) => {
    resolve = done
  })
  return { promise, resolve }
}
