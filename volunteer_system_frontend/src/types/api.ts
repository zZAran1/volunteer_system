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