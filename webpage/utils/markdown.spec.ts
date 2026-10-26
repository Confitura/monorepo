import { describe, expect, it } from 'vitest'
import { plainText, renderMarkdown } from './markdown'

describe('renderMarkdown', () => {
  it('renders Markdown to HTML', () => {
    expect(renderMarkdown('Buy a **ticket**.')).toContain('<strong>ticket</strong>')
  })

  it('leaves an HTML body unchanged', () => {
    const html = '<p>Open: <a href="https://x.pl/?a=1&amp;b=2">tickets</a></p>\n<ul>\n<li>one</li>\n</ul>'
    expect(renderMarkdown(html).trim()).toBe(html)
  })

  it('keeps a seeded link opening in a new tab', () => {
    const html = '<p><a href="https://x.pl" target="_blank" rel="noopener">tickets</a></p>'
    expect(renderMarkdown(html).trim()).toBe(html)
  })

  it('drops event handlers and javascript: links', () => {
    expect(renderMarkdown('<img src="x.png" onerror="alert(1)">').trim()).toBe('<img src="x.png">')
    expect(renderMarkdown('[x](javascript:alert(1))').trim()).toBe('<p><a>x</a></p>')
  })

  it('renders a missing body as nothing', () => {
    expect(renderMarkdown(null)).toBe('')
  })
})

describe('plainText', () => {
  it('drops the markup and decodes entities', () => {
    expect(plainText('<p>Tickets &amp; <strong>talks</strong></p>\n<p>are here</p>')).toBe('Tickets & talks are here')
    expect(plainText('## Hello\n\nSee *you* there')).toBe('Hello See you there')
  })

  it('never returns markup, even from an unclosed tag or escaped angle brackets', () => {
    expect(plainText('Hi <script')).toBe('Hi script')
    expect(plainText('Use &lt;script&gt;alert(1)&lt;/script&gt; here')).toBe('Use scriptalert(1)/script here')
  })

  it('cuts a long body at a word boundary with an ellipsis', () => {
    const text = plainText('word '.repeat(100), 20)
    expect(text).toBe('word word word word…')
  })
})
