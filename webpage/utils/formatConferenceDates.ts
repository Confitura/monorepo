const MONTHS = [
  'January', 'February', 'March', 'April', 'May', 'June',
  'July', 'August', 'September', 'October', 'November', 'December'
]

// A date-only 'YYYY-MM-DD' split into parts, so no timezone shifts the day.
function parts(date: string) {
  const [year, month, day] = date.slice(0, 10).split('-').map(Number)
  return { year, month: MONTHS[month - 1], day }
}

// The conference dates as the banner words them: "September 25–26, 2026", or one date for a one-day conference.
// `conferenceStart` carries the conference's own UTC offset, so its first ten characters are the local start day.
export function formatConferenceDates(
  conferenceStart: string,
  lastDay: string | null | undefined,
  { withYear = true }: { withYear?: boolean } = {}
): string {
  const first = parts(conferenceStart)
  const last = lastDay ? parts(lastDay) : first
  const sameMonth = first.month === last.month
  let range = `${first.month} ${first.day}`
  if (last.day !== first.day || !sameMonth) {
    range += sameMonth ? `–${last.day}` : ` – ${last.month} ${last.day}`
  }
  return withYear ? `${range}, ${last.year}` : range
}
