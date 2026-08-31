import { post } from './request'

/**
 * 报名 / 取消报名。
 * 后端接口参数是 `@Validated RegistrationDTO`（没有 @RequestBody），
 * 由 Spring 从表单 / 查询参数绑定，所以 activity_id 走查询串而不是 JSON body。
 */
export function registrant(activityId: number): Promise<void> {
  return post<void>('/registration/registrant', undefined, {
    params: { activity_id: activityId },
  })
}

export function unRegistrant(activityId: number): Promise<void> {
  return post<void>('/registration/unRegistrant', undefined, {
    params: { activity_id: activityId },
  })
}
