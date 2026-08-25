import axios from 'axios'
import type { AxiosRequestConfig } from 'axios'
import type { Result } from '@/types/api'
import { authState, clearAuth } from '@/stores/auth'

export const http = axios.create({
  baseURL: '/api',
  timeout: 10000,
})

http.interceptors.request.use((config) => {
  if (authState.token) {
    config.headers.Authorization = `Bearer ${authState.token}`
  }
  return config
})

function extractError(err: unknown): Error {
  if (axios.isAxiosError(err)) {
    const data = err.response?.data as Result | undefined
    if (data && typeof data.msg === 'string' && data.msg) {
      return new Error(data.msg)
    }
    if (err.code === 'ECONNABORTED') {
      return new Error('请求超时，请稍后重试')
    }
    if (!err.response) {
      return new Error('无法连接服务器，请确认后端服务已启动')
    }
    return new Error('请求失败，请稍后重试')
  }
  if (err instanceof Error) {
    return err
  }
  return new Error('请求失败，请稍后重试')
}

async function request<T>(config: AxiosRequestConfig): Promise<T> {
  try {
    const res = await http.request<Result<T>>(config)
    const result = res.data
    if (!result || result.code !== 200) {
      // 登录态失效：清除凭证，由上层跳转登录页
      if (result && result.code === 1002) {
        clearAuth()
      }
      throw new Error(result?.msg || '请求失败')
    }
    return result.data
  } catch (err) {
    throw extractError(err)
  }
}

export const get = <T>(url: string, config?: AxiosRequestConfig): Promise<T> =>
  request<T>({ ...config, method: 'GET', url })

export const post = <T>(
  url: string,
  data?: unknown,
  config?: AxiosRequestConfig,
): Promise<T> => request<T>({ ...config, method: 'POST', url, data })

export const put = <T>(
  url: string,
  data?: unknown,
  config?: AxiosRequestConfig,
): Promise<T> => request<T>({ ...config, method: 'PUT', url, data })

export const del = <T>(url: string, config?: AxiosRequestConfig): Promise<T> =>
  request<T>({ ...config, method: 'DELETE', url })