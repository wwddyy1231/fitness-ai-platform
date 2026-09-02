import type { FavoriteStatus, FavoriteStatusDto } from './types'

export function mapFavoriteStatusDto(dto: FavoriteStatusDto): FavoriteStatus {
  if (typeof dto.articleId !== 'string' || !dto.articleId.trim()) {
    throw new TypeError('收藏文章 ID 必须以非空字符串传输')
  }
  if (typeof dto.favorited !== 'boolean') throw new TypeError('收藏状态必须为 boolean')
  return { articleId: dto.articleId, favorited: dto.favorited }
}
