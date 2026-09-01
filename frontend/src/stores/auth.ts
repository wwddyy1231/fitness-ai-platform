import { defineStore } from 'pinia'
import { computed, ref, shallowRef } from 'vue'

import { getCurrentUser, login as requestLogin, type AuthUser, type LoginRequest } from '@/api/auth'
import { mapAuthUserDto, mapLoginDto } from '@/api/auth/mapper'
import { toApiClientError } from '@/api/error'
import { readAccessToken, removeAccessToken, writeAccessToken } from '@/utils/authStorage'

export type AuthStatus = 'idle' | 'loading' | 'authenticated' | 'error'

export const useAuthStore = defineStore('auth', () => {
  const token = ref<string | null>(readAccessToken())
  const user = shallowRef<AuthUser | null>(null)
  const status = ref<AuthStatus>('idle')
  const errorMessage = ref('')
  const isAuthenticated = computed(() => Boolean(token.value && user.value))

  async function login(credentials: LoginRequest): Promise<boolean> {
    status.value = 'loading'
    errorMessage.value = ''
    try {
      const result = mapLoginDto(await requestLogin(credentials))
      token.value = result.accessToken
      user.value = result.user
      writeAccessToken(result.accessToken)
      status.value = 'authenticated'
      return true
    } catch (error: unknown) {
      const normalized = toApiClientError(error)
      token.value = null
      user.value = null
      removeAccessToken()
      errorMessage.value = normalized.message
      status.value = 'error'
      return false
    }
  }

  function logout(): void {
    token.value = null
    user.value = null
    status.value = 'idle'
    errorMessage.value = ''
    removeAccessToken()
  }

  async function restoreToken(): Promise<boolean> {
    if (!token.value) {
      status.value = 'idle'
      return false
    }

    status.value = 'loading'
    errorMessage.value = ''
    try {
      user.value = mapAuthUserDto(await getCurrentUser())
      status.value = 'authenticated'
      return true
    } catch {
      logout()
      return false
    }
  }

  return { token, user, status, errorMessage, isAuthenticated, login, logout, restoreToken }
})
