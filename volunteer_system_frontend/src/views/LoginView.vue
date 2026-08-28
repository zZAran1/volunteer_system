<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import AuthLayout from '@/components/AuthLayout.vue'
import { getCaptcha, login } from '@/api/user'
import { setToken } from '@/stores/auth'
import { useToast } from '@/composables/toast'

const router = useRouter()
const route = useRoute()
const toast = useToast()

const form = reactive({
  email: '',
  password: '',
  captchaText: '',
})

const error = ref('')
const loading = ref(false)

const captchaId = ref('')
const captchaImage = ref('')
const captchaLoading = ref(false)
const captchaError = ref('')

const emailValid = (v: string) => /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(v)

async function fetchCaptcha() {
  captchaLoading.value = true
  captchaError.value = ''
  try {
    const cap = await getCaptcha()
    captchaId.value = cap.captchaId
    captchaImage.value = cap.imageBase64
  } catch (e) {
    captchaId.value = ''
    captchaImage.value = ''
    captchaError.value = (e as Error).message
  } finally {
    captchaLoading.value = false
  }
}

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
  if (!form.captchaText.trim()) {
    error.value = '请输入图形验证码'
    return
  }
  if (!captchaId.value) {
    error.value = '图形验证码尚未加载，请点击验证码重试'
    fetchCaptcha()
    return
  }

  loading.value = true
  try {
    const { token } = await login({
      email: form.email.trim(),
      password: form.password,
      captchaId: captchaId.value,
      captchaText: form.captchaText.trim(),
    })
    setToken(token)
    toast.success('登录成功，欢迎回来')
    const redirect =
      typeof route.query.redirect === 'string' ? route.query.redirect : '/profile'
    router.push(redirect)
  } catch (e) {
    error.value = (e as Error).message
    // 验证码可能已使用或过期，登录失败后换一张新的
    form.captchaText = ''
    fetchCaptcha()
  } finally {
    loading.value = false
  }
}

onMounted(fetchCaptcha)
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

        <div class="field">
          <label class="field-label" for="login-captcha">图形验证码</label>
          <div class="captcha-row">
            <input
              id="login-captcha"
              v-model="form.captchaText"
              class="input"
              :class="{ 'is-error': !!captchaError }"
              type="text"
              autocomplete="off"
              maxlength="4"
              placeholder="输入右侧验证码"
            />
            <button
              type="button"
              class="captcha-img"
              :disabled="captchaLoading"
              aria-label="点击刷新验证码"
              title="点击刷新验证码"
              @click="fetchCaptcha"
            >
              <img
                v-if="captchaImage"
                :src="captchaImage"
                alt="图形验证码"
                width="130"
                height="48"
              />
              <span v-else class="captcha-hint">
                <span v-if="captchaLoading" class="captcha-spinner" aria-hidden="true"></span>
                <template v-else>
                  <svg viewBox="0 0 16 16" width="14" height="14" aria-hidden="true">
                    <path
                      d="M13.5 8a5.5 5.5 0 1 1-1.6-3.9"
                      stroke="currentColor"
                      stroke-width="1.5"
                      fill="none"
                      stroke-linecap="round"
                    />
                    <path
                      d="M13.8 1.8v2.9h-2.9"
                      stroke="currentColor"
                      stroke-width="1.5"
                      fill="none"
                      stroke-linecap="round"
                      stroke-linejoin="round"
                    />
                  </svg>
                  刷新验证码
                </template>
              </span>
            </button>
          </div>
          <p v-if="captchaError" class="form-error">
            验证码加载失败：{{ captchaError }}
          </p>
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

.captcha-row {
  display: flex;
  align-items: stretch;
  gap: 10px;
}

.captcha-row .input {
  flex: 1;
  min-width: 0;
}

.captcha-img {
  flex: none;
  width: 130px;
  height: 48px;
  display: grid;
  place-items: center;
  padding: 0;
  border: 1px solid var(--c-line-strong);
  border-radius: var(--r-sm);
  background: var(--c-surface-soft);
  color: var(--c-ink-mute);
  cursor: pointer;
  overflow: hidden;
  transition:
    border-color 0.16s ease,
    box-shadow 0.16s ease,
    color 0.16s ease;
}

.captcha-img:hover:not(:disabled) {
  border-color: var(--c-primary);
  color: var(--c-primary-deep);
  box-shadow: var(--shadow-ring);
}

.captcha-img:disabled {
  cursor: not-allowed;
}

.captcha-img img {
  display: block;
}

.captcha-hint {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  font-size: 12px;
  font-weight: 600;
}

.captcha-spinner {
  width: 16px;
  height: 16px;
  border-radius: 50%;
  border: 2px solid var(--c-primary-soft);
  border-top-color: var(--c-primary);
  animation: captcha-spin 0.8s linear infinite;
}

@keyframes captcha-spin {
  to {
    transform: rotate(360deg);
  }
}
</style>