// @vitest-environment nuxt
import { describe, it, expect, vi, beforeEach } from 'vitest'
import { mountSuspended } from '@nuxt/test-utils/runtime'
import ChatWidget from '~/components/ChatWidget.vue'

function sseResponse(body: string, status = 200): Response {
  const stream = new ReadableStream({
    start(controller) {
      controller.enqueue(new TextEncoder().encode(body))
      controller.close()
    },
  })
  return new Response(stream, { status })
}

function statusResponse(enabled: boolean): Response {
  return new Response(JSON.stringify({ enabled }), {
    status: 200,
    headers: { 'content-type': 'application/json' },
  })
}

// URL-aware fetch stub: /chat/status gates whether the widget renders; /chat/ask is the answer.
function stubFetch(opts: { enabled?: boolean; ask?: () => Response; statusThrows?: boolean } = {}) {
  const fn = vi.fn(async (url: string) => {
    if (String(url).endsWith('/chat/status')) {
      if (opts.statusThrows) throw new Error('unreachable')
      return statusResponse(opts.enabled ?? true)
    }
    return opts.ask ? opts.ask() : sseResponse('event: answer\ndata: {"response":"ok"}\n\n')
  })
  vi.stubGlobal('fetch', fn)
  return fn
}

const flush = () => new Promise((r) => setTimeout(r, 20))

describe('ChatWidget', () => {
  beforeEach(() => {
    vi.restoreAllMocks()
    stubFetch() // enabled by default
  })

  it('opens the panel from the launcher when enabled', async () => {
    const wrapper = await mountSuspended(ChatWidget)
    await flush() // let the /chat/status check resolve
    expect(wrapper.find('.chat-panel').exists()).toBe(false)
    await wrapper.find('.chat-launcher').trigger('click')
    expect(wrapper.find('.chat-panel').exists()).toBe(true)
    const powered = wrapper.find('.chat-powered')
    expect(powered.exists()).toBe(true)
    expect(powered.text()).toContain('powered by')
    expect(powered.find('img').attributes('alt')).toBe('DataLinks')
  })

  it('stays hidden when the backend reports chat disabled', async () => {
    stubFetch({ enabled: false })
    const wrapper = await mountSuspended(ChatWidget)
    await flush()
    expect(wrapper.find('.chat-launcher').exists()).toBe(false)
    expect(wrapper.find('.chat-widget').exists()).toBe(false)
  })

  it('stays hidden when the status check is unreachable (fail-closed)', async () => {
    stubFetch({ statusThrows: true })
    const wrapper = await mountSuspended(ChatWidget)
    await flush()
    expect(wrapper.find('.chat-launcher').exists()).toBe(false)
  })

  it('toggles maximize on the panel', async () => {
    const wrapper = await mountSuspended(ChatWidget)
    await flush()
    await wrapper.find('.chat-launcher').trigger('click')
    expect(wrapper.find('.chat-panel').classes()).not.toContain('maximized')
    await wrapper.find('button[aria-label="Maximize"]').trigger('click')
    expect(wrapper.find('.chat-panel').classes()).toContain('maximized')
    await wrapper.find('button[aria-label="Restore"]').trigger('click')
    expect(wrapper.find('.chat-panel').classes()).not.toContain('maximized')
  })

  it('shows the question and the streamed answer', async () => {
    stubFetch({
      ask: () => sseResponse('event: answer\ndata: {"response":"Artur Laskowski speaks about Kafka"}\n\n'),
    })
    const wrapper = await mountSuspended(ChatWidget)
    await flush()
    await wrapper.find('.chat-launcher').trigger('click')
    await wrapper.find('input').setValue('who speaks about kafka?')
    await wrapper.find('form').trigger('submit')
    await flush()

    const text = wrapper.text()
    expect(text).toContain('who speaks about kafka?')
    expect(text).toContain('Artur Laskowski speaks about Kafka')
  })

  it('renders the assistant answer as markdown', async () => {
    stubFetch({
      ask: () => sseResponse('event: answer\ndata: {"response":"**Ścieżka kariery** for deweloperów"}\n\n'),
    })
    const wrapper = await mountSuspended(ChatWidget)
    await flush()
    await wrapper.find('.chat-launcher').trigger('click')
    await wrapper.find('input').setValue('kariera?')
    await wrapper.find('form').trigger('submit')
    await flush()

    const bubble = wrapper.find('.chat-bubble.markdown')
    expect(bubble.html()).toContain('<strong>Ścieżka kariery</strong>')
    expect(bubble.text()).toContain('deweloperów')
  })

  it('surfaces a friendly message when rate limited', async () => {
    stubFetch({ ask: () => sseResponse('', 429) })
    const wrapper = await mountSuspended(ChatWidget)
    await flush()
    await wrapper.find('.chat-launcher').trigger('click')
    await wrapper.find('input').setValue('hi')
    await wrapper.find('form').trigger('submit')
    await flush()

    expect(wrapper.find('.chat-error').text()).toContain('Too many questions')
  })
})
