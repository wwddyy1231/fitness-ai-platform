import type { AuthUser, AuthUserDto, LoginDto, LoginResult } from './types'

export function mapAuthUserDto(dto: AuthUserDto): AuthUser {
  return {
    id: requireStringId(dto.id),
    username: requireText(dto.username, 'username'),
    nickname: requireText(dto.nickname, 'nickname'),
    email: normalizeEmail(dto.email),
    roles: Array.isArray(dto.roles) ? dto.roles.map((role) => requireText(role, 'role')) : [],
  }
}

export function mapLoginDto(dto: LoginDto): LoginResult {
  const accessToken = requireText(dto.accessToken, 'accessToken')
  if (dto.tokenType !== 'Bearer') throw new TypeError('登录响应 tokenType 必须为 Bearer')
  if (!Number.isFinite(dto.expiresIn) || dto.expiresIn <= 0) {
    throw new TypeError('登录响应 expiresIn 无效')
  }

  return {
    accessToken,
    tokenType: 'Bearer',
    expiresIn: dto.expiresIn,
    user: mapAuthUserDto(dto.user),
  }
}

function requireStringId(value: string): string {
  if (typeof value !== 'string' || !value.trim()) {
    throw new TypeError('用户 ID 必须以非空字符串传输')
  }
  return value
}

function requireText(value: string, field: string): string {
  if (typeof value !== 'string' || !value.trim()) throw new TypeError(`登录响应 ${field} 无效`)
  return value.trim()
}

function normalizeEmail(value: string | null): string | null {
  if (value === null) return null
  if (typeof value !== 'string') throw new TypeError('登录响应 email 无效')
  return value.trim() || null
}
