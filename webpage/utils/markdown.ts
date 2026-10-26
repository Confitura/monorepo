import DOMPurify from 'isomorphic-dompurify'
import { marked } from 'marked'

// Talk descriptions, news bodies, FAQ answers and partner descriptions are Markdown; the seeded
// entries are HTML, which Markdown passes through. Speakers write talk descriptions, so the result
// is sanitised (here, so it holds during server-side rendering too) before it reaches v-html.
// `target` is kept so the seeded links that open in a new tab still do.
export function renderMarkdown(source: string | null): string {
  return DOMPurify.sanitize(marked.parse(source ?? '', { async: false }), { ADD_ATTR: ['target'] })
}

// The start of a Markdown body as plain text, for share-card descriptions.
export function plainText(source: string, maxLength = 200): string {
  let text = renderMarkdown(source).replace(/<\/(p|h\d|li|div)>|<br\s*\/?>/g, ' ')
  // Strip tags until none are left, then drop any stray angle bracket (an unclosed tag, or a
  // decoded &lt;/&gt;), so the description can never carry markup.
  let previous
  do {
    previous = text
    text = text.replace(/<[^>]*>/g, '')
  } while (text !== previous)
  text = text
    .replace(/&nbsp;/g, ' ')
    .replace(/&lt;/g, '<')
    .replace(/&gt;/g, '>')
    .replace(/&quot;/g, '"')
    .replace(/&#39;/g, "'")
    .replace(/&amp;/g, '&')
    .replace(/[<>]/g, '')
    .replace(/\s+/g, ' ')
    .trim()
  if (text.length <= maxLength) return text
  return text.slice(0, maxLength).replace(/\s*\S*$/, '') + '…'
}
