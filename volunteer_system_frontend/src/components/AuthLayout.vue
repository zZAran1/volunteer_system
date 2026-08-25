<script setup lang="ts">
import BrandMark from './BrandMark.vue'

defineProps<{
  headline: string
  tagline: string
  switchPrompt: string
  switchText: string
  switchTo: string
}>()
</script>

<template>
  <div class="auth">
    <aside class="auth-hero">
      <div class="auth-hero-inner">
        <div class="auth-brand">
          <BrandMark :size="40" />
          <span>志愿同行</span>
        </div>

        <h1 class="auth-headline">{{ headline }}</h1>
        <p class="auth-tagline">{{ tagline }}</p>

        <ul class="auth-points">
          <li>
            <span class="auth-point-ico" aria-hidden="true">
              <svg viewBox="0 0 16 16" width="14" height="14">
                <path d="M3 8.2 6.4 11.5 13 4.5" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" fill="none" />
              </svg>
            </span>
            汇聚每一份微小善意
          </li>
          <li>
            <span class="auth-point-ico" aria-hidden="true">
              <svg viewBox="0 0 16 16" width="14" height="14">
                <path d="M3 8.2 6.4 11.5 13 4.5" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" fill="none" />
              </svg>
            </span>
            让志愿服务更有温度
          </li>
          <li>
            <span class="auth-point-ico" aria-hidden="true">
              <svg viewBox="0 0 16 16" width="14" height="14">
                <path d="M3 8.2 6.4 11.5 13 4.5" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" fill="none" />
              </svg>
            </span>
            高效协作，让管理更轻松
          </li>
        </ul>
      </div>

      <div class="auth-blob auth-blob-1" aria-hidden="true"></div>
      <div class="auth-blob auth-blob-2" aria-hidden="true"></div>
    </aside>

    <main class="auth-main">
      <div class="auth-card animate-in">
        <slot />
      </div>
      <p class="auth-switch">
        {{ switchPrompt }}
        <RouterLink :to="{ name: switchTo }">{{ switchText }}</RouterLink>
      </p>
    </main>
  </div>
</template>

<style scoped>
.auth {
  display: grid;
  grid-template-columns: 1.06fr 1fr;
  min-height: 100vh;
}

/* ---------- 左侧品牌面板 ---------- */
.auth-hero {
  position: relative;
  overflow: hidden;
  display: flex;
  align-items: center;
  padding: 64px 72px;
  color: #fff;
  background: linear-gradient(150deg, #2e9c74 0%, #21775a 52%, #1b5f48 100%);
}

.auth-hero-inner {
  position: relative;
  z-index: 2;
  max-width: 460px;
}

.auth-brand {
  display: flex;
  align-items: center;
  gap: 12px;
  font-weight: 700;
  font-size: 20px;
  letter-spacing: -0.01em;
}

.auth-headline {
  margin-top: 52px;
  font-size: clamp(30px, 3.4vw, 44px);
  line-height: 1.16;
  font-weight: 800;
  letter-spacing: -0.02em;
  color: #fff;
}

.auth-tagline {
  margin-top: 18px;
  font-size: 16px;
  line-height: 1.7;
  color: rgba(255, 255, 255, 0.84);
  max-width: 380px;
}

.auth-points {
  margin-top: 40px;
  list-style: none;
  padding: 0;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.auth-points li {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 15px;
  font-weight: 500;
  color: rgba(255, 255, 255, 0.94);
}

.auth-point-ico {
  display: grid;
  place-items: center;
  width: 26px;
  height: 26px;
  flex: none;
  border-radius: 50%;
  color: #1d6a4d;
  background: #fff;
}

.auth-blob {
  position: absolute;
  border-radius: 50%;
  filter: blur(2px);
  pointer-events: none;
}

.auth-blob-1 {
  width: 340px;
  height: 340px;
  right: -120px;
  top: -90px;
  background: radial-gradient(circle at 30% 30%, rgba(255, 255, 255, 0.16), transparent 65%);
}

.auth-blob-2 {
  width: 300px;
  height: 300px;
  left: -110px;
  bottom: -100px;
  background: radial-gradient(circle at 70% 70%, rgba(242, 166, 90, 0.22), transparent 65%);
}

/* ---------- 右侧表单区 ---------- */
.auth-main {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 22px;
  padding: 48px 24px;
}

.auth-card {
  width: 100%;
  max-width: 420px;
}

.auth-switch {
  font-size: 14px;
  color: var(--c-ink-mute);
}

.auth-switch a {
  color: var(--c-primary-deep);
  font-weight: 600;
  transition: color 0.16s ease;
}

.auth-switch a:hover {
  color: var(--c-primary);
}

@media (max-width: 900px) {
  .auth {
    grid-template-columns: 1fr;
  }

  .auth-hero {
    display: none;
  }

  .auth-main {
    padding: 56px 20px;
  }
}
</style>