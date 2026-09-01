import axios from 'axios'
import type { InternalAxiosRequestConfig } from 'axios'

import { ApiClientError, toApiClientError } from './error'
import { isApiResponse } from './response'

const DEFAULT_TIMEOUT_MS = 10_000

function getTimeout(): number {
  const configured = Number(import.meta.env.VITE_API_TIMEOUT_MS)
  return Number.isFinite(configured) && configured > 0 ? configured : DEFAULT_TIMEOUT_MS
}

export const apiClient = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL?.trim() || '/api',
  timeout: getTimeout(),
  headers: {
    Accept: 'application/json',
  },
})

interface ApiAuthHooks {
  getToken: () => string | null
  onUnauthorized: () => void | Promise<void>
}

let authHooks: ApiAuthHooks = {
  getToken: () => null,
  onUnauthorized: () => undefined,
}

export function configureApiAuth(hooks: ApiAuthHooks): void {
  authHooks = hooks
}

apiClient.interceptors.request.use((config: InternalAxiosRequestConfig) => {
  const token = authHooks.getToken()
  if (token) config.headers.Authorization = `Bearer ${token}`
  return config
})

apiClient.interceptors.response.use(
  (response) => {
    if (isApiResponse(response.data) && response.data.code !== 0) {
      return Promise.reject(
        new ApiClientError(response.data.message, {
          code: response.data.code,
          status: response.status,
        }),
      )
    }

    return response
  },
  async (error: unknown) => {
    const normalized = toApiClientError(error)
    if (normalized.status === 401) await authHooks.onUnauthorized()
    return Promise.reject(normalized)
  },
)
