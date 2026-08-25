import { computed, reactive } from 'vue'
import type { ProfileVO } from '@/types/api'

const TOKEN_KEY = 'volunteer_system_token'

interface AuthState {
  token: string | null
  profile: ProfileVO | null
}

/** 全局认证状态（模块单例，无循环依赖） */
export const authState = reactive<AuthState>({
  token: localStorage.getItem(TOKEN_KEY),
  profile: null,
})

export const isAuthenticated = computed(() => !!authState.token)

export const currentRole = computed(() => authState.profile?.role ?? null)

export function setToken(token: string): void {
  authState.token = token
  localStorage.setItem(TOKEN_KEY, token)
}

export function setProfile(profile: ProfileVO): void {
  authState.profile = profile
}

export function clearAuth(): void {
  authState.token = null
  authState.profile = null
  localStorage.removeItem(TOKEN_KEY)
}