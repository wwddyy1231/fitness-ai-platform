export interface ImageAsset {
  src: string
  alt: string
  width: number
  height: number
}

export interface ArticleSummary {
  id: string
  category: string
  title: string
  summary: string
  publishedAt: string
  readCount: number
  tags: string[]
  image: ImageAsset
}

export interface TrendingArticle {
  id: string
  title: string
  category: string
}

export type CategoryIcon = 'muscle' | 'fat-loss' | 'strength' | 'home' | 'nutrition' | 'equipment'

export interface FitnessCategory {
  id: string
  slug: string
  name: string
  description: string
  articleCount: number | null
  icon: CategoryIcon
}

export type TrainingLevel = '初级' | '中级' | '高级'

export interface TrainingPlan {
  id: string
  level: TrainingLevel
  title: string
  duration: string
  frequency: string
  focus: string
}

export interface FitnessVideo {
  id: string
  title: string
  category: string
  duration: string
  image: ImageAsset
}
