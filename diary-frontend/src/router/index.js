import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue')
  },
  {
    path: '/',
    name: 'DiaryList',
    component: () => import('@/views/DiaryList.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/write',
    name: 'DiaryWrite',
    component: () => import('@/views/DiaryWrite.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/write/:id',
    name: 'DiaryEdit',
    component: () => import('@/views/DiaryWrite.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/detail/:id',
    name: 'DiaryDetail',
    component: () => import('@/views/DiaryDetail.vue'),
    meta: { requiresAuth: true }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  if (to.meta.requiresAuth && !token) {
    next('/login')
  } else if (to.path === '/login' && token) {
    next('/')
  } else {
    next()
  }
})

export default router