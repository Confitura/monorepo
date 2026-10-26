import { describe, it, expect, vi, afterEach } from 'vitest'
import { mount } from '@vue/test-utils'
import { createVuetify } from 'vuetify'
import { createTestingPinia } from '@pinia/testing'
import VoteForPapersPage from '@/pages/vote-for-papers.vue'

const api = vi.hoisted(() => ({
  start: vi.fn(),
  save: vi.fn(),
}))

vi.mock('@/utils/api.ts', () => api)

describe('vote-for-papers page', () => {
  afterEach(() => {
    vi.clearAllMocks()
    localStorage.clear()
  })

  // Enter on the focused Start button clicks it and also reaches the page's own Enter shortcut.
  it('starts voting once while a start is in flight', async () => {
    api.start.mockReturnValue(new Promise(() => {}))
    const wrapper = mount(VoteForPapersPage, {
      global: { plugins: [createVuetify(), createTestingPinia({ createSpy: vi.fn })] },
    })
    const startButton = wrapper.findAll('button').find((button) => button.text() === 'Start')!

    await startButton.trigger('click')
    document.dispatchEvent(new KeyboardEvent('keyup', { code: 'Enter' }))
    await startButton.trigger('click')

    expect(api.start).toHaveBeenCalledTimes(1)
    wrapper.unmount()
  })
})
