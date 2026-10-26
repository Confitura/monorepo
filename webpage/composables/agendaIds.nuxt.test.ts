// @vitest-environment nuxt
import { describe, it, expect, vi } from 'vitest'
import { defineComponent } from 'vue'
import { mountSuspended } from '@nuxt/test-utils/runtime'

const FEED = {
  rooms: [{ id: 'r1' }],
  agendaEntries: [
    { presentationId: 'p1', presentation: { description: 'long text' } },
    { presentationId: null },
    { presentationId: 'p2' },
  ],
  presentations: [{ id: 'p1', description: 'long text' }],
}

// Applies the call's transform to the feed, as useFetch does, and keeps what each call ends up holding.
const held = vi.hoisted(() => ({ byKey: {} as Record<string, unknown> }))

vi.mock('~/composables/useAPIFetch', async () => {
  const { ref } = await import('vue')
  return {
    useArchiveFetch: vi.fn((path: string, options: { key: string, transform?: (d: unknown) => unknown }) => {
      const value = path.endsWith('day-1.json') || path.endsWith('day-1-workshops.json') ? FEED : null
      const data = ref(options.transform ? options.transform(value) : value)
      held.byKey[options.key] = data.value
      return { data }
    }),
  }
})

import { useScheduledPresentationIds } from '~/composables/useScheduledPresentationIds'
import { usePresentationDayIndex } from '~/composables/usePresentationDayIndex'

async function run<T>(composable: () => T, dayId?: string): Promise<T> {
  let result!: T
  await mountSuspended(defineComponent({ setup() { result = composable(); return () => null } }), { route: { params: { dayId } } })
  return result
}

describe('the agenda id composables', () => {
  it('collect the scheduled presentation ids and keep only ids from each feed', async () => {
    held.byKey = {}
    const ids = await run(() => useScheduledPresentationIds())
    expect([...ids.value]).toEqual(['p1', 'p2'])
    expect(held.byKey['scheduled-agenda-day-1']).toEqual(['p1', 'p2'])
  })

  it('map each presentation id to its day and keep only ids from the day the page does not show', async () => {
    held.byKey = {}
    const index = await run(() => usePresentationDayIndex(), 'day-2')
    expect([...index.value]).toEqual([['p1', 'day-1'], ['p2', 'day-1']])
    expect(held.byKey['day-index-day-1']).toEqual({ agendaEntries: [{ presentationId: 'p1' }, { presentationId: 'p2' }] })
  })

  it('reuse the schedule page\'s own fetch for the day it shows', async () => {
    held.byKey = {}
    await run(() => usePresentationDayIndex(), 'day-2')
    expect(Object.keys(held.byKey).sort()).toEqual(['day-agenda-day-2', 'day-index-day-1'])
  })
})
