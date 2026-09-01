import { createApp } from 'vue'

import App from './App.vue'
import { configureApiAuth } from './api/client'
import { router } from './router'
import { createAppPinia } from './stores'
import { useAuthStore } from './stores/auth'
import './styles/index.css'

const app = createApp(App)
const pinia = createAppPinia()

app.use(pinia)
app.use(router)
const authStore = useAuthStore(pinia)

configureApiAuth({
  getToken: () => authStore.token,
  onUnauthorized: async () => {
    authStore.logout()
    if (router.currentRoute.value.name !== 'login') {
      await router.replace({
        name: 'login',
        query: { redirect: router.currentRoute.value.fullPath },
      })
    }
  },
})

void authStore.restoreToken()
app.mount('#app')
