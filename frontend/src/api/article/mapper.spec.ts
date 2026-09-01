import { describe, expect, it } from 'vitest'

import { mapArticleDetailDto } from './mapper'
import type { ArticleDetailDto } from './types'

describe('mapArticleDetailDto', () => {
  it('maps a complete article and preserves a large string id', () => {
    const result = mapArticleDetailDto(
      article({ id: '9223372036854775807', categoryId: '9223372036854775806' }),
    )

    expect(result).toMatchObject({
      id: '9223372036854775807',
      categoryId: '9223372036854775806',
      categoryName: '力量训练',
      title: '深蹲训练基础',
      viewCount: 12840,
      tags: ['深蹲', '力量'],
    })
  })

  it('normalizes nullable and blank fields without producing undefined', () => {
    const result = mapArticleDetailDto(
      article({ categoryName: null, summary: ' ', content: null, coverUrl: null, tags: null }),
    )

    expect(result.categoryName).toBe('健身资讯')
    expect(result.summary).toBeNull()
    expect(result.content).toBeNull()
    expect(result.coverUrl).toBeTruthy()
    expect(result.tags).toEqual([])
  })

  it('rejects numeric database ids instead of converting them', () => {
    const invalid = article({ id: 9_223_372_036_854_776_000 as unknown as string })

    expect(() => mapArticleDetailDto(invalid)).toThrow('数据库 ID 必须以字符串传输')
  })
})

function article(overrides: Partial<ArticleDetailDto> = {}): ArticleDetailDto {
  return {
    id: '101',
    categoryId: '10',
    categoryName: '力量训练',
    title: '深蹲训练基础',
    summary: '摘要',
    content: '正文',
    coverUrl: '/files/squat.jpg',
    status: 'PUBLISHED',
    recommended: true,
    viewCount: 12840,
    publishedAt: '2026-08-30T09:00:00',
    tags: ['深蹲', '力量'],
    ...overrides,
  }
}
