import type { AxiosResponse } from 'axios'
import { afterEach, describe, expect, it, vi } from 'vitest'

import { apiClient } from '@/api/client'

import { getHome } from './index'
import type { HomeDto } from './types'

describe('getHome', () => {
  afterEach(() => vi.restoreAllMocks())

  it('requests the backend home endpoint with the provided signal', async () => {
    const dto = emptyHomeDto()
    const signal = new AbortController().signal
    vi.spyOn(apiClient, 'get').mockResolvedValue({
      data: { code: 0, message: 'success', data: dto },
      status: 200,
    } as AxiosResponse)

    await expect(getHome(signal)).resolves.toBe(dto)
    expect(apiClient.get).toHaveBeenCalledWith('/v1/home', { signal })
  })

  it('rejects a successful envelope without home data', async () => {
    vi.spyOn(apiClient, 'get').mockResolvedValue({
      data: { code: 0, message: 'success' },
      status: 200,
    } as AxiosResponse)

    await expect(getHome()).rejects.toMatchObject({ message: '首页响应缺少数据' })
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
