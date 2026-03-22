import { describe, it, expect, vi } from 'vitest'
import { mount } from '@vue/test-utils'

// jsdom doesn't implement ResizeObserver, required by Vuetify's VSlideGroup, VPagination, etc.
global.ResizeObserver = class ResizeObserver {
  observe() {}
  unobserve() {}
  disconnect() {}
}
import { createVuetify } from 'vuetify'
import { createTestingPinia } from '@pinia/testing'
import { createRouter, createWebHistory } from 'vue-router'

// ---------------------------------------------------------------------------
// Mock all API clients — pages call these in onMounted, we don't want real HTTP
// ---------------------------------------------------------------------------
vi.mock('@/utils/api.ts', () => ({
  api: {
    defaults: { headers: { common: {} } },
    post: vi.fn().mockResolvedValue({ data: {} }),
    get: vi.fn().mockResolvedValue({ data: {} }),
  },
  usersApi: {
    save1: vi.fn().mockResolvedValue({ data: {} }),
    getUser: vi.fn().mockResolvedValue({ data: {} }),
    getUserPresentations: vi.fn().mockResolvedValue({ data: [] }),
    getUserWorkshops: vi.fn().mockResolvedValue({ data: [] }),
    getCospeakers: vi.fn().mockResolvedValue({ data: [] }),
    addCospeaker: vi.fn().mockResolvedValue({ data: {} }),
    removeCospeaker: vi.fn().mockResolvedValue({ data: {} }),
  },
  pagesApi: {
    getPage: vi.fn().mockResolvedValue({ data: '' }),
    getPages: vi.fn().mockResolvedValue({ data: [] }),
    savePage: vi.fn().mockResolvedValue({ data: {} }),
    deletePage: vi.fn().mockResolvedValue({ data: {} }),
  },
  presentationApi: {
    getAllPresentations: vi.fn().mockResolvedValue({ data: [] }),
    addRating: vi.fn().mockResolvedValue({ data: {} }),
    getCospeakers: vi.fn().mockResolvedValue({ data: [] }),
    rates1: vi.fn().mockResolvedValue({ data: {} }),
  },
  adminPresentationApi: {
    accept: vi.fn().mockResolvedValue({ data: {} }),
    reject: vi.fn().mockResolvedValue({ data: {} }),
    rates: vi.fn().mockResolvedValue({ data: [] }),
  },
  adminUsersApi: {
    markAsAdmin: vi.fn().mockResolvedValue({ data: {} }),
    markAsVolunteer: vi.fn().mockResolvedValue({ data: {} }),
    getUsers: vi.fn().mockResolvedValue({ data: [] }),
    getAllUsers: vi.fn().mockResolvedValue({ data: [] }),
  },
  dashboardApi: {
    usersStats: vi.fn().mockResolvedValue({ data: { total: 0 } }),
    submissionStats: vi.fn().mockResolvedValue({ data: { total: 0, workshops: 0, presentations: 0 } }),
    newsletterStat: vi.fn().mockResolvedValue({ data: { subscribersCount: 0 } }),
  },
  tokenAPi: {
    refreshToken: vi.fn().mockResolvedValue({ data: '' }),
  },
  resourcesApi: {
    storeUserProfilePicture: vi.fn().mockResolvedValue({ data: {} }),
  },
  voteForPapersApi: {
    start: vi.fn().mockResolvedValue({ data: [] }),
  },
  adminTasksApi: {
    getLastWebpageDump: vi.fn().mockResolvedValue({ data: {} }),
    triggerWebpageDump: vi.fn().mockResolvedValue({ data: {} }),
  },
  daysApi: {
    getAllDays: vi.fn().mockResolvedValue({ data: [] }),
    getDayById: vi.fn().mockResolvedValue({ data: {} }),
  },
  agendaApi: {
    getAllTimeSlots: vi.fn().mockResolvedValue({ data: [] }),
    getAllRooms1: vi.fn().mockResolvedValue({ data: [] }),
    getAgendaEntriesByDay: vi.fn().mockResolvedValue({ data: [] }),
  },
  roomsApi: { getRooms: vi.fn().mockResolvedValue({ data: [] }) },
  publishedApi: { getPublished: vi.fn().mockResolvedValue({ data: {} }) },
}))

// login.vue and login.[provider].vue import the router singleton directly
vi.mock('@/plugins/router.ts', async () => {
  const { createRouter, createWebHistory } = await import('vue-router')
  const router = createRouter({
    history: createWebHistory(),
    routes: [{ path: '/:pathMatch(.*)*', component: { template: '<div/>' } }],
  })
  return { default: router }
})

