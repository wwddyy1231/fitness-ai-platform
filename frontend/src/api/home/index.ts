import { ApiClientError } from '@/api/error'
import type { ApiResponse } from '@/api/types'

import { apiClient } from '../client'
import type { HomeDto } from './types'

export async function getHome(signal?: globalThis.AbortSignal): Promise<HomeDto> {
  const response = await apiClient.get<ApiResponse<HomeDto>>('/v1/home', { signal })
  if (!response.data.data) {
    throw new ApiClientError('首页响应缺少数据', {
      code: response.data.code,
      status: response.status,
    })
  }
  return response.data.data
}

export type { HomeArticleDto, HomeCategoryDto, HomeDto } from './types'
