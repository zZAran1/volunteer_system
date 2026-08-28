<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { getAllActivities } from '@/api/activity'
import {
  ACTIVITY_STATUS,
  activityStatusLabel,
  type ActivityVO,
} from '@/types/api'

const activities = ref<ActivityVO[]>([])
const loading = ref(true)
const loadError = ref('')
const keyword = ref('')

const filtered = computed(() => {
  const k = keyword.value.trim().toLowerCase()
  if (!k) return activities.value
  return activities.value.filter(
    (a) =>
      a.title.toLowerCase().includes(k) ||
      a.address.toLowerCase().includes(k) ||
      (a.poster_name || '').toLowerCase().includes(k),
  )
})

async function load() {
  loading.value = true
  loadError.value = ''
  try {
    activities.value = await getAllActivities()
  } catch (e) {
    loadError.value = (e as Error).message
  } finally {
    loading.value = false
  }
}

function formatDate(value: string): string {
  const [y, m, d] = value.split('-')
  if (!y || !m || !d) return value
  return `${Number(m)} 月 ${Number(d)} 日`
}

function dateRange(a: ActivityVO): string {
  if (!a.start_date || !a.end_date) return '时间待定'
  return `${formatDate(a.start_date)} — ${formatDate(a.end_date)}`
}

function progressOf(a: ActivityVO): number {
  const limit = a.headcount_limit
  if (!limit) return 0
  return Math.min(100, Math.round(((a.headcount ?? 0) / limit) * 100))
}

function headcountText(a: ActivityVO): string {
  return a.headcount_limit
    ? `${a.headcount ?? 0} / ${a.headcount_limit}`
    : `已报名 ${a.headcount ?? 0} 人 · 不限人数`
}

function statusClass(status: number): string {
  if (status === ACTIVITY_STATUS.RECRUITING) return 'badge-green'
  if (status === ACTIVITY_STATUS.PENDING) return 'badge-accent'
  return 'badge-mute'
}

onMounted(load)
</script>

