import type {
  Presentation,
  Page,
  AgendaDay,
  Sponsor,
  FaqEntry,
  NewsFeed,
  TalkRow,
  PageRow,
  AgendaRow,
  SponsorRow,
  FaqRow,
  NewsRow,
} from './types'

// Relative page URLs — the chat widget runs on the site, so these link in-place.
function talkUrl(id: string, workshop: boolean): string {
  return workshop ? `/workshops#${id}` : `/presentations#${id}`
}

function stripHtml(html: string): string {
  return html
    .replace(/<[^>]+>/g, ' ')
    .replace(/&nbsp;/g, ' ')
    .replace(/\s+/g, ' ')
    .trim()
}

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
    url: talkUrl(p.id, p.workshop),
  }))
}

// CMS pages (venue, about, …) are ingested as their raw markdown body.
export function toPageRows(pages: Page[]): PageRow[] {
  return pages.map((p) => ({ slug: p.slug, content: p.content, url: `/${p.slug}` }))
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
        url: talk.workshop ? `/schedule/workshops/${day.dayId}` : `/schedule/${day.dayId}`,
      })
    }
  }
  return rows
}

// Sponsors are companies — no PII. Linked to their partner page (slug when set,
// else the id) and their external website.
export function toSponsorRows(sponsors: Sponsor[]): SponsorRow[] {
  return sponsors
    .filter((s) => s.published)
    .map((s) => ({
      id: s.id,
      name: s.name,
      tier: s.type,
      www: s.www,
      description: s.description,
      url: `/partners/${s.slug || s.id}`,
    }))
}

export function toFaqRows(entries: FaqEntry[]): FaqRow[] {
  return entries
    .filter((e) => e.published)
    .map((e) => ({ category: e.category, question: e.question, answer: e.answer, url: '/faq' }))
}

export function toNewsRows(feed: NewsFeed): NewsRow[] {
  return (feed.all ?? []).map((n) => ({
    title: n.title,
    body: stripHtml(n.body ?? ''),
    date: n.publishedAt,
    url: '/news',
  }))
}
