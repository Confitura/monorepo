import { defineStore } from 'pinia'
import router from '@/plugins/router.ts'
import { api } from '@/utils/api.ts'
import { jwtDecode } from 'jwt-decode'

export interface User {
  sub: string
  jti: string
  isVolunteer: boolean
  isAdmin: boolean
  isNew: boolean
  isSpeaker: boolean
}

function extractUser(token: string | null): User | null {
  if (token == null) {
    return null
  }
  const body = jwtDecode(token)
  return body as User
}

export const useAuthStore = defineStore('auth', {
  state: () => {
    const token = localStorage.getItem('token')
    const user = extractUser(token)
    api.defaults.headers.common['Authorization'] = token
    return {
      user,
      token,
    }
  },
  actions: {
    login(token: string) {
      localStorage.setItem('token', token)
      this.token = token
      this.user = extractUser(token)
      api.defaults.headers.common['Authorization'] = token
      if (this.user?.isAdmin) {
        router.push('/dashboard')
      } else {
        router.push('/homepage')
      }
    },
    logout() {
      localStorage.removeItem('token')
      this.token = null
      this.user = null
      api.defaults.headers.common['Authorization'] = null
      router.push('/login')
    },
  },
})
