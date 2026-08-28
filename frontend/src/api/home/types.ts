export interface HomeArticleDto {
  id: string
  categoryId: string
  title: string
  summary: string | null
  content: string | null
  coverUrl: string | null
  status: string
  recommended: boolean
  viewCount: number
  publishedAt: string | null
  tags: string[] | null
}

export interface HomeCategoryDto {
  id: string
  parentId: string
  name: string
  slug: string
  sort: number
}

export interface HomeDto {
  latestArticles: HomeArticleDto[]
  hotArticles: HomeArticleDto[]
  recommendedContent: HomeArticleDto[]
  categoryNavigation: HomeCategoryDto[]
}
