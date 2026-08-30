import { get, post, put } from './request'
import type { PersonalDTO, PersonalVO } from '@/types/api'

/** 为当前登录用户创建一条空的个人真实信息记录 */
export function createPersonal(): Promise<void> {
  return post<void>('/personal/createPersonal')
}

/** 获取当前账号的个人真实信息 */
export function getPersonal(): Promise<PersonalVO> {
  return get<PersonalVO>('/personal/getPersonal')
}

/** 更新当前账号的个人真实信息 */
export function updatePersonal(dto: PersonalDTO): Promise<void> {
  return put<void>('/personal/updatePersonal', dto)
}
