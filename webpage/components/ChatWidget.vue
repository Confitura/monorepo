<template>
  <div class="chat-widget">
    <button
      v-if="!open"
      class="chat-launcher"
      type="button"
      aria-label="Open conference assistant"
      @click="open = true"
    >
      <span aria-hidden="true">💬</span> Ask
    </button>

    <section v-else class="chat-panel" aria-label="Conference assistant">
      <header class="chat-header">
        <div class="chat-header-main">
          <span class="chat-title">Conference assistant</span>
          <a
            class="chat-powered"
            href="https://datalinks.com"
            target="_blank"
            rel="noopener noreferrer"
          >
            <span>powered by</span>
            <img :src="datalinksLogo" alt="DataLinks" class="chat-powered-logo" />
          </a>
        </div>
        <button type="button" class="chat-close" aria-label="Close" @click="open = false">×</button>
      </header>

      <div ref="log" class="chat-log">
        <p v-if="messages.length === 0" class="chat-hint">
          Ask about talks, speakers, the agenda, the venue or FAQ.
        </p>
        <div
          v-for="(m, i) in messages"
          :key="i"
          class="chat-msg"
          :class="m.role"
        >
          <!-- eslint-disable-next-line vue/no-v-html -- source is HTML-escaped before marked -->
          <span v-if="m.role === 'assistant'" class="chat-bubble markdown" v-html="renderAssistant(m)"></span>
          <span v-else class="chat-bubble">{{ m.text }}</span>
        </div>
        <p v-if="error" class="chat-error">{{ error }}</p>
      </div>

      <form class="chat-input" @submit.prevent="send">
        <input
          v-model="question"
          type="text"
          :maxlength="maxLength"
          placeholder="Type your question…"
          aria-label="Your question"
          :disabled="pending"
        />
        <button type="submit" :disabled="pending || !question.trim()">Send</button>
      </form>
    </section>
  </div>
</template>

<script setup lang="ts">
import { ref, nextTick } from 'vue'
import { marked } from 'marked'
import datalinksLogo from '~/assets/partners/2025/datalinks.svg'

interface Message {
  role: 'user' | 'assistant'
  text: string
}

// Escape raw HTML so model output can't inject markup; marked then produces
// only the safe structural HTML from the markdown itself.
function escapeHtml(s: string): string {
  return s.replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;')
}

function renderAssistant(m: Message): string {
  if (!m.text) return pending.value ? '…' : ''
  return marked.parse(escapeHtml(m.text), { async: false, breaks: true }) as string
}

const config = useRuntimeConfig()
const apiBase = (config.public.chatApiBase as string).replace(/\/$/, '')
const maxLength = 500

const open = ref(false)
const question = ref('')
const messages = ref<Message[]>([])
const pending = ref(false)
const error = ref('')
const conversationId = ref<string | null>(null)
const log = ref<HTMLElement | null>(null)

async function scrollDown() {
  await nextTick()
  if (log.value) log.value.scrollTop = log.value.scrollHeight
}

async function send() {
  const text = question.value.trim()
  if (!text || pending.value) return

  messages.value.push({ role: 'user', text })
  const assistant: Message = { role: 'assistant', text: '' }
  messages.value.push(assistant)
  question.value = ''
  pending.value = true
  error.value = ''
  await scrollDown()

  try {
    const headers: Record<string, string> = {
      'Content-Type': 'application/json',
      Accept: 'text/event-stream',
    }
    const secret = config.public.chatSecret as string
    if (secret) headers['X-Chat-Secret'] = secret

    const res = await fetch(`${apiBase}/chat/ask`, {
      method: 'POST',
      headers,
      body: JSON.stringify({
        question: text,
        conversationId: conversationId.value ?? undefined,
      }),
    })

    if (!res.ok) {
      error.value =
        res.status === 429
          ? 'Too many questions — please wait a moment.'
          : res.status === 503
            ? 'The assistant is unavailable right now.'
            : res.status === 401
              ? 'The assistant is not available.'
              : 'Something went wrong. Please try again.'
      messages.value.pop()
      return
    }

    await readStream(res, assistant)
  } catch {
    error.value = 'Could not reach the assistant.'
    messages.value.pop()
  } finally {
    pending.value = false
    await scrollDown()
  }
}

async function readStream(res: Response, assistant: Message) {
  const reader = res.body?.getReader()
  if (!reader) return
  const decoder = new TextDecoder()
  let buffer = ''

  for (;;) {
    const { value, done } = await reader.read()
    if (done) break
    buffer += decoder.decode(value, { stream: true })

    let sep: number
    // Events are separated by a blank line; process each complete one.
    while ((sep = buffer.indexOf('\n\n')) !== -1) {
      const raw = buffer.slice(0, sep)
      buffer = buffer.slice(sep + 2)
      handleEvent(raw, assistant)
      await scrollDown()
    }
  }
}

