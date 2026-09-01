import { describe, expect, it } from 'vitest'

import { mapSearchPageDto } from './mapper'
import type { SearchArticleDto } from './types'

describe('search mapper', () => {
  it('maps results and preserves large string ids', () => {
    const result = mapSearchPageDto({
      records: [article()],
      total: 1,
      page: 1,
      size: 10,
    })

    expect(result.records[0]).toMatchObject({
      id: '9223372036854775807',
      category: '健身文章',
      readCount: 12840,
      tags: ['深蹲', '力量'],
    })
  })

  it('normalizes nullable fields and supplies a cover fallback', () => {
    const result = mapSearchPageDto({
      records: [
        article({
          categoryName: null,
          summary: null,
          coverUrl: null,
          publishedAt: null,
          tags: null,
          viewCount: Number.NaN,
        }),
      ],
      total: Number.NaN,
      page: 0,
      size: 0,
    })

    expect(result).toMatchObject({ total: 0, page: 1, size: 10 })
    expect(result.records[0]).toMatchObject({
      category: '健身资讯',
      summary: '暂无摘要',
      publishedAt: '',
      readCount: 0,
      tags: [],
    })
    expect(result.records[0]?.image.src).toBeTruthy()
  })

  it('rejects numeric article or category ids', () => {
    expect(() =>
      mapSearchPageDto({
        records: [article({ id: 101 as unknown as string })],
        total: 1,
        page: 1,
        size: 10,
      }),
    ).toThrow('数据库 ID 必须以字符串传输')

    expect(() =>
      mapSearchPageDto({
        records: [article({ categoryId: 101 as unknown as string })],
        total: 1,
        page: 1,
        size: 10,
      }),
    ).toThrow('数据库 ID 必须以字符串传输')
  })
})

function article(overrides: Partial<SearchArticleDto> = {}): SearchArticleDto {
  return {
    id: '9223372036854775807',
    categoryId: '101',
    categoryName: '健身文章',
    title: '深蹲训练基础',
    summary: '训练摘要',
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
