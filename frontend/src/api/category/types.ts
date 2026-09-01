export interface CategoryDto {
  id: string
  parentId: string
  name: string
  slug: string
  sort: number
}

export interface ArticleListDto {
  id: string
  categoryId: string
  categoryName: string | null
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

export interface ArticlePageDto {
  records: ArticleListDto[]
  total: number
  page: number
  size: number
}

export interface CategoryInfo {
  id: string
  parentId: string
  name: string
  slug: string
  description: string
}
