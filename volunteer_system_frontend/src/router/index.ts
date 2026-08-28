import { createRouter, createWebHistory } from 'vue-router'
import { authState, setProfile, clearAuth } from '@/stores/auth'
import { getProfile } from '@/api/user'
import { ROLE } from '@/types/api'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    { path: '/', redirect: '/profile' },
    {
      path: '/login',
      name: 'login',
      component: () => import('@/views/LoginView.vue'),
      meta: { guest: true },
    },
    {
      path: '/register',
      name: 'register',
      component: () => import('@/views/RegisterView.vue'),
      meta: { guest: true },
    },
    {
      path: '/profile',
      name: 'profile',
      component: () => import('@/views/ProfileView.vue'),
      meta: { requiresAuth: true },
    },
    {
      path: '/activities',
      name: 'activities',
      component: () => import('@/views/ActivityListView.vue'),
      meta: { requiresAuth: true },
    },
    {
      path: '/my-activities',
      name: 'myActivities',
      component: () => import('@/views/MyActivitiesView.vue'),
      meta: { requiresAuth: true },
    },
    {
      path: '/admin',
      name: 'admin',
      component: () => import('@/views/AdminView.vue'),
      meta: { requiresAuth: true, adminOnly: true },
    },
    { path: '/:pathMatch(.*)*', redirect: '/' },
  ],
})

router.beforeEach(async (to) => {
  const authed = !!authState.token

  if (to.meta.requiresAuth && !authed) {
    return { name: 'login', query: { redirect: to.fullPath } }
  }
  if (to.meta.guest && authed) {
    return { name: 'profile' }
  }

  // 已登录但尚未拉取资料时，补一次 profile，用于权限判断与顶栏展示
  if (authed && !authState.profile) {
    try {
      setProfile(await getProfile())
    } catch {
      clearAuth()
      if (to.meta.requiresAuth) {
        return { name: 'login' }
      }
    }
  }

  if (to.meta.adminOnly && authState.profile?.role === ROLE.USER) {
    return { name: 'profile' }
  }

  return true
})

export default router