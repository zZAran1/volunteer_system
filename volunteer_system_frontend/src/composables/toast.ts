import { reactive } from 'vue'

export type ToastType = 'success' | 'error' | 'info'

export interface ToastItem {
  id: number
  type: ToastType
  message: string
}

const toasts = reactive<ToastItem[]>([])
let seed = 0

function dismiss(id: number): void {
  const index = toasts.findIndex((t) => t.id === id)
  if (index > -1) {
    toasts.splice(index, 1)
  }
}

function push(type: ToastType, message: string): void {
  const id = ++seed
  toasts.push({ id, type, message })
  window.setTimeout(() => dismiss(id), 3400)
}

export function useToast() {
  return {
    toasts,
    success: (message: string) => push('success', message),
    error: (message: string) => push('error', message),
    info: (message: string) => push('info', message),
  }
}