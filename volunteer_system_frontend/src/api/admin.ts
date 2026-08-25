import { post, put, get } from './request'
import type { ChangeRoleDTO, UserVO } from '@/types/api'

/**
 * 后端 `/api/admin/banUser` 与 `/unbanUser` 接口使用 `@RequestBody String`，
 * 期望请求体是一个 JSON 字符串字面量（形如 "someone@example.com"）。
 * 这里传入原始 email 字符串并指定 application/json，由 axios 序列化为 JSON 字符串。
 */
function banLike(url: string, email: string): Promise<void> {
  return put<void>(url, email, { headers: { 'Content-Type': 'application/json' } })
}

export function banUser(email: string): Promise<void> {
  return banLike('/admin/banUser', email)
}

export function unbanUser(email: string): Promise<void> {
  return banLike('/admin/unbanUser', email)
}

/** 普通志愿者列表（管理员 / 超级管理员可用） */
export function getUsers(): Promise<UserVO[]> {
  return get<UserVO[]>('/admin/getUser')
}

/** 全部用户列表：管理员 + 志愿者（仅超级管理员可用） */
export function getAllUsers(): Promise<UserVO[]> {
  return get<UserVO[]>('/admin/getAllUser')
}

export function changeRole(dto: ChangeRoleDTO): Promise<void> {
  return post<void>('/admin/changeRole', dto)
}