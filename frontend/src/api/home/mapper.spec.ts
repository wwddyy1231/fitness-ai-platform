import { describe, expect, it } from 'vitest'

import { isHomeViewModelEmpty, mapHomeDto } from './mapper'
import type { HomeArticleDto, HomeDto } from './types'

describe('mapHomeDto', () => {
  it('maps backend fields and keeps database ids as strings', () => {
    const model = mapHomeDto({
      ...emptyHomeDto(),
      categoryNavigation: [
        { id: '9223372036854775807', parentId: '0', name: '健身营养', slug: 'nutrition', sort: 1 },
      ],
      recommendedContent: [article({ id: '9223372036854775806' })],
      latestArticles: [article({ id: '9223372036854775805', viewCount: 42 })],
      hotArticles: [article({ id: '9223372036854775804' })],
    })

    expect(model.featuredArticle).toMatchObject({
      id: '9223372036854775806',
      category: '健身营养',
    })
    expect(model.latestArticles[0]).toMatchObject({
      id: '9223372036854775805',
      readCount: 42,
    })
    expect(model.trendingArticles[0]?.id).toBe('9223372036854775804')
    expect(model.fitnessCategories[0]?.id).toBe('9223372036854775807')
  })

  it('normalizes nullable article fields in one adapter', () => {
    const model = mapHomeDto({ ...emptyHomeDto(), latestArticles: [article()] })

    expect(model.featuredArticle).toMatchObject({
      summary: '暂无摘要',
      publishedAt: '',
      category: '健身资讯',
      readCount: 0,
    })
    expect(model.featuredArticle?.image.alt).toContain('封面图片')
    expect(model.featuredArticle?.image.src).toBeTruthy()
  })

  it('identifies an empty backend response', () => {
    expect(isHomeViewModelEmpty(mapHomeDto(emptyHomeDto()))).toBe(true)
  })

  it('rejects numeric database ids instead of risking precision loss', () => {
    const invalid = article({ id: 9_223_372_036_854_776_000 as unknown as string })
    expect(() => mapHomeDto({ ...emptyHomeDto(), latestArticles: [invalid] })).toThrow(
      '数据库 ID 必须以字符串传输',
    )
  })
})

function article(overrides: Partial<HomeArticleDto> = {}): HomeArticleDto {
  return {
    id: '101',
    categoryId: '9223372036854775807',
    title: '训练文章',
    summary: null,
    content: null,
    coverUrl: null,
    status: 'PUBLISHED',
    recommended: false,
    viewCount: Number.NaN,
    publishedAt: null,
    tags: null,
    ...overrides,
  }
}

function emptyHomeDto(): HomeDto {
  return {
    latestArticles: [],
    hotArticles: [],
    recommendedContent: [],
    categoryNavigation: [],
  }
}
