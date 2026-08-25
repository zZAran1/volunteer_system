<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { banUser, changeRole, getAllUsers, getUsers, unbanUser } from '@/api/admin'
import { authState } from '@/stores/auth'
import { ROLE, STATUS, roleLabel, statusLabel, type UserVO } from '@/types/api'
import { useToast } from '@/composables/toast'

const toast = useToast()

const users = ref<UserVO[]>([])
const loading = ref(true)
const loadError = ref('')
const keyword = ref('')
const busyEmail = ref('')

const isSuperAdmin = computed(() => authState.profile?.role === ROLE.SUPER_ADMIN)

const filtered = computed(() => {
  const k = keyword.value.trim().toLowerCase()
  if (!k) return users.value
  return users.value.filter(
    (u) =>
      u.username.toLowerCase().includes(k) || u.email.toLowerCase().includes(k),
  )
})

interface Stat {
  key: string
  label: string
  value: number
  tone: 'green' | 'accent' | 'danger' | 'mute'
}

const stats = computed<Stat[]>(() => {
  const list = users.value
  const total = list.length
  const active = list.filter((u) => u.status === STATUS.ACTIVE).length
  const banned = list.filter((u) => u.status === STATUS.BANNED).length
  const admins = list.filter((u) => u.role === ROLE.ADMIN).length

  const result: Stat[] = [
    { key: 'total', label: '用户总数', value: total, tone: 'mute' },
    { key: 'active', label: '正常', value: active, tone: 'green' },
    { key: 'banned', label: '已封禁', value: banned, tone: 'danger' },
  ]
  if (isSuperAdmin.value) {
    result.push({ key: 'admin', label: '管理员', value: admins, tone: 'accent' })
  }
  return result
})

async function load() {
  loading.value = true
  loadError.value = ''
  try {
    users.value = isSuperAdmin.value ? await getAllUsers() : await getUsers()
  } catch (e) {
    loadError.value = (e as Error).message
  } finally {
    loading.value = false
  }
}

function initials(u: UserVO): string {
  return (u.username || '?').slice(0, 1).toUpperCase()
}

async function toggleBan(u: UserVO) {
  const banning = u.status !== STATUS.BANNED
  busyEmail.value = u.email
  try {
    if (banning) {
      await banUser(u.email)
      toast.success(`已封禁「${u.username}」`)
    } else {
      await unbanUser(u.email)
      toast.success(`已解封「${u.username}」`)
    }
    await load()
  } catch (e) {
    toast.error((e as Error).message)
  } finally {
    busyEmail.value = ''
  }
}

async function assignRole(u: UserVO, value: number) {
  busyEmail.value = u.email
  try {
    await changeRole({ email: u.email, value })
    toast.success(`已将「${u.username}」设为${roleLabel(value)}`)
    await load()
  } catch (e) {
    toast.error((e as Error).message)
  } finally {
    busyEmail.value = ''
  }
}

onMounted(load)
</script>

