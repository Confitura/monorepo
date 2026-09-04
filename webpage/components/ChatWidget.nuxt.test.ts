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

const flush = () => new Promise((r) => setTimeout(r, 20))

describe('ChatWidget', () => {
  beforeEach(() => {
    vi.restoreAllMocks()
  })

  it('opens the panel from the launcher', async () => {
    const wrapper = await mountSuspended(ChatWidget)
    expect(wrapper.find('.chat-panel').exists()).toBe(false)
    await wrapper.find('.chat-launcher').trigger('click')
    expect(wrapper.find('.chat-panel').exists()).toBe(true)
    // "powered by DataLinks" logo in the header
    const powered = wrapper.find('.chat-powered')
    expect(powered.exists()).toBe(true)
    expect(powered.text()).toContain('powered by')
    expect(powered.find('img').attributes('alt')).toBe('DataLinks')
  })

  it('toggles maximize on the panel', async () => {
    const wrapper = await mountSuspended(ChatWidget)
    await wrapper.find('.chat-launcher').trigger('click')
    expect(wrapper.find('.chat-panel').classes()).not.toContain('maximized')

    await wrapper.find('button[aria-label="Maximize"]').trigger('click')
    expect(wrapper.find('.chat-panel').classes()).toContain('maximized')

    await wrapper.find('button[aria-label="Restore"]').trigger('click')
    expect(wrapper.find('.chat-panel').classes()).not.toContain('maximized')
  })

  it('shows the question and the streamed answer', async () => {
    vi.stubGlobal(
      'fetch',
      vi.fn(async () =>
        sseResponse('event: answer\ndata: {"response":"Artur Laskowski speaks about Kafka"}\n\n'),
      ),
    )

    const wrapper = await mountSuspended(ChatWidget)
    await wrapper.find('.chat-launcher').trigger('click')
    await wrapper.find('input').setValue('who speaks about kafka?')
    await wrapper.find('form').trigger('submit')
    await flush()

    const text = wrapper.text()
    expect(text).toContain('who speaks about kafka?')
    expect(text).toContain('Artur Laskowski speaks about Kafka')
  })

  it('renders the assistant answer as markdown', async () => {
    vi.stubGlobal(
      'fetch',
      vi.fn(async () =>
        sseResponse('event: answer\ndata: {"response":"**Ścieżka kariery** for deweloperów"}\n\n'),
      ),
    )

    const wrapper = await mountSuspended(ChatWidget)
    await wrapper.find('.chat-launcher').trigger('click')
    await wrapper.find('input').setValue('kariera?')
    await wrapper.find('form').trigger('submit')
    await flush()

    const bubble = wrapper.find('.chat-bubble.markdown')
    // bold rendered to <strong>, and Polish characters preserved
    expect(bubble.html()).toContain('<strong>Ścieżka kariery</strong>')
    expect(bubble.text()).toContain('deweloperów')
  })

  it('surfaces a friendly message when rate limited', async () => {
    vi.stubGlobal('fetch', vi.fn(async () => sseResponse('', 429)))

    const wrapper = await mountSuspended(ChatWidget)
    await wrapper.find('.chat-launcher').trigger('click')
    await wrapper.find('input').setValue('hi')
    await wrapper.find('form').trigger('submit')
    await flush()

    expect(wrapper.find('.chat-error').text()).toContain('Too many questions')
  })
})
