<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import {
  createActivity,
  deleteActivity,
  getMyActivities,
  updateActivity,
} from '@/api/activity'
import {
  ACTIVITY_STATUS,
  activityStatusLabel,
  type ActivityVO,
} from '@/types/api'
import { useToast } from '@/composables/toast'

const toast = useToast()

const activities = ref<ActivityVO[]>([])
const loading = ref(true)
const loadError = ref('')

const editingId = ref<number | null>(null)
const submitting = ref(false)
const busyId = ref<number | null>(null)
const formEl = ref<HTMLElement | null>(null)

const form = reactive({
  title: '',
  address: '',
  start_date: '',
  end_date: '',
  description: '',
  headcount_limit: 1,
})

async function load() {
  loading.value = true
  loadError.value = ''
  try {
    activities.value = await getMyActivities()
  } catch (e) {
    loadError.value = (e as Error).message
  } finally {
    loading.value = false
  }
}

function validate(): string | null {
  if (!form.title.trim() || !form.address.trim() || !form.description.trim()) {
    return '请填写活动标题、地点与描述'
  }
  if (!form.start_date || !form.end_date) {
    return '请选择活动起止日期'
  }
  if (form.end_date < form.start_date) {
    return '结束日期不能早于开始日期'
  }
  if (!Number.isInteger(form.headcount_limit) || form.headcount_limit < 1) {
    return '报名人数上限需为不小于 1 的整数'
  }
  return null
}

function resetForm() {
  editingId.value = null
  form.title = ''
  form.address = ''
  form.start_date = ''
  form.end_date = ''
  form.description = ''
  form.headcount_limit = 1
}

async function onSubmit() {
  const msg = validate()
  if (msg) {
    toast.error(msg)
    return
  }
  submitting.value = true
  try {
    const payload = {
      title: form.title.trim(),
      address: form.address.trim(),
      start_date: form.start_date,
      end_date: form.end_date,
      description: form.description.trim(),
      headcount_limit: form.headcount_limit,
    }
    if (editingId.value == null) {
      await createActivity(payload)
      toast.success('活动发布成功')
    } else {
      await updateActivity({ id: editingId.value, ...payload })
      toast.success('活动修改成功')
    }
    resetForm()
    await load()
  } catch (e) {
    toast.error((e as Error).message)
  } finally {
    submitting.value = false
  }
}

function startEdit(a: ActivityVO) {
  editingId.value = a.id
  form.title = a.title
  form.address = a.address
  form.start_date = a.start_date
  form.end_date = a.end_date
  form.description = a.description
  form.headcount_limit = a.headcount_limit
  formEl.value?.scrollIntoView({ behavior: 'smooth', block: 'start' })
}

