// @vitest-environment happy-dom

import { flushPromises, mount } from '@vue/test-utils'
import ElementPlus from 'element-plus'
import { createPinia, setActivePinia } from 'pinia'
import { beforeEach, describe, expect, it, vi } from 'vitest'

import { useAuthStore } from '@/stores/auth'

import LoginView from './LoginView.vue'

const replace = vi.fn()

vi.mock('vue-router', async (importOriginal) => ({
  ...(await importOriginal<typeof import('vue-router')>()),
  useRouter: () => ({ replace }),
}))

describe('LoginView', () => {
  beforeEach(() => {
    setActivePinia(createPinia())
    window.localStorage.clear()
    replace.mockReset()
  })

  it('submits credentials, exposes loading, and redirects after success', async () => {
    const store = useAuthStore()
    let resolveLogin: ((value: boolean) => void) | undefined
    vi.spyOn(store, 'login').mockImplementation(
      () => new Promise<boolean>((resolve) => (resolveLogin = resolve)),
    )
    const wrapper = mount(LoginView, { global: { plugins: [ElementPlus] } })

    await wrapper.get('input[name="username"]').setValue('member')
    await wrapper.get('input[name="password"]').setValue('password123')
    await wrapper.get('form').trigger('submit')
    await flushPromises()

    expect(store.login).toHaveBeenCalledWith({ username: 'member', password: 'password123' })
    resolveLogin?.(true)
    await flushPromises()
    expect(replace).toHaveBeenCalledWith({ name: 'home' })
  })

  it('renders the store error message', async () => {
    const store = useAuthStore()
    store.errorMessage = '用户名或密码错误'

    const wrapper = mount(LoginView, { global: { plugins: [ElementPlus] } })

    expect(wrapper.text()).toContain('用户名或密码错误')
  })
})
