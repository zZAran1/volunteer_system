<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { deleteUser, getProfile, updateProfile, uploadAvatar } from '@/api/user'
import { createPersonal, getPersonal, updatePersonal } from '@/api/personal'
import { roleLabel, type PersonalDTO } from '@/types/api'
import { authState, clearAuth, setProfile } from '@/stores/auth'
import { useToast } from '@/composables/toast'

const router = useRouter()
const toast = useToast()

const profile = computed(() => authState.profile)
const initials = computed(() => (profile.value?.username ?? '志').slice(0, 1).toUpperCase())
const roleText = computed(() =>
  profile.value ? roleLabel(profile.value.role) : '',
)

const username = ref('')
const saving = ref(false)
const deleting = ref(false)

const fileInput = ref<HTMLInputElement | null>(null)
const avatarPreview = ref('')
const uploading = ref(false)

/** 头像展示：优先本地预览（刚上传未刷新时），否则回显服务器头像 */
const avatarSrc = computed(() => avatarPreview.value || profile.value?.image_url || '')

/** 个人真实信息表单 */
const personalForm = reactive({
  realName: '',
  email: '',
  phone: '',
  age: '',
  gender: '',
  idNumber: '',
  current_address: '',
})
const personalLoading = ref(true)
const savingPersonal = ref(false)

function fillPersonalForm(p: {
  realName?: string
  email?: string
  phone?: string
  age?: number
  gender?: string
  idNumber?: string
  current_address?: string
}) {
  personalForm.realName = p.realName ?? ''
  personalForm.email = p.email ?? ''
  personalForm.phone = p.phone ?? ''
  personalForm.age = p.age != null ? String(p.age) : ''
  personalForm.gender = p.gender ?? ''
  personalForm.idNumber = p.idNumber ?? ''
  personalForm.current_address = p.current_address ?? ''
}

async function loadPersonal() {
  personalLoading.value = true
  try {
    let p = await getPersonal()
    if (!p) {
      // 老账号可能没有 personal 记录（注册时未自动创建），先创建空行再加载，
      // 否则后续 updatePersonal 更新不到任何行
      await createPersonal()
      p = await getPersonal()
    }
    fillPersonalForm(p ?? {})
  } catch {
    // 接口异常时保持空表单，等待用户填写
    fillPersonalForm({})
  } finally {
    personalLoading.value = false
  }
}

function validatePersonal(): string | null {
  if (
    personalForm.age !== '' &&
    (!Number.isInteger(Number(personalForm.age)) ||
      Number(personalForm.age) < 1 ||
      Number(personalForm.age) > 120)
  ) {
    return '年龄需为 1-120 之间的整数'
  }
  const idNumber = personalForm.idNumber.trim()
  if (idNumber && !/^\d{17}[\dXx]$/.test(idNumber)) {
    return '身份证号格式不正确'
  }
  return null
}

async function onSavePersonal() {
  const msg = validatePersonal()
  if (msg) {
    toast.error(msg)
    return
  }
  const payload: PersonalDTO = {
    realName: personalForm.realName.trim(),
    email: personalForm.email.trim(),
    phone: personalForm.phone.trim(),
    age: personalForm.age === '' ? undefined : Number(personalForm.age),
    gender: personalForm.gender,
    idNumber: personalForm.idNumber.trim(),
    current_address: personalForm.current_address.trim(),
  }
  savingPersonal.value = true
  try {
    await updatePersonal(payload)
    toast.success('个人真实信息已保存')
  } catch (e) {
    toast.error((e as Error).message)
  } finally {
    savingPersonal.value = false
  }
}

function pickAvatar() {
  fileInput.value?.click()
}

async function onAvatarChange(e: Event) {
  const input = e.target as HTMLInputElement
  const file = input.files?.[0]
  input.value = ''
  if (!file) return
  if (!file.type.startsWith('image/')) {
    toast.error('请选择图片文件')
    return
  }
  if (file.size > 5 * 1024 * 1024) {
    toast.error('图片大小不能超过 5MB')
    return
  }
  uploading.value = true
  try {
    await uploadAvatar(file)
    // 上传成功后拉取最新资料（含 image_url），并清除本地临时预览
    setProfile(await getProfile())
    if (avatarPreview.value) URL.revokeObjectURL(avatarPreview.value)
    avatarPreview.value = ''
    toast.success('头像已更新')
  } catch (err) {
    toast.error((err as Error).message)
  } finally {
    uploading.value = false
  }
}

onMounted(() => {
  username.value = profile.value?.username ?? ''
  loadPersonal()
})

