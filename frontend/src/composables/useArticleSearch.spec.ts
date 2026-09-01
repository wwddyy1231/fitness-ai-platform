import { effectScope } from 'vue'
import { describe, expect, it } from 'vitest'

import type { SearchPageDto } from '@/api/search'
import { ApiClientError } from '@/api/error'

import { normalizeKeyword, normalizeSearchPage, useArticleSearch } from './useArticleSearch'

describe('useArticleSearch', () => {
  it('normalizes keyword and page values', () => {
    expect(normalizeKeyword('  深蹲  ')).toBe('深蹲')
    expect(normalizeKeyword(null)).toBe('')
    expect(normalizeSearchPage('-1')).toBe(1)
    expect(normalizeSearchPage('1.5')).toBe(1)
    expect(normalizeSearchPage('2')).toBe(2)
  })

  it('stays idle for a blank keyword without requesting data', async () => {
    let calls = 0
    const scope = effectScope()
    const search = scope.run(() =>
      useArticleSearch(async () => {
        calls += 1
        return pageDto()
      }),
    )!

    await search.load('  ', 1)

    expect(search.status.value).toBe('idle')
    expect(calls).toBe(0)
    scope.stop()
  })

  it('reports empty results', async () => {
    const scope = effectScope()
    const search = scope.run(() => useArticleSearch(async () => pageDto()))!

    await search.load('深蹲', 1)

    expect(search.status.value).toBe('empty')
    expect(search.pageData.value?.records).toEqual([])
    scope.stop()
  })

  it('exposes errors and retries the last search', async () => {
    let attempt = 0
    const scope = effectScope()
    const search = scope.run(() =>
      useArticleSearch(async () => {
        attempt += 1
        if (attempt === 1) throw new ApiClientError('搜索服务不可用', { status: 503 })
        return pageDto({ records: [article()] })
      }),
    )!

    await search.load('深蹲', 1)
    expect(search.status.value).toBe('error')
    expect(search.errorMessage.value).toBe('搜索服务不可用')

    await search.retry()
    expect(search.status.value).toBe('success')
    expect(attempt).toBe(2)
    scope.stop()
  })

  it('cancels the old request and ignores its late response', async () => {
    const signals: AbortSignal[] = []
    const requests: Array<ReturnType<typeof deferred<SearchPageDto>>> = []
    const scope = effectScope()
    const search = scope.run(() =>
      useArticleSearch(({ signal }) => {
        signals.push(signal!)
        const request = deferred<SearchPageDto>()
        requests.push(request)
        return request.promise
      }),
    )!

    const first = search.load('深蹲', 1)
    const second = search.load('营养', 1)
    requests[1]!.resolve(pageDto({ records: [article({ id: '2', title: '营养文章' })] }))
    await second
    requests[0]!.resolve(pageDto({ records: [article({ id: '1', title: '深蹲文章' })] }))
    await first

    expect(signals[0]?.aborted).toBe(true)
    expect(search.keyword.value).toBe('营养')
    expect(search.pageData.value?.records[0]?.id).toBe('2')
    scope.stop()
  })
})

function article(overrides: Partial<SearchPageDto['records'][number]> = {}) {
  return {
    id: '1',
    categoryId: '101',
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

function pageDto(overrides: Partial<SearchPageDto> = {}): SearchPageDto {
  return { records: [], total: 0, page: 1, size: 10, ...overrides }
}

function deferred<T>() {
  let resolve!: (value: T) => void
  const promise = new Promise<T>((done) => {
    resolve = done
  })
  return { promise, resolve }
}
