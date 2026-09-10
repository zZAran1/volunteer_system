import { post, put, get } from './request'
import type { ChangeRoleDTO, UserVO } from '@/types/api'

/**
 * 后端 `/api/admin/banUser` 与 `/unbanUser` 接口签名为 `@Valid @RequestBody UsernameDTO`，
 * 期望请求体为对象 `{ "username": "zhangsan" }`，且按「用户名」定位用户。
 */
function banLike(url: string, username: string): Promise<void> {
  return put<void>(url, { username })
}

export function banUser(username: string): Promise<void> {
  return banLike('/admin/banUser', username)
}

export function unbanUser(username: string): Promise<void> {
  return banLike('/admin/unbanUser', username)
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