function formatDate(value: string): string {
  const [y, m, d] = value.split('-')
  if (!y || !m || !d) return value
  return `${y} 年 ${Number(m)} 月 ${Number(d)} 日`
}

async function onSave() {
  const next = username.value.trim()
  if (!next) {
    toast.error('用户名不能为空')
    return
  }
  if (next === profile.value?.username) {
    toast.info('未做任何修改')
    return
  }

  saving.value = true
  try {
    await updateProfile({ username: next })
    setProfile(await getProfile())
    toast.success('资料已更新')
  } catch (e) {
    toast.error((e as Error).message)
  } finally {
    saving.value = false
  }
}

async function onDelete() {
  const confirmed = window.confirm(
    '确定要注销账户吗？此操作不可撤销，账户相关信息将被永久删除。',
  )
  if (!confirmed) return

  deleting.value = true
  try {
    await deleteUser()
    clearAuth()
    toast.success('账户已注销')
    router.push({ name: 'login' })
  } catch (e) {
    toast.error((e as Error).message)
  } finally {
    deleting.value = false
  }
}
</script>

<template>
  <div class="container">
    <div v-if="!profile" class="empty animate-in">
      <div class="empty-icon">
        <svg viewBox="0 0 24 24" width="30" height="30" fill="none">
          <circle cx="12" cy="12" r="9" stroke="currentColor" stroke-width="1.8" />
          <path d="M12 8v4l2.6 1.6" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round" />
        </svg>
      </div>
      <p class="empty-title">加载中…</p>
    </div>

    <div v-else class="profile-grid animate-in">
      <!-- 左侧：身份卡片 -->
      <section class="card card-pad identity">
        <div class="avatar avatar-lg" :class="{ 'avatar-img': !!avatarSrc }">
          <img v-if="avatarSrc" :src="avatarSrc" alt="我的头像" />
          <template v-else>{{ initials }}</template>
        </div>
        <div class="identity-body">
          <h2 class="identity-name">{{ profile.username }}</h2>
          <p class="identity-email">{{ profile.email }}</p>
          <span class="badge badge-green">{{ roleText }}</span>
        </div>
        <button class="btn btn-ghost btn-sm" :disabled="uploading" @click="pickAvatar">
          {{ uploading ? '上传中…' : '更换头像' }}
        </button>
        <input
          ref="fileInput"
          class="visually-hidden"
          type="file"
          accept="image/png,image/jpeg,image/gif,image/webp"
          @change="onAvatarChange"
        />
      </section>

      <!-- 右侧：资料与操作 -->
      <div class="stack">
        <section class="card card-pad">
          <h3 class="section-title">基本信息</h3>
          <dl class="info-list">
            <div class="info-row">
              <dt>邮箱</dt>
              <dd>{{ profile.email }}</dd>
            </div>
            <div class="info-row">
              <dt>角色</dt>
              <dd>{{ roleText }}</dd>
            </div>
            <div class="info-row">
              <dt>加入时间</dt>
              <dd>{{ formatDate(profile.created_at) }}</dd>
            </div>
          </dl>
        </section>

        <section class="card card-pad">
          <h3 class="section-title">编辑用户名</h3>
          <div class="edit-row">
            <input
              v-model="username"
              class="input"
              type="text"
              maxlength="30"
              placeholder="输入新的用户名"
            />
            <button class="btn btn-primary" :disabled="saving" @click="onSave">
              {{ saving ? '保存中…' : '保存' }}
            </button>
          </div>
        </section>

        <section class="card card-pad">
          <h3 class="section-title">个人真实信息</h3>
          <p class="muted personal-hint">
            用于身份核验与活动保障，仅自己可见。尚未填写时可在此完善。
          </p>
          <div v-if="personalLoading" class="personal-empty">
            <div class="spinner" aria-hidden="true"></div>
            <p class="muted">正在加载个人真实信息…</p>
          </div>
          <form v-else class="personal-form" novalidate @submit.prevent="onSavePersonal">
            <div class="p-grid">
              <div class="field">
                <label class="field-label" for="p-realname">真实姓名</label>
                <input
                  id="p-realname"
                  v-model="personalForm.realName"
                  class="input"
                  type="text"
                  maxlength="30"
                  placeholder="请输入真实姓名"
                />
              </div>
              <div class="field">
                <label class="field-label" for="p-gender">性别</label>
                <select id="p-gender" v-model="personalForm.gender" class="input">
                  <option value="">未选择</option>
                  <option value="男">男</option>
                  <option value="女">女</option>
                  <option value="其他">其他</option>
                </select>
              </div>
              <div class="field">
                <label class="field-label" for="p-age">年龄</label>
                <input
                  id="p-age"
                  v-model="personalForm.age"
                  class="input"
                  type="number"
                  min="1"
                  max="120"
                  step="1"
                  placeholder="1-120"
                />
              </div>
              <div class="field">
                <label class="field-label" for="p-phone">联系电话</label>
                <input
                  id="p-phone"
                  v-model="personalForm.phone"
                  class="input"
                  type="tel"
                  maxlength="20"
                  placeholder="请输入手机号"
                />
              </div>
              <div class="field">
                <label class="field-label" for="p-email">联系邮箱</label>
                <input
                  id="p-email"
                  v-model="personalForm.email"
                  class="input"
                  type="email"
                  maxlength="50"
                  placeholder="可用于接收活动通知"
                />
              </div>
              <div class="field">
                <label class="field-label" for="p-idnumber">身份证号</label>
                <input
                  id="p-idnumber"
                  v-model="personalForm.idNumber"
                  class="input"
                  type="text"
                  maxlength="18"
                  placeholder="18 位身份证号（选填）"
                />
              </div>
              <div class="field p-full">
                <label class="field-label" for="p-address">现居地址</label>
                <input
                  id="p-address"
                  v-model="personalForm.current_address"
                  class="input"
                  type="text"
                  maxlength="100"
                  placeholder="请输入当前居住地址"
                />
              </div>
            </div>
            <div class="p-actions">
              <button class="btn btn-primary" type="submit" :disabled="savingPersonal">
                {{ savingPersonal ? '保存中…' : '保存个人真实信息' }}
              </button>
            </div>
          </form>
        </section>

        <section class="card card-pad danger-zone">
          <div>
            <h3 class="section-title text-danger">注销账户</h3>
            <p class="muted danger-desc">注销后，你的账户与相关信息将被永久删除，无法恢复。</p>
          </div>
          <button class="btn btn-danger-ghost" :disabled="deleting" @click="onDelete">
            {{ deleting ? '注销中…' : '注销账户' }}
          </button>
        </section>
      </div>
    </div>
  </div>
