import { mount, flushPromises } from '@vue/test-utils'
import { createVuetify } from 'vuetify'
import { createTestingPinia } from '@pinia/testing'
import { createRouter, createWebHistory } from 'vue-router'
import PresentationPreview from '@/pages/admin/presentation-preview.[[id]].vue'

const api = vi.hoisted(() => ({
  getAllPresentations: vi.fn(),
  getCospeakers: vi.fn(),
  getById: vi.fn(),
  accept: vi.fn(),
  reject: vi.fn(),
}))

vi.mock('@/utils/api.ts', () => api)

describe('admin presentation preview', () => {
  it("shows each speaker's bio and links from their profile", async () => {
    api.getAllPresentations.mockResolvedValue({ data: [{ id: 'p1', title: 'Talk', speakers: [{ name: 'Ada' }] }] })
    api.getCospeakers.mockResolvedValue({ data: [{ id: 'u1', name: 'Ada', email: 'ada@example.test' }] })
    api.getById.mockResolvedValue({ data: { id: 'u1', name: 'Ada', bio: 'Writes compilers', twitter: 'ada' } })
    const router = createRouter({
      history: createWebHistory(),
      routes: [{ path: '/admin/presentation-preview/:id?', component: PresentationPreview }],
    })
    await router.push('/admin/presentation-preview/p1')

    const wrapper = mount(PresentationPreview, {
      global: {
        plugins: [createVuetify(), createTestingPinia(), router],
        stubs: { Rating: true },
      },
    })
    await flushPromises()

    expect(api.getById).toHaveBeenCalledWith({ path: { id: 'u1' } })
    expect(wrapper.text()).toContain('Writes compilers')
    expect(wrapper.text()).toContain('@ada')
  })
})
