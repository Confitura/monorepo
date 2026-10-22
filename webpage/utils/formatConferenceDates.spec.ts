import { describe, expect, it } from 'vitest'
import { formatConferenceDates } from './formatConferenceDates'

describe('formatConferenceDates', () => {
  it('shows a two-day conference as a range within the month', () => {
    expect(formatConferenceDates('2026-09-25T09:00:00+02:00', '2026-09-26')).toBe('September 25–26, 2026')
  })

  it('shows a one-day conference as a single date', () => {
    expect(formatConferenceDates('2026-09-25T09:00:00+02:00', '2026-09-25')).toBe('September 25, 2026')
  })

  it('shows a single date when the last day is missing', () => {
    expect(formatConferenceDates('2026-09-25T09:00:00+02:00', null)).toBe('September 25, 2026')
  })

  it('names both months when the conference spans two', () => {
    expect(formatConferenceDates('2031-06-30T09:00:00+02:00', '2031-07-01')).toBe('June 30 – July 1, 2031')
  })

  it('leaves the year out when asked', () => {
    expect(formatConferenceDates('2026-09-25T09:00:00+02:00', '2026-09-26', { withYear: false })).toBe('September 25–26')
  })

  it('reads the start date on the conference clock, not the reader\'s', () => {
    expect(formatConferenceDates('2026-09-25T00:30:00+02:00', '2026-09-26')).toBe('September 25–26, 2026')
  })
})
