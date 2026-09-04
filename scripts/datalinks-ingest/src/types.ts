// Shapes of the public Confitura resource JSON we read (only the fields we use).

export interface Tag {
  id: string
  name: string
}

export interface PublicSpeakerRef {
  id: string
  name: string
  photoUrl: string
}

export interface Presentation {
  id: string
  title: string
  sortableTitle: string
  shortDescription: string | null
  description: string | null
  level: string | null
  language: string | null
  workshop: boolean
  isFree: boolean | null
  expectedPrice: number | null
  durationInMinutes: number | null
  maxGroupSize: number | null
  tags: Tag[]
  speakers: PublicSpeakerRef[]
}

export interface AgendaTimeSlot {
  index: number
  label: string
}

export interface AgendaRoom {
  id: string
  label: string
  displayOrder: number
}

export interface AgendaEntry {
  timeSlotIndex: number
  roomId: string
  presentationId: string | null
}

export interface AgendaDay {
  dayId: string
  timeSlots: AgendaTimeSlot[]
  rooms: AgendaRoom[]
  presentations: Presentation[]
  entries: AgendaEntry[]
}

export interface Page {
  slug: string
  content: string
}

// Sanitized rows we send to Datalinks — no speaker PII, opaque ids only.

export interface TalkRow {
  id: string
  title: string
  shortDescription: string | null
  description: string | null
  level: string | null
  language: string | null
  workshop: boolean
  durationInMinutes: number | null
  tags: string[]
  speakerIds: string[]
}

export interface PageRow {
  slug: string
  content: string
}

export interface AgendaRow {
  dayId: string
  time: string
  room: string
  talkId: string
  talkTitle: string
  speakerIds: string[]
}
