import { describe, it, expect, vi, beforeEach, afterEach } from 'vitest'
import { mount, flushPromises } from '@vue/test-utils'
import { createVuetify } from 'vuetify'
import { createTestingPinia } from '@pinia/testing'
import { VAutocomplete, VBtn, VCard, VRadioGroup } from 'vuetify/components'
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
  adminMergeUsers: vi.fn(),
  previewAdminMerge: vi.fn(),
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

// The merge dialog is teleported out of the page, so it is reached through its components.
async function choose(wrapper: ReturnType<typeof mountPage>, otherId: string, survivorId: string) {
  await wrapper.findComponent(VAutocomplete).setValue(otherId)
  await wrapper.findComponent(VRadioGroup).setValue(survivorId)
  await flushPromises()
}

async function merge(wrapper: ReturnType<typeof mountPage>, otherId: string, survivorId: string) {
  await choose(wrapper, otherId, survivorId)
  await mergeButton(wrapper)!.trigger('click')
  await flushPromises()
}

const mergeButton = (wrapper: ReturnType<typeof mountPage>) =>
  wrapper.findAllComponents(VBtn).find((button) => button.text() === 'Merge')

const mergeDialogText = (wrapper: ReturnType<typeof mountPage>) =>
  wrapper.findAllComponents(VCard).find((card) => card.text().includes('with another user'))!.text()

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
    api.previewAdminMerge.mockResolvedValue({ data: { name: 'Cy' }, status: 200 })
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

  it('merges a user with another one found by email, keeping the one the admin chooses', async () => {
    api.adminMergeUsers.mockResolvedValue({ status: 204 })
    const wrapper = mountPage()
    await flushPromises()

    await rowOf(wrapper, 'Bob').get('[aria-label="Merge Bob"]').trigger('click')
    const search = wrapper.findComponent(VAutocomplete).find('input')
    await search.trigger('focus')
    await search.setValue('@example.com')
    await flushPromises()
    // Everyone but Bob himself.
    expect([...document.querySelectorAll('.v-list-item')].map((item) => item.textContent))
      .toEqual(['Ada (ada@example.com)', 'Cy (cy@example.com)'])
    await merge(wrapper, 'u3', 'u3')

    expect(api.adminMergeUsers).toHaveBeenCalledWith(expect.objectContaining({ path: { survivorId: 'u3', absorbedId: 'u2' } }))
    expect(api.getAllUsers).toHaveBeenCalledTimes(2)
  })

  it('previews the resulting profile, roles gained and talks that move before the merge is confirmed', async () => {
    api.previewAdminMerge.mockResolvedValue({
      data: { name: 'Cy', email: 'cy@example.com', becomesAdmin: true, becomesVolunteer: false, talksToMove: ["Bob's talk"] },
      status: 200,
    })
    const wrapper = mountPage()
    await flushPromises()

    await rowOf(wrapper, 'Bob').get('[aria-label="Merge Bob"]').trigger('click')
    await choose(wrapper, 'u3', 'u3')

    expect(api.previewAdminMerge).toHaveBeenCalledWith(expect.objectContaining({ path: { survivorId: 'u3', absorbedId: 'u2' } }))
    expect(mergeDialogText(wrapper)).toContain('cy@example.com')
    expect(mergeDialogText(wrapper)).toContain('The kept user also becomes an admin.')
    expect(mergeDialogText(wrapper)).toContain('These talks move to the kept user:')
    expect(mergeDialogText(wrapper)).toContain("Bob's talk")
    expect(mergeButton(wrapper)!.attributes('disabled')).toBeUndefined()
    expect(api.adminMergeUsers).not.toHaveBeenCalled()
  })

  it("shows the backend's reason in place of the Merge button when the merge would be refused", async () => {
    const reason = 'Both users have a ticket, so they cannot be merged. Please contact an admin.'
    api.previewAdminMerge.mockRejectedValue({ status: 409, response: { data: reason } })
    const wrapper = mountPage()
    await flushPromises()

    await rowOf(wrapper, 'Bob').get('[aria-label="Merge Bob"]').trigger('click')
    await choose(wrapper, 'u3', 'u2')

    expect(mergeDialogText(wrapper)).toContain(reason)
    expect(mergeButton(wrapper)).toBeUndefined()
  })

  it('previews the latest choice of the user to keep, even when an earlier preview answers last', async () => {
    let answerEarlier!: (preview: unknown) => void
    api.previewAdminMerge
      .mockReturnValueOnce(new Promise((resolve) => { answerEarlier = resolve }))
      .mockResolvedValueOnce({ data: { name: 'Bob', email: 'latest@example.com' }, status: 200 })
    const wrapper = mountPage()
    await flushPromises()

    await rowOf(wrapper, 'Bob').get('[aria-label="Merge Bob"]').trigger('click')
    await choose(wrapper, 'u3', 'u3')
    await wrapper.findComponent(VRadioGroup).setValue('u2')
    await flushPromises()
    answerEarlier({ data: { name: 'Cy', email: 'earlier@example.com' }, status: 200 })
    await flushPromises()

    expect(mergeDialogText(wrapper)).toContain('latest@example.com')
    expect(mergeDialogText(wrapper)).not.toContain('earlier@example.com')
  })

  // The merge itself may still be refused after its preview, e.g. when a ticket was issued in between.
  it('shows why the backend refuses a merge', async () => {
    const reason = 'Both users have a ticket, so they cannot be merged. Please contact an admin.'
    api.adminMergeUsers.mockRejectedValue({ status: 409, response: { data: reason } })
    const wrapper = mountPage()
    await flushPromises()

    await rowOf(wrapper, 'Bob').get('[aria-label="Merge Bob"]').trigger('click')
    await merge(wrapper, 'u3', 'u2')

    expect(api.adminMergeUsers).toHaveBeenCalledWith(expect.objectContaining({ path: { survivorId: 'u2', absorbedId: 'u3' } }))
    expect(useNotificationStore().addNotification).toHaveBeenCalledWith(reason, 'error')
  })
})
