const ACCESS_TOKEN_KEY = 'fitness-ai.access-token'

export function readAccessToken(): string | null {
  if (typeof window === 'undefined') return null
  const token = window.localStorage.getItem(ACCESS_TOKEN_KEY)?.trim()
  return token || null
}

export function writeAccessToken(token: string): void {
  if (typeof window !== 'undefined') window.localStorage.setItem(ACCESS_TOKEN_KEY, token)
}

export function removeAccessToken(): void {
  if (typeof window !== 'undefined') window.localStorage.removeItem(ACCESS_TOKEN_KEY)
}

export { ACCESS_TOKEN_KEY }