async function onDelete(a: ActivityVO) {
  if (!window.confirm(`确定要删除活动「${a.title}」吗？该操作不可撤销。`)) return
  busyId.value = a.id
  try {
    await deleteActivity(a.id)
    toast.success('活动已删除')
    if (editingId.value === a.id) resetForm()
    await load()
  } catch (e) {
    toast.error((e as Error).message)
  } finally {
    busyId.value = null
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
        <h1 class="page-title">我的活动</h1>
        <p class="muted">发布、编辑并管理你发起的志愿活动。</p>
      </div>
      <button class="btn btn-ghost" :disabled="loading" @click="load">刷新列表</button>
    </div>

    <div class="manage-grid animate-in">
      <!-- 发布 / 编辑表单 -->
      <section class="card card-pad publish">
        <h2 class="publish-title">
          {{ editingId == null ? '发布新活动' : '编辑活动' }}
        </h2>
        <p class="muted publish-desc">
          {{
            editingId == null
              ? '填写活动信息，发布后等待审核。'
              : '修改内容保存后，将重新进入待审核状态。'
          }}
        </p>

        <form ref="formEl" class="form" novalidate @submit.prevent="onSubmit">
          <div class="field">
            <label class="field-label" for="act-title">活动标题</label>
            <input
              id="act-title"
              v-model="form.title"
              class="input"
              type="text"
              maxlength="50"
              placeholder="例如：敬老院周末陪伴"
            />
          </div>

          <div class="field">
            <label class="field-label" for="act-address">活动地点</label>
            <input
              id="act-address"
              v-model="form.address"
              class="input"
              type="text"
              maxlength="100"
              placeholder="例如：香洲区某某社区服务中心"
            />
          </div>

          <div class="date-grid">
            <div class="field">
              <label class="field-label" for="act-start">开始日期</label>
              <input id="act-start" v-model="form.start_date" class="input" type="date" />
            </div>
            <div class="field">
              <label class="field-label" for="act-end">结束日期</label>
              <input
                id="act-end"
                v-model="form.end_date"
                class="input"
                type="date"
                :min="form.start_date || undefined"
              />
            </div>
          </div>

          <div class="field">
            <label class="field-label" for="act-limit">报名人数上限</label>
            <input
              id="act-limit"
              v-model.number="form.headcount_limit"
              class="input"
              type="number"
              min="1"
              step="1"
            />
          </div>

          <div class="field">
            <label class="field-label" for="act-desc">活动描述</label>
            <textarea
              id="act-desc"
              v-model="form.description"
              class="input textarea"
              rows="4"
              maxlength="500"
              placeholder="介绍一下活动内容、集合方式与注意事项"
            ></textarea>
          </div>

          <div class="form-actions">
            <button class="btn btn-primary" type="submit" :disabled="submitting">
              {{
                submitting
                  ? editingId == null
                    ? '发布中…'
                    : '保存中…'
                  : editingId == null
                    ? '发布活动'
                    : '保存修改'
              }}
            </button>
            <button
              v-if="editingId != null"
              class="btn btn-ghost"
              type="button"
              :disabled="submitting"
              @click="resetForm"
            >
              取消编辑
            </button>
          </div>
        </form>
      </section>

      <!-- 我发布的活动 -->
      <section class="list">
        <div v-if="loadError" class="inline-hint error" role="alert">
          <svg viewBox="0 0 16 16" width="16" height="16" aria-hidden="true">
            <circle cx="8" cy="8" r="6.5" stroke="currentColor" stroke-width="1.6" fill="none" />
            <path d="M8 5v3.4" stroke="currentColor" stroke-width="1.6" stroke-linecap="round" />
            <circle cx="8" cy="10.8" r="0.9" fill="currentColor" />
          </svg>
          {{ loadError }}
        </div>

        <div v-if="loading" class="empty card">
          <div class="spinner" aria-hidden="true"></div>
          <p class="empty-title">正在加载…</p>
        </div>

        <div v-else-if="!loadError && activities.length === 0" class="empty card">
          <div class="empty-icon">
            <svg viewBox="0 0 24 24" width="30" height="30" fill="none">
              <path
                d="M12 20h9M16.5 3.5a2.1 2.1 0 0 1 3 3L7 19l-4 1 1-4L16.5 3.5Z"
                stroke="currentColor"
                stroke-width="1.8"
                stroke-linecap="round"
                stroke-linejoin="round"
              />
            </svg>
          </div>
          <p class="empty-title">你还没有发布过活动</p>
          <p class="empty-desc">在左侧填写信息，发布你的第一场志愿活动。</p>
        </div>

        <div v-else class="act-list">
          <article v-for="a in activities" :key="a.id" class="card act-item">
            <div class="act-item-main">
              <div class="act-item-top">
                <span class="badge" :class="statusClass(a.status)">
                  {{ activityStatusLabel(a.status) }}
                </span>
                <span class="act-meta-item">
                  已报名 {{ a.headcount ?? 0 }} / {{ a.headcount_limit ?? '不限' }}
                </span>
              </div>
              <h3 class="act-item-title">{{ a.title }}</h3>
              <p class="act-item-desc">{{ a.description }}</p>
              <div class="act-item-meta">
                <span class="act-meta-item">
                  <svg viewBox="0 0 16 16" width="14" height="14" aria-hidden="true">
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
                  {{ dateRange(a) }}
                </span>
                <span class="act-meta-item">
                  <svg viewBox="0 0 16 16" width="14" height="14" aria-hidden="true">
                    <path
                      d="M8 14c2.9-2.6 5-5 5-7.4A5 5 0 0 0 3 6.6C3 9 5.1 11.4 8 14Z"
                      stroke="currentColor"
                      stroke-width="1.4"
                      fill="none"
                    />
                    <circle cx="8" cy="6.5" r="1.8" stroke="currentColor" stroke-width="1.4" fill="none" />
                  </svg>
                  {{ a.address }}
                </span>
              </div>
            </div>
            <div class="act-item-actions">
              <button
                class="btn btn-ghost btn-sm"
                :disabled="busyId === a.id"
                @click="startEdit(a)"
              >
                编辑
              </button>
              <button
                class="btn btn-danger-ghost btn-sm"
                :disabled="busyId === a.id"
                @click="onDelete(a)"
              >
                删除
              </button>
            </div>
          </article>
        </div>
      </section>
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

.manage-grid {
  display: grid;
  grid-template-columns: 400px 1fr;
  gap: 24px;
  align-items: start;
}

.publish {
  position: sticky;
  top: 92px;
}

.publish-title {
  font-size: 18px;
}

.publish-desc {
  margin: 6px 0 20px;
  font-size: 13px;
}

.form {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.date-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px;
}

.form-actions {
  display: flex;
  gap: 10px;
  margin-top: 2px;
}

.form-actions .btn-primary {
  flex: 1;
}

.textarea {
  resize: vertical;
  min-height: 110px;
  line-height: 1.6;
}

.list {
  display: flex;
  flex-direction: column;
  gap: 16px;
  min-width: 0;
}

.act-list {
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.act-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 16px;
  padding: 20px 22px;
}

.act-item-main {
  min-width: 0;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.act-item-top {
  display: flex;
  align-items: center;
  gap: 10px;
}

.act-item-title {
  font-size: 17px;
  line-height: 1.3;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.act-item-desc {
  font-size: 13px;
  line-height: 1.6;
  color: var(--c-ink-soft);
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.act-item-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 8px 18px;
}

.act-meta-item {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
  color: var(--c-ink-mute);
}

.act-meta-item svg {
  color: var(--c-primary);
  flex: none;
}

.act-item-actions {
  display: flex;
  flex-direction: column;
  gap: 8px;
  flex: none;
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

@media (max-width: 960px) {
  .manage-grid {
    grid-template-columns: 1fr;
  }

  .publish {
    position: static;
  }

  .act-item-actions {
    flex-direction: row;
  }
}

@media (max-width: 560px) {
  .date-grid {
    grid-template-columns: 1fr;
  }

  .act-item {
    flex-direction: column;
    align-items: stretch;
  }

  .act-item-actions {
    justify-content: flex-end;
  }
}
</style>