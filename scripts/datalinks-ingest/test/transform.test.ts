import { describe, it, expect } from 'vitest'
import {
  toTalkRows,
  toPageRows,
  toAgendaRows,
  toSponsorRows,
  toFaqRows,
  toNewsRows,
} from '../src/transform'
import type { Presentation, AgendaDay, Sponsor, FaqEntry, NewsFeed } from '../src/types'

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
    expect(row.url).toBe('/presentations#talk-1')
  })

  it('links workshops to the workshops page', () => {
    const rows = toTalkRows([{ ...presentation, id: 'ws-1', workshop: true }])
    expect(rows[0].url).toBe('/workshops#ws-1')
  })

  it('never leaks speaker name, photo, bio, or email (GDPR boundary)', () => {
    assertNoPii(toTalkRows([presentation]))
  })
})

describe('toPageRows', () => {
  it('carries page slug, markdown content and a url', () => {
    const rows = toPageRows([{ slug: 'venue', content: '## Venue\nWarsaw' }])
    expect(rows).toEqual([{ slug: 'venue', content: '## Venue\nWarsaw', url: '/venue' }])
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
    expect(row.url).toBe('/schedule/day-1')
    assertNoPii(rows)
  })

  it('links workshop entries to the workshops schedule', () => {
    const day: AgendaDay = {
      dayId: 'day-2',
      timeSlots: [{ index: 0, label: '10:00' }],
      rooms: [{ id: 'room-a', label: 'Room A', displayOrder: 0 }],
      presentations: [{ ...presentation, id: 'ws-1', workshop: true }],
      entries: [{ timeSlotIndex: 0, roomId: 'room-a', presentationId: 'ws-1' }],
    }
    expect(toAgendaRows([day])[0].url).toBe('/schedule/workshops/day-2')
  })
})

describe('toSponsorRows', () => {
  const sponsor: Sponsor = {
    id: 'uuid-1',
    slug: 'xtb',
    name: 'XTB',
    type: 'gold',
    www: 'https://xtb.com',
    description: 'A gold partner.',
    orientation: 'horizontal',
    published: true,
  }

  it('maps published sponsors with a partner-page url', () => {
    const rows = toSponsorRows([sponsor])
    expect(rows).toEqual([
      { id: 'uuid-1', name: 'XTB', tier: 'gold', www: 'https://xtb.com', description: 'A gold partner.', url: '/partners/xtb' },
    ])
  })

  it('falls back to id in the url when slug is missing, and skips unpublished', () => {
    const rows = toSponsorRows([
      { ...sponsor, slug: null },
      { ...sponsor, id: 'uuid-2', published: false },
    ])
    expect(rows).toHaveLength(1)
    expect(rows[0].url).toBe('/partners/uuid-1')
  })
})

describe('toFaqRows', () => {
  it('maps published entries to category/question/answer with the faq url', () => {
    const entries: FaqEntry[] = [
      { id: 'e1', category: '02. Tickets', question: 'How to buy?', answer: 'Online.', displayOrder: 0, published: true },
      { id: 'e2', category: '02. Tickets', question: 'Draft', answer: 'x', displayOrder: 1, published: false },
    ]
    const rows = toFaqRows(entries)
    expect(rows).toEqual([
      { category: '02. Tickets', question: 'How to buy?', answer: 'Online.', url: '/faq' },
    ])
  })
})

describe('toNewsRows', () => {
  it('strips html from the body and links to the news page', () => {
    const feed: NewsFeed = {
      latest: null,
      all: [
        { title: 'TICKETS', body: '<p>Tickets are <a href="x">HERE!</a></p>', publishedAt: '2026-08-03T09:50:32Z' },
      ],
    }
    const rows = toNewsRows(feed)
    expect(rows).toHaveLength(1)
    expect(rows[0].title).toBe('TICKETS')
    expect(rows[0].body).toBe('Tickets are HERE!')
    expect(rows[0].url).toBe('/news')
    expect(rows[0].date).toBe('2026-08-03T09:50:32Z')
  })
})
