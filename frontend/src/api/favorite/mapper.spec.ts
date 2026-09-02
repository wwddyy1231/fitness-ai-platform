import { describe, expect, it } from 'vitest'

import { mapFavoriteStatusDto } from './mapper'
import type { FavoriteStatusDto } from './types'

describe('favorite mapper', () => {
  it('preserves a large string article ID', () => {
    expect(mapFavoriteStatusDto({ articleId: '9223372036854775807', favorited: true })).toEqual({
      articleId: '9223372036854775807',
      favorited: true,
    })
  })

  it('rejects numeric IDs instead of converting them', () => {
    const dto = {
      articleId: Number.MAX_SAFE_INTEGER + 1,
      favorited: false,
    } as unknown as FavoriteStatusDto
    expect(() => mapFavoriteStatusDto(dto)).toThrow('收藏文章 ID 必须以非空字符串传输')
  })

  it('rejects a non-boolean status', () => {
    const dto = { articleId: '10', favorited: 1 } as unknown as FavoriteStatusDto
    expect(() => mapFavoriteStatusDto(dto)).toThrow('收藏状态必须为 boolean')
  })
})
