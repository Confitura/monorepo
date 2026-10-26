import { describe, expect, it } from 'vitest'
import { formatNewsDate } from './formatNewsDate'

// Whatever zone the server or browser is in, a News Entry shows its Warsaw day.
process.env.TZ = 'America/New_York'

describe('formatNewsDate', () => {
  it('shows the Warsaw day of an entry dated just after midnight in Warsaw', () => {
    expect(formatNewsDate('2026-09-24T22:30:00Z')).toBe('September 25, 2026')
  })
})
