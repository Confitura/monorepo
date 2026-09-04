// Minimal Datalinks REST client for ingestion.
// Contract taken from https://api.dev.datalinks.com/openapi (server base /api/v1).

export interface DatalinksConfig {
  baseUrl: string
  token: string
  username: string
  namespace: string
}

export class DatalinksClient {
  private readonly baseUrl: string

  constructor(private readonly cfg: DatalinksConfig) {
    this.baseUrl = cfg.baseUrl.replace(/\/$/, '')
  }

  private authHeaders(json = true): Record<string, string> {
    const headers: Record<string, string> = { Authorization: `Bearer ${this.cfg.token}` }
    if (json) headers['Content-Type'] = 'application/json'
    return headers
  }

  private async call(method: string, path: string, body?: unknown): Promise<Response> {
    const res = await fetch(`${this.baseUrl}${path}`, {
      method,
      headers: this.authHeaders(),
      body: body === undefined ? undefined : JSON.stringify(body),
    })
    if (!res.ok) {
      const text = await res.text().catch(() => '')
      throw new Error(`${method} ${path} -> ${res.status} ${res.statusText} ${text}`.trim())
    }
    return res
  }

  // POST /ingest/new/{namespace}/{datasetName}
  async createDataset(datasetName: string, dataDescription: string): Promise<void> {
    await this.call('POST', `/ingest/new/${this.cfg.namespace}/${datasetName}`, {
      dataDescription,
    })
  }

  // POST /data/{username}/{namespace}/{datasetName}/clear
  async clearDataset(datasetName: string): Promise<void> {
    const res = await fetch(
      `${this.baseUrl}/data/${this.cfg.username}/${this.cfg.namespace}/${datasetName}/clear`,
      { method: 'POST', headers: this.authHeaders(false) },
    )
    // 404 = dataset doesn't exist yet; nothing to clear.
    if (!res.ok && res.status !== 404) {
      throw new Error(`clear ${datasetName} -> ${res.status} ${res.statusText}`)
    }
  }

  // POST /ingest/{namespace}/{datasetName} -> { indexed }
  async ingest(
    datasetName: string,
    data: object[],
    dataDescription: string,
  ): Promise<number> {
    const res = await this.call('POST', `/ingest/${this.cfg.namespace}/${datasetName}`, {
      data,
      dataDescription,
      curate: true,
    })
    const json = (await res.json()) as { indexed: number }
    return json.indexed
  }
}
