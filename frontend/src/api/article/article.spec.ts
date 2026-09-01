import type { AxiosResponse } from 'axios'
import { afterEach, describe, expect, it, vi } from 'vitest'

import { apiClient } from '@/api/client'

import { getArticleDetail } from './index'
import type { ArticleDetailDto } from './types'

describe('getArticleDetail', () => {
  afterEach(() => vi.restoreAllMocks())

  it('requests an article with the provided signal', async () => {
    const dto = article()
    const signal = new AbortController().signal
    vi.spyOn(apiClient, 'get').mockResolvedValue({
      data: { code: 0, message: 'success', data: dto },
      status: 200,
    } as AxiosResponse)

    await expect(getArticleDetail('9223372036854775807', signal)).resolves.toBe(dto)
    expect(apiClient.get).toHaveBeenCalledWith('/v1/articles/9223372036854775807', { signal })
  })

  it('rejects a successful envelope without article data', async () => {
    vi.spyOn(apiClient, 'get').mockResolvedValue({
      data: { code: 0, message: 'success' },
      status: 200,
    } as AxiosResponse)

    await expect(getArticleDetail('101')).rejects.toMatchObject({
      message: '文章详情响应缺少数据',
    })
  })
})

function article(): ArticleDetailDto {
  return {
    id: '101',
    categoryId: '10',
    categoryName: '力量训练',
    title: '深蹲训练基础',
    summary: null,
    content: '正文',
    coverUrl: null,
    status: 'PUBLISHED',
    recommended: true,
    viewCount: 10,
    publishedAt: null,
    tags: [],
  }
}