function handleEvent(raw: string, assistant: Message) {
  let event = 'message'
  const dataLines: string[] = []
  for (const line of raw.split('\n')) {
    if (line.startsWith('event:')) event = line.slice(6).trim()
    else if (line.startsWith('data:')) dataLines.push(line.slice(5).trim())
  }
  const data = dataLines.join('\n')
  if (!data) return

  let parsed: Record<string, unknown> = {}
  try {
    parsed = JSON.parse(data)
  } catch {
    return
  }

  // Continue the same Datalinks conversation on follow-ups.
  if (typeof parsed.conversationId === 'string') conversationId.value = parsed.conversationId

  if (event === 'answer' && typeof parsed.response === 'string') {
    assistant.text = parsed.response
  } else if (event === 'error') {
    error.value = 'The assistant could not answer that.'
  }
}
</script>

<style scoped lang="scss">
.chat-widget {
  position: fixed;
  right: 1rem;
  bottom: 1rem;
  z-index: 1000;
  font-size: 14px;
}

.chat-launcher {
  background: #d81b60;
  color: #fff;
  border: none;
  border-radius: 999px;
  padding: 0.75rem 1.25rem;
  font-weight: 600;
  cursor: pointer;
  box-shadow: 0 4px 14px rgba(0, 0, 0, 0.25);
}

.chat-panel {
  display: flex;
  flex-direction: column;
  width: min(360px, calc(100vw - 2rem));
  height: min(520px, calc(100vh - 2rem));
  background: #fff;
  color: #1a1a1a;
  border-radius: 12px;
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.3);
  overflow: hidden;
}

.chat-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0.75rem 1rem;
  background: #d81b60;
  color: #fff;
}

.chat-header-main {
  display: flex;
  flex-direction: column;
  gap: 3px;
}

.chat-title {
  font-weight: 600;
}

.chat-powered {
  display: inline-flex;
  align-items: center;
  gap: 5px;
  font-size: 10px;
  font-weight: 400;
  text-transform: uppercase;
  letter-spacing: 0.03em;
  color: rgba(255, 255, 255, 0.85);
  text-decoration: none;
}

.chat-powered-logo {
  height: 13px;
  width: auto;
  background: #fff;
  border-radius: 3px;
  padding: 2px 4px;
}

.chat-close {
  background: none;
  border: none;
  color: #fff;
  font-size: 1.4rem;
  line-height: 1;
  cursor: pointer;
}

.chat-log {
  flex: 1;
  overflow-y: auto;
  padding: 1rem;
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.chat-hint {
  color: #666;
  margin: 0;
}

.chat-msg {
  display: flex;
}

.chat-msg.user {
  justify-content: flex-end;
}

.chat-bubble {
  display: inline-block;
  padding: 0.5rem 0.75rem;
  border-radius: 12px;
  max-width: 85%;
  white-space: pre-wrap;
  word-break: break-word;
}

.chat-msg.user .chat-bubble {
  background: #d81b60;
  color: #fff;
}

.chat-msg.assistant .chat-bubble {
  background: #f0f0f0;
  color: #1a1a1a;
}

.chat-bubble.markdown {
  display: block;
  max-width: 100%;
}

.markdown :first-child {
  margin-top: 0;
}

.markdown :last-child {
  margin-bottom: 0;
}

.markdown p {
  margin: 0.4em 0;
}

.markdown ul,
.markdown ol {
  margin: 0.4em 0;
  padding-left: 1.2em;
}

.markdown a {
  color: #d81b60;
}

.markdown code {
  background: rgba(0, 0, 0, 0.06);
  padding: 0 0.25em;
  border-radius: 3px;
}

.markdown pre {
  background: rgba(0, 0, 0, 0.06);
  padding: 0.5em;
  border-radius: 6px;
  overflow-x: auto;
}

.markdown table {
  display: block;
  width: 100%;
  overflow-x: auto;
  border-collapse: collapse;
  font-size: 0.9em;
}

.markdown th,
.markdown td {
  border: 1px solid #ccc;
  padding: 0.3em 0.5em;
  text-align: left;
}

.chat-error {
  color: #c62828;
  margin: 0.25rem 0 0;
}

.chat-input {
  display: flex;
  gap: 0.5rem;
  padding: 0.75rem;
  border-top: 1px solid #eee;
}

.chat-input input {
  flex: 1;
  padding: 0.5rem 0.75rem;
  border: 1px solid #ccc;
  border-radius: 8px;
  font: inherit;
}

.chat-input button {
  background: #d81b60;
  color: #fff;
  border: none;
  border-radius: 8px;
  padding: 0 1rem;
  font-weight: 600;
  cursor: pointer;
}

.chat-input button:disabled {
  opacity: 0.5;
  cursor: default;
}
</style>
