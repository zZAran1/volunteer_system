import { del, get, post, put } from './request'
import type {
  ActivityVO,
  CreateActivityDTO,
  UpdateActivityDTO,
} from '@/types/api'

/** 活动广场：公开可见（已审核通过，status=1）的活动列表 */
export function viewActivities(): Promise<ActivityVO[]> {
  return get<ActivityVO[]>('/activity/viewActivity')
}

/** 全部活动列表（仅管理员 / 超级管理员可用） */
export function getAllActivities(): Promise<ActivityVO[]> {
  return get<ActivityVO[]>('/activity/getAllActivity')
}

/** 我发布的活动列表 */
export function getMyActivities(): Promise<ActivityVO[]> {
  return get<ActivityVO[]>('/activity/getMyActivity')
}

/** 我报名的活动列表 */
export function getRegisteredActivities(): Promise<ActivityVO[]> {
  return get<ActivityVO[]>('/activity/getRegistered')
}

/** 管理员：待审核的活动（status=0） */
export function getUnderReviewActivities(): Promise<ActivityVO[]> {
  return get<ActivityVO[]>('/activity/underReviewActivity')
}

/** 管理员：进行中的活动（status=2） */
export function getOngoingActivities(): Promise<ActivityVO[]> {
  return get<ActivityVO[]>('/activity/ongoingActivity')
}

/** 管理员：已满员的活动（status=3） */
export function getFullActivities(): Promise<ActivityVO[]> {
  return get<ActivityVO[]>('/activity/fullActivity')
}

/** 管理员：已结束的活动（status=4） */
export function getEndedActivities(): Promise<ActivityVO[]> {
  return get<ActivityVO[]>('/activity/endedActivity')
}

/** 管理员：审核不通过的活动（status=5） */
export function getRejectedActivities(): Promise<ActivityVO[]> {
  return get<ActivityVO[]>('/activity/rejectedActivity')
}

/** 发布活动 */
export function createActivity(dto: CreateActivityDTO): Promise<void> {
  return post<void>('/activity/create', dto)
}

/** 修改活动（需携带 id） */
export function updateActivity(dto: UpdateActivityDTO): Promise<void> {
  return put<void>('/activity/update', dto)
}

/**
 * 删除自己发布的活动。
 * 后端签名为 `@RequestParam Integer activity_id`，参数名必须是 activity_id。
 */
export function deleteMyActivity(id: number): Promise<void> {
  return del<void>('/activity/deleteMyActivity', { params: { activity_id: id } })
}

/**
 * 管理员删除任意活动。
 * 后端签名为 `@RequestParam Integer activity_id`。
 */
export function deleteActivityByAdmin(activityId: number): Promise<void> {
  return del<void>('/activity/deleteActivity', { params: { activity_id: activityId } })
}

/**
 * 按标题模糊搜索活动。
 * 后端 `@RequestParam SelectActivityDTO`，从查询串绑定 title；
 * 该接口不做状态过滤（返回全部状态），仅适合管理员使用。
 */
export function searchActivitiesByTitle(title: string): Promise<ActivityVO[]> {
  return get<ActivityVO[]>('/activity/titleSelectActivity', { params: { title } })
}

/** 审核通过活动（JSON body：{ activity_id }） */
export function reviewApprove(activityId: number): Promise<void> {
  return put<void>('/activity/reviewEvent_Approved', { activity_id: activityId })
}

/** 驳回活动（JSON body：{ activity_id }） */
export function reviewReject(activityId: number): Promise<void> {
  return put<void>('/activity/reviewEvent_Rejected', { activity_id: activityId })
}
