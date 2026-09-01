import strengthSquatImage from '@/assets/images/strength-squat.jpg'

import type { ArticleDetail, ArticleDetailDto } from './types'

export function mapArticleDetailDto(dto: ArticleDetailDto): ArticleDetail {
  const id = requireId(dto.id)
  const categoryId = requireId(dto.categoryId)
  const title = dto.title.trim() || '未命名文章'

  return {
    id,
    categoryId,
    categoryName: dto.categoryName?.trim() || '健身资讯',
    title,
    summary: normalizeOptionalText(dto.summary),
    content: normalizeOptionalText(dto.content),
    coverUrl: normalizeCoverUrl(dto.coverUrl) || strengthSquatImage,
    coverAlt: `${title}封面图片`,
    viewCount: Number.isFinite(dto.viewCount) ? dto.viewCount : 0,
    publishedAt: dto.publishedAt,
    tags: (dto.tags ?? []).map((tag) => tag.trim()).filter(Boolean),
  }
}

function normalizeOptionalText(value: string | null): string | null {
  const normalized = value?.trim()
  return normalized || null
}

function normalizeCoverUrl(value: string | null): string {
  const url = value?.trim()
  if (!url) return ''
  if (/^https?:\/\//i.test(url) || url.startsWith('/')) return url
  return `/${url}`
}

function requireId(value: string): string {
  if (typeof value !== 'string') throw new TypeError('数据库 ID 必须以字符串传输')
  return value
}
