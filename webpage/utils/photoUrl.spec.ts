import { describe, expect, it } from 'vitest'
import { photoUrl } from './photoUrl'

describe('photoUrl', () => {
  it('returns empty string for missing src', () => {
    expect(photoUrl(undefined, 90)).toBe('')
    expect(photoUrl(null, 90)).toBe('')
    expect(photoUrl('', 90)).toBe('')
  })

  it('adds size param to uploaded photos and rewrites the resources host', () => {
    expect(photoUrl('https://api.confitura.pl/api/resources/photos/a.png', 120))
      .toBe('https://resources.confitura.pl/photos/a.png?size=120')
    expect(photoUrl('https://api.confitura.pl/api/resources/photos/a.png?x=1', 120))
      .toBe('https://resources.confitura.pl/photos/a.png?x=1&size=120')
  })

  it('appends GitHub avatar size param', () => {
    expect(photoUrl('https://avatars.githubusercontent.com/u/1?v=4', 64))
      .toBe('https://avatars.githubusercontent.com/u/1?v=4&s=64')
  })

  it('replaces Gravatar size param', () => {
    expect(photoUrl('https://www.gravatar.com/avatar/abc?s=300', 48))
      .toBe('https://www.gravatar.com/avatar/abc?s=48')
  })

  it('does not treat the host name as a bare substring (anti-spoof)', () => {
    // host is evil.com — githubusercontent.com only appears in the path
    expect(photoUrl('https://evil.com/githubusercontent.com/x', 64))
      .toBe('https://evil.com/githubusercontent.com/x')
    // host is githubusercontent.com.evil.com — a suffix attacker domain
    expect(photoUrl('https://githubusercontent.com.evil.com/x', 64))
      .toBe('https://githubusercontent.com.evil.com/x')
  })

  it('returns unknown or non-absolute URLs unchanged', () => {
    expect(photoUrl('https://example.com/pic.png', 64)).toBe('https://example.com/pic.png')
    expect(photoUrl('/relative/pic.png', 64)).toBe('/relative/pic.png')
  })
})
