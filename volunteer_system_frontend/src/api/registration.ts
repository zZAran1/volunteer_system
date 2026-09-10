import { post } from './request'
import type { RegistrationDTO } from '@/types/api'

/**
 * 报名 / 取消报名。
 * 后端签名为 `@Validated @RequestBody RegistrationDTO`，
 * 因此 activity_id 必须放在 JSON body 中提交（不能走查询串）。
 */
export function registrant(activityId: number): Promise<void> {
  const payload: RegistrationDTO = { activity_id: activityId }
  return post<void>('/registration/registrant', payload)
}

export function unRegistrant(activityId: number): Promise<void> {
  const payload: RegistrationDTO = { activity_id: activityId }
  return post<void>('/registration/unRegistrant', payload)
}
