import kettlebellCoachImage from '@/assets/images/kettlebell-coach.jpg'
import performanceNutritionImage from '@/assets/images/performance-nutrition.jpg'
import strengthSquatImage from '@/assets/images/strength-squat.jpg'
import type { ArticleSummary } from '@/types/content'

import type { ArticleListDto, ArticlePageDto, CategoryDto, CategoryInfo } from './types'

export interface CategoryArticlePage {
  records: ArticleSummary[]
  total: number
  page: number
  size: number
}

const presentationBySlug: Record<string, { description: string; fallbackImage: string }> = {
  fitness: {
    description: '训练方法、动作质量与恢复策略',
    fallbackImage: strengthSquatImage,
  },
  nutrition: {
    description: '表现、恢复与体重管理',
    fallbackImage: performanceNutritionImage,
  },
  equipment: {
    description: '器材选购、使用与维护',
    fallbackImage: kettlebellCoachImage,
  },
}

const defaultPresentation = {
  description: '专业健身内容与实用训练知识',
  fallbackImage: strengthSquatImage,
}

export function mapCategoryDto(dto: CategoryDto): CategoryInfo {
  const slug = dto.slug.trim()
  const presentation = presentationBySlug[slug] ?? defaultPresentation
  return {
    id: requireId(dto.id),
    parentId: requireId(dto.parentId),
    name: dto.name.trim() || '未命名分类',
    slug,
    description: presentation.description,
  }
}

export function mapArticlePageDto(
  dto: ArticlePageDto,
  category: CategoryInfo,
): CategoryArticlePage {
  const presentation = presentationBySlug[category.slug] ?? defaultPresentation
  return {
    records: dto.records.map((article) =>
      mapArticle(article, category, presentation.fallbackImage),
    ),
    total: normalizeCount(dto.total),
    page: normalizePositiveInteger(dto.page, 1),
    size: normalizePositiveInteger(dto.size, 10),
  }
}

function mapArticle(
  article: ArticleListDto,
  category: CategoryInfo,
  fallbackImage: string,
): ArticleSummary {
  const id = requireId(article.id)
  requireId(article.categoryId)
  const title = article.title.trim() || '未命名文章'
  return {
    id,
    category: article.categoryName?.trim() || category.name,
    title,
    summary: article.summary?.trim() || '暂无摘要',
    publishedAt: article.publishedAt ?? '',
    readCount: normalizeCount(article.viewCount),
    tags: (article.tags ?? []).map((tag) => tag.trim()).filter(Boolean),
    image: {
      src: normalizeCoverUrl(article.coverUrl) || fallbackImage,
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
