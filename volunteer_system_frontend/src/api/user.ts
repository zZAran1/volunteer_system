import { post, put, get, del } from './request'
import type {
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