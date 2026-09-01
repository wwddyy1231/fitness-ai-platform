import type { AxiosResponse } from 'axios'
import { describe, expect, it } from 'vitest'

import { apiClient, configureApiAuth } from './client'

describe('apiClient response handling', () => {
  it('rejects a business error when Jackson omitted data', async () => {
    const adapter = async (): Promise<AxiosResponse> => ({
      data: { code: 40401, message: '文章不存在' },
      status: 200,
      statusText: 'OK',
      headers: {},
      config: { headers: {} as AxiosResponse['config']['headers'] },
    })

    await expect(apiClient.get('/test', { adapter })).rejects.toMatchObject({
      message: '文章不存在',
      code: 40401,
      status: 200,
    })
  })

  it('adds a bearer token and invokes the unauthorized handler for HTTP 401', async () => {
    let authorization: unknown
    let unauthorizedCalls = 0
    configureApiAuth({
      getToken: () => 'stored-token',
      onUnauthorized: () => {
        unauthorizedCalls += 1
      },
    })
    const adapter = async (config: AxiosResponse['config']): Promise<AxiosResponse> => {
      authorization = config.headers.Authorization
      return Promise.reject({
        isAxiosError: true,
        message: 'Request failed with status code 401',
        config,
        response: {
          data: { code: 401, message: '请先登录' },
          status: 401,
          statusText: 'Unauthorized',
          headers: {},
          config,
        },
        toJSON: () => ({}),
      })
    }

    await expect(apiClient.get('/private', { adapter })).rejects.toMatchObject({ status: 401 })
    expect(authorization).toBe('Bearer stored-token')
    expect(unauthorizedCalls).toBe(1)
    configureApiAuth({ getToken: () => null, onUnauthorized: () => undefined })
  })
})
