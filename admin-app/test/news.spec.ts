import { describe, it, expect, vi, beforeEach, afterEach } from 'vitest'
import { mount, flushPromises } from '@vue/test-utils'
import { createVuetify } from 'vuetify'
import { createTestingPinia } from '@pinia/testing'
import NewsPage from '@/pages/admin/news.vue'

global.ResizeObserver = class ResizeObserver {
  observe() {}
  unobserve() {}
  disconnect() {}
}

// The API returns News Entries newest first; the page keeps that order.
const entries = [
  { id: 'n1', title: 'Coming soon', slug: 'coming-soon', date: '2026-12-01T09:00:00Z', published: true },
  { id: 'n2', title: 'Hello world', slug: 'hello-world', date: '2026-06-15T10:30:00Z', published: true },
  { id: 'n3', title: 'Unfinished', slug: 'unfinished', date: '2026-05-01T08:00:00Z', published: false },
]

const api = vi.hoisted(() => ({
  getAllNewsEntries: vi.fn(),
  deleteNewsEntry: vi.fn(),
}))

vi.mock('@/utils/api.ts', () => api)

vi.mock('@/components/DialogConfirm.vue', () => ({
  default: {
    name: 'DialogConfirm',
    template: '<div />',
    setup(_: unknown, { expose }: { expose: (o: unknown) => void }) {
      expose({ open: () => Promise.resolve(true) })
      return {}
    },
  },
}))

function mountPage() {
  return mount(NewsPage, {
    global: { plugins: [createVuetify(), createTestingPinia({ createSpy: vi.fn })] },
  })
}

describe('admin news page', () => {
  beforeEach(() => {
    vi.useFakeTimers({ toFake: ['Date'] })
    vi.setSystemTime(new Date('2026-10-22T12:00:00Z'))
    api.getAllNewsEntries.mockResolvedValue({ data: entries, status: 200 })
    api.deleteNewsEntry.mockResolvedValue({ status: 204 })
  })

  afterEach(() => {
    vi.useRealTimers()
    vi.clearAllMocks()
  })

  it('lists entries in the order the API returns them, newest first', async () => {
    const wrapper = mountPage()
    await flushPromises()
    const titles = wrapper.findAll('tbody tr').map((row) => row.text())
    expect(titles).toHaveLength(3)
    expect(titles[0]).toContain('Coming soon')
    expect(titles[1]).toContain('Hello world')
    expect(titles[2]).toContain('Unfinished')
  })

  it('marks each entry draft, scheduled or public', async () => {
    const wrapper = mountPage()
    await flushPromises()
    const rows = wrapper.findAll('tbody tr').map((row) => row.text())
    expect(rows[0]).toContain('Scheduled')
    expect(rows[1]).toContain('Public')
    expect(rows[2]).toContain('Draft')
  })

  it('shows dates in Europe/Warsaw time', async () => {
    const wrapper = mountPage()
    await flushPromises()
    // 10:30 UTC in June is 12:30 in Warsaw (CEST); 09:00 UTC in December is 10:00 (CET)
    const rows = wrapper.findAll('tbody tr').map((row) => row.text())
    expect(rows[1]).toContain('15 Jun 2026')
    expect(rows[1]).toContain('12:30')
    expect(rows[0]).toContain('10:00')
  })

  it('deletes an entry after confirmation', async () => {
    const wrapper = mountPage()
    await flushPromises()
    ;(wrapper.vm as any).confirmDelete({ id: 'n3', title: 'Unfinished' })
    await flushPromises()
    expect(api.deleteNewsEntry).toHaveBeenCalledWith({ path: { id: 'n3' } })
  })
})
