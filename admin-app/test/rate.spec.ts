import { describe, it, expect, vi } from 'vitest'
import { mount, flushPromises } from '@vue/test-utils'
import { createVuetify } from 'vuetify'
import { createRouter, createWebHistory } from 'vue-router'
import RatePage from '@/pages/rate.[[entryId]].vue'

// Whatever zone the browser is in, the opening day must not shift.
process.env.TZ = 'America/New_York'

global.ResizeObserver = class ResizeObserver {
  observe() {}
  unobserve() {}
  disconnect() {}
}

vi.mock('@/utils/api.ts', () => ({
  addRating: vi.fn(),
  getRatingEnabled: vi.fn().mockResolvedValue({ data: { ratingEnabled: true }, status: 200 }),
  getRatingStatus: vi.fn().mockResolvedValue({ data: { open: false, opensAt: '2026-09-25' }, status: 200 }),
}))

describe('rate page before rating opens', () => {
  it('names the opening day as the backend dates it', async () => {
    const router = createRouter({ history: createWebHistory(), routes: [{ path: '/rate/:entryId?', component: RatePage }] })
    await router.push('/rate/p1')
    const wrapper = mount(RatePage, { global: { plugins: [createVuetify(), router] } })
    await flushPromises()

    expect(wrapper.text()).toContain('Rating will be enabled on September 25, 2026')
  })
})
