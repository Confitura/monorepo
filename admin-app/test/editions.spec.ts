import { describe, it, expect, vi, beforeEach, afterEach } from 'vitest'
import { mount, flushPromises } from '@vue/test-utils'
import { createVuetify } from 'vuetify'
import { createTestingPinia } from '@pinia/testing'
import EditionsPage from '@/pages/admin/editions.vue'
import { useNotificationStore } from '@/stores/notification'

global.ResizeObserver = class ResizeObserver {
  observe() {}
  unobserve() {}
  disconnect() {}
}

// The API returns Editions newest year first; the page keeps that order.
const editions = [
  {
    id: 'e2026',
    year: 2026,
    name: 'Confitura 2026',
    current: true,
    conferenceStart: '2026-09-25T07:00:00Z',
    lastDay: '2026-09-26',
    c4pStart: '2026-01-10T09:30:00Z',
    c4pEnd: '2026-12-31T22:30:00Z',
  },
  {
    id: 'e2025',
    year: 2025,
    name: 'Confitura 2025',
    current: false,
    conferenceStart: '2025-09-26T07:00:00Z',
    lastDay: '2025-09-27',
    c4pStart: '2025-03-01T09:00:00Z',
    c4pEnd: '2025-06-01T09:00:00Z',
  },
  {
    id: 'e2024',
    year: 2024,
    name: 'Confitura 2024',
    current: false,
    conferenceStart: '2024-09-20T07:00:00Z',
    lastDay: '2024-09-21',
  },
]

const api = vi.hoisted(() => ({
  getAllEditions: vi.fn(),
  deleteEdition: vi.fn(),
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
  return mount(EditionsPage, {
    global: { plugins: [createVuetify(), createTestingPinia({ createSpy: vi.fn })] },
  })
}

const rowsOf = (wrapper: ReturnType<typeof mountPage>) => wrapper.findAll('tbody tr').map((row) => row.text())

describe('admin editions page', () => {
  beforeEach(() => {
    vi.useFakeTimers({ toFake: ['Date'] })
    vi.setSystemTime(new Date('2026-10-22T12:00:00Z'))
    api.getAllEditions.mockResolvedValue({ data: editions, status: 200 })
    api.deleteEdition.mockResolvedValue({ status: 200 })
  })

  afterEach(() => {
    vi.useRealTimers()
    vi.clearAllMocks()
    document.body.innerHTML = ''
  })

  it('lists editions in the order the API returns them, newest first', async () => {
    const wrapper = mountPage()
    await flushPromises()
    const rows = rowsOf(wrapper)
    expect(rows).toHaveLength(3)
    expect(rows[0]).toContain('Confitura 2026')
    expect(rows[1]).toContain('Confitura 2025')
    expect(rows[2]).toContain('Confitura 2024')
  })

  it('marks the current edition', async () => {
    const wrapper = mountPage()
    await flushPromises()
    const rows = rowsOf(wrapper)
    expect(rows[0]).toContain('Current')
    expect(rows[1]).not.toContain('Current')
    expect(rows[2]).not.toContain('Current')
  })

  it('shows the conference and C4P dates in Europe/Warsaw time', async () => {
    const wrapper = mountPage()
    await flushPromises()
    const rows = rowsOf(wrapper)
    // 07:00 UTC in September is 09:00 in Warsaw (CEST); 09:30 UTC in January is 10:30 (CET)
    // (en-GB spells September "Sep" or "Sept" depending on the ICU version)
    expect(rows[0]).toMatch(/25 Sep\w* 2026, 09:00/)
    expect(rows[0]).toMatch(/26 Sep\w* 2026/)
    expect(rows[0]).toContain('10 Jan 2026, 10:30')
    // 22:30 UTC on 31 December is 23:30 in Warsaw
    expect(rows[0]).toContain('31 Dec 2026, 23:30')
  })

  it('shows whether C4P is open now, closed without a C4P period', async () => {
    const wrapper = mountPage()
    await flushPromises()
    const rows = rowsOf(wrapper)
    expect(rows[0]).toContain('C4P open')
    expect(rows[1]).toContain('C4P closed')
    expect(rows[2]).toContain('C4P closed')
  })

  it('treats C4P as closed before it starts and after it ends', async () => {
    vi.setSystemTime(new Date('2026-01-10T09:29:59Z'))
    const wrapper = mountPage()
    await flushPromises()
    expect(rowsOf(wrapper)[0]).toContain('C4P closed')
    vi.setSystemTime(new Date('2026-12-31T22:30:01Z'))
    wrapper.unmount()
    const later = mountPage()
    await flushPromises()
    expect(rowsOf(later)[0]).toContain('C4P closed')
  })

  it('deletes an edition after confirmation and reloads the list', async () => {
    const wrapper = mountPage()
    await flushPromises()
    ;(wrapper.vm as any).confirmDelete({ id: 'e2024', name: 'Confitura 2024' })
    await flushPromises()
    expect(api.deleteEdition).toHaveBeenCalledWith({ path: { id: 'e2024' }, throwOnError: true })
    expect(api.getAllEditions).toHaveBeenCalledTimes(2)
    const notifications = useNotificationStore().addNotification as ReturnType<typeof vi.fn>
    expect(notifications.mock.calls[0][1]).toBe('success')
  })

  it('shows why a refused delete was refused', async () => {
    vi.spyOn(console, 'error').mockImplementation(() => {})
    api.deleteEdition.mockRejectedValue({ response: { status: 409 } })
    const wrapper = mountPage()
    await flushPromises()
    ;(wrapper.vm as any).confirmDelete(editions[0])
    await flushPromises()
    const notifications = useNotificationStore().addNotification as ReturnType<typeof vi.fn>
    expect(notifications).toHaveBeenCalledTimes(1)
    expect(notifications.mock.calls[0][1]).toBe('error')
    expect(notifications.mock.calls[0][0]).toContain('current')
    expect(notifications.mock.calls[0][0]).toContain('talks, workshops or agenda days')
    expect(api.getAllEditions).toHaveBeenCalledTimes(1)
  })

  it('reports an edition that is already gone', async () => {
    vi.spyOn(console, 'error').mockImplementation(() => {})
    api.deleteEdition.mockRejectedValue({ response: { status: 404 } })
    const wrapper = mountPage()
    await flushPromises()
    ;(wrapper.vm as any).confirmDelete(editions[2])
    await flushPromises()
    const notifications = useNotificationStore().addNotification as ReturnType<typeof vi.fn>
    expect(notifications.mock.calls[0][0]).toContain('no longer exists')
    // the list is stale, so it is reloaded
    expect(api.getAllEditions).toHaveBeenCalledTimes(2)
  })
})
