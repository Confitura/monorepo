import { defineStore } from 'pinia'
import router from '@/plugins/router.ts'
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
  try {
    return jwtDecode(token) as User
  } catch {
    return null
  }
}

export const useAuthStore = defineStore('auth', {
  state: () => {
    let token = localStorage.getItem('token')
    const user: User | null = extractUser(token)
    // A stored token that can't be decoded (e.g. the string "undefined") signs no one in, so drop it
    // rather than let it break every page until the person clears their site data.
    if (user == null && token != null) {
      localStorage.removeItem('token')
      token = null
    }
    return { user, token }
  },
  actions: {
    login(token: string) {
      this.replaceToken(token)
      if (this.user?.isAdmin) {
        router.push('/dashboard')
      } else {
        router.push('/homepage')
      }
    },
    // Swaps in a newly issued token for the signed-in User (e.g. after a merge) without leaving the page.
    replaceToken(token: string) {
      localStorage.setItem('token', token)
      this.token = token
      this.user = extractUser(token)
    },
    logout() {
      localStorage.removeItem('token')
      this.token = null
      this.user = null
      router.push('/login')
    },
  },
})
