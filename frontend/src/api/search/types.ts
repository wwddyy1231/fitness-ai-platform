export interface SearchArticleDto {
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

export interface SearchPageDto {
  records: SearchArticleDto[]
  total: number
  page: number
  size: number
}
