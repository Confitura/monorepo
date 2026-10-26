import { describe, it, expect, vi, beforeEach, afterEach } from 'vitest'
import { mount, flushPromises } from '@vue/test-utils'
import { createVuetify } from 'vuetify'
import { AxiosError, type InternalAxiosRequestConfig } from 'axios'
import { client } from '@/client/client.gen'
import router from '@/plugins/router.ts'
import { startSignInAttempt } from '@/utils/sign-in-attempt.ts'
import PresentationFormPage from '@/pages/presentation-form.[[id]].[[userId]].vue'
import RatePage from '@/pages/rate.[[entryId]].vue'
import VoteForPapersPage from '@/pages/vote-for-papers.vue'
import ProfileFormPage from '@/pages/profile-form.[[id]].vue'
import LoginProviderPage from '@/pages/login.[provider].vue'
import DashboardPage from '@/pages/dashboard.vue'
import PresentationPreviewPage from '@/pages/admin/presentation-preview.[[id]].vue'

// The pages run against the real generated client; only the HTTP layer is faked, so a refused request
// fails exactly as it does in the browser.
vi.mock('@/plugins/router.ts', async () => {
  const { createRouter, createWebHistory } = await import('vue-router')
  const paths = ['/login/:provider', '/presentation-form/:id?/:userId?', '/rate/:entryId?', '/admin/presentation-preview/:id?']
  const routes = [...paths, '/:pathMatch(.*)*'].map((path) => ({ path, component: { template: '<div />' } }))
  return { default: createRouter({ history: createWebHistory(), routes }) }
})

// jsdom doesn't implement ResizeObserver, required by some Vuetify components
global.ResizeObserver = class { observe() {} unobserve() {} disconnect() {} }

function fakeJwt(jti: string) {
  const part = (o: object) => btoa(JSON.stringify(o)).replace(/=+$/, '')
  return `${part({ alg: 'HS256' })}.${part({ jti, sub: jti, isAdmin: false })}.sig`
}
const TOKEN = fakeJwt('u1')

// Answers each request by "METHOD /url"; anything unlisted is refused with 403.
function backendAnswers(answers: Record<string, { status: number; data?: unknown }>) {
  client.instance.defaults.adapter = async (config: InternalAxiosRequestConfig) => {
    const path = config.url!.replace(import.meta.env.VITE_API_URL, '')
    const { status, data = {} } = answers[`${config.method?.toUpperCase()} ${path}`] ?? { status: 403 }
    const response = { status, statusText: '', headers: {}, config, data }
    if (status < 400) return response
    throw new AxiosError(`Request failed with status code ${status}`, AxiosError.ERR_BAD_REQUEST, config, null, response)
  }
}

let wrapper: ReturnType<typeof mount> | null = null

async function mountPage(component: any, route: string, stubs: Record<string, boolean> = {}) {
  await router.push(route)
  const plugins = [createVuetify(), createPinia(), router]
  wrapper = mount(component, { attachTo: document.body, global: { plugins, stubs } })
  await flushPromises()
  return wrapper
}

const notifications = () => useNotificationStore().notifications.map(({ type, text }) => `${type}: ${text}`)

const button = (text: string) => wrapper!.findAll('button').find((b) => b.text() === text)!

