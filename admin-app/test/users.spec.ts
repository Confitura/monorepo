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
  { id: 'u1', name: 'Ada', email: 'ada@example.com', logins: [{ id: 'l1', provider: 'github' }, { id: 'l2', provider: 'google' }] },
  { id: 'u2', name: 'Bob', email: 'bob@example.com', logins: [] },
  { id: 'u3', name: 'Cy', email: 'cy@example.com', logins: [{ id: 'l3', provider: 'facebook' }] },
]

const api = vi.hoisted(() => ({
  getAllUsers: vi.fn(),
  createManual: vi.fn(),
  markAsAdmin: vi.fn(),
  markAsVolunteer: vi.fn(),
  unlinkLogin: vi.fn(),
}))

vi.mock('@/utils/api.ts', () => api)

// The confirm dialog answers yes, and records what it asked.
const confirm = vi.hoisted(() => vi.fn())
vi.mock('@/components/DialogConfirm.vue', () => ({
  default: {
    name: 'DialogConfirm',
    template: '<div />',
    setup(_: unknown, { expose }: { expose: (o: unknown) => void }) {
      expose({ open: confirm })
      return {}
    },
  },
}))

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
    confirm.mockResolvedValue(true)
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
    expect(rowOf(wrapper, 'Ada').text()).toContain('GitHub')
    expect(rowOf(wrapper, 'Ada').text()).toContain('Google')
  })

  it('shows Manual for a user with no login', async () => {
    const wrapper = mountPage()
    await flushPromises()
    expect(rowOf(wrapper, 'Bob').text()).toContain('Manual')
  })

  it("removes a user's Login after confirming", async () => {
    api.unlinkLogin.mockResolvedValue({ status: 204 })
    const wrapper = mountPage()
    await flushPromises()

    await rowOf(wrapper, 'Ada').get('[aria-label="Remove GitHub login"]').trigger('click')
    await flushPromises()

    expect(confirm).toHaveBeenCalledWith("Remove Ada's GitHub login?")
    expect(api.unlinkLogin).toHaveBeenCalledWith(expect.objectContaining({ path: { id: 'u1', loginId: 'l1' } }))
    expect(api.getAllUsers).toHaveBeenCalledTimes(2)
  })

  it("removes a user's last Login too, warning they can no longer sign in", async () => {
    api.unlinkLogin.mockResolvedValue({ status: 204 })
    const wrapper = mountPage()
    await flushPromises()

    await rowOf(wrapper, 'Cy').get('[aria-label="Remove Facebook login"]').trigger('click')
    await flushPromises()

    expect(confirm).toHaveBeenCalledWith(expect.stringContaining('will not be able to sign in'))
    expect(api.unlinkLogin).toHaveBeenCalledWith(expect.objectContaining({ path: { id: 'u3', loginId: 'l3' } }))
  })
})
