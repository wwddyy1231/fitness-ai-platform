export interface AuthUserDto {
  id: string
  username: string
  nickname: string
  email: string | null
  roles: string[]
}

export interface LoginRequest {
  username: string
  password: string
}

export interface LoginDto {
  accessToken: string
  tokenType: string
  expiresIn: number
  user: AuthUserDto
}

export interface AuthUser {
  id: string
  username: string
  nickname: string
  email: string | null
  roles: string[]
}

export interface LoginResult {
  accessToken: string
  tokenType: 'Bearer'
  expiresIn: number
  user: AuthUser
}
