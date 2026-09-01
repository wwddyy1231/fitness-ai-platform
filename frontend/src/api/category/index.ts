import { ApiClientError } from '@/api/error'
import type { ApiResponse } from '@/api/types'

import { apiClient } from '../client'
import type { ArticlePageDto, CategoryDto } from './types'

export interface ArticlePageParams {
  categoryId: string
  page: number
  size: number
  signal?: globalThis.AbortSignal
}

export async function getCategories(signal?: globalThis.AbortSignal): Promise<CategoryDto[]> {
  const response = await apiClient.get<ApiResponse<CategoryDto[]>>('/v1/categories', { signal })
  if (!response.data.data) {
    throw new ApiClientError('分类响应缺少数据', {
      code: response.data.code,
      status: response.status,
    })
  }
  return response.data.data
}

export async function getCategoryArticlePage({
  categoryId,
  page,
  size,
  signal,
}: ArticlePageParams): Promise<ArticlePageDto> {
  requireId(categoryId)
  const response = await apiClient.get<ApiResponse<ArticlePageDto>>('/v1/articles', {
    params: { categoryId, page, size },
    signal,
  })
  if (!response.data.data) {
    throw new ApiClientError('文章分页响应缺少数据', {
      code: response.data.code,
      status: response.status,
    })
  }
  return response.data.data
}

function requireId(value: string): void {
  if (typeof value !== 'string') throw new TypeError('数据库 ID 必须以字符串传输')
}

export type { ArticleListDto, ArticlePageDto, CategoryDto, CategoryInfo } from './types'
