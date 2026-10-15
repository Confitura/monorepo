import { describe, it, expect, vi, beforeEach, afterEach } from 'vitest'
import { mount, flushPromises } from '@vue/test-utils'
import { createVuetify } from 'vuetify'
import { createTestingPinia } from '@pinia/testing'
import PartnersPage from '@/pages/admin/partners.vue'

global.ResizeObserver = class ResizeObserver {
  observe() {}
  unobserve() {}
  disconnect() {}
}

const partners = [
  { id: 'p1', name: 'XTB', type: 'gold', www: 'https://xtb.com', description: 'x', orientation: 'horizontal', published: true },
  { id: 'p2', name: 'Draft', type: 'bronze', www: '', description: '', orientation: 'box', published: false },
]

const api = vi.hoisted(() => ({
  getAllPartners: vi.fn(),
  createPartner: vi.fn(),
  updatePartner: vi.fn(),
  deletePartner: vi.fn(),
  storePartnerLogo: vi.fn(),
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
  return mount(PartnersPage, {
    global: { plugins: [createVuetify(), createTestingPinia({ createSpy: vi.fn })] },
  })
}

describe('admin partners page', () => {
  beforeEach(() => {
    api.getAllPartners.mockResolvedValue({ data: partners, status: 200 })
    api.createPartner.mockResolvedValue({ data: { id: 'p-new' }, status: 201 })
    api.updatePartner.mockResolvedValue({ data: { id: 'p1' }, status: 200 })
    api.deletePartner.mockResolvedValue({ status: 204 })
    api.storePartnerLogo.mockResolvedValue({ data: {}, status: 200 })
  })

  afterEach(() => vi.clearAllMocks())

  it('loads partners', async () => {
    const wrapper = mountPage()
    await flushPromises()
    expect((wrapper.vm as any).partners).toHaveLength(2)
    expect(wrapper.text()).toContain('XTB')
  })

  it('creates a partner and uploads the logo', async () => {
    const wrapper = mountPage()
    await flushPromises()
    const vm = wrapper.vm as any
    vm.openCreate()
    vm.form.name = 'DPD'
    vm.form.type = 'bronze'
    vm.logoFile = new File(['x'], 'dpd.png', { type: 'image/png' })
    await vm.save()
    await flushPromises()
    expect(api.createPartner).toHaveBeenCalledTimes(1)
    expect(api.createPartner.mock.calls[0][0].body).toMatchObject({ name: 'DPD', type: 'bronze' })
    // logo uploaded to the id returned by create
    expect(api.storePartnerLogo).toHaveBeenCalledWith({ path: { id: 'p-new' }, body: { file: expect.any(File) } })
  })

  it('creates a partner without a logo (no upload)', async () => {
    const wrapper = mountPage()
    await flushPromises()
    const vm = wrapper.vm as any
    vm.openCreate()
    vm.form.name = 'NoLogo'
    await vm.save()
    await flushPromises()
    expect(api.createPartner).toHaveBeenCalledTimes(1)
    expect(api.storePartnerLogo).not.toHaveBeenCalled()
  })

  it('updates a partner', async () => {
    const wrapper = mountPage()
    await flushPromises()
    const vm = wrapper.vm as any
    vm.editing = true
    vm.form = { id: 'p1', name: 'XTB Group', type: 'gold', published: true }
    await vm.save()
    await flushPromises()
    expect(api.updatePartner).toHaveBeenCalledTimes(1)
    expect(api.updatePartner.mock.calls[0][0].path).toEqual({ id: 'p1' })
  })

  it('deletes a partner after confirmation', async () => {
    const wrapper = mountPage()
    await flushPromises()
    const vm = wrapper.vm as any
    vm.confirmDelete({ id: 'p2', name: 'Draft' })
    await flushPromises()
    expect(api.deletePartner).toHaveBeenCalledWith({ path: { id: 'p2' } })
  })
})
