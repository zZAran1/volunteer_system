<script setup lang="ts">
import { onMounted, ref } from 'vue'
import {
  deleteActivityByAdmin,
  getAllActivities,
  getEndedActivities,
  getFullActivities,
  getOngoingActivities,
  getRejectedActivities,
  getUnderReviewActivities,
  reviewApprove,
  reviewReject,
} from '@/api/activity'
import {
  activityStatusClass,
  activityStatusLabel,
  type ActivityVO,
} from '@/types/api'
import { useToast } from '@/composables/toast'

const toast = useToast()

type TabKey =
  | 'review'
  | 'all'
  | 'ongoing'
  | 'full'
  | 'ended'
  | 'rejected'

interface Tab {
  key: TabKey
  label: string
}

const tabs: Tab[] = [
  { key: 'review', label: '待审核' },
  { key: 'all', label: '全部活动' },
  { key: 'ongoing', label: '进行中' },
  { key: 'full', label: '已满员' },
  { key: 'ended', label: '已结束' },
  { key: 'rejected', label: '已驳回' },
]

const activeTab = ref<TabKey>('review')
const activities = ref<ActivityVO[]>([])
const loading = ref(false)
const loadError = ref('')
const busyId = ref<number | null>(null)

const loaders: Record<TabKey, () => Promise<ActivityVO[]>> = {
  review: getUnderReviewActivities,
  all: getAllActivities,
  ongoing: getOngoingActivities,
  full: getFullActivities,
  ended: getEndedActivities,
  rejected: getRejectedActivities,
}

async function load() {
  loading.value = true
  loadError.value = ''
  try {
    activities.value = await loaders[activeTab.value]()
  } catch (e) {
    loadError.value = (e as Error).message
  } finally {
    loading.value = false
  }
}

async function switchTab(key: TabKey) {
  activeTab.value = key
  await load()
}

async function onApprove(a: ActivityVO) {
  if (!window.confirm(`确认通过活动「${a.title}」的审核？通过后将对用户可见。`)) return
  busyId.value = a.id
  try {
    await reviewApprove(a.id)
    toast.success('已通过审核')
    await load()
  } catch (e) {
    toast.error((e as Error).message)
  } finally {
    busyId.value = null
  }
}

async function onReject(a: ActivityVO) {
  if (!window.confirm(`确认驳回活动「${a.title}」？`)) return
  busyId.value = a.id
  try {
    await reviewReject(a.id)
    toast.success('已驳回该活动')
    await load()
  } catch (e) {
    toast.error((e as Error).message)
  } finally {
    busyId.value = null
  }
}

async function onDelete(a: ActivityVO) {
  if (!window.confirm(`确定要删除活动「${a.title}」吗？该操作不可撤销。`)) return
  busyId.value = a.id
  try {
    await deleteActivityByAdmin(a.id)
    toast.success('活动已删除')
    await load()
  } catch (e) {
    toast.error((e as Error).message)
  } finally {
    busyId.value = null
  }
}

function dateRange(a: ActivityVO): string {
  if (!a.start_date || !a.end_date) return '时间待定'
  return `${a.start_date} ~ ${a.end_date}`
}

onMounted(load)
</script>

<template>
  <div class="container">
    <div class="admin-head animate-in">
      <div>
        <h1 class="admin-title">活动管理</h1>
        <p class="muted">审核新发布的活动，并按状态查看、管理全部活动。</p>
      </div>
      <button class="btn btn-ghost" :disabled="loading" @click="load">刷新列表</button>
    </div>

    <div class="tabs animate-in">
      <button
        v-for="t in tabs"
        :key="t.key"
        class="tab"
        :class="{ 'is-active': activeTab === t.key }"
        @click="switchTab(t.key)"
      >
        {{ t.label }}
      </button>
    </div>

    <div v-if="loadError" class="inline-hint error animate-in" role="alert">{{ loadError }}</div>

    <div v-if="loading" class="empty">
      <div class="spinner" aria-hidden="true"></div>
      <p class="empty-title">加载中…</p>
    </div>

    <div v-else-if="activities.length === 0" class="empty">
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
      <p class="empty-title">该分类下暂无活动</p>
      <p class="empty-desc">切换其他分类查看，或等待用户发布新活动</p>
    </div>

    <div v-else class="card card-pad animate-in">
      <div class="table-wrap">
        <table class="table">
          <thead>
            <tr>
              <th>活动</th>
              <th>发布者</th>
              <th>状态</th>
              <th>时间</th>
              <th>人数</th>
              <th style="text-align: right">操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="a in activities" :key="a.id">
              <td>
                <div class="act-title">{{ a.title }}</div>
                <div class="act-address">{{ a.address }}</div>
              </td>
              <td class="muted">{{ a.poster_name || '未知' }}</td>
              <td>
                <span class="badge" :class="activityStatusClass(a.status)">
                  {{ activityStatusLabel(a.status) }}
                </span>
              </td>
              <td class="muted">{{ dateRange(a) }}</td>
              <td class="muted">
                {{ a.headcount ?? 0 }}{{ a.headcount_limit ? ` / ${a.headcount_limit}` : '' }}
              </td>
              <td>
                <div class="row-actions" style="justify-content: flex-end">
                  <template v-if="activeTab === 'review'">
                    <button
                      class="btn btn-primary btn-sm"
                      :disabled="busyId === a.id"
                      @click="onApprove(a)"
                    >
                      通过
                    </button>
                    <button
                      class="btn btn-danger-ghost btn-sm"
                      :disabled="busyId === a.id"
                      @click="onReject(a)"
                    >
                      驳回
                    </button>
                  </template>
                  <button
                    class="btn btn-danger-ghost btn-sm"
                    :disabled="busyId === a.id"
                    @click="onDelete(a)"
                  >
                    删除
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
  margin-bottom: 24px;
}

.admin-title {
  font-size: 28px;
}

.tabs {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-bottom: 20px;
}

.tab {
  padding: 8px 16px;
  border: 1px solid var(--c-line-strong);
  border-radius: var(--r-pill);
  background: var(--c-surface);
  color: var(--c-ink-soft);
  font-size: 13.5px;
  font-weight: 600;
  cursor: pointer;
  transition:
    border-color 0.16s ease,
    color 0.16s ease,
    background 0.16s ease;
}

.tab:hover {
  border-color: var(--c-primary);
  color: var(--c-primary-deep);
}

.tab.is-active {
  border-color: var(--c-primary);
  background: var(--c-primary);
  color: #fff;
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

.act-title {
  font-weight: 600;
  font-size: 14px;
}

.act-address {
  font-size: 12.5px;
  color: var(--c-ink-mute);
  margin-top: 2px;
}

.row-actions {
  display: flex;
  gap: 8px;
}
</style>