</template>

<style scoped>
.profile-grid {
  display: grid;
  grid-template-columns: 320px 1fr;
  gap: 24px;
  align-items: start;
}

.stack {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.identity {
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
  gap: 14px;
}

.identity-body {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
}

.identity-name {
  font-size: 22px;
}

.avatar-img {
  padding: 0;
  overflow: hidden;
  background: var(--c-primary-soft);
}

.avatar-img img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.visually-hidden {
  position: absolute;
  width: 1px;
  height: 1px;
  padding: 0;
  margin: -1px;
  overflow: hidden;
  clip: rect(0 0 0 0);
  white-space: nowrap;
  border: 0;
}

.identity-email {
  color: var(--c-ink-mute);
  font-size: 14px;
  word-break: break-all;
}

.section-title {
  font-size: 16px;
  margin-bottom: 18px;
}

.info-list {
  display: flex;
  flex-direction: column;
  gap: 0;
}

.info-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 13px 0;
  border-bottom: 1px solid var(--c-line);
}

.info-row:last-child {
  border-bottom: none;
}

.info-row dt {
  font-size: 14px;
  color: var(--c-ink-mute);
}

.info-row dd {
  font-size: 14px;
  font-weight: 600;
  color: var(--c-ink);
}

.edit-row {
  display: flex;
  gap: 12px;
}

.edit-row .input {
  flex: 1;
}

.personal-hint {
  margin: -6px 0 18px;
  font-size: 13px;
}

.personal-empty {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 18px 0;
}

.personal-form {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.p-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 14px;
}

.p-full {
  grid-column: 1 / -1;
}

.p-actions {
  display: flex;
  justify-content: flex-end;
}

.spinner {
  width: 22px;
  height: 22px;
  border-radius: 50%;
  border: 3px solid var(--c-primary-soft);
  border-top-color: var(--c-primary);
  animation: profile-spin 0.8s linear infinite;
}

@keyframes profile-spin {
  to {
    transform: rotate(360deg);
  }
}

.danger-zone {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 20px;
  border-color: rgba(217, 88, 79, 0.3);
}

.danger-desc {
  margin-top: 6px;
  font-size: 13px;
  max-width: 360px;
}

@media (max-width: 860px) {
  .profile-grid {
    grid-template-columns: 1fr;
  }

  .danger-zone {
    flex-direction: column;
    align-items: flex-start;
  }

  .edit-row {
    flex-direction: column;
  }

  .p-grid {
    grid-template-columns: 1fr;
  }
}
</style>