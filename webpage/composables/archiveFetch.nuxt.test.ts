// @vitest-environment nuxt
import { describe, it, expect, vi, beforeEach } from 'vitest'
import { defineComponent } from 'vue'
import { mockNuxtImport, mountSuspended } from '@nuxt/test-utils/runtime'
import { useArchiveFetch } from '~/composables/useAPIFetch'

// Stands in for a server render: the request event it answers and the status it gets.
const render = vi.hoisted(() => ({ event: { path: '/' }, setResponseStatus: vi.fn() }))
mockNuxtImport('useRequestEvent', () => () => render.event)
mockNuxtImport('setResponseStatus', () => render.setResponseStatus)

// Renders a component that loads `path` from an archive server that answers every request with `answer`.
async function renderFeed(path: string, answer: () => Promise<Response>) {
  const archive = $fetch.create({}, { fetch: answer })
  await mountSuspended(defineComponent({
    async setup() {
      await useArchiveFetch(path, { $fetch: archive })
      return () => null
    },
  }))
}

describe('an archive fetch during a server render', () => {
  beforeEach(() => { render.setResponseStatus.mockClear() })

  it('answers 503 when the archive answers a server error, so the empty page is not cached', async () => {
    await renderFeed('/failing.json', async () => new Response('', { status: 502 }))
    expect(render.setResponseStatus).toHaveBeenCalledWith(render.event, 503)
  })

  it('answers 503 when the archive cannot be reached', async () => {
    await renderFeed('/unreachable.json', () => Promise.reject(new TypeError('fetch failed')))
    expect(render.setResponseStatus).toHaveBeenCalledWith(render.event, 503)
  })

  it('keeps the page status when an optional feed is not published yet (404)', async () => {
    await renderFeed('/agenda/day-2.json', async () => new Response('', { status: 404 }))
    expect(render.setResponseStatus).not.toHaveBeenCalled()
  })

  it('keeps the page status when a fetch is cancelled by a newer one of the same feed', async () => {
    // e.g. the banner and the timer both read edition.json: the second read cancels the first while it is still running
    const archive = $fetch.create({}, {
      fetch: (_: RequestInfo, init?: RequestInit) => new Promise<Response>((resolve, reject) => {
        init?.signal?.addEventListener('abort', () => reject(init.signal!.reason))
        setTimeout(() => resolve(Response.json({ year: 2026 })), 10)
      }),
    })
    await mountSuspended(defineComponent({
      async setup() {
        await Promise.all([1, 2].map(() => useArchiveFetch('/shared.json', { key: 'shared', $fetch: archive })))
        return () => null
      },
    }))
    expect(render.setResponseStatus).not.toHaveBeenCalled()
  })

  it('keeps the page status when the feed loads', async () => {
    await renderFeed('/edition.json', async () => Response.json({ year: 2026 }))
    expect(render.setResponseStatus).not.toHaveBeenCalled()
  })
})
