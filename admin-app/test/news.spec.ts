import { describe, it, expect, vi, beforeEach, afterEach } from 'vitest'
import { mount, flushPromises } from '@vue/test-utils'
import { createVuetify } from 'vuetify'
import { createTestingPinia } from '@pinia/testing'
import NewsPage from '@/pages/admin/news.vue'
import { useNotificationStore } from '@/stores/notification'

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
  createNewsEntry: vi.fn(),
  updateNewsEntry: vi.fn(),
  deleteNewsEntry: vi.fn(),
  storeNewsEntryCover: vi.fn(),
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
    api.createNewsEntry.mockResolvedValue({ data: {}, status: 200 })
    api.updateNewsEntry.mockResolvedValue({ data: {}, status: 200 })
    api.deleteNewsEntry.mockResolvedValue({ status: 204 })
    api.storeNewsEntryCover.mockResolvedValue({ status: 200 })
  })

  afterEach(() => {
    vi.useRealTimers()
    vi.clearAllMocks()
    document.body.innerHTML = ''
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

  it('opens the create dialog with the date defaulting to now in Warsaw time and no slug', async () => {
    const wrapper = mountPage()
    await flushPromises()
    const vm = wrapper.vm as any
    vm.openCreate()
    // 12:00 UTC on 22 October is 14:00 in Warsaw (CEST)
    expect(vm.dateInput).toBe('2026-10-22T14:00')
    expect(vm.form.slug).toBe('')
    expect(vm.form.published).toBe(false)
  })

  it('creates an entry, leaving the slug out so the server generates it', async () => {
    const wrapper = mountPage()
    await flushPromises()
    const vm = wrapper.vm as any
    vm.openCreate()
    vm.form.title = 'Big news'
    vm.form.body = '**Hello**'
    vm.form.published = true
    vm.dateInput = '2026-11-02T08:15'
    vm.save()
    await flushPromises()
    // 08:15 in Warsaw on 2 November is CET (UTC+1)
    expect(api.createNewsEntry).toHaveBeenCalledWith({
      body: { title: 'Big news', slug: undefined, body: '**Hello**', date: '2026-11-02T07:15:00.000Z', published: true },
    })
    expect(api.storeNewsEntryCover).not.toHaveBeenCalled()
  })

  it('uploads a chosen cover to the entry the create returns', async () => {
    api.createNewsEntry.mockResolvedValue({ data: { id: 'n-new', slug: 'big-news' }, status: 201 })
    const wrapper = mountPage()
    await flushPromises()
    const vm = wrapper.vm as any
    vm.openCreate()
    vm.form.title = 'Big news'
    const cover = new File(['x'], 'cover.png', { type: 'image/png' })
    vm.coverFile = cover
    vm.save()
    await flushPromises()
    expect(api.storeNewsEntryCover).toHaveBeenCalledWith({ path: { id: 'n-new' }, body: { file: cover } })
    expect(document.body.textContent).not.toContain('Add news entry')
  })

  it('replaces the cover of an entry being edited, keeping it until the upload', async () => {
    api.updateNewsEntry.mockResolvedValue({ data: { ...entries[1], cover: 'https://res/n2.png' }, status: 200 })
    const wrapper = mountPage()
    await flushPromises()
    const vm = wrapper.vm as any
    vm.openEdit({ ...entries[1], cover: 'https://res/n2.png' })
    const cover = new File(['x'], 'new.jpg', { type: 'image/jpeg' })
    vm.coverFile = cover
    vm.save()
    await flushPromises()
    expect(api.updateNewsEntry.mock.calls[0][0].body.removeCover).toBeUndefined()
    expect(api.storeNewsEntryCover).toHaveBeenCalledWith({ path: { id: 'n2' }, body: { file: cover } })
  })

  it('shows the cover of an entry being edited and removes it on save', async () => {
    api.updateNewsEntry.mockResolvedValue({ data: entries[1], status: 200 })
    const wrapper = mountPage()
    await flushPromises()
    const vm = wrapper.vm as any
    vm.openEdit({ ...entries[1], cover: 'https://res/n2.png' })
    await flushPromises()
    expect(document.body.querySelector('img[src="https://res/n2.png"]')).not.toBeNull()
    const remove = [...document.body.querySelectorAll('button')].find((b) => b.textContent?.trim() === 'Remove cover')!
    remove.click()
    await flushPromises()
    expect(document.body.querySelector('img[src="https://res/n2.png"]')).toBeNull()
    vm.save()
    await flushPromises()
    expect(api.updateNewsEntry.mock.calls[0][0].body.removeCover).toBe(true)
    expect(api.storeNewsEntryCover).not.toHaveBeenCalled()
  })

  it('keeps an entry whose cover upload failed, so saving again updates it rather than creating another', async () => {
    vi.spyOn(console, 'error').mockImplementation(() => {})
    api.createNewsEntry.mockResolvedValue({ data: { id: 'n-new', slug: 'big-news' }, status: 201 })
    api.updateNewsEntry.mockResolvedValue({ data: { id: 'n-new', slug: 'big-news' }, status: 200 })
    api.storeNewsEntryCover.mockRejectedValueOnce({ status: 413 })
    const wrapper = mountPage()
    await flushPromises()
    const vm = wrapper.vm as any
    vm.openCreate()
    vm.form.title = 'Big news'
    vm.coverFile = new File(['x'], 'huge.png', { type: 'image/png' })
    vm.save()
    await flushPromises()
    const notifications = useNotificationStore().addNotification as ReturnType<typeof vi.fn>
    expect(notifications).toHaveBeenCalledWith('News entry saved, but the cover file is too large', 'error')
    expect(document.body.textContent).toContain('Edit news entry')
    vm.save()
    await flushPromises()
    expect(api.createNewsEntry).toHaveBeenCalledTimes(1)
    expect(api.updateNewsEntry.mock.calls[0][0].path).toEqual({ id: 'n-new' })
    expect(api.storeNewsEntryCover).toHaveBeenCalledTimes(2)
  })

  it('sends a chosen slug when creating', async () => {
    const wrapper = mountPage()
    await flushPromises()
    const vm = wrapper.vm as any
    vm.openCreate()
    vm.form.title = 'Big news'
    vm.form.slug = 'my-slug'
    vm.save()
    await flushPromises()
    expect(api.createNewsEntry.mock.calls[0][0].body.slug).toBe('my-slug')
  })

  it('does not save without a title', async () => {
    const wrapper = mountPage()
    await flushPromises()
    const vm = wrapper.vm as any
    vm.openCreate()
    vm.save()
    await flushPromises()
    expect(api.createNewsEntry).not.toHaveBeenCalled()
  })

  it('edits an entry, showing its date in Warsaw time and sending it back as an instant', async () => {
    const wrapper = mountPage()
    await flushPromises()
    const vm = wrapper.vm as any
    vm.openEdit({ ...entries[1], body: 'Text' })
    expect(vm.dateInput).toBe('2026-06-15T12:30')
    vm.form.title = 'Hello again'
    vm.save()
    await flushPromises()
    expect(api.updateNewsEntry).toHaveBeenCalledWith({
      path: { id: 'n2' },
      body: { title: 'Hello again', slug: 'hello-world', body: 'Text', date: '2026-06-15T10:30:00.000Z', published: true },
    })
  })

  it('warns that shared links will break when an existing entry slug is changed', async () => {
    const wrapper = mountPage()
    await flushPromises()
    const vm = wrapper.vm as any
    vm.openEdit(entries[1])
    await flushPromises()
    expect(document.body.textContent).not.toContain('shared links will stop working')
    vm.form.slug = 'new-slug'
    await flushPromises()
    expect(document.body.textContent).toContain('shared links will stop working')
    vm.form.slug = 'hello-world'
    await flushPromises()
    expect(document.body.textContent).not.toContain('shared links will stop working')
  })

  it('does not warn about the slug when creating', async () => {
    const wrapper = mountPage()
    await flushPromises()
    const vm = wrapper.vm as any
    vm.openCreate()
    vm.form.slug = 'fresh'
    await flushPromises()
    expect(document.body.textContent).not.toContain('shared links will stop working')
  })

  it('keeps the dialog open with the edits and reports an error when saving fails', async () => {
    vi.spyOn(console, 'error').mockImplementation(() => {})
    api.updateNewsEntry.mockRejectedValue(new Error('409'))
    const wrapper = mountPage()
    await flushPromises()
    const vm = wrapper.vm as any
    vm.openEdit(entries[1])
    vm.form.title = 'Edited title'
    vm.save()
    await flushPromises()
    const notifications = useNotificationStore().addNotification as ReturnType<typeof vi.fn>
    expect(notifications).toHaveBeenCalledTimes(1)
    expect(notifications.mock.calls[0][1]).toBe('error')
    expect(document.body.textContent).toContain('Edit news entry')
    expect(vm.form.title).toBe('Edited title')
    // the failed save is over, so the admin can try again
    vm.save()
    await flushPromises()
    expect(api.updateNewsEntry).toHaveBeenCalledTimes(2)
  })

  it('sends only one request when Save is triggered again while a save is pending', async () => {
    let finish: (value: unknown) => void = () => {}
    api.createNewsEntry.mockReturnValue(new Promise((resolve) => (finish = resolve)))
    const wrapper = mountPage()
    await flushPromises()
    const vm = wrapper.vm as any
    vm.openCreate()
    vm.form.title = 'Big news'
    vm.save()
    vm.save()
    await flushPromises()
    expect(api.createNewsEntry).toHaveBeenCalledTimes(1)
    finish({ data: {}, status: 200 })
    await flushPromises()
  })

  it('cannot be cancelled while a save is pending, so a slow save cannot close another editor', async () => {
    let finish: (value: unknown) => void = () => {}
    api.createNewsEntry.mockReturnValue(new Promise((resolve) => (finish = resolve)))
    const wrapper = mountPage()
    await flushPromises()
    const vm = wrapper.vm as any
    vm.openCreate()
    vm.form.title = 'Big news'
    vm.save()
    await flushPromises()
    const cancel = () =>
      [...document.body.querySelectorAll('button')].find((b) => b.textContent?.trim() === 'Cancel')!
    expect(cancel().disabled).toBe(true)
    finish({ data: {}, status: 200 })
    await flushPromises()
    expect(cancel().disabled).toBe(false)
  })
})
