import { get, put } from './request'
import type { PersonalDTO, PersonalVO } from '@/types/api'

/** 获取当前账号的个人真实信息 */
export function getPersonal(): Promise<PersonalVO> {
  return get<PersonalVO>('/personal/getPersonal')
}

/** 更新当前账号的个人真实信息 */
export function updatePersonal(dto: PersonalDTO): Promise<void> {
  return put<void>('/personal/updatePersonal', dto)
}
