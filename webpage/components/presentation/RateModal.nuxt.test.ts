// @vitest-environment nuxt
import { describe, it, expect, vi, beforeEach } from 'vitest'
import { mountSuspended } from '@nuxt/test-utils/runtime'

// Counts a list as fetched when a call runs it straight away (the default) or when it is executed later.
const fetched = vi.hoisted(() => ({ paths: [] as string[] }))

vi.mock('~/composables/useAPIFetch', async () => {
  const { ref } = await import('vue')
  return {
    useArchiveFetch: vi.fn((path: string, options: { immediate?: boolean } = {}) => {
      if (options.immediate !== false) fetched.paths.push(path)
      const execute = vi.fn(async () => { fetched.paths.push(path) })
      const result = { data: ref(null), pending: ref(false), error: ref(null), refresh: vi.fn(), execute }
      return Object.assign(Promise.resolve(result), result)
    }),
  }
})

import RateModal from '~/components/presentation/RateModal.vue'

describe('the rating popup', () => {
  beforeEach(() => { fetched.paths = [] })

  it('fetches no lists while it is closed', async () => {
    await mountSuspended(RateModal, { props: { presentationId: null } })
    expect(fetched.paths).toEqual([])
  })

  it('fetches the presentation and workshop lists once it is opened', async () => {
    const wrapper = await mountSuspended(RateModal, { props: { presentationId: null } })
    await wrapper.setProps({ presentationId: 'p1' })
    expect(fetched.paths.sort()).toEqual(['/presentations/accepted.json', '/workshops/accepted.json'])
  })
})
