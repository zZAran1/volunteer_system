<script setup lang="ts">
import { computed, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import BrandMark from '@/components/BrandMark.vue'
import AppToast from '@/components/AppToast.vue'
import { authState, clearAuth, isAuthenticated } from '@/stores/auth'
import { ROLE, roleLabel } from '@/types/api'

const route = useRoute()
const router = useRouter()

const isGuestRoute = computed(() => route.meta.guest === true)

const isAdmin = computed(() => {
  const r = authState.profile?.role
  return r === ROLE.SUPER_ADMIN || r === ROLE.ADMIN
})

const displayName = computed(() => authState.profile?.username || '志愿者')
const initials = computed(() => displayName.value.slice(0, 1).toUpperCase() || '志')
const roleText = computed(() => {
  const r = authState.profile?.role
  return r == null ? '' : roleLabel(r)
})

function logout() {
  clearAuth()
  router.push({ name: 'login' })
}

// 登录态失效（如 token 过期被清除）时自动回到登录页
watch(isAuthenticated, (val) => {
  if (!val && route.meta.requiresAuth) {
    router.push({ name: 'login' })
  }
})
</script>

<template>
  <template v-if="isGuestRoute">
    <RouterView />
  </template>

  <div v-else class="shell">
    <header class="topbar">
      <div class="container topbar-inner">
        <RouterLink to="/profile" class="topbar-brand">
          <BrandMark :size="32" />
          <span>志愿同行</span>
        </RouterLink>

        <nav class="topbar-nav">
          <RouterLink
            to="/profile"
            class="nav-link"
            :class="{ 'is-active': route.name === 'profile' }"
          >
            个人中心
          </RouterLink>
          <RouterLink
            to="/activities"
            class="nav-link"
            :class="{ 'is-active': route.name === 'activities' }"
          >
            活动广场
          </RouterLink>
          <RouterLink
            to="/my-activities"
            class="nav-link"
            :class="{ 'is-active': route.name === 'myActivities' }"
          >
            我的活动
          </RouterLink>
          <RouterLink
            v-if="isAdmin"
            to="/admin"
            class="nav-link"
            :class="{ 'is-active': route.name === 'admin' }"
          >
            管理台
          </RouterLink>
        </nav>

        <div class="topbar-user">
          <div class="topbar-who">
            <span class="avatar">{{ initials }}</span>
            <div class="topbar-meta">
              <span class="topbar-name">{{ displayName }}</span>
              <span class="topbar-role">{{ roleText }}</span>
            </div>
          </div>
          <button class="btn btn-ghost btn-sm" @click="logout">退出登录</button>
        </div>
      </div>
    </header>

    <main class="page">
      <RouterView />
    </main>
  </div>

  <AppToast />
</template>

<style scoped>
@media (max-width: 860px) {
  .topbar-nav {
    gap: 2px;
  }

  .nav-link {
    padding: 7px 10px;
    font-size: 13px;
  }
}

@media (max-width: 720px) {
  .topbar-meta {
    display: none;
  }
}
</style>