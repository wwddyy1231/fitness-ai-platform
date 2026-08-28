import axios from 'axios'

import { isApiResponse } from './response'

export class ApiClientError extends Error {
  readonly code: number | undefined
  readonly status: number | undefined

  constructor(message: string, options: { code?: number; status?: number; cause?: unknown } = {}) {
    super(message, { cause: options.cause })
    this.name = 'ApiClientError'
    this.code = options.code
    this.status = options.status
  }
}

export function toApiClientError(error: unknown): ApiClientError {
  if (error instanceof ApiClientError) return error

  if (axios.isAxiosError(error)) {
    const body: unknown = error.response?.data
    if (isApiResponse(body)) {
      return new ApiClientError(body.message, {
        code: body.code,
        status: error.response?.status,
        cause: error,
      })
    }

    return new ApiClientError(error.message || '网络请求失败', {
      status: error.response?.status,
      cause: error,
    })
  }

  return new ApiClientError('发生未知请求错误', { cause: error })
}
