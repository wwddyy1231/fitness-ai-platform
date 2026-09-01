import type { AxiosResponse } from 'axios'
import { afterEach, describe, expect, it, vi } from 'vitest'

import { apiClient } from '@/api/client'

import { getCategories, getCategoryArticlePage } from './index'

describe('category API', () => {
  afterEach(() => vi.restoreAllMocks())

  it('requests categories with the provided signal', async () => {
    const signal = new AbortController().signal
    vi.spyOn(apiClient, 'get').mockResolvedValue({
      data: { code: 0, message: 'success', data: [] },
      status: 200,
    } as AxiosResponse)

    await expect(getCategories(signal)).resolves.toEqual([])
    expect(apiClient.get).toHaveBeenCalledWith('/v1/categories', { signal })
  })

  it('sends string categoryId, page and size as article query parameters', async () => {
    const signal = new AbortController().signal
    vi.spyOn(apiClient, 'get').mockResolvedValue({
      data: {
        code: 0,
        message: 'success',
        data: { records: [], total: 0, page: 2, size: 10 },
      },
      status: 200,
    } as AxiosResponse)

    await getCategoryArticlePage({
      categoryId: '9223372036854775807',
      page: 2,
      size: 10,
      signal,
    })

    expect(apiClient.get).toHaveBeenCalledWith('/v1/articles', {
      params: { categoryId: '9223372036854775807', page: 2, size: 10 },
      signal,
    })
  })

  it('rejects a numeric category id at runtime', async () => {
    const request = getCategoryArticlePage({
      categoryId: 9_223_372_036_854_776_000 as unknown as string,
      page: 1,
      size: 10,
    })

    await expect(request).rejects.toThrow('数据库 ID 必须以字符串传输')
  })
})
