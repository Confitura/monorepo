import { describe, it, expect } from 'vitest'
import { fromWarsawInput, toWarsawInput } from '@/utils/warsaw-time'

// Whatever zone the browser is in, a Warsaw wall-clock time must map to the same instant.
process.env.TZ = 'America/New_York'

describe('Warsaw date and time input', () => {
  it('reads a summer time as CEST (UTC+2)', () => {
    expect(fromWarsawInput('2026-06-15T12:30')).toBe('2026-06-15T10:30:00.000Z')
  })

  it('reads a winter time as CET (UTC+1)', () => {
    expect(fromWarsawInput('2026-12-01T10:00')).toBe('2026-12-01T09:00:00.000Z')
  })

  it('shows an instant as Warsaw wall-clock time', () => {
    expect(toWarsawInput('2026-06-15T10:30:00Z')).toBe('2026-06-15T12:30')
    expect(toWarsawInput('2026-12-01T09:00:00Z')).toBe('2026-12-01T10:00')
  })

  it('switches offset at the spring-forward boundary (2026-03-29, 02:00 -> 03:00)', () => {
    expect(fromWarsawInput('2026-03-29T01:59')).toBe('2026-03-29T00:59:00.000Z')
    expect(fromWarsawInput('2026-03-29T03:00')).toBe('2026-03-29T01:00:00.000Z')
    expect(toWarsawInput('2026-03-29T01:00:00Z')).toBe('2026-03-29T03:00')
  })

  it('switches offset at the fall-back boundary (2026-10-25, 03:00 -> 02:00)', () => {
    expect(fromWarsawInput('2026-10-25T01:59')).toBe('2026-10-24T23:59:00.000Z')
    expect(fromWarsawInput('2026-10-25T04:00')).toBe('2026-10-25T03:00:00.000Z')
    expect(toWarsawInput('2026-10-25T01:30:00Z')).toBe('2026-10-25T02:30')
  })

  it('moves a time that does not exist on spring-forward day an hour later', () => {
    expect(fromWarsawInput('2026-03-29T02:30')).toBe('2026-03-29T01:30:00.000Z')
  })

  it('round-trips an instant', () => {
    expect(fromWarsawInput(toWarsawInput('2026-07-04T18:45:00Z'))).toBe('2026-07-04T18:45:00.000Z')
  })
})
