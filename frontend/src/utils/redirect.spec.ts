import { describe, expect, it } from 'vitest'

import { safeInternalRedirect } from './redirect'

describe('safeInternalRedirect', () => {
  it('accepts a local article path', () => {
    expect(safeInternalRedirect('/article/9223372036854775807')).toBe(
      '/article/9223372036854775807',
    )
  })

  it.each(['https://example.com', '//example.com', '/\\example.com'])('rejects %s', (value) => {
    expect(safeInternalRedirect(value)).toBeNull()
  })
})
