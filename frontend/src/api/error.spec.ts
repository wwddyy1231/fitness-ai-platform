import axios from 'axios'
import { describe, expect, it } from 'vitest'

import { toApiClientError } from './error'

describe('toApiClientError', () => {
  it('preserves the backend error code and message when data is omitted', () => {
    const error = new axios.AxiosError('Request failed', 'ERR_BAD_REQUEST', undefined, undefined, {
      data: { code: 403, message: '无权访问该资源' },
      status: 403,
      statusText: 'Forbidden',
      headers: {},
      config: { headers: new axios.AxiosHeaders() },
    })

    expect(toApiClientError(error)).toMatchObject({
      message: '无权访问该资源',
      code: 403,
      status: 403,
    })
  })
})
