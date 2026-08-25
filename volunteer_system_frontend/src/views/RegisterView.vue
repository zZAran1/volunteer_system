<script setup lang="ts">
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import AuthLayout from '@/components/AuthLayout.vue'
import { register } from '@/api/user'
import { useToast } from '@/composables/toast'

const router = useRouter()
const toast = useToast()

const form = reactive({
  username: '',
  email: '',
  password: '',
})

const error = ref('')
const loading = ref(false)

const emailValid = (v: string) => /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(v)

async function onSubmit() {
  error.value = ''
  if (!form.username.trim() || !form.email.trim() || !form.password) {
    error.value = '请填写完整的注册信息'
    return
  }
  if (!emailValid(form.email.trim())) {
    error.value = '请输入有效的邮箱地址'
    return
  }
  if (form.password.length < 6) {
    error.value = '密码长度至少 6 位'
    return
  }

  loading.value = true
  try {
    await register({
      username: form.username.trim(),
      email: form.email.trim(),
      password: form.password,
    })
    toast.success('注册成功，请登录')
    router.push({ name: 'login' })
  } catch (e) {
    error.value = (e as Error).message
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <AuthLayout
    headline="加入我们，成为光的一束"
    tagline="注册志愿同行账户，开启你的公益之旅，让每一次付出都被看见。"
    switch-prompt="已有账户？"
    switch-text="直接登录"
    switch-to="login"
  >
    <div class="card card-pad">
      <div class="form-head">
        <h2>注册</h2>
        <p class="muted">创建账户，加入志愿者的大家庭</p>
      </div>

      <form class="form" novalidate @submit.prevent="onSubmit">
        <div class="field">
          <label class="field-label" for="reg-username">用户名</label>
          <input
            id="reg-username"
            v-model="form.username"
            class="input"
            type="text"
            autocomplete="username"
            placeholder="你的称呼"
          />
        </div>

        <div class="field">
          <label class="field-label" for="reg-email">邮箱</label>
          <input
            id="reg-email"
            v-model="form.email"
            class="input"
            :class="{ 'is-error': !!error }"
            type="email"
            autocomplete="email"
            placeholder="you@example.com"
          />
        </div>

        <div class="field">
          <label class="field-label" for="reg-password">密码</label>
          <input
            id="reg-password"
            v-model="form.password"
            class="input"
            :class="{ 'is-error': !!error }"
            type="password"
            autocomplete="new-password"
            placeholder="至少 6 位"
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
          {{ loading ? '注册中…' : '创建账户' }}
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