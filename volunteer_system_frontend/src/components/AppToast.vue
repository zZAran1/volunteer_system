<script setup lang="ts">
import { useToast } from '@/composables/toast'

const { toasts } = useToast()
</script>

<template>
  <Teleport to="body">
    <div class="toast-host" aria-live="polite">
      <TransitionGroup name="toast">
        <div
          v-for="t in toasts"
          :key="t.id"
          class="toast"
          :class="`toast-${t.type}`"
        >
          <span class="toast-dot" aria-hidden="true"></span>
          <span class="toast-msg">{{ t.message }}</span>
        </div>
      </TransitionGroup>
    </div>
  </Teleport>
</template>

<style scoped>
.toast-host {
  position: fixed;
  top: 20px;
  left: 50%;
  transform: translateX(-50%);
  z-index: 200;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10px;
  pointer-events: none;
}

.toast {
  display: flex;
  align-items: center;
  gap: 10px;
  max-width: min(90vw, 420px);
  padding: 12px 18px;
  border-radius: var(--r-md);
  font-size: 14px;
  font-weight: 600;
  color: var(--c-ink);
  background: var(--c-surface);
  border: 1px solid var(--c-line);
  box-shadow: var(--shadow-pop);
}

.toast-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  flex: none;
}

.toast-success .toast-dot {
  background: var(--c-primary);
}

.toast-error .toast-dot {
  background: var(--c-danger);
}

.toast-info .toast-dot {
  background: var(--c-accent);
}

.toast-enter-active,
.toast-leave-active {
  transition: all 0.28s cubic-bezier(0.22, 1, 0.36, 1);
}

.toast-enter-from {
  opacity: 0;
  transform: translateY(-14px) scale(0.96);
}

.toast-leave-to {
  opacity: 0;
  transform: translateY(-10px) scale(0.96);
}
</style>