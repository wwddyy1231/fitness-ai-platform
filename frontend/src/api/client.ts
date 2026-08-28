import axios from 'axios'

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
  (error: unknown) => Promise.reject(toApiClientError(error)),
)
