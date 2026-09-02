import type { AxiosResponse } from 'axios'
import { afterEach, describe, expect, it, vi } from 'vitest'

import { apiClient } from '@/api/client'

import { favoriteArticle, getFavoriteStatus, unfavoriteArticle } from './index'

describe('favorite API', () => {
  afterEach(() => vi.restoreAllMocks())

  it.each([
    ['get', getFavoriteStatus],
    ['put', favoriteArticle],
    ['delete', unfavoriteArticle],
  ] as const)('uses %s for the article favorite resource', async (method, request) => {
    const signal = new AbortController().signal
    vi.spyOn(apiClient, 'request').mockResolvedValue({
      data: { code: 0, message: 'success', data: { articleId: '10', favorited: true } },
      status: 200,
    } as AxiosResponse)

    await expect(request('10', signal)).resolves.toMatchObject({ articleId: '10' })
    expect(apiClient.request).toHaveBeenCalledWith({
      method,
      url: '/v1/favorites/articles/10',
      signal,
    })
  })

  it('rejects a successful response without status data', async () => {
    vi.spyOn(apiClient, 'request').mockResolvedValue({
      data: { code: 0, message: 'success' },
      status: 200,
    } as AxiosResponse)

    await expect(getFavoriteStatus('10')).rejects.toMatchObject({
      message: '收藏状态响应缺少数据',
    })
  })
})
