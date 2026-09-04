import type {
  Presentation,
  Page,
  AgendaDay,
  TalkRow,
  PageRow,
  AgendaRow,
} from './types'

// Turns accepted presentations/workshops into talk rows linked to speakers by
// OPAQUE ID ONLY. Speaker name and photo are deliberately dropped so they are
// never sent to Datalinks (a third-party processor).
export function toTalkRows(presentations: Presentation[]): TalkRow[] {
  return presentations.map((p) => ({
    id: p.id,
    title: p.title,
    shortDescription: p.shortDescription,
    description: p.description,
    level: p.level,
    language: p.language,
    workshop: p.workshop,
    durationInMinutes: p.durationInMinutes,
    tags: p.tags.map((t) => t.name),
    speakerIds: p.speakers.map((s) => s.id),
  }))
}

// CMS pages (faq, venue, tickets, …) are ingested as their raw markdown body.
export function toPageRows(pages: Page[]): PageRow[] {
  return pages.map((p) => ({ slug: p.slug, content: p.content }))
}

// Flattens the per-day agenda into "talk X is in room Y at time Z" rows, again
// referencing speakers by opaque id only.
export function toAgendaRows(days: AgendaDay[]): AgendaRow[] {
  const rows: AgendaRow[] = []
  for (const day of days) {
    const timeByIndex = new Map(day.timeSlots.map((t) => [t.index, t.label]))
    const roomById = new Map(day.rooms.map((r) => [r.id, r.label]))
    const talkById = new Map(day.presentations.map((p) => [p.id, p]))
    for (const entry of day.entries) {
      if (!entry.presentationId) continue
      const talk = talkById.get(entry.presentationId)
      if (!talk) continue
      rows.push({
        dayId: day.dayId,
        time: timeByIndex.get(entry.timeSlotIndex) ?? '',
        room: roomById.get(entry.roomId) ?? '',
        talkId: talk.id,
        talkTitle: talk.title,
        speakerIds: talk.speakers.map((s) => s.id),
      })
    }
  }
  return rows
}
