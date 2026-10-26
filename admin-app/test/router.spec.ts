import { describe, it, expect, beforeEach, afterEach } from 'vitest'
import type { RouteNamedMap } from 'vue-router/auto-routes'
import router from '@/plugins/router.ts'

// The real router with every page, so the guard sees the routes and meta the app has.
type AdminPage = Extract<keyof RouteNamedMap, `/admin${string}`>
const adminPages = router
  .getRoutes()
  .map((route) => route.name)
  .filter((name): name is AdminPage => typeof name === 'string' && name.startsWith('/admin'))

function signInAs(claims: { isAdmin: boolean; isSpeaker: boolean }) {
  const part = (o: object) => btoa(JSON.stringify(o)).replace(/=+$/, '')
  localStorage.setItem('token', `${part({ alg: 'HS256' })}.${part({ jti: 'u1', sub: 'u1', isNew: false, ...claims })}.sig`)
  setActivePinia(createPinia())
}

const notifications = () => useNotificationStore().notifications.map(({ type, text }) => `${type}: ${text}`)

describe('the router', () => {
  beforeEach(async () => {
    setActivePinia(createPinia())
    await router.push('/privacy-policy')
  })

  afterEach(() => {
    localStorage.clear()
  })

  it('finds the admin pages', () => {
    expect(adminPages).toContain('/admin/users')
  })

  it.each(adminPages)('sends a signed-out visitor opening %s to login', async (name) => {
    await router.push({ name })

    expect(router.currentRoute.value.path).toBe('/login')
  })

  it.each(adminPages)('refuses %s to a signed-in speaker', async (name) => {
    signInAs({ isAdmin: false, isSpeaker: true })

    await router.push({ name })

    expect(router.currentRoute.value.path).toBe('/privacy-policy')
    expect(notifications()).toEqual(['warning: You do not have the required permissions to access this page'])
  })

  it('lets an admin open an admin page', async () => {
    signInAs({ isAdmin: true, isSpeaker: false })

    await router.push('/admin/users')

    expect(router.currentRoute.value.path).toBe('/admin/users')
  })

  it('keeps a page without flags open to a signed-out visitor', async () => {
    await router.push('/rate')

    expect(router.currentRoute.value.path).toBe('/rate')
  })
})
