import type { AxiosResponse } from 'axios'
import { afterEach, describe, expect, it, vi } from 'vitest'

import { apiClient } from '@/api/client'

import { searchArticles } from './index'

describe('searchArticles', () => {
  afterEach(() => vi.restoreAllMocks())

  it('sends keyword, page and size to the existing article endpoint', async () => {
    const signal = new AbortController().signal
    vi.spyOn(apiClient, 'get').mockResolvedValue({
      data: {
        code: 0,
        message: 'success',
        data: { records: [], total: 0, page: 2, size: 10 },
      },
      status: 200,
    } as AxiosResponse)

    await searchArticles({ keyword: '深蹲', page: 2, size: 10, signal })

    expect(apiClient.get).toHaveBeenCalledWith('/v1/articles', {
      params: { keyword: '深蹲', page: 2, size: 10 },
      signal,
    })
  })

  it('rejects an envelope without page data', async () => {
    vi.spyOn(apiClient, 'get').mockResolvedValue({
      data: { code: 0, message: 'success' },
      status: 200,
    } as AxiosResponse)

    await expect(searchArticles({ keyword: '深蹲', page: 1, size: 10 })).rejects.toMatchObject({
      message: '搜索响应缺少数据',
    })
  })
})
