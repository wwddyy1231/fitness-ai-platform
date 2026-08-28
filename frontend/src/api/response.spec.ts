import { describe, expect, it } from 'vitest'

import { isApiResponse } from './response'

describe('isApiResponse', () => {
  it('accepts the backend response envelope', () => {
    expect(isApiResponse({ code: 0, message: 'success', data: null })).toBe(true)
  })

  it('rejects values that do not match the backend response envelope', () => {
    expect(isApiResponse({ status: 200, result: [] })).toBe(false)
    expect(isApiResponse(null)).toBe(false)
  })
})