describe('a request the backend refuses', () => {
  beforeEach(() => {
    localStorage.setItem('token', TOKEN)
  })

  afterEach(async () => {
    wrapper?.unmount()
    wrapper = null
    localStorage.clear()
    sessionStorage.clear()
    await flushPromises()
  })

  it('the talk form says it failed, not "saved", when the C4P is closed', async () => {
    backendAnswers({
      'GET /tags': { status: 200, data: [] },
      'GET /users/u1/presentations/p1': {
        status: 200,
        data: { title: 'Kotlin', shortDescription: 'Short', description: 'Long', level: 'Beginner', language: 'English', tags: [] },
      },
      'POST /users/u1/presentations/p1': { status: 403 },
    })
    await mountPage(PresentationFormPage, '/presentation-form/p1')

    await wrapper!.get('form').trigger('submit')
    await flushPromises()

    expect(notifications()).toEqual(['error: Failed to save presentation Kotlin'])
  })

  it('tells the attendee their rating was not submitted', async () => {
    backendAnswers({
      'GET /rating/status': { status: 200, data: { open: true } },
      'GET /presentations/p1/rating-enabled': { status: 200, data: { ratingEnabled: true } },
      'POST /presentations/p1/ratings': { status: 409 },
    })
    await mountPage(RatePage, '/rate/p1')

    await wrapper!.findAll('.v-rating__item label')[3].trigger('click')
    await button('Submit 4').trigger('click')
    await flushPromises()

    expect(wrapper!.text()).toContain('Failed to submit rating')
    expect(wrapper!.text()).not.toContain('Thanks for your feedback!')
  })

  it('keeps the voter on the talk whose vote was not saved, and says so', async () => {
    localStorage.setItem('v4p-token', 'v4p')
    localStorage.setItem('v4p-position', '0')
    backendAnswers({
      'POST /votes/start/v4p': {
        status: 200,
        data: [{ id: 'v1', presentation: { title: 'First talk' } }, { id: 'v2', presentation: { title: 'Second talk' } }],
      },
      'POST /votes': { status: 500 },
    })
    await mountPage(VoteForPapersPage, '/vote-for-papers')

    await button('+1').trigger('click')
    await flushPromises()

    expect(wrapper!.text()).toContain('First talk')
    expect(notifications()).toEqual(['error: Your vote was not saved, please try again'])
  })

  it('never signs the user in without a token when refreshing it after a profile save fails', async () => {
    backendAnswers({
      'GET /users/u1': { status: 200, data: { id: 'u1', name: 'Jane', email: 'jane@example.com', privacyPolicyAccepted: true } },
      'GET /users/u1/logins': { status: 200, data: [] },
      'POST /users': { status: 200 },
      'POST /tokens/refresh-token': { status: 500 },
    })
    await mountPage(ProfileFormPage, '/profile-form')

    await wrapper!.get('form').trigger('submit')
    await flushPromises()

    expect(localStorage.getItem('token')).toBe(TOKEN)
    expect(router.currentRoute.value.path).toBe('/profile-form')
    expect(notifications()).toEqual(['error: Failed to save user'])
  })

  it('still goes back to the profile when cancelling an added Login fails', async () => {
    const state = startSignInAttempt('add-login')
    backendAnswers({
      'POST /users/merge/preview': { status: 200, data: { name: 'Jane' } },
      'POST /users/merge/cancel': { status: 500 },
    })
    await mountPage(LoginProviderPage, `/login/google?state=${state}#access_token=${fakeJwt('u2')}`)

    await button('Cancel').trigger('click')
    await flushPromises()

    expect(router.currentRoute.value.path).toBe('/profile-form')
    expect(localStorage.getItem('token')).toBe(TOKEN)
  })

  it('shows the dashboard stats that loaded when the newsletter count fails', async () => {
    backendAnswers({
      'GET /dashboard/users': { status: 200, data: { total: 7 } },
      'GET /dashboard/submissions': { status: 200, data: { total: 5, workshops: 2, presentations: 3 } },
      'GET /dashboard/newsletter': { status: 500 },
    })
    await mountPage(DashboardPage, '/dashboard', { ChartUserTypes: true, ChartSubmissionTypes: true, ChartVotesOverTime: true })

    expect(wrapper!.text()).toContain('Workshops: 2, prestations: 3')
  })

  it('still shows the other speakers of a talk when one profile fails to load', async () => {
    backendAnswers({
      'GET /presentations': { status: 200, data: [{ id: 'p1', title: 'Talk', speakers: [] }] },
      'GET /presentations/p1/cospeakers': { status: 200, data: [{ id: 'u1' }, { id: 'u2' }] },
      'GET /users/u1': { status: 200, data: { id: 'u1', name: 'Ada', bio: 'Writes compilers' } },
    })
    await mountPage(PresentationPreviewPage, '/admin/presentation-preview/p1', { Rating: true, HomepageRating: true })

    expect(wrapper!.text()).toContain('Writes compilers')
  })
})
