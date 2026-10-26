// @vitest-environment nuxt
import { describe, it, expect } from 'vitest'
import { mountSuspended } from '@nuxt/test-utils/runtime'
import PresentationBox from '~/components/presentation/Box.vue'

const TALK = { id: 'p1', title: 'A talk', speakers: [{ id: 's1', name: 'Jane Speaker' }], language: 'en', level: 'basic', tags: [] }

describe('the talk card', () => {
  it('renders a talk that has no description', async () => {
    const wrapper = await mountSuspended(PresentationBox, { props: { presentation: { ...TALK, description: null } } })
    expect(wrapper.find('.presentationBox__title').text()).toBe('A talk')
    expect(wrapper.find('.presentationBox__description').text()).toBe('')
  })

  it('renders the description as Markdown without script-capable HTML or javascript: links', async () => {
    const description = 'A **great** talk.\n\n<img src="x.png" onerror="alert(1)">\n\n[x](javascript:alert(1)) and [docs](https://x.pl)'
    const wrapper = await mountSuspended(PresentationBox, { props: { presentation: { ...TALK, description } } })
    const rendered = wrapper.find('.presentationBox__description')
    expect(rendered.find('strong').text()).toBe('great')
    expect(rendered.find('img').attributes()).toEqual({ src: 'x.png' })
    expect(rendered.findAll('a').map((a) => a.attributes('href'))).toEqual([undefined, 'https://x.pl'])
    expect(rendered.html()).not.toContain('javascript:')
  })
})
