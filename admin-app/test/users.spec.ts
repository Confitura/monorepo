import { describe, it, expect, vi, beforeEach, afterEach } from 'vitest'
import { mount, flushPromises } from '@vue/test-utils'
import { createVuetify } from 'vuetify'
import { createTestingPinia } from '@pinia/testing'
import UsersPage from '@/pages/admin/users.vue'

global.ResizeObserver = class ResizeObserver {
  observe() {}
  unobserve() {}
  disconnect() {}
}

const users = [
  { id: 'u1', name: 'Ada', email: 'ada@example.com', loginProviders: ['github', 'google'] },
  { id: 'u2', name: 'Bob', email: 'bob@example.com', loginProviders: [] },
]

const api = vi.hoisted(() => ({
  getAllUsers: vi.fn(),
  createManual: vi.fn(),
  markAsAdmin: vi.fn(),
  markAsVolunteer: vi.fn(),
}))

vi.mock('@/utils/api.ts', () => api)

let appBar: HTMLElement

function mountPage() {
  return mount(UsersPage, {
    global: { plugins: [createVuetify(), createTestingPinia({ createSpy: vi.fn })] },
  })
}

function rowOf(wrapper: ReturnType<typeof mountPage>, name: string) {
  return wrapper.findAll('tbody tr').find((row) => row.text().includes(name))!
}

describe('admin users page', () => {
  beforeEach(() => {
    // users.vue teleports its search bar into the layout's #app-bar
    appBar = document.createElement('div')
    appBar.id = 'app-bar'
    document.body.appendChild(appBar)
    api.getAllUsers.mockResolvedValue({ data: users, status: 200 })
  })

  afterEach(() => {
    appBar.remove()
    vi.clearAllMocks()
  })

  it('shows a Logins column instead of Origin', async () => {
    const wrapper = mountPage()
    await flushPromises()
    const headers = wrapper.findAll('thead th').map((th) => th.text())
    expect(headers).toContain('Logins')
    expect(headers).not.toContain('Origin')
  })

  it("lists each user's login providers", async () => {
    const wrapper = mountPage()
    await flushPromises()
    expect(rowOf(wrapper, 'Ada').text()).toContain('github, google')
  })

  it('shows Manual for a user with no login', async () => {
    const wrapper = mountPage()
    await flushPromises()
    expect(rowOf(wrapper, 'Bob').text()).toContain('Manual')
  })
})
