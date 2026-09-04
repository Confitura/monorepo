import { describe, it, expect } from 'vitest'
import { toTalkRows, toPageRows, toAgendaRows } from '../src/transform'
import type { Presentation, AgendaDay } from '../src/types'

const presentation: Presentation = {
  id: 'talk-1',
  title: 'Exactly-Once Delivery',
  sortableTitle: 'exactly-once delivery',
  shortDescription: 'A short abstract.',
  description: 'A longer abstract mentioning Kafka.',
  level: 'Intermediate',
  language: 'Polish',
  workshop: false,
  isFree: null,
  expectedPrice: null,
  durationInMinutes: 45,
  maxGroupSize: null,
  tags: [
    { id: 'java', name: 'Java' },
    { id: 'microservices', name: 'Microservices' },
  ],
  speakers: [
    {
      id: 'spk-1',
      name: 'Artur Laskowski',
      photoUrl: 'https://api.confitura.pl/api/resources/photos/spk-1.png',
    },
  ],
}

// The GDPR boundary: nothing derived from these fields may appear in what we send.
const PII_FIELD_NAMES = ['name', 'photoUrl', 'photo', 'bio', 'email']
const PII_VALUES = ['Artur Laskowski', 'spk-1.png']

function assertNoPii(payload: unknown) {
  const json = JSON.stringify(payload)
  for (const field of PII_FIELD_NAMES) {
    // no object key equal to a PII field name
    expect(json).not.toMatch(new RegExp(`"${field}"\\s*:`))
  }
  for (const value of PII_VALUES) {
    expect(json).not.toContain(value)
  }
}

describe('toTalkRows', () => {
  it('keeps talk content and links speakers only by opaque id', () => {
    const rows = toTalkRows([presentation])
    expect(rows).toHaveLength(1)
    const row = rows[0]
    expect(row.id).toBe('talk-1')
    expect(row.title).toBe('Exactly-Once Delivery')
    expect(row.description).toContain('Kafka')
    expect(row.level).toBe('Intermediate')
    expect(row.workshop).toBe(false)
    expect(row.tags).toEqual(['Java', 'Microservices'])
    expect(row.speakerIds).toEqual(['spk-1'])
  })

  it('never leaks speaker name, photo, bio, or email (GDPR boundary)', () => {
    assertNoPii(toTalkRows([presentation]))
  })
})

describe('toPageRows', () => {
  it('carries page slug and markdown content', () => {
    const rows = toPageRows([{ slug: 'faq', content: '## Registration\nStill closed' }])
    expect(rows).toEqual([{ slug: 'faq', content: '## Registration\nStill closed' }])
  })
})

describe('toAgendaRows', () => {
  it('places talks by day/time/room using opaque ids only, no speaker PII', () => {
    const day: AgendaDay = {
      dayId: 'day-1',
      timeSlots: [{ index: 0, label: '10:00 - 10:45' }],
      rooms: [{ id: 'room-a', label: 'Room A', displayOrder: 0 }],
      presentations: [presentation],
      entries: [
        { timeSlotIndex: 0, roomId: 'room-a', presentationId: 'talk-1' },
      ],
    }
    const rows = toAgendaRows([day])
    expect(rows).toHaveLength(1)
    const row = rows[0]
    expect(row.dayId).toBe('day-1')
    expect(row.time).toBe('10:00 - 10:45')
    expect(row.room).toBe('Room A')
    expect(row.talkId).toBe('talk-1')
    expect(row.talkTitle).toBe('Exactly-Once Delivery')
    expect(row.speakerIds).toEqual(['spk-1'])
    assertNoPii(rows)
  })
})
