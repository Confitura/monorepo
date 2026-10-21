import { describe, it, expect, vi, beforeEach } from 'vitest'
import { AxiosError, type InternalAxiosRequestConfig } from 'axios'
import { client } from '@/client/client.gen'
import { refreshToken } from '@/utils/api.ts'
import router from '@/plugins/router.ts'

// The real router imports every page; two routes are enough to see where the app navigates.
vi.mock('@/plugins/router.ts', async () => {
  const { createRouter, createMemoryHistory } = await import('vue-router')
  const router = createRouter({
    history: createMemoryHistory(),
    routes: [
      { path: '/', component: { template: '<div />' } },
      { path: '/login', component: { template: '<div />' } },
    ],
  })
  return { default: router }
})

// Payload: {"jti":"user-1","sub":"Jane","isAdmin":false,"isVolunteer":false,"isSpeaker":false,"isNew":false}
const TOKEN =
  'eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiJ1c2VyLTEiLCJzdWIiOiJKYW5lIiwiaXNBZG1pbiI6ZmFsc2UsImlzVm9sdW50ZWVyIjpmYWxzZSwiaXNTcGVha2VyIjpmYWxzZSwiaXNOZXciOmZhbHNlfQ.signature'

function backendRespondsWith(status: number) {
  client.instance.defaults.adapter = (config: InternalAxiosRequestConfig) =>
    Promise.reject(
      new AxiosError(`Request failed with status code ${status}`, AxiosError.ERR_BAD_REQUEST, config, null, {
        status,
        statusText: '',
        headers: {},
        config,
        data: {},
      }),
    )
}

describe('api client', () => {
  beforeEach(async () => {
    localStorage.setItem('token', TOKEN)
    setActivePinia(createPinia())
    await router.push('/')
  })

  it('signs the user out and returns to the login page when the backend rejects their token', async () => {
    backendRespondsWith(401)

    await refreshToken()

    expect(localStorage.getItem('token')).toBeNull()
    expect(useAuthStore().user).toBeNull()
    await vi.waitFor(() => expect(router.currentRoute.value.path).toBe('/login'))
  })

  it('keeps the user signed in when a request is only forbidden', async () => {
    backendRespondsWith(403)

    await refreshToken()

    expect(localStorage.getItem('token')).toBe(TOKEN)
    expect(useAuthStore().user?.jti).toBe('user-1')
    expect(router.currentRoute.value.path).toBe('/')
  })
})
