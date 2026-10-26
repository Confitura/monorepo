import { describe, it, expect, vi, afterEach } from 'vitest'
import { defineComponent } from 'vue'
import { mount, flushPromises } from '@vue/test-utils'
import { useRoomDoor } from '@/composables/useRoomDoor.ts'

// Whatever zone the tablet is in, the screen follows Warsaw time.
process.env.TZ = 'America/New_York'

const days = vi.hoisted(() => ({ fail: false, calls: 0 }))

vi.mock('@/utils/api.ts', () => {
  const ok = (data: unknown) => Promise.resolve({ data })
  const slot = (displayOrder: number, start: string, end: string) => ({ displayOrder, start, end })
  return {
    getAllDays: () => {
      days.calls++
      return days.fail ? Promise.reject(new Error('offline')) : ok([{ id: 'd1', date: '2026-06-01' }, { id: 'd2', date: '2026-06-02' }])
    },
    getAllRooms1: () => ok([{ id: 'r2', label: 'B', displayOrder: 2 }, { id: 'r1', label: 'A', displayOrder: 1 }]),
    getAllTimeSlots: () => ok([slot(1, '10:00:00', '11:00:00'), slot(2, '11:00:00', '11:30:00'), slot(3, '11:30:00', '12:30:00')]),
    getAgendaEntriesByDay: ({ path }: { path: { dayId: string } }) =>
      ok(
        path.dayId === 'd2'
          ? [{ timeSlotIndex: 1, roomId: 'r1', presentationId: 'p2' }]
          : [
              { timeSlotIndex: 1, roomId: 'r1', presentationId: 'p1' },
              { timeSlotIndex: 2, label: 'Coffee' },
              { timeSlotIndex: 3, roomId: 'r1', presentationId: 'p2' },
            ],
      ),
    getAllPresentations: () =>
      ok([
        { id: 'p1', title: 'First', flatSpeakers: 'Ann', speakers: [] },
        { id: 'p2', title: 'Second', flatSpeakers: '', speakers: [{ name: 'Bob' }, { name: 'Cy' }] },
      ]),
  }
})

function mountDoor() {
  let door!: ReturnType<typeof useRoomDoor>
  const wrapper = mount(defineComponent({ setup() { door = useRoomDoor(); return () => null } }))
  return { wrapper, door: () => door }
}

// `warsawTime` is a Warsaw wall clock on the first conference day, 2026-06-01 (CEST, UTC+2)
async function stateAt(warsawTime: string, roomId: string, date = '2026-06-01') {
  vi.useFakeTimers({ toFake: ['Date'] })
  vi.setSystemTime(new Date(`${date}T${warsawTime}+02:00`))
  const { wrapper, door } = mountDoor()
  await flushPromises()
  const state = door().roomStateFor(roomId)
  wrapper.unmount()
  return state
}

describe('useRoomDoor room state', () => {
  afterEach(() => vi.useRealTimers())

  it('shows the presentation in progress with its end', async () => {
    expect(await stateAt('10:30:00', 'r1')).toEqual({ type: 'current', title: 'First', speakers: 'Ann', endsAt: '11:00:00' })
  })

  it('shows a break with the next presentation after it', async () => {
    expect(await stateAt('11:10:00', 'r1')).toEqual({
      type: 'break',
      label: 'Coffee',
      endsAt: '11:30:00',
      next: { title: 'Second', speakers: 'Bob, Cy', startsAt: '11:30:00' },
    })
  })

  it('ends when the room has nothing scheduled now or later', async () => {
    expect(await stateAt('10:30:00', 'r2')).toEqual({ type: 'ended' })
  })

  it('shows the first presentation before the day starts', async () => {
    expect(await stateAt('09:00:00', 'r1')).toEqual({
      type: 'upcoming',
      next: { title: 'First', speakers: 'Ann', startsAt: '10:00:00' },
    })
  })

  it('ends after the last slot', async () => {
    expect(await stateAt('13:00:00', 'r1')).toEqual({ type: 'ended' })
  })

  it('shows nothing as now on the day before the conference', async () => {
    expect(await stateAt('10:15:00', 'r1', '2026-05-31')).toEqual({
      type: 'upcoming',
      next: { title: 'First', speakers: 'Ann', startsAt: '10:00:00' },
    })
  })

  it('uses the Warsaw date near midnight, not the UTC one', async () => {
    // 00:30 Warsaw on 2026-06-01 is still 2026-05-31 in UTC
    expect(await stateAt('00:30:00', 'r1')).toEqual({
      type: 'upcoming',
      next: { title: 'First', speakers: 'Ann', startsAt: '10:00:00' },
    })
  })

  it('switches to the next day after midnight in Warsaw', async () => {
    vi.useFakeTimers({ toFake: ['Date', 'setInterval', 'clearInterval'] })
    vi.setSystemTime(new Date('2026-06-01T23:59:50+02:00'))
    const { wrapper, door } = mountDoor()
    await flushPromises()
    expect(door().roomStateFor('r1')).toEqual({ type: 'ended' })

    await vi.advanceTimersByTimeAsync(20_000)
    await flushPromises()
    expect(door().roomStateFor('r1')).toEqual({
      type: 'upcoming',
      next: { title: 'Second', speakers: 'Bob, Cy', startsAt: '10:00:00' },
    })
    wrapper.unmount()
  })
})

describe('useRoomDoor failed reload', () => {
  afterEach(() => {
    vi.useRealTimers()
    days.fail = false
  })

  it('retries about once a minute until the schedule loads again', async () => {
    vi.useFakeTimers({ toFake: ['Date', 'setInterval', 'clearInterval'] })
    vi.setSystemTime(new Date('2026-06-01T23:59:50+02:00'))
    const { wrapper, door } = mountDoor()
    await flushPromises()

    days.fail = true
    await vi.advanceTimersByTimeAsync(20_000) // past midnight: the reload fails
    await flushPromises()
    expect(door().error.value).toBe('Failed to load schedule')

    const callsAfterFailure = days.calls
    await vi.advanceTimersByTimeAsync(30_000)
    expect(days.calls).toBe(callsAfterFailure) // not retried on every tick

    days.fail = false
    await vi.advanceTimersByTimeAsync(40_000)
    await flushPromises()
    expect(door().error.value).toBeNull()
    expect(door().roomStateFor('r1')).toEqual({
      type: 'upcoming',
      next: { title: 'Second', speakers: 'Bob, Cy', startsAt: '10:00:00' },
    })
    wrapper.unmount()
  })
})

describe('useRoomDoor countdown', () => {
  afterEach(() => vi.useRealTimers())

  async function countdown(warsawTime: string, target: string) {
    vi.useFakeTimers({ toFake: ['Date'] })
    vi.setSystemTime(new Date(`2026-06-01T${warsawTime}+02:00`))
    const { wrapper, door } = mountDoor()
    await flushPromises()
    const text = door().formatCountdown(target)
    wrapper.unmount()
    return text
  }

  it('shows 1 minute at exactly 60 seconds remaining', async () => {
    expect(await countdown('10:59:00', '11:00:00')).toBe('1 minute')
  })

  it('counts seconds under a minute', async () => {
    expect(await countdown('10:59:30', '11:00:00')).toBe('00:30')
  })

  it('counts minutes in Warsaw time', async () => {
    expect(await countdown('10:30:00', '11:00:00')).toBe('30 minutes')
  })
})
