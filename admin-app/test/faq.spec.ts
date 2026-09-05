import { describe, it, expect, vi, beforeEach, afterEach } from 'vitest'
import { mount, flushPromises } from '@vue/test-utils'
import { createVuetify } from 'vuetify'
import { createTestingPinia } from '@pinia/testing'
import FaqPage from '@/pages/admin/faq.vue'

// jsdom doesn't implement ResizeObserver, required by some Vuetify components
global.ResizeObserver = class ResizeObserver {
  observe() {}
  unobserve() {}
  disconnect() {}
}

const entries = [
  { id: 'e1', category: 'General', question: 'First', answer: 'A1', displayOrder: 0, published: true },
  { id: 'e2', category: 'General', question: 'Second', answer: 'A2', displayOrder: 1, published: true },
  { id: 'e3', category: 'Venue', question: 'Where?', answer: 'A3', displayOrder: 0, published: false },
]

const api = vi.hoisted(() => ({
  getAllFaqEntries: vi.fn(),
  createFaqEntry: vi.fn(),
  updateFaqEntry: vi.fn(),
  deleteFaqEntry: vi.fn(),
  reorderFaqEntries: vi.fn(),
}))

vi.mock('@/utils/api.ts', () => api)

// stub the drag wrapper to a passthrough that renders each item slot
vi.mock('vuedraggable', () => ({
  default: {
    name: 'draggable',
    props: ['modelValue'],
    template:
      '<div><template v-for="element in modelValue" :key="element.id"><slot name="item" :element="element" /></template></div>',
  },
}))

// stub the confirm dialog so open() resolves confirmed
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
  return mount(FaqPage, {
    global: {
      plugins: [createVuetify(), createTestingPinia({ createSpy: vi.fn })],
    },
  })
}

describe('admin FAQ page', () => {
  beforeEach(() => {
    api.getAllFaqEntries.mockResolvedValue({ data: entries, status: 200 })
    api.createFaqEntry.mockResolvedValue({ data: {}, status: 201 })
    api.updateFaqEntry.mockResolvedValue({ data: {}, status: 200 })
    api.deleteFaqEntry.mockResolvedValue({ status: 204 })
    api.reorderFaqEntries.mockResolvedValue({ status: 204 })
  })

  afterEach(() => vi.clearAllMocks())

  it('loads and groups entries by category', async () => {
    const wrapper = mountPage()
    await flushPromises()
    const groups = (wrapper.vm as any).grouped
    expect(groups.map((g: any) => g.category)).toEqual(['General', 'Venue'])
    expect(groups[0].items).toHaveLength(2)
    expect(wrapper.text()).toContain('First')
    expect(wrapper.text()).toContain('Where?')
  })

  it('creates a new entry', async () => {
    const wrapper = mountPage()
    await flushPromises()
    const vm = wrapper.vm as any
    vm.editing = false
    vm.form.category = 'Tickets'
    vm.form.question = 'How much?'
    vm.form.answer = 'Free'
    vm.save()
    await flushPromises()
    expect(api.createFaqEntry).toHaveBeenCalledTimes(1)
    expect(api.createFaqEntry.mock.calls[0][0].body).toMatchObject({
      category: 'Tickets',
      question: 'How much?',
    })
  })

  it('updates an existing entry', async () => {
    const wrapper = mountPage()
    await flushPromises()
    const vm = wrapper.vm as any
    vm.editing = true
    vm.form = { id: 'e1', category: 'General', question: 'Edited', answer: 'x', published: true }
    vm.save()
    await flushPromises()
    expect(api.updateFaqEntry).toHaveBeenCalledTimes(1)
    expect(api.updateFaqEntry.mock.calls[0][0].path).toEqual({ id: 'e1' })
  })

  it('deletes an entry after confirmation', async () => {
    const wrapper = mountPage()
    await flushPromises()
    const vm = wrapper.vm as any
    vm.confirmDelete({ id: 'e2', question: 'Second' })
    await flushPromises()
    expect(api.deleteFaqEntry).toHaveBeenCalledWith({ path: { id: 'e2' } })
  })

  it('persists the new order on reorder', async () => {
    const wrapper = mountPage()
    await flushPromises()
    const vm = wrapper.vm as any
    // simulate a drag: swap the two General entries
    vm.grouped[0].items.reverse()
    vm.persistOrder()
    await flushPromises()
    expect(api.reorderFaqEntries).toHaveBeenCalledTimes(1)
    expect(api.reorderFaqEntries.mock.calls[0][0].body.ids).toEqual(['e2', 'e1', 'e3'])
  })
})
