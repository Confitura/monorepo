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

const categories = [
  { id: 'c1', name: 'General', displayOrder: 0, published: true },
  { id: 'c2', name: 'Venue', displayOrder: 1, published: false },
]

const api = vi.hoisted(() => ({
  getAllFaqEntries: vi.fn(),
  createFaqEntry: vi.fn(),
  updateFaqEntry: vi.fn(),
  deleteFaqEntry: vi.fn(),
  reorderFaqEntries: vi.fn(),
  getFaqCategories: vi.fn(),
  createFaqCategory: vi.fn(),
  updateFaqCategory: vi.fn(),
  reorderFaqCategories: vi.fn(),
  mergeFaqCategories: vi.fn(),
  deleteFaqCategory: vi.fn(),
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
    api.getFaqCategories.mockResolvedValue({ data: categories, status: 200 })
    api.createFaqEntry.mockResolvedValue({ data: {}, status: 201 })
    api.updateFaqEntry.mockResolvedValue({ data: {}, status: 200 })
    api.deleteFaqEntry.mockResolvedValue({ status: 204 })
    api.reorderFaqEntries.mockResolvedValue({ status: 204 })
    api.createFaqCategory.mockResolvedValue({ data: {}, status: 201 })
    api.updateFaqCategory.mockResolvedValue({ data: {}, status: 200 })
    api.reorderFaqCategories.mockResolvedValue({ status: 204 })
    api.mergeFaqCategories.mockResolvedValue({ status: 204 })
    api.deleteFaqCategory.mockResolvedValue({ status: 204 })
  })

  afterEach(() => vi.clearAllMocks())

  it('builds groups from the category dictionary, attaching entries', async () => {
    const wrapper = mountPage()
    await flushPromises()
    const groups = (wrapper.vm as any).groups
    expect(groups.map((g: any) => g.category.name)).toEqual(['General', 'Venue'])
    expect(groups[0].items).toHaveLength(2)
    expect(wrapper.text()).toContain('First')
    expect(wrapper.text()).toContain('Where?')
  })

  it('creates a new entry, sending the category as a name', async () => {
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

  it('renames a category by id', async () => {
    const wrapper = mountPage()
    await flushPromises()
    const vm = wrapper.vm as any
    vm.openRename(categories[0])
    vm.renameTo = 'Basics'
    vm.renameConfirm()
    await flushPromises()
    expect(api.updateFaqCategory).toHaveBeenCalledWith({ path: { id: 'c1' }, body: { name: 'Basics' } })
  })

  it('toggles category visibility', async () => {
    const wrapper = mountPage()
    await flushPromises()
    const vm = wrapper.vm as any
    vm.toggleCategoryPublished(categories[0]) // currently published → hide
    await flushPromises()
    expect(api.updateFaqCategory).toHaveBeenCalledWith({ path: { id: 'c1' }, body: { published: false } })
  })

  it('reorders categories with the move buttons', async () => {
    const wrapper = mountPage()
    await flushPromises()
    const vm = wrapper.vm as any
    vm.moveCategory(0, 1) // General down, Venue up
    await flushPromises()
    expect(api.reorderFaqCategories).toHaveBeenCalledTimes(1)
    expect(api.reorderFaqCategories.mock.calls[0][0].body.ids).toEqual(['c2', 'c1'])
  })

  it('merges one category into another', async () => {
    const wrapper = mountPage()
    await flushPromises()
    const vm = wrapper.vm as any
    vm.openMerge(categories[0])
    vm.mergeTo = 'c2'
    vm.mergeConfirm()
    await flushPromises()
    expect(api.mergeFaqCategories).toHaveBeenCalledWith({ body: { from: 'c1', to: 'c2' } })
  })

  it('deletes an empty category, refuses a non-empty one', async () => {
    const wrapper = mountPage()
    await flushPromises()
    const vm = wrapper.vm as any
    // Venue (c2) has one entry → refused (no API call)
    vm.confirmDeleteCategory(vm.groups[1])
    await flushPromises()
    expect(api.deleteFaqCategory).not.toHaveBeenCalled()

    // simulate an empty category
    vm.groups[1].items = []
    vm.confirmDeleteCategory(vm.groups[1])
    await flushPromises()
    expect(api.deleteFaqCategory).toHaveBeenCalledWith({ path: { id: 'c2' } })
  })

  it('persists entry order within one category', async () => {
    const wrapper = mountPage()
    await flushPromises()
    const vm = wrapper.vm as any
    // simulate a drag within General: swap the two entries
    vm.groups[0].items.reverse()
    vm.persistEntryOrder(vm.groups[0])
    await flushPromises()
    expect(api.reorderFaqEntries).toHaveBeenCalledTimes(1)
    expect(api.reorderFaqEntries.mock.calls[0][0].body.ids).toEqual(['e2', 'e1'])
  })
})
