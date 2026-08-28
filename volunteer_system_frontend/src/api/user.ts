import { post, put, get, del } from './request'
import type {
  CaptchaVO,
  LoginDTO,
  LoginVO,
  ProfileVO,
  RegisterDTO,
  UpdateProfileDTO,
} from '@/types/api'

export function login(dto: LoginDTO): Promise<LoginVO> {
  return post<LoginVO>('/user/login', dto)
}

export function register(dto: RegisterDTO): Promise<void> {
  return post<void>('/user/register', dto)
}

export function updateProfile(dto: UpdateProfileDTO): Promise<void> {
  return put<void>('/user/updateProfile', dto)
}

export function getProfile(): Promise<ProfileVO> {
  return get<ProfileVO>('/user/profile')
}

export function deleteUser(): Promise<void> {
  return del<void>('/user/deleteUser')
}

/** 图形验证码 */
export function getCaptcha(): Promise<CaptchaVO> {
  return get<CaptchaVO>('/user/captcha')
}

/**
 * 上传头像（multipart/form-data）。
 * 后端约定字段名为 file，注意不要手动设置 Content-Type，
 * axios 会自动携带 multipart 边界。
 */
export function uploadAvatar(file: File): Promise<void> {
  const fd = new FormData()
  fd.append('file', file)
  return put<void>('/user/avatarUpdate', fd)
}