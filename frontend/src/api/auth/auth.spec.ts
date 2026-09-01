import type { AxiosResponse } from 'axios'
import { describe, expect, it } from 'vitest'

import { apiClient } from '@/api/client'

import { getCurrentUser, login } from './index'

describe('auth API', () => {
  it('posts login credentials to the existing backend endpoint', async () => {
    let requestUrl = ''
    let requestBody = ''
    const adapter = async (config: AxiosResponse['config']): Promise<AxiosResponse> => {
      requestUrl = config.url ?? ''
      requestBody = String(config.data)
      return response(
        {
          code: 0,
          message: 'success',
          data: {
            accessToken: 'token',
            tokenType: 'Bearer',
            expiresIn: 7200,
            user: user(),
          },
        },
        config,
      )
    }

    const originalAdapter = apiClient.defaults.adapter
    apiClient.defaults.adapter = adapter
    try {
      await expect(login({ username: 'member', password: 'password123' })).resolves.toMatchObject({
        accessToken: 'token',
      })
      expect(requestUrl).toBe('/v1/auth/login')
      expect(JSON.parse(requestBody)).toEqual({ username: 'member', password: 'password123' })
    } finally {
      apiClient.defaults.adapter = originalAdapter
    }
  })

  it('loads the current user from /me', async () => {
    const originalAdapter = apiClient.defaults.adapter
    apiClient.defaults.adapter = async (config) =>
      response({ code: 0, message: 'success', data: user() }, config)
    try {
      await expect(getCurrentUser()).resolves.toMatchObject({ id: '9223372036854775807' })
    } finally {
      apiClient.defaults.adapter = originalAdapter
    }
  })
})

function response(data: unknown, config: AxiosResponse['config']): AxiosResponse {
  return { data, status: 200, statusText: 'OK', headers: {}, config }
}

function user() {
  return {
    id: '9223372036854775807',
    username: 'member',
    nickname: '训练者',
    email: null,
    roles: ['MEMBER'],
  }
}
