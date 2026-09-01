import strengthSquatImage from '@/assets/images/strength-squat.jpg'
import type { ArticleSummary } from '@/types/content'

import type { SearchArticleDto, SearchPageDto } from './types'

export interface SearchResultPage {
  records: ArticleSummary[]
  total: number
  page: number
  size: number
}

export function mapSearchPageDto(dto: SearchPageDto): SearchResultPage {
  return {
    records: dto.records.map(mapArticle),
    total: normalizeCount(dto.total),
    page: normalizePositiveInteger(dto.page, 1),
    size: normalizePositiveInteger(dto.size, 10),
  }
}

function mapArticle(article: SearchArticleDto): ArticleSummary {
  const id = requireId(article.id)
  requireId(article.categoryId)
  const title = article.title.trim() || '未命名文章'
  return {
    id,
    category: article.categoryName?.trim() || '健身资讯',
    title,
    summary: article.summary?.trim() || '暂无摘要',
    publishedAt: article.publishedAt ?? '',
    readCount: normalizeCount(article.viewCount),
    tags: (article.tags ?? []).map((tag) => tag.trim()).filter(Boolean),
    image: {
      src: normalizeCoverUrl(article.coverUrl) || strengthSquatImage,
      alt: `${title}封面图片`,
      width: 1600,
      height: 1067,
    },
  }
}

function normalizeCoverUrl(value: string | null): string {
  const url = value?.trim()
  if (!url) return ''
  if (/^https?:\/\//i.test(url) || url.startsWith('/')) return url
  return `/${url}`
}

function normalizeCount(value: number): number {
  return Number.isFinite(value) && value >= 0 ? value : 0
}

function normalizePositiveInteger(value: number, fallback: number): number {
  return Number.isSafeInteger(value) && value > 0 ? value : fallback
}

function requireId(value: string): string {
  if (typeof value !== 'string') throw new TypeError('数据库 ID 必须以字符串传输')
  return value
}
