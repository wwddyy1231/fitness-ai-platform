import { createRouter, createWebHistory, type RouteRecordRaw } from 'vue-router'

const routes: RouteRecordRaw[] = [
  {
    path: '/',
    component: () => import('@/layouts/PortalLayout.vue'),
    children: [
      {
        path: '',
        name: 'home',
        component: () => import('@/views/HomeView.vue'),
        meta: {
          title: 'Fitness AI Platform｜专业健身资讯与 AI 健身助手',
          description:
            '获取力量训练、减脂、健身营养、器材与训练计划知识，并使用 AI 健身助手梳理你的下一步行动。',
        },
      },
    ],
  },
]

export const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes,
  scrollBehavior: () => ({ top: 0 }),
})

router.afterEach((to) => {
  document.title = typeof to.meta.title === 'string' ? to.meta.title : 'Fitness AI Platform'
  const description =
    typeof to.meta.description === 'string' ? to.meta.description : '专业健身资讯与 AI 健身助手'
  document
    .querySelector<HTMLMetaElement>('meta[name="description"]')
    ?.setAttribute('content', description)
})