// ---------------------------------------------------------------------------
// Page imports (after mocks are hoisted)
// ---------------------------------------------------------------------------
import LoginPage from '@/pages/login.vue'
import LoginProviderPage from '@/pages/login.[provider].vue'
import CatchAllPage from '@/pages/[...all].vue'
import PrivacyPolicyPage from '@/pages/privacy-policy.vue'
import DashboardPage from '@/pages/dashboard.vue'
import HomepagePage from '@/pages/homepage.[[id]].vue'
import ProfileFormPage from '@/pages/profile-form.[[id]].vue'
import PresentationFormPage from '@/pages/presentation-form.[[id]].[[userId]].vue'
import RatePage from '@/pages/rate.[[entryId]].vue'
import VoteForPapersPage from '@/pages/vote-for-papers.vue'
import AdminPage from '@/pages/admin.vue'
import AdminAgendaPage from '@/pages/admin/agenda.vue'
import AdminPagesPage from '@/pages/admin/pages.vue'
import AdminPresentationsPage from '@/pages/admin/presentations.vue'
import AdminPresentationPreviewPage from '@/pages/admin/presentation-preview.[[id]].vue'
import AdminRatesPage from '@/pages/admin/rates.vue'
import AdminUsersPage from '@/pages/admin/users.vue'

// ---------------------------------------------------------------------------
// Mount helper
// ---------------------------------------------------------------------------
async function mountPage(component: any, route = '/') {
  const vuetify = createVuetify()
  const pinia = createTestingPinia({ stubActions: false })
  const router = createRouter({
    history: createWebHistory(),
    routes: [{ path: '/:pathMatch(.*)*', component: { template: '<div/>' } }],
  })

  await router.push(route)
  await router.isReady()

  const wrapper = mount(component, {
    global: {
      plugins: [vuetify, pinia, router],
      stubs: {
        // Stub chart/editor components that rely on canvas/WebGL unavailable in jsdom
        ChartUserTypes: { template: '<div class="stub-chart" />' },
        ChartSubmissionTypes: { template: '<div class="stub-chart" />' },
        AgendaEditor: { template: '<div class="stub-agenda-editor" />' },
        RouterWrapper: { template: '<div class="stub-router-wrapper" />' },
        // Rating is auto-imported as HomepageRating by unplugin-vue-components
        HomepageRating: { template: '<div class="stub-rating" />' },
        Rating: { template: '<div class="stub-rating" />' },
      },
    },
  })
  return wrapper
}

// ---------------------------------------------------------------------------
// Tests
// ---------------------------------------------------------------------------
describe('admin-app pages render without errors', () => {
  it('renders the login page', async () => {
    const wrapper = await mountPage(LoginPage)
    expect(wrapper.html()).toContain('Call for Papers')
  })

  it('renders the OAuth callback page', async () => {
    const wrapper = await mountPage(LoginProviderPage)
    // Shows loading state while processing the OAuth token
    expect(wrapper.html()).toBeDefined()
  })

  it('renders the catch-all / 404 page', async () => {
    const wrapper = await mountPage(CatchAllPage, '/some-unknown-path')
    expect(wrapper.html()).toBeDefined()
  })

  it('renders the privacy policy page', async () => {
    const wrapper = await mountPage(PrivacyPolicyPage)
    expect(wrapper.html()).toBeDefined()
  })

  it('renders the dashboard page', async () => {
    const wrapper = await mountPage(DashboardPage)
    expect(wrapper.html()).toBeDefined()
  })

  it('renders the homepage page', async () => {
    const wrapper = await mountPage(HomepagePage)
    expect(wrapper.html()).toContain('Workshops')
    expect(wrapper.html()).toContain('Presentations')
  })

  it('renders the profile form page', async () => {
    const wrapper = await mountPage(ProfileFormPage)
    expect(wrapper.html()).toBeDefined()
  })

  it('renders the presentation form page', async () => {
    const wrapper = await mountPage(PresentationFormPage)
    expect(wrapper.html()).toBeDefined()
  })

  it('renders the rate page', async () => {
    const wrapper = await mountPage(RatePage)
    expect(wrapper.html()).toBeDefined()
  })

  it('renders the vote-for-papers page', async () => {
    const wrapper = await mountPage(VoteForPapersPage)
    expect(wrapper.html()).toBeDefined()
  })

  it('renders the admin layout wrapper', async () => {
    const wrapper = await mountPage(AdminPage)
    expect(wrapper.html()).toBeDefined()
  })

  it('renders the admin agenda page', async () => {
    const wrapper = await mountPage(AdminAgendaPage)
    expect(wrapper.html()).toBeDefined()
  })

  it('renders the admin pages management page', async () => {
    const wrapper = await mountPage(AdminPagesPage)
    expect(wrapper.html()).toBeDefined()
  })

  it('renders the admin presentations page', async () => {
    const wrapper = await mountPage(AdminPresentationsPage)
    expect(wrapper.html()).toBeDefined()
  })

  it('renders the admin presentation preview page', async () => {
    const wrapper = await mountPage(AdminPresentationPreviewPage)
    expect(wrapper.html()).toBeDefined()
  })

  it('renders the admin rates page', async () => {
    const wrapper = await mountPage(AdminRatesPage)
    expect(wrapper.html()).toBeDefined()
  })

  it('renders the admin users page', async () => {
    const wrapper = await mountPage(AdminUsersPage)
    expect(wrapper.html()).toBeDefined()
  })
})
