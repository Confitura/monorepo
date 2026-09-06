import { describe, it, expect, vi, beforeEach, afterEach } from 'vitest'
import { mount, flushPromises } from '@vue/test-utils'
import { createVuetify } from 'vuetify'
import { createTestingPinia } from '@pinia/testing'
import { createRouter, createWebHistory } from 'vue-router'

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
import AdminFaqPage from '@/pages/admin/faq.vue'
import AdminPartnersPage from '@/pages/admin/partners.vue'
import AdminPresentationsPage from '@/pages/admin/presentations.vue'
import AdminPresentationPreviewPage from '@/pages/admin/presentation-preview.[[id]].vue'
import AdminRatesPage from '@/pages/admin/rates.vue'
import AdminUsersPage from '@/pages/admin/users.vue'

// jsdom doesn't implement ResizeObserver, required by Vuetify's VSlideGroup, VPagination, etc.
global.ResizeObserver = class ResizeObserver {
  observe() {}
  unobserve() {}
  disconnect() {}
}

// ---------------------------------------------------------------------------
// Mock all API clients — pages call these in onMounted, we don't want real HTTP
// ---------------------------------------------------------------------------
// The generated hey-api SDK exposes one flat function per operation, each resolving to a
// hey-api result that spreads the axios response ({ data, status, ... }). Mock them here.
vi.mock('@/utils/api.ts', () => {
  const ok = (data: unknown) => vi.fn().mockResolvedValue({ data, status: 200 })
  return {
    // presentations & workshops
    getAllPresentations: ok([]),
    getAllTags: ok([]),
    getCospeakers: ok([]),
    getPresentation: ok({}),
    updatePresentation: ok({}),
    addPresentationToUser: ok({}),
    deletePresentation: ok({}),
    getWorkshop: ok({}),
    updateWorkshop: ok({}),
    addWorkshopToUser: ok({}),
    deleteWorkshop: ok({}),
    addCospeaker: ok({}),
    removeCospeaker: ok({}),
    addRating: ok({}),
    rates1: ok({}),
    accept: ok({}),
    reject: ok({}),
    rates: ok([]),
    setPreSelection: ok({}),
    results: ok([]),
    // users
    save1: ok({}),
    getById: ok({}),
    getUserPresentations: ok([]),
    getUserWorkshops: ok([]),
    getAllUsers: ok([]),
    createManual: ok({}),
    markAsAdmin: ok({}),
    markAsVolunteer: ok({}),
    // pages
    getPage: ok(''),
    getPages: ok([]),
    createPage: ok({}),
    updatePage: ok({}),
    deletePage: ok({}),
    // faq
    getAllFaqEntries: ok([]),
    getPublishedFaqEntries: ok([]),
    getFaqEntry: ok({}),
    createFaqEntry: ok({}),
    updateFaqEntry: ok({}),
    deleteFaqEntry: ok({}),
    reorderFaqEntries: ok({}),
    renameCategory: ok({}),
    // partners
    getAllPartners: ok([]),
    getPublishedPartners: ok([]),
    getPartner: ok({}),
    createPartner: ok({}),
    updatePartner: ok({}),
    deletePartner: ok({}),
    storePartnerLogo: ok({}),
    // dashboard
    usersStats: ok({ total: 0 }),
    submissionStats: ok({ total: 0, workshops: 0, presentations: 0 }),
    newsletterStat: ok({ subscribersCount: 0 }),
    votes: ok([['date', 'total']]),
    // tokens / resources / votes / tasks
    refreshToken: ok(''),
    storeUserProfilePicture: ok({}),
    start: ok([]),
    save: ok({}),
    getLastWebpageDump: ok({}),
    triggerWebpageDump: ok({}),
    // days & agenda
    getAllDays: ok([]),
    getDayById: ok({}),
    saveDay: ok({}),
    updateDay: ok({}),
    deleteDay: ok({}),
    getAllTimeSlots: ok([]),
    getAllRooms1: ok([]),
    getAgendaEntriesByDay: ok([]),
  }
})

// vuedraggable renders its #item slot per element; stub it to a simple passthrough
vi.mock('vuedraggable', () => ({
  default: {
    name: 'draggable',
    props: ['modelValue'],
    template:
      '<div><template v-for="element in modelValue" :key="element.id"><slot name="item" :element="element" /></template></div>',
  },
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
// Mount helper + lifecycle
// Each test mounts into a real DOM container so Vuetify's internal Teleport
// targets (#app-bar) are present. The wrapper is unmounted in afterEach so
// Vue's reactive system is fully torn down before the next test starts,
// preventing "update after unmount" unhandled rejections.
// ---------------------------------------------------------------------------
function fakeJwt() {
  const part = (o: object) =>
    btoa(JSON.stringify(o)).replace(/\+/g, '-').replace(/\//g, '_').replace(/=+$/, '')
  return `${part({ alg: 'HS256' })}.${part({ jti: 'u1', sub: 'Test', isAdmin: false })}.sig`
}

let _wrapper: ReturnType<typeof mount> | null = null
let _container: HTMLElement | null = null
let _appBar: HTMLElement | null = null

async function mountPage(component: any, route = '/') {
  const vuetify = createVuetify()
  const pinia = createTestingPinia({ stubActions: false })
  const router = createRouter({
    history: createWebHistory(),
    routes: [{ path: '/:pathMatch(.*)*', component: { template: '<div/>' } }],
  })

  await router.push(route)
  await router.isReady()

  _wrapper = mount(component, {
    attachTo: _container!,
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
  // Flush all pending promises so onMounted async calls complete while the
  // component is alive — eliminates post-test reactive-update errors.
  await flushPromises()
  return _wrapper
}

// ---------------------------------------------------------------------------
// Tests
// ---------------------------------------------------------------------------
describe('admin-app pages render without errors', () => {
  beforeEach(() => {
    // Provide a mount container and the #app-bar teleport target that
    // Vuetify's VCard and VDialog internally teleport content into.
    _container = document.createElement('div')
    document.body.appendChild(_container)
    _appBar = document.createElement('div')
    _appBar.id = 'app-bar'
    document.body.appendChild(_appBar)
  })

  afterEach(async () => {
    _wrapper?.unmount()
    _wrapper = null
    _container?.remove()
    _container = null
    _appBar?.remove()
    _appBar = null
    // Drain any final microtasks scheduled during unmount
    await flushPromises()
  })
  it('renders the login page', async () => {
    const wrapper = await mountPage(LoginPage)
    expect(wrapper.html()).toContain('Call for Papers')
  })

  it('renders the OAuth callback page', async () => {
    const wrapper = await mountPage(LoginProviderPage)
    // Shows loading state while processing the OAuth token
    expect(wrapper.html()).toBeDefined()
  })

  it('takes the OAuth token from the URL fragment', async () => {
    const jwt = fakeJwt()
    try {
      await mountPage(LoginProviderPage, `/login/google#access_token=${jwt}`)
      expect(localStorage.getItem('token')).toBe(jwt)
    } finally {
      localStorage.removeItem('token')
    }
  })

  it('ignores an OAuth token passed in the query string', async () => {
    try {
      await mountPage(LoginProviderPage, `/login/google?access_token=${fakeJwt()}`)
      expect(localStorage.getItem('token')).toBeNull()
    } finally {
      localStorage.removeItem('token')
    }
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

  it('renders the admin faq page', async () => {
    const wrapper = await mountPage(AdminFaqPage)
    expect(wrapper.html()).toBeDefined()
  })

  it('renders the admin partners page', async () => {
    const wrapper = await mountPage(AdminPartnersPage)
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
