import { mount, flushPromises } from '@vue/test-utils'
import { createVuetify } from 'vuetify'
import { createTestingPinia } from '@pinia/testing'
import { VBtn, VSelect } from 'vuetify/components'
import HomepageId from '@/pages/homepage.[[id]].vue'
import { renderWithVuetify } from '@/../test/helpers'
import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(),
  routes: [{ path: '/:pathMatch(.*)*', component: HomepageId }],
})

const api = vi.hoisted(() => ({
  getUserEditions: vi.fn(),
  getUserPresentations: vi.fn(),
  getUserWorkshops: vi.fn(),
  getById: vi.fn(),
}))

vi.mock('@/utils/api.ts', () => api)

vi.mock('@/components/homepage/rating.vue', () => ({
  default: { props: ['presentation'], template: '<div>ratings of {{ presentation.title }}</div>' },
}))

const current = { id: 'e2026', year: 2026, name: 'Confitura 2026', current: true }
const past = { id: 'e2025', year: 2025, name: 'Confitura 2025', current: false }

const talk = (title: string) => ({ id: title, title, shortDescription: '', description: '', tags: [] })

describe('homepage', () => {
  it('renders workshops and presentations sections', async () => {
    const { getByText } = renderWithVuetify(HomepageId, {
      global: {
        plugins: [router],
      },
    })

    getByText('Workshops')
    getByText('Presentations')
  })
})

describe('homepage Editions', () => {
  beforeEach(() => {
    api.getById.mockResolvedValue({ data: { id: 'u1', name: 'Speaker' } })
    api.getUserPresentations.mockImplementation(({ query }) =>
      Promise.resolve({ data: [talk(query?.editionId === 'e2025' ? "Last year's talk" : "This year's talk")] }),
    )
    api.getUserWorkshops.mockImplementation(({ query }) =>
      Promise.resolve({ data: [talk(query?.editionId === 'e2025' ? "Last year's workshop" : "This year's workshop")] }),
    )
  })

  afterEach(() => vi.clearAllMocks())

  async function mountPage(editions: object[], path = '/homepage', user: object = { jti: 'u1' }) {
    api.getUserEditions.mockResolvedValue({ data: editions })
    const testRouter = createRouter({
      history: createWebHistory(),
      routes: [{ path: '/homepage/:id?', component: HomepageId }],
    })
    await testRouter.push(path)
    const wrapper = mount(HomepageId, {
      global: {
        plugins: [createVuetify(), createTestingPinia({ initialState: { auth: { user } } }), testRouter],
      },
    })
    await flushPromises()
    return wrapper
  }

  const controls = (wrapper: Awaited<ReturnType<typeof mountPage>>) =>
    wrapper.findAllComponents(VBtn).map((button) => button.props('icon') || button.text())

  it('lists the Current Edition with no picker when the speaker has no talks in other Editions', async () => {
    const wrapper = await mountPage([current])

    expect(wrapper.text()).toContain("This year's talk")
    expect(wrapper.text()).toContain("This year's workshop")
    expect(wrapper.findComponent(VSelect).exists()).toBe(false)
    expect(controls(wrapper)).toEqual(expect.arrayContaining(['submit new presentation', 'mdi-delete', 'mdi-pencil-outline', 'mdi-account-multiple']))
  })

  it("shows a picked past Edition's talks with their ratings but without changing controls", async () => {
    const wrapper = await mountPage([current, past])

    const picker = wrapper.findComponent(VSelect)
    expect(picker.props('items')).toEqual([current, past])
    await picker.setValue('e2025')
    await flushPromises()

    expect(api.getUserPresentations).toHaveBeenLastCalledWith({ path: { id: 'u1' }, query: { editionId: 'e2025' } })
    expect(wrapper.text()).toContain("Last year's talk")
    expect(wrapper.text()).toContain("ratings of Last year's talk")
    expect(wrapper.text()).toContain("Last year's workshop")
    expect(wrapper.text()).not.toContain("This year's")
    expect(controls(wrapper)).toEqual(['edit']) // the profile's, which stays
  })

  it("gives an admin viewing a user's homepage the same picker", async () => {
    const wrapper = await mountPage([current, past], '/homepage/u2', { jti: 'admin', isAdmin: true })

    expect(api.getUserEditions).toHaveBeenCalledWith({ path: { id: 'u2' } })
    expect(wrapper.findComponent(VSelect).exists()).toBe(true)
  })
})
