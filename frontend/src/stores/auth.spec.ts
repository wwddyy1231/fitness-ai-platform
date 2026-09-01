// @vitest-environment happy-dom

import { createPinia, setActivePinia } from 'pinia'
import { beforeEach, describe, expect, it, vi } from 'vitest'

import { getCurrentUser, login } from '@/api/auth'
import { ACCESS_TOKEN_KEY } from '@/utils/authStorage'

import { useAuthStore } from './auth'

vi.mock('@/api/auth', () => ({
  login: vi.fn(),
  getCurrentUser: vi.fn(),
}))

const mockedLogin = vi.mocked(login)
const mockedCurrentUser = vi.mocked(getCurrentUser)

describe('auth store', () => {
  beforeEach(() => {
    setActivePinia(createPinia())
    window.localStorage.clear()
    vi.clearAllMocks()
  })

  it('saves the token and user after login', async () => {
    mockedLogin.mockResolvedValue(loginDto())
    const store = useAuthStore()

    await expect(store.login({ username: 'member', password: 'password123' })).resolves.toBe(true)

    expect(store.isAuthenticated).toBe(true)
    expect(store.user?.id).toBe('9223372036854775807')
    expect(window.localStorage.getItem(ACCESS_TOKEN_KEY)).toBe('signed-token')
  })

  it('clears authentication on logout', async () => {
    mockedLogin.mockResolvedValue(loginDto())
    const store = useAuthStore()
    await store.login({ username: 'member', password: 'password123' })

    store.logout()

    expect(store.token).toBeNull()
    expect(store.user).toBeNull()
    expect(window.localStorage.getItem(ACCESS_TOKEN_KEY)).toBeNull()
  })

  it('restores and validates a stored token through /me', async () => {
    window.localStorage.setItem(ACCESS_TOKEN_KEY, 'stored-token')
    mockedCurrentUser.mockResolvedValue(userDto())
    const store = useAuthStore()

    await expect(store.restoreToken()).resolves.toBe(true)

    expect(store.token).toBe('stored-token')
    expect(store.user?.username).toBe('member')
    expect(store.status).toBe('authenticated')
  })
})

function loginDto() {
  return {
    accessToken: 'signed-token',
    tokenType: 'Bearer',
    expiresIn: 7200,
    user: userDto(),
  } as const
}

function userDto() {
  return {
    id: '9223372036854775807',
    username: 'member',
    nickname: '训练者',
    email: null,
    roles: ['MEMBER'],
  }
}
