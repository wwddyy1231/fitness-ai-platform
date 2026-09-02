// @vitest-environment happy-dom

import { flushPromises, mount } from '@vue/test-utils'
import ElementPlus from 'element-plus'
import { createPinia, setActivePinia } from 'pinia'
import { beforeEach, describe, expect, it, vi } from 'vitest'

import { useAuthStore } from '@/stores/auth'

import LoginView from './LoginView.vue'

const replace = vi.fn()
const routeQuery: { redirect?: string } = {}

vi.mock('vue-router', async (importOriginal) => ({
  ...(await importOriginal<typeof import('vue-router')>()),
  useRouter: () => ({ replace }),
  useRoute: () => ({ query: routeQuery }),
}))

describe('LoginView', () => {
  beforeEach(() => {
    setActivePinia(createPinia())
    window.localStorage.clear()
    replace.mockReset()
    delete routeQuery.redirect
  })

  it('submits credentials, exposes loading, and redirects after success', async () => {
    const store = useAuthStore()
    let resolveLogin: ((value: boolean) => void) | undefined
    vi.spyOn(store, 'login').mockImplementation(
      () => new Promise<boolean>((resolve) => (resolveLogin = resolve)),
    )
    const wrapper = mount(LoginView, { global: { plugins: [ElementPlus] } })

    await fillAndSubmit(wrapper)
    expect(store.login).toHaveBeenCalledWith({ username: 'member', password: 'password123' })
    resolveLogin?.(true)
    await flushPromises()
    expect(replace).toHaveBeenCalledWith({ name: 'home' })
  })

  it('renders the store error message', () => {
    const store = useAuthStore()
    store.errorMessage = '用户名或密码错误'
    const wrapper = mount(LoginView, { global: { plugins: [ElementPlus] } })
    expect(wrapper.text()).toContain('用户名或密码错误')
  })

  it('returns to a safe internal redirect after login', async () => {
    routeQuery.redirect = '/article/9223372036854775807'
    const store = useAuthStore()
    vi.spyOn(store, 'login').mockResolvedValue(true)
    const wrapper = mount(LoginView, { global: { plugins: [ElementPlus] } })

    await fillAndSubmit(wrapper)

    expect(replace).toHaveBeenCalledWith('/article/9223372036854775807')
  })

  it('rejects an external redirect and returns home', async () => {
    routeQuery.redirect = '//example.com'
    const store = useAuthStore()
    vi.spyOn(store, 'login').mockResolvedValue(true)
    const wrapper = mount(LoginView, { global: { plugins: [ElementPlus] } })

    await fillAndSubmit(wrapper)

    expect(replace).toHaveBeenCalledWith({ name: 'home' })
  })
})

async function fillAndSubmit(wrapper: ReturnType<typeof mount>): Promise<void> {
  await wrapper.get('input[name="username"]').setValue('member')
  await wrapper.get('input[name="password"]').setValue('password123')
  await wrapper.get('form').trigger('submit')
  await flushPromises()
}
