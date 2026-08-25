<script setup lang="ts">
import { reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import AuthLayout from '@/components/AuthLayout.vue'
import { login } from '@/api/user'
import { setToken } from '@/stores/auth'
import { useToast } from '@/composables/toast'

const router = useRouter()
const route = useRoute()
const toast = useToast()

const form = reactive({
  email: '',
  password: '',
})

const error = ref('')
const loading = ref(false)

const emailValid = (v: string) => /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(v)

async function onSubmit() {
  error.value = ''
  if (!form.email.trim() || !form.password) {
    error.value = '请输入邮箱和密码'
    return
  }
  if (!emailValid(form.email.trim())) {
    error.value = '请输入有效的邮箱地址'
    return
  }

  loading.value = true
  try {
    const { token } = await login({
      email: form.email.trim(),
      password: form.password,
    })
    setToken(token)
    toast.success('登录成功，欢迎回来')
    const redirect =
      typeof route.query.redirect === 'string' ? route.query.redirect : '/profile'
    router.push(redirect)
  } catch (e) {
    error.value = (e as Error).message
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <AuthLayout
    headline="欢迎回来，继续传递温暖"
    tagline="登录你的志愿账户，查看个人资料、参与活动与管理志愿工作。"
    switch-prompt="还没有账户？"
    switch-text="立即注册"
    switch-to="register"
  >
    <div class="card card-pad">
      <div class="form-head">
        <h2>登录</h2>
        <p class="muted">使用邮箱与密码登录志愿同行</p>
      </div>

      <form class="form" novalidate @submit.prevent="onSubmit">
        <div class="field">
          <label class="field-label" for="login-email">邮箱</label>
          <input
            id="login-email"
            v-model="form.email"
            class="input"
            :class="{ 'is-error': !!error }"
            type="email"
            autocomplete="email"
            placeholder="you@example.com"
          />
        </div>

        <div class="field">
          <label class="field-label" for="login-password">密码</label>
          <input
            id="login-password"
            v-model="form.password"
            class="input"
            :class="{ 'is-error': !!error }"
            type="password"
            autocomplete="current-password"
            placeholder="请输入密码"
          />
        </div>

        <div v-if="error" class="inline-hint error" role="alert">
          <svg viewBox="0 0 16 16" width="16" height="16" aria-hidden="true">
            <circle cx="8" cy="8" r="6.5" stroke="currentColor" stroke-width="1.6" fill="none" />
            <path d="M8 5v3.4" stroke="currentColor" stroke-width="1.6" stroke-linecap="round" />
            <circle cx="8" cy="10.8" r="0.9" fill="currentColor" />
          </svg>
          {{ error }}
        </div>

        <button class="btn btn-primary form-submit" type="submit" :disabled="loading">
          {{ loading ? '登录中…' : '登录' }}
        </button>
      </form>
    </div>
  </AuthLayout>
</template>

<style scoped>
.form-head {
  display: flex;
  flex-direction: column;
  gap: 6px;
  margin-bottom: 26px;
}

.form-head h2 {
  font-size: 24px;
}

.form {
  display: flex;
  flex-direction: column;
  gap: 18px;
}

.form-submit {
  margin-top: 6px;
  width: 100%;
  padding: 13px 18px;
  font-size: 15px;
}
</style>