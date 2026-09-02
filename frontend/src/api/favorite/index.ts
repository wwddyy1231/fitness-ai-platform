import { ApiClientError } from '@/api/error'
import type { ApiResponse } from '@/api/types'

import { apiClient } from '../client'
import type { FavoriteStatusDto } from './types'

export async function getFavoriteStatus(
  articleId: string,
  signal?: AbortSignal,
): Promise<FavoriteStatusDto> {
  return request('get', articleId, signal)
}

export async function favoriteArticle(
  articleId: string,
  signal?: AbortSignal,
): Promise<FavoriteStatusDto> {
  return request('put', articleId, signal)
}

export async function unfavoriteArticle(
  articleId: string,
  signal?: AbortSignal,
): Promise<FavoriteStatusDto> {
  return request('delete', articleId, signal)
}

async function request(
  method: 'get' | 'put' | 'delete',
  articleId: string,
  signal?: AbortSignal,
): Promise<FavoriteStatusDto> {
  const response = await apiClient.request<ApiResponse<FavoriteStatusDto>>({
    method,
    url: `/v1/favorites/articles/${encodeURIComponent(articleId)}`,
    signal,
  })
  if (!response.data.data) {
    throw new ApiClientError('收藏状态响应缺少数据', {
      code: response.data.code,
      status: response.status,
    })
  }
  return response.data.data
}

export type { FavoriteStatus, FavoriteStatusDto } from './types'
