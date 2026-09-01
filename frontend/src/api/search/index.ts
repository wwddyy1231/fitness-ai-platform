import { ApiClientError } from '@/api/error'
import type { ApiResponse } from '@/api/types'

import { apiClient } from '../client'
import type { SearchPageDto } from './types'

export interface SearchParams {
  keyword: string
  page: number
  size: number
  signal?: globalThis.AbortSignal
}

export async function searchArticles({
  keyword,
  page,
  size,
  signal,
}: SearchParams): Promise<SearchPageDto> {
  const response = await apiClient.get<ApiResponse<SearchPageDto>>('/v1/articles', {
    params: { keyword, page, size },
    signal,
  })
  if (!response.data.data) {
    throw new ApiClientError('搜索响应缺少数据', {
      code: response.data.code,
      status: response.status,
    })
  }
  return response.data.data
}

export type { SearchArticleDto, SearchPageDto } from './types'
