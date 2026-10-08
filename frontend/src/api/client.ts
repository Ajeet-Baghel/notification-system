import axios from 'axios'
import type { ApiError } from '../types'

const TOKEN_KEY = 'auth.token'

export const getToken = () => localStorage.getItem(TOKEN_KEY)
export const setToken = (token: string) => localStorage.setItem(TOKEN_KEY, token)
export const clearToken = () => localStorage.removeItem(TOKEN_KEY)

export const api = axios.create({
  baseURL: '/api',
  headers: { 'Content-Type': 'application/json' },
})

api.interceptors.request.use((config) => {
  const token = getToken()
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

/** Pull a human-readable message out of our backend's {message} error body. */
export function errorMessage(error: unknown): string {
  if (axios.isAxiosError<ApiError>(error) && error.response?.data?.message) {
    return error.response.data.message
  }
  if (axios.isAxiosError(error) && !error.response) {
    return 'Cannot reach the server. Is the backend running?'
  }
  return 'Something went wrong'
}
