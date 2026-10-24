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
  createEdition: vi.fn(),
  updateEdition: vi.fn(),
  deleteEdition: vi.fn(),
  makeEditionCurrent: vi.fn(),
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
    api.createEdition.mockResolvedValue({ data: {}, status: 201 })
    api.updateEdition.mockResolvedValue({ data: {}, status: 200 })
    api.deleteEdition.mockResolvedValue({ status: 200 })
    api.makeEditionCurrent.mockResolvedValue({ data: {}, status: 200 })
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

  it('shows an error and keeps the list when a reload fails', async () => {
    vi.spyOn(console, 'error').mockImplementation(() => {})
    const wrapper = mountPage()
    await flushPromises()
    api.getAllEditions.mockRejectedValue({ response: { status: 500 } })
    ;(wrapper.vm as any).reload()
    await flushPromises()
    expect(api.getAllEditions).toHaveBeenLastCalledWith({ throwOnError: true })
    expect(rowsOf(wrapper)).toHaveLength(3)
    const notifications = useNotificationStore().addNotification as ReturnType<typeof vi.fn>
    expect(notifications.mock.calls[0][1]).toBe('error')
    expect(notifications.mock.calls[0][0]).toContain('Failed to load')
  })

  const fillNew2027 = (vm: any) => {
    vm.openCreate()
    vm.form.year = 2027
    vm.form.name = 'Confitura 2027'
    vm.form.conferenceStart = '2027-09-24T09:00'
    vm.form.lastDay = '2027-09-25'
  }
  const notificationsOf = () => useNotificationStore().addNotification as ReturnType<typeof vi.fn>

  it('creates an edition with dates converted from Warsaw time, leaving the C4P period out when empty', async () => {
    const wrapper = mountPage()
    await flushPromises()
    const vm = wrapper.vm as any
    fillNew2027(vm)
    vm.save()
    await flushPromises()
    // 09:00 in Warsaw on 24 September is CEST (UTC+2); the last day is a calendar date and is sent as typed
    expect(api.createEdition).toHaveBeenCalledWith({
      body: {
        year: 2027,
        name: 'Confitura 2027',
        conferenceStart: '2027-09-24T07:00:00.000Z',
        lastDay: '2027-09-25',
        c4pStart: undefined,
        c4pEnd: undefined,
      },
      throwOnError: true,
    })
    expect(notificationsOf().mock.calls[0][1]).toBe('success')
    expect(vm.dialog).toBe(false)
    expect(api.getAllEditions).toHaveBeenCalledTimes(2)
  })

  it('sends the C4P period in UTC when both dates are entered', async () => {
    const wrapper = mountPage()
    await flushPromises()
    const vm = wrapper.vm as any
    fillNew2027(vm)
    vm.form.c4pStart = '2027-01-10T10:30'
    vm.form.c4pEnd = '2027-03-01T23:30'
    vm.save()
    await flushPromises()
    const body = api.createEdition.mock.calls[0][0].body
    expect(body.c4pStart).toBe('2027-01-10T09:30:00.000Z')
    expect(body.c4pEnd).toBe('2027-03-01T22:30:00.000Z')
  })

  it('edits an edition with its dates shown in Warsaw time, repeating the stored year', async () => {
    const wrapper = mountPage()
    await flushPromises()
    const vm = wrapper.vm as any
    vm.openEdit(editions[0])
    expect(vm.form).toMatchObject({
      name: 'Confitura 2026',
      conferenceStart: '2026-09-25T09:00',
      lastDay: '2026-09-26',
      c4pStart: '2026-01-10T10:30',
      c4pEnd: '2026-12-31T23:30',
    })
    vm.form.name = 'Confitura 2026!'
    vm.save()
    await flushPromises()
    expect(api.updateEdition).toHaveBeenCalledWith({
      path: { id: 'e2026' },
      body: {
        year: 2026,
        name: 'Confitura 2026!',
        conferenceStart: '2026-09-25T07:00:00Z',
        lastDay: '2026-09-26',
        c4pStart: '2026-01-10T09:30:00Z',
        c4pEnd: '2026-12-31T22:30:00Z',
      },
      throwOnError: true,
    })
    expect(vm.dialog).toBe(false)
  })

  it('keeps the stored seconds of the dates the admin did not change', async () => {
    const wrapper = mountPage()
    await flushPromises()
    const vm = wrapper.vm as any
    // the form shows minutes only, so 23:59:59 reads as 01:59 and must not be rounded down on save
    vm.openEdit({
      ...editions[1],
      conferenceStart: '2025-09-26T07:00:30Z',
      c4pStart: '2025-03-01T09:00:15Z',
      c4pEnd: '2025-06-01T21:59:59Z',
    })
    vm.form.name = 'Confitura 2025!'
    vm.save()
    await flushPromises()
    const body = api.updateEdition.mock.calls[0][0].body
    expect(body.conferenceStart).toBe('2025-09-26T07:00:30Z')
    expect(body.c4pStart).toBe('2025-03-01T09:00:15Z')
    expect(body.c4pEnd).toBe('2025-06-01T21:59:59Z')
  })

  it('converts a date the admin changed from Warsaw time, keeping the others as stored', async () => {
    const wrapper = mountPage()
    await flushPromises()
    const vm = wrapper.vm as any
    vm.openEdit({ ...editions[1], c4pEnd: '2025-06-01T21:59:59Z' })
    vm.form.c4pStart = '2025-03-02T10:00'
    vm.save()
    await flushPromises()
    const body = api.updateEdition.mock.calls[0][0].body
    expect(body.c4pStart).toBe('2025-03-02T09:00:00.000Z')
    expect(body.c4pEnd).toBe('2025-06-01T21:59:59Z')
    expect(body.conferenceStart).toBe('2025-09-26T07:00:00Z')
  })

  it('asks for the year only when creating', async () => {
    const wrapper = mountPage()
    await flushPromises()
    const vm = wrapper.vm as any
    vm.openCreate()
    await flushPromises()
    expect(document.body.textContent).toContain('Add Edition')
    expect(document.body.textContent).toContain('Year')
    vm.dialog = false
    vm.openEdit(editions[1])
    await flushPromises()
    expect(document.body.textContent).toContain('Edit Edition')
    expect(document.body.textContent).not.toContain('Year')
  })

  it('closes the C4P of an edition when both its dates are cleared', async () => {
    const wrapper = mountPage()
    await flushPromises()
    const vm = wrapper.vm as any
    vm.openEdit(editions[1])
    vm.form.c4pStart = ''
    vm.form.c4pEnd = ''
    vm.save()
    await flushPromises()
    const body = api.updateEdition.mock.calls[0][0].body
    expect(body.c4pStart).toBeUndefined()
    expect(body.c4pEnd).toBeUndefined()
  })

  it.each([
    ['only one C4P date is entered', (f: any) => (f.c4pEnd = '')],
    ['the C4P ends when it starts', (f: any) => (f.c4pEnd = f.c4pStart)],
    ['the last day is before the conference starts', (f: any) => (f.lastDay = '2026-09-24')],
    ['the name is empty', (f: any) => (f.name = ' ')],
  ])('does not save when %s', async (_, change) => {
    const wrapper = mountPage()
    await flushPromises()
    const vm = wrapper.vm as any
    vm.openEdit(editions[0])
    change(vm.form)
    vm.save()
    await flushPromises()
    expect(api.updateEdition).not.toHaveBeenCalled()
  })

  it('does not create without a year', async () => {
    const wrapper = mountPage()
    await flushPromises()
    const vm = wrapper.vm as any
    fillNew2027(vm)
    vm.form.year = undefined
    vm.save()
    await flushPromises()
    expect(api.createEdition).not.toHaveBeenCalled()
  })

  it('compares the last day with the conference start in Warsaw, not UTC', async () => {
    const wrapper = mountPage()
    await flushPromises()
    const vm = wrapper.vm as any
    fillNew2027(vm)
    // 00:30 on 25 September in Warsaw is still 24 September in UTC; the last day may be the 25th
    vm.form.conferenceStart = '2027-09-25T00:30'
    vm.save()
    await flushPromises()
    expect(api.createEdition).toHaveBeenCalledTimes(1)
  })

  it.each([
    [409, 'already exists'],
    [400, 'dates'],
    [500, 'Failed to save'],
  ])('keeps the dialog open with the edits and explains a refusal with status %s', async (status, reason) => {
    vi.spyOn(console, 'error').mockImplementation(() => {})
    api.createEdition.mockRejectedValue({ response: { status } })
    const wrapper = mountPage()
    await flushPromises()
    const vm = wrapper.vm as any
    fillNew2027(vm)
    vm.save()
    await flushPromises()
    expect(notificationsOf()).toHaveBeenCalledTimes(1)
    expect(notificationsOf().mock.calls[0][1]).toBe('error')
    expect(notificationsOf().mock.calls[0][0]).toContain(reason)
    expect(vm.dialog).toBe(true)
    expect(vm.form.name).toBe('Confitura 2027')
    // the failed save is over, so the admin can try again
    vm.save()
    await flushPromises()
    expect(api.createEdition).toHaveBeenCalledTimes(2)
  })

  it('sends only one request when Save is triggered again while a save is pending, and cannot be cancelled', async () => {
    let finish: (value: unknown) => void = () => {}
    api.createEdition.mockReturnValue(new Promise((resolve) => (finish = resolve)))
    const wrapper = mountPage()
    await flushPromises()
    const vm = wrapper.vm as any
    fillNew2027(vm)
    vm.save()
    vm.save()
    await flushPromises()
    expect(api.createEdition).toHaveBeenCalledTimes(1)
    const cancel = () => [...document.body.querySelectorAll('button')].find((b) => b.textContent?.trim() === 'Cancel')!
    expect(cancel().disabled).toBe(true)
    finish({ data: {}, status: 201 })
    await flushPromises()
    expect(vm.dialog).toBe(false)
  })

  it('offers to make current only the editions that are not current', async () => {
    const wrapper = mountPage()
    await flushPromises()
    const rows = rowsOf(wrapper)
    expect(rows[0]).not.toContain('Make current')
    expect(rows[1]).toContain('Make current')
    expect(rows[2]).toContain('Make current')
  })

  it('lists the five effects of making an edition current before anything happens', async () => {
    const wrapper = mountPage()
    await flushPromises()
    await wrapper.findAll('tbody tr')[1].findAll('button').find((b) => b.text() === 'Make current')!.trigger('click')
    await flushPromises()
    const effects = [...document.body.querySelectorAll('ol li')].map((li) => li.textContent)
    expect(effects).toHaveLength(5)
    expect(effects[0]).toContain('edition-2025')
    expect(effects[1]).toContain('Confitura 2026')
    expect(effects[1]).toContain('Archive')
    expect(effects[2]).toContain('IP addresses')
    expect(effects[2]).toContain('cannot be undone')
    expect(effects[3]).toContain('admins and volunteers')
    expect(effects[4]).toContain('NEXT-EDITION.md')
    expect(api.makeEditionCurrent).not.toHaveBeenCalled()
  })

  it('makes an edition current on confirmation and reloads the list', async () => {
    const wrapper = mountPage()
    await flushPromises()
    const vm = wrapper.vm as any
    vm.openMakeCurrent(editions[1])
    vm.makeCurrent()
    await flushPromises()
    expect(api.makeEditionCurrent).toHaveBeenCalledWith({ path: { id: 'e2025' }, throwOnError: true })
    expect(notificationsOf().mock.calls[0][1]).toBe('success')
    expect(vm.switchDialog).toBe(false)
    expect(api.getAllEditions).toHaveBeenCalledTimes(2)
  })

  it('sends only one request when confirmed again while a switch is pending, and cannot be cancelled', async () => {
    let finish: (value: unknown) => void = () => {}
    api.makeEditionCurrent.mockReturnValue(new Promise((resolve) => (finish = resolve)))
    const wrapper = mountPage()
    await flushPromises()
    const vm = wrapper.vm as any
    vm.openMakeCurrent(editions[1])
    vm.makeCurrent()
    vm.makeCurrent()
    await flushPromises()
    expect(api.makeEditionCurrent).toHaveBeenCalledTimes(1)
    const cancel = () => [...document.body.querySelectorAll('button')].find((b) => b.textContent?.trim() === 'Cancel')!
    expect(cancel().disabled).toBe(true)
    finish({ data: {}, status: 200 })
    await flushPromises()
    expect(vm.switchDialog).toBe(false)
  })

  it('reports a failed switch, keeps the dialog open and reloads the list', async () => {
    vi.spyOn(console, 'error').mockImplementation(() => {})
    api.makeEditionCurrent.mockRejectedValue({ response: { status: 500 } })
    const wrapper = mountPage()
    await flushPromises()
    const vm = wrapper.vm as any
    vm.openMakeCurrent(editions[1])
    vm.makeCurrent()
    await flushPromises()
    expect(notificationsOf()).toHaveBeenCalledTimes(1)
    expect(notificationsOf().mock.calls[0][1]).toBe('error')
    expect(vm.switchDialog).toBe(true)
    // whatever happened on the server, the list shows it
    expect(api.getAllEditions).toHaveBeenCalledTimes(2)
  })
})
