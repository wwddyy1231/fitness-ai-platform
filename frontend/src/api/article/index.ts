import { ApiClientError } from '@/api/error'
import type { ApiResponse } from '@/api/types'

import { apiClient } from '../client'
import type { ArticleDetailDto } from './types'

export async function getArticleDetail(
  id: string,
  signal?: globalThis.AbortSignal,
): Promise<ArticleDetailDto> {
  const response = await apiClient.get<ApiResponse<ArticleDetailDto>>(
    `/v1/articles/${encodeURIComponent(id)}`,
    { signal },
  )
  if (!response.data.data) {
    throw new ApiClientError('文章详情响应缺少数据', {
      code: response.data.code,
      status: response.status,
    })
  }
  return response.data.data
}

export type { ArticleDetail, ArticleDetailDto } from './types'
