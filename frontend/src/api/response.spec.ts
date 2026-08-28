import { describe, expect, it } from 'vitest'

import { isApiResponse } from './response'

describe('isApiResponse', () => {
  it('accepts the backend response envelope', () => {
    expect(isApiResponse({ code: 0, message: 'success', data: { id: '1' } })).toBe(true)
  })

  it('accepts responses whose null data was omitted by Jackson', () => {
    expect(isApiResponse({ code: 40401, message: '文章不存在' })).toBe(true)
  })

  it('rejects values that do not match the backend response envelope', () => {
    expect(isApiResponse({ status: 200, result: [] })).toBe(false)
    expect(isApiResponse(null)).toBe(false)
  })
})
