import { ApiClientError } from '@/api/error'
import type { ApiResponse } from '@/api/types'

import { apiClient } from '../client'
import type { AuthUserDto, LoginDto, LoginRequest } from './types'

export async function login(request: LoginRequest): Promise<LoginDto> {
  const response = await apiClient.post<ApiResponse<LoginDto>>('/v1/auth/login', request)
  if (!response.data.data) throw missingDataError('登录', response.status, response.data.code)
  return response.data.data
}

export async function getCurrentUser(signal?: AbortSignal): Promise<AuthUserDto> {
  const response = await apiClient.get<ApiResponse<AuthUserDto>>('/v1/auth/me', { signal })
  if (!response.data.data) throw missingDataError('当前用户', response.status, response.data.code)
  return response.data.data
}

function missingDataError(label: string, status: number, code: number): ApiClientError {
  return new ApiClientError(`${label}响应缺少数据`, { status, code })
}

export type { AuthUser, AuthUserDto, LoginDto, LoginRequest, LoginResult } from './types'
