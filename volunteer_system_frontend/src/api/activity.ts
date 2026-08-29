import { del, get, post, put } from './request'
import type {
  ActivityVO,
  CreateActivityDTO,
  UpdateActivityDTO,
} from '@/types/api'

/** 活动广场：公开可见（已审核通过）的活动列表 */
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

/** 发布活动 */
export function createActivity(dto: CreateActivityDTO): Promise<void> {
  return post<void>('/activity/create', dto)
}

/** 修改活动（需携带 id） */
export function updateActivity(dto: UpdateActivityDTO): Promise<void> {
  return put<void>('/activity/update', dto)
}

/**
 * 删除活动。
 * 后端接口为 DELETE /activity/delete，使用 @RequestParam 将参数绑定到
 * UpdateActivityDTO，实际只用到了 id，因此这里通过查询串传 id。
 */
export function deleteActivity(id: number): Promise<void> {
  return del<void>('/activity/delete', { params: { id } })
}