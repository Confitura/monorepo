import { marked } from 'marked'

// News bodies and FAQ answers are Markdown; the seeded entries are HTML, which Markdown passes through.
export function renderMarkdown(source: string): string {
  return marked.parse(source ?? '', { async: false })
}

// The start of a Markdown body as plain text, for share-card descriptions.
export function plainText(source: string, maxLength = 200): string {
  const text = renderMarkdown(source)
    .replace(/<\/(p|h\d|li|div)>|<br\s*\/?>/g, ' ')
    .replace(/<[^>]*>/g, '')
    .replace(/&nbsp;/g, ' ')
    .replace(/&lt;/g, '<')
    .replace(/&gt;/g, '>')
    .replace(/&quot;/g, '"')
    .replace(/&#39;/g, "'")
    .replace(/&amp;/g, '&')
    .replace(/\s+/g, ' ')
    .trim()
  if (text.length <= maxLength) return text
  return text.slice(0, maxLength).replace(/\s*\S*$/, '') + '…'
}