<template>
  <div class="container">
    <div class="page-head animate-in">
      <div>
        <h1 class="page-title">活动广场</h1>
        <p class="muted">浏览平台上的志愿服务，向每一份善意靠近。</p>
      </div>
      <button class="btn btn-ghost" :disabled="loading" @click="load">刷新活动</button>
    </div>

    <div class="toolbar animate-in">
      <div class="search">
        <svg viewBox="0 0 16 16" width="16" height="16" aria-hidden="true">
          <circle cx="7" cy="7" r="5" stroke="currentColor" stroke-width="1.6" fill="none" />
          <path d="m11 11 3 3" stroke="currentColor" stroke-width="1.6" stroke-linecap="round" />
        </svg>
        <input
          v-model="keyword"
          class="search-input"
          type="text"
          placeholder="搜索活动名称、地点或发布者"
        />
      </div>
      <span class="muted count-hint">共 {{ filtered.length }} 场</span>
    </div>

    <div v-if="loadError" class="inline-hint error animate-in" role="alert">
      <svg viewBox="0 0 16 16" width="16" height="16" aria-hidden="true">
        <circle cx="8" cy="8" r="6.5" stroke="currentColor" stroke-width="1.6" fill="none" />
        <path d="M8 5v3.4" stroke="currentColor" stroke-width="1.6" stroke-linecap="round" />
        <circle cx="8" cy="10.8" r="0.9" fill="currentColor" />
      </svg>
      {{ loadError }}
    </div>

    <div v-if="loading" class="empty">
      <div class="spinner" aria-hidden="true"></div>
      <p class="empty-title">正在加载活动…</p>
    </div>

    <div v-else-if="!loadError && filtered.length === 0" class="empty">
      <div class="empty-icon">
        <svg viewBox="0 0 24 24" width="30" height="30" fill="none">
          <path
            d="M12 3 4 6v6c0 4.4 3.4 7.4 8 9 4.6-1.6 8-4.6 8-9V6l-8-3Z"
            stroke="currentColor"
            stroke-width="1.8"
            stroke-linejoin="round"
          />
          <path
            d="m9 12 2 2 4-4"
            stroke="currentColor"
            stroke-width="1.8"
            stroke-linecap="round"
            stroke-linejoin="round"
          />
        </svg>
      </div>
      <p class="empty-title">{{ keyword ? '没有匹配的活动' : '暂无可浏览的活动' }}</p>
      <p class="empty-desc">{{ keyword ? '换个关键词试试' : '新发布的活动会出现在这里' }}</p>
    </div>

    <div v-else class="activity-grid animate-in">
      <article v-for="a in filtered" :key="a.id" class="card activity-card">
        <div class="activity-card-top">
          <span class="badge" :class="statusClass(a.status)">
            {{ activityStatusLabel(a.status) }}
          </span>
          <span class="poster">由 {{ a.poster_name || '热心义工' }} 发布</span>
        </div>

        <h2 class="activity-title">{{ a.title }}</h2>
        <p class="activity-desc">{{ a.description }}</p>

        <div class="activity-rows">
          <div class="activity-row">
            <svg viewBox="0 0 16 16" width="15" height="15" aria-hidden="true">
              <rect
                x="2"
                y="3.5"
                width="12"
                height="10.5"
                rx="2"
                stroke="currentColor"
                stroke-width="1.4"
                fill="none"
              />
              <path
                d="M2 6.5h12M5.5 2v3M10.5 2v3"
                stroke="currentColor"
                stroke-width="1.4"
                stroke-linecap="round"
              />
            </svg>
            <span>{{ dateRange(a) }}</span>
          </div>
          <div class="activity-row">
            <svg viewBox="0 0 16 16" width="15" height="15" aria-hidden="true">
              <path
                d="M8 14c2.9-2.6 5-5 5-7.4A5 5 0 0 0 3 6.6C3 9 5.1 11.4 8 14Z"
                stroke="currentColor"
                stroke-width="1.4"
                fill="none"
              />
              <circle cx="8" cy="6.5" r="1.8" stroke="currentColor" stroke-width="1.4" fill="none" />
            </svg>
            <span>{{ a.address }}</span>
          </div>
        </div>

        <div class="activity-progress">
          <div class="activity-progress-head">
            <span>报名进度</span>
            <span>{{ headcountText(a) }}</span>
          </div>
          <div class="activity-progress-track">
            <div
              class="activity-progress-bar"
              :style="{ width: `${progressOf(a)}%` }"
            ></div>
          </div>
        </div>
      </article>
    </div>
  </div>
</template>

<style scoped>
.page-head {
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  gap: 16px;
  margin-bottom: 28px;
}

.page-title {
  font-size: 28px;
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
  max-width: 380px;
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

.activity-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(310px, 1fr));
  gap: 20px;
}

.activity-card {
  display: flex;
  flex-direction: column;
  gap: 13px;
  padding: 22px;
}

.activity-card-top {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 10px;
}

.poster {
  font-size: 12.5px;
  color: var(--c-ink-mute);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.activity-title {
  font-size: 18px;
  line-height: 1.3;
}

.activity-desc {
  font-size: 13.5px;
  line-height: 1.65;
  color: var(--c-ink-soft);
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.activity-rows {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.activity-row {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 13.5px;
  color: var(--c-ink-soft);
}

.activity-row svg {
  color: var(--c-primary);
  flex: none;
}

.activity-progress {
  margin-top: auto;
  border-top: 1px solid var(--c-line);
  padding-top: 13px;
}

.activity-progress-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  font-size: 12.5px;
  font-weight: 600;
  color: var(--c-ink-mute);
  margin-bottom: 7px;
}

.activity-progress-track {
  height: 6px;
  border-radius: var(--r-pill);
  background: var(--c-surface-soft);
  border: 1px solid var(--c-line);
  overflow: hidden;
}

.activity-progress-bar {
  height: 100%;
  border-radius: var(--r-pill);
  background: linear-gradient(90deg, var(--c-primary), var(--c-accent));
  transition: width 0.3s ease;
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