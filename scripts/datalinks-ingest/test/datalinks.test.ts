import { describe, it, expect, vi, beforeEach, afterEach } from 'vitest'
import { DatalinksClient } from '../src/datalinks'

const cfg = {
  baseUrl: 'https://api.example.test/api/v1',
  token: 'tok',
  username: 'confitura',
  namespace: 'confitura-2026',
}

function jsonResponse(body: unknown, status = 200): Response {
  return new Response(JSON.stringify(body), {
    status,
    headers: { 'content-type': 'application/json' },
  })
}

describe('DatalinksClient', () => {
  let fetchMock: ReturnType<typeof vi.fn>

  beforeEach(() => {
    fetchMock = vi.fn(async () => jsonResponse({ indexed: 0 }))
    vi.stubGlobal('fetch', fetchMock)
  })

  afterEach(() => {
    vi.unstubAllGlobals()
  })

  it('ingests without link discovery or curation', async () => {
    fetchMock.mockResolvedValueOnce(jsonResponse({ indexed: 3 }))
    const client = new DatalinksClient(cfg)

    const indexed = await client.ingest('talks', [{ id: 'a' }], 'desc')

    expect(indexed).toBe(3)
    const [url, init] = fetchMock.mock.calls[0]
    expect(url).toBe(`${cfg.baseUrl}/ingest/${cfg.namespace}/talks`)
    const body = JSON.parse((init as RequestInit).body as string)
    expect(body).toEqual({ data: [{ id: 'a' }], dataDescription: 'desc', curate: false })
    // no auto link discovery
    expect(body).not.toHaveProperty('link')
  })

  it('adds a directional link with the configured owner and namespace', async () => {
    fetchMock.mockResolvedValueOnce(jsonResponse({}, 201))
    const client = new DatalinksClient(cfg)

    await client.addLink(
      { dataset: 'agenda', columnName: 'talkId' },
      { dataset: 'talks', columnName: 'id' },
    )

    const [url, init] = fetchMock.mock.calls[0]
    expect(url).toBe(`${cfg.baseUrl}/links/add`)
    const body = JSON.parse((init as RequestInit).body as string)
    expect(body).toEqual({
      from: { username: 'confitura', namespace: 'confitura-2026', dataset: 'agenda', columnName: 'talkId' },
      to: { username: 'confitura', namespace: 'confitura-2026', dataset: 'talks', columnName: 'id' },
      matchType: 'ExactMatch',
    })
  })

  it('throws with the response text when a call fails', async () => {
    fetchMock.mockResolvedValueOnce(new Response('boom', { status: 500, statusText: 'Server Error' }))
    const client = new DatalinksClient(cfg)

    await expect(
      client.addLink({ dataset: 'agenda', columnName: 'talkId' }, { dataset: 'talks', columnName: 'id' }),
    ).rejects.toThrow(/links\/add -> 500/)
  })
})
