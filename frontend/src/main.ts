import { createApp } from 'vue'

import App from './App.vue'
import { router } from './router'
import { createAppPinia } from './stores'
import './styles/index.css'

const app = createApp(App)

app.use(createAppPinia())
app.use(router)
app.mount('#app')
