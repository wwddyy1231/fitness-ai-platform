export interface ArticleDetailDto {
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

export interface ArticleDetail {
  id: string
  categoryId: string
  categoryName: string
  title: string
  summary: string | null
  content: string | null
  coverUrl: string | null
  coverAlt: string
  viewCount: number
  publishedAt: string | null
  tags: string[]
}
