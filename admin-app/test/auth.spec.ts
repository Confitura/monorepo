import { describe, it, expect, vi, beforeEach, afterEach } from 'vitest'
import { h } from 'vue'
import { mount, flushPromises } from '@vue/test-utils'
import { createRouter, createMemoryHistory } from 'vue-router'
import { createVuetify } from 'vuetify'
import { VApp } from 'vuetify/components'
import WholePageLayout from '@/layouts/whole-page.vue'
import LoginPage from '@/pages/login.vue'

// The real router imports every page; the store only needs somewhere to navigate.
vi.mock('@/plugins/router.ts', async () => {
  const { createRouter, createMemoryHistory } = await import('vue-router')
  const router = createRouter({
    history: createMemoryHistory(),
    routes: [{ path: '/:pathMatch(.*)*', component: { template: '<div />' } }],
  })
  return { default: router }
})

describe('auth store', () => {
  beforeEach(() => setActivePinia(createPinia()))
  afterEach(() => localStorage.removeItem('token'))

  // "undefined" is what a failed token refresh used to leave behind.
  it.each(['undefined', 'not-a-jwt'])('treats the undecodable stored token %j as signed out and clears it', (stored) => {
    localStorage.setItem('token', stored)

    const store = useAuthStore()

    expect(store.user).toBeNull()
    expect(store.token).toBeNull()
    expect(localStorage.getItem('token')).toBeNull()
  })

  it('still shows the login page, in its layout, when the stored token is undecodable', async () => {
    localStorage.setItem('token', 'undefined')
    const router = createRouter({ history: createMemoryHistory(), routes: [{ path: '/login', component: LoginPage }] })
    await router.push('/login')

    const wrapper = mount(() => h(VApp, () => h(WholePageLayout)), {
      global: { plugins: [createVuetify(), router] },
    })
    await flushPromises()

    // Left mounted: unmounting resets the route first, and the layout's title needs a matched route.
    expect(wrapper.text()).toContain('Call for Papers')
  })
})
