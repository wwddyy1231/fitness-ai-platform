export interface ApiResponse<T> {
  code: number
  message: string
  data: T | null
}

export interface PageResponse<T> {
  records: T[]
  total: number
  page: number
  size: number
}
