import type { AxiosResponse } from 'axios'
import { describe, expect, it } from 'vitest'

import { apiClient } from './client'

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
})