<template>
  <div class="container">
    <div class="admin-head animate-in">
      <div>
        <h1 class="admin-title">用户管理</h1>
        <p class="muted">
          {{ isSuperAdmin ? '管理平台内的管理员与志愿者账号。' : '查看并管理志愿者账号。' }}
        </p>
      </div>
      <button class="btn btn-ghost" @click="load">刷新列表</button>
    </div>

    <div class="stats animate-in">
      <div v-for="s in stats" :key="s.key" class="stat card">
        <span class="stat-value" :class="`stat-${s.tone}`">{{ s.value }}</span>
        <span class="stat-label">{{ s.label }}</span>
      </div>
    </div>

    <div class="card card-pad animate-in">
      <div class="toolbar">
        <div class="search">
          <svg viewBox="0 0 16 16" width="16" height="16" aria-hidden="true">
            <circle cx="7" cy="7" r="5" stroke="currentColor" stroke-width="1.6" fill="none" />
            <path d="m11 11 3 3" stroke="currentColor" stroke-width="1.6" stroke-linecap="round" />
          </svg>
          <input
            v-model="keyword"
            class="search-input"
            type="text"
            placeholder="搜索用户名或邮箱"
          />
        </div>
        <span class="muted count-hint">共 {{ filtered.length }} 条</span>
      </div>

      <div v-if="loadError" class="inline-hint error" role="alert">{{ loadError }}</div>

      <div v-if="loading" class="empty">
        <div class="spinner" aria-hidden="true"></div>
        <p class="empty-title">加载中…</p>
      </div>

      <div v-else-if="filtered.length === 0" class="empty">
        <div class="empty-icon">
          <svg viewBox="0 0 24 24" width="30" height="30" fill="none">
            <circle cx="12" cy="8" r="3.4" stroke="currentColor" stroke-width="1.8" />
            <path d="M5 19c.8-3.2 3.4-5 7-5s6.2 1.8 7 5" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" />
          </svg>
        </div>
        <p class="empty-title">{{ keyword ? '没有匹配的用户' : '暂无用户' }}</p>
        <p class="empty-desc">{{ keyword ? '换个关键词试试' : '新注册的志愿者会出现在这里' }}</p>
      </div>

      <div v-else class="table-wrap">
        <table class="table">
          <thead>
            <tr>
              <th>用户</th>
              <th>角色</th>
              <th>状态</th>
              <th>加入时间</th>
              <th style="text-align: right">操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="u in filtered" :key="u.id">
              <td>
                <div class="cell-user">
                  <span class="avatar">{{ initials(u) }}</span>
                  <div>
                    <div class="cell-user-name">{{ u.username }}</div>
                    <div class="cell-user-email">{{ u.email }}</div>
                  </div>
                </div>
              </td>
              <td>
                <span class="badge" :class="u.role === ROLE.ADMIN ? 'badge-accent' : 'badge-mute'">
                  {{ roleLabel(u.role) }}
                </span>
              </td>
              <td>
                <span class="badge" :class="u.status === STATUS.BANNED ? 'badge-danger' : 'badge-green'">
                  {{ statusLabel(u.status) }}
                </span>
              </td>
              <td class="muted">{{ u.created_at }}</td>
              <td>
                <div class="row-actions" style="justify-content: flex-end">
                  <template v-if="isSuperAdmin">
                    <button
                      v-if="u.role === ROLE.USER"
                      class="btn btn-ghost btn-sm"
                      :disabled="busyEmail === u.email"
                      @click="assignRole(u, ROLE.ADMIN)"
                    >
                      设为管理员
                    </button>
                    <button
                      v-else-if="u.role === ROLE.ADMIN"
                      class="btn btn-ghost btn-sm"
                      :disabled="busyEmail === u.email"
                      @click="assignRole(u, ROLE.USER)"
                    >
                      设为志愿者
                    </button>
                  </template>

                  <button
                    v-if="u.status === STATUS.ACTIVE"
                    class="btn btn-danger-ghost btn-sm"
                    :disabled="busyEmail === u.email"
                    @click="toggleBan(u)"
                  >
                    封禁
                  </button>
                  <button
                    v-else
                    class="btn btn-ghost btn-sm"
                    :disabled="busyEmail === u.email"
                    @click="toggleBan(u)"
                  >
                    解封
                  </button>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
  </div>
</template>

<style scoped>
.admin-head {
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  gap: 16px;
  margin-bottom: 28px;
}

.admin-title {
  font-size: 28px;
}

.stats {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(150px, 1fr));
  gap: 16px;
  margin-bottom: 24px;
}

.stat {
  display: flex;
  flex-direction: column;
  gap: 4px;
  padding: 20px 22px;
}

.stat-value {
  font-size: 30px;
  font-weight: 800;
  line-height: 1;
  letter-spacing: -0.02em;
}

.stat-green {
  color: var(--c-primary-deep);
}

.stat-accent {
  color: #a9641c;
}

.stat-danger {
  color: var(--c-danger-strong);
}

.stat-mute {
  color: var(--c-ink);
}

.stat-label {
  font-size: 13px;
  color: var(--c-ink-mute);
}

.toolbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  margin-bottom: 20px;
}

.search {
  display: flex;
  align-items: center;
  gap: 10px;
  flex: 1;
  max-width: 360px;
  padding: 10px 14px;
  border: 1px solid var(--c-line-strong);
  border-radius: var(--r-sm);
  color: var(--c-ink-mute);
  background: var(--c-surface);
  transition: border-color 0.16s ease, box-shadow 0.16s ease;
}

.search:focus-within {
  border-color: var(--c-primary);
  box-shadow: var(--shadow-ring);
}

.search-input {
  border: none;
  outline: none;
  background: transparent;
  width: 100%;
  font-size: 14px;
  color: var(--c-ink);
}

.search-input::placeholder {
  color: var(--c-ink-mute);
}

.count-hint {
  font-size: 13px;
  white-space: nowrap;
}

.spinner {
  width: 28px;
  height: 28px;
  border-radius: 50%;
  border: 3px solid var(--c-primary-soft);
  border-top-color: var(--c-primary);
  animation: spin 0.8s linear infinite;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

@media (max-width: 640px) {
  .toolbar {
    flex-direction: column;
    align-items: stretch;
  }

  .search {
    max-width: none;
  }
}
</style>