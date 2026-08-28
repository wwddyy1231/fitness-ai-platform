import kettlebellCoachImage from '@/assets/images/kettlebell-coach.jpg'
import performanceNutritionImage from '@/assets/images/performance-nutrition.jpg'
import strengthSquatImage from '@/assets/images/strength-squat.jpg'
import type {
  ArticleSummary,
  CategoryIcon,
  FitnessCategory,
  TrendingArticle,
} from '@/types/content'

import type { HomeArticleDto, HomeCategoryDto, HomeDto } from './types'

export interface HomeViewModel {
  featuredArticle: ArticleSummary | null
  featuredRecommendations: ArticleSummary[]
  trendingArticles: TrendingArticle[]
  latestArticles: ArticleSummary[]
  fitnessCategories: FitnessCategory[]
}

const categoryPresentation: Record<
  string,
  { icon: CategoryIcon; description: string; fallbackImage: string }
> = {
  fitness: {
    icon: 'strength',
    description: '训练方法、动作质量与恢复策略',
    fallbackImage: strengthSquatImage,
  },
  nutrition: {
    icon: 'nutrition',
    description: '表现、恢复与体重管理',
    fallbackImage: performanceNutritionImage,
  },
  equipment: {
    icon: 'equipment',
    description: '器材选购、使用与维护',
    fallbackImage: kettlebellCoachImage,
  },
  muscle: {
    icon: 'muscle',
    description: '训练容量与渐进超负荷',
    fallbackImage: strengthSquatImage,
  },
  'fat-loss': {
    icon: 'fat-loss',
    description: '饮食、训练与行为习惯',
    fallbackImage: kettlebellCoachImage,
  },
  home: {
    icon: 'home',
    description: '有限器械下的高效训练',
    fallbackImage: kettlebellCoachImage,
  },
}

const defaultPresentation = {
  icon: 'strength' as const,
  description: '专业健身内容与实用训练知识',
  fallbackImage: strengthSquatImage,
}

export function mapHomeDto(dto: HomeDto): HomeViewModel {
  const categoryById = new Map(
    dto.categoryNavigation.map((category) => [requireId(category.id), category]),
  )
  const recommended = dto.recommendedContent.map((article) => mapArticle(article, categoryById))
  const latest = dto.latestArticles.map((article) => mapArticle(article, categoryById))
  const featuredArticle = recommended[0] ?? latest[0] ?? null
  const recommendationCandidates = [...recommended.slice(1), ...latest].filter(
    (article, index, items) =>
      article.id !== featuredArticle?.id &&
      items.findIndex((item) => item.id === article.id) === index,
  )

  return {
    featuredArticle,
    featuredRecommendations: recommendationCandidates.slice(0, 4),
    trendingArticles: dto.hotArticles.slice(0, 5).map((article) => ({
      id: requireId(article.id),
      title: article.title,
      category: categoryName(article.categoryId, categoryById),
    })),
    latestArticles: latest,
    fitnessCategories: dto.categoryNavigation.map(mapCategory),
  }
}

export function isHomeViewModelEmpty(model: HomeViewModel): boolean {
  return (
    !model.featuredArticle &&
    model.trendingArticles.length === 0 &&
    model.latestArticles.length === 0 &&
    model.fitnessCategories.length === 0
  )
}

function mapArticle(
  article: HomeArticleDto,
  categoryById: ReadonlyMap<string, HomeCategoryDto>,
): ArticleSummary {
  const id = requireId(article.id)
  const category = categoryById.get(requireId(article.categoryId))
  const presentation = categoryPresentation[category?.slug ?? ''] ?? defaultPresentation
  const title = article.title.trim() || '未命名文章'

  return {
    id,
    category: category?.name || '健身资讯',
    title,
    summary: article.summary?.trim() || '暂无摘要',
    publishedAt: article.publishedAt ?? '',
    readCount: Number.isFinite(article.viewCount) ? article.viewCount : 0,
    image: {
      src: normalizeCoverUrl(article.coverUrl) || presentation.fallbackImage,
      alt: `${title}封面图片`,
      width: 1600,
      height: 1067,
    },
  }
}

function mapCategory(category: HomeCategoryDto): FitnessCategory {
  requireId(category.parentId)
  const presentation = categoryPresentation[category.slug] ?? defaultPresentation
  return {
    id: requireId(category.id),
    name: category.name,
    description: presentation.description,
    articleCount: null,
    icon: presentation.icon,
  }
}

function categoryName(
  categoryId: string,
  categoryById: ReadonlyMap<string, HomeCategoryDto>,
): string {
  return categoryById.get(requireId(categoryId))?.name || '健身资讯'
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
