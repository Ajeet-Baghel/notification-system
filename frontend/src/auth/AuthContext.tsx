import { createContext, useContext, useMemo, useState } from 'react'
import type { ReactNode } from 'react'
import { api, clearToken, getToken, setToken } from '../api/client'
import type { AuthResponse, LoginRequest, RegisterRequest } from '../types'

interface AuthState {
  name: string
  email: string
}

interface AuthContextValue {
  user: AuthState | null
  isAuthenticated: boolean
  login: (request: LoginRequest) => Promise<void>
  register: (request: RegisterRequest) => Promise<void>
  logout: () => void
}

const AuthContext = createContext<AuthContextValue | null>(null)

const USER_KEY = 'auth.user'

export function AuthProvider({ children }: { children: ReactNode }) {
  const [user, setUser] = useState<AuthState | null>(() => {
    const stored = localStorage.getItem(USER_KEY)
    return stored ? (JSON.parse(stored) as AuthState) : null
  })

  const handleAuth = (response: AuthResponse) => {
    setToken(response.token)
    const state = { name: response.name, email: response.email }
    localStorage.setItem(USER_KEY, JSON.stringify(state))
    setUser(state)
  }

  const value = useMemo<AuthContextValue>(() => ({
    user,
    isAuthenticated: Boolean(user && getToken()),
    login: async (request) => {
      const { data } = await api.post<AuthResponse>('/auth/login', request)
      handleAuth(data)
    },
    register: async (request) => {
      const { data } = await api.post<AuthResponse>('/auth/register', request)
      handleAuth(data)
    },
    logout: () => {
      clearToken()
      localStorage.removeItem(USER_KEY)
      setUser(null)
    },
  }), [user])

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>
}

export function useAuth(): AuthContextValue {
  const ctx = useContext(AuthContext)
  if (!ctx) throw new Error('useAuth must be used inside AuthProvider')
  return ctx
}
