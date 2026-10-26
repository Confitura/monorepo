import { describe, it, expect, vi, afterEach } from 'vitest'
import { defineComponent } from 'vue'
import { mount, flushPromises } from '@vue/test-utils'
import { useRoomDoor } from '@/composables/useRoomDoor.ts'

vi.mock('@/utils/api.ts', () => {
  const ok = (data: unknown) => Promise.resolve({ data })
  const slot = (displayOrder: number, start: string, end: string) => ({ displayOrder, start, end })
  return {
    // A date far ahead, so the loader picks it as the next day with an agenda
    getAllDays: () => ok([{ id: 'd1', date: '2099-01-01' }]),
    getAllRooms1: () => ok([{ id: 'r2', label: 'B', displayOrder: 2 }, { id: 'r1', label: 'A', displayOrder: 1 }]),
    getAllTimeSlots: () => ok([slot(1, '10:00:00', '11:00:00'), slot(2, '11:00:00', '11:30:00'), slot(3, '11:30:00', '12:30:00')]),
    getAgendaEntriesByDay: () =>
      ok([
        { timeSlotIndex: 1, roomId: 'r1', presentationId: 'p1' },
        { timeSlotIndex: 2, label: 'Coffee' },
        { timeSlotIndex: 3, roomId: 'r1', presentationId: 'p2' },
      ]),
    getAllPresentations: () =>
      ok([
        { id: 'p1', title: 'First', flatSpeakers: 'Ann', speakers: [] },
        { id: 'p2', title: 'Second', flatSpeakers: '', speakers: [{ name: 'Bob' }, { name: 'Cy' }] },
      ]),
  }
})

async function stateAt(hours: number, minutes: number, roomId: string) {
  vi.useFakeTimers({ toFake: ['Date'] })
  vi.setSystemTime(new Date(2026, 5, 1, hours, minutes, 0))
  let door!: ReturnType<typeof useRoomDoor>
  const wrapper = mount(defineComponent({ setup() { door = useRoomDoor(); return () => null } }))
  await flushPromises()
  const state = door.roomStateFor(roomId)
  wrapper.unmount()
  return state
}

describe('useRoomDoor room state', () => {
  afterEach(() => vi.useRealTimers())

  it('shows the presentation in progress with its end', async () => {
    expect(await stateAt(10, 30, 'r1')).toEqual({ type: 'current', title: 'First', speakers: 'Ann', endsAt: '11:00:00' })
  })

  it('shows a break with the next presentation after it', async () => {
    expect(await stateAt(11, 10, 'r1')).toEqual({
      type: 'break',
      label: 'Coffee',
      endsAt: '11:30:00',
      next: { title: 'Second', speakers: 'Bob, Cy', startsAt: '11:30:00' },
    })
  })

  it('ends when the room has nothing scheduled now or later', async () => {
    expect(await stateAt(10, 30, 'r2')).toEqual({ type: 'ended' })
  })

  it('shows the first presentation before the day starts', async () => {
    expect(await stateAt(9, 0, 'r1')).toEqual({
      type: 'upcoming',
      next: { title: 'First', speakers: 'Ann', startsAt: '10:00:00' },
    })
  })

  it('ends after the last slot', async () => {
    expect(await stateAt(13, 0, 'r1')).toEqual({ type: 'ended' })
  })
})
