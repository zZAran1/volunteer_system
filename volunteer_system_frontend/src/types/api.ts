// 与后端 (volunteer_system_backend) 约定一致的数据结构

/** 后端统一返回结构 */
export interface Result<T = unknown> {
  code: number
  msg: string | null
  data: T
}

/** 登录入参 */
export interface LoginDTO {
  email: string
  password: string
  /** 图形验证码标识（来自验证码接口） */
  captchaId: string
  /** 图形验证码文本 */
  captchaText: string
}

/** 注册入参 */
export interface RegisterDTO {
  username: string
  email: string
  password: string
}

/** 修改资料入参 */
export interface UpdateProfileDTO {
  username: string
}

/** 修改角色入参 */
export interface ChangeRoleDTO {
  email: string
  value: number
}

/** 登录返回 */
export interface LoginVO {
  token: string
}

/** 个人资料 */
export interface ProfileVO {
  username: string
  email: string
  created_at: string
  role: number
}

/** 用户列表项（管理端） */
export interface UserVO {
  id: number
  username: string
  email: string
  created_at: string
  role: number
  status: number
}

/** 角色枚举：0 超级管理员 · 1 管理员 · 2 志愿者 */
export const ROLE = {
  SUPER_ADMIN: 0,
  ADMIN: 1,
  USER: 2,
} as const

/** 账号状态：1 正常 · 2 封禁 */
export const STATUS = {
  ACTIVE: 1,
  BANNED: 2,
} as const

export function roleLabel(role: number): string {
  switch (role) {
    case ROLE.SUPER_ADMIN:
      return '超级管理员'
    case ROLE.ADMIN:
      return '管理员'
    case ROLE.USER:
      return '志愿者'
    default:
      return '未知'
  }
}

export function statusLabel(status: number): string {
  return status === STATUS.BANNED ? '已封禁' : '正常'
}

/** 图形验证码（后端 CaptchaVO） */
export interface CaptchaVO {
  /** 验证码唯一标识，登录时随验证码文本一起提交 */
  captchaId: string
  /** PNG 图片 base64，已含 data:image/png;base64, 前缀 */
  imageBase64: string
}

/** 活动视图对象（后端 ActivityVO，JSON 字段为下划线风格） */
export interface ActivityVO {
  id: number
  /** 发布者用户名（后端 LEFT JOIN 查询，可能为空） */
  poster_name: string
  title: string
  address: string
  start_date: string
  end_date: string
  description: string
  /** 已报名人数 */
  headcount: number
  /** 报名人数上限 */
  headcount_limit: number
  status: number
}

/** 发布活动入参（后端 CreateActivityDTO） */
export interface CreateActivityDTO {
  title: string
  address: string
  start_date: string
  end_date: string
  description: string
  headcount_limit: number
}

/** 修改活动入参（后端 UpdateActivityDTO，需携带 id） */
export interface UpdateActivityDTO extends CreateActivityDTO {
  id: number
}

/** 活动状态：0 待审核 · 1 招募中 */
export const ACTIVITY_STATUS = {
  PENDING: 0,
  RECRUITING: 1,
} as const

export function activityStatusLabel(status: number): string {
  switch (status) {
    case ACTIVITY_STATUS.RECRUITING:
      return '招募中'
    case ACTIVITY_STATUS.PENDING:
      return '待审核'
    default:
      return '状态未知'
  }
}