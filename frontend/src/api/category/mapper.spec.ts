import { describe, expect, it } from 'vitest'

import { mapArticlePageDto, mapCategoryDto } from './mapper'
import type { ArticleListDto, CategoryDto } from './types'

describe('category mapper', () => {
  it('maps category and article fields while preserving large string ids', () => {
    const category = mapCategoryDto(categoryDto())
    const result = mapArticlePageDto(
      {
        records: [articleDto()],
        total: 1,
        page: 1,
        size: 10,
      },
      category,
    )

    expect(category).toMatchObject({
      id: '9223372036854775807',
      parentId: '0',
      slug: 'fitness',
    })
    expect(result.records[0]).toMatchObject({
      id: '9223372036854775806',
      category: '健身文章',
      readCount: 12840,
      tags: ['深蹲', '力量'],
    })
  })

  it('normalizes nullable article fields and supplies a cover fallback', () => {
    const category = mapCategoryDto(categoryDto())
    const result = mapArticlePageDto(
      {
        records: [
          articleDto({
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
      },
      category,
    )

    expect(result).toMatchObject({ total: 0, page: 1, size: 10 })
    expect(result.records[0]).toMatchObject({
      category: '健身文章',
      summary: '暂无摘要',
      publishedAt: '',
      readCount: 0,
      tags: [],
    })
    expect(result.records[0]?.image.src).toBeTruthy()
  })

  it('rejects numeric category and article ids', () => {
    expect(() => mapCategoryDto(categoryDto({ id: 101 as unknown as string }))).toThrow(
      '数据库 ID 必须以字符串传输',
    )

    const category = mapCategoryDto(categoryDto())
    expect(() =>
      mapArticlePageDto(
        {
          records: [articleDto({ id: 101 as unknown as string })],
          total: 1,
          page: 1,
          size: 10,
        },
        category,
      ),
    ).toThrow('数据库 ID 必须以字符串传输')
  })
})

function categoryDto(overrides: Partial<CategoryDto> = {}): CategoryDto {
  return {
    id: '9223372036854775807',
    parentId: '0',
    name: '健身文章',
    slug: 'fitness',
    sort: 1,
    ...overrides,
  }
}

function articleDto(overrides: Partial<ArticleListDto> = {}): ArticleListDto {
  return {
    id: '9223372036854775806',
    categoryId: '9223372036854775807',
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
