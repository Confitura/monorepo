// @vitest-environment nuxt
import { describe, it, expect, vi } from 'vitest'
import { mountSuspended } from '@nuxt/test-utils/runtime'

// Mock the archive/api fetch composable used by pages and PageFragment
vi.mock('~/composables/useAPIFetch', async () => {
  const { ref } = await import('vue')
  const createFetch = () => {
    const data = ref(null)
    const result = { data, pending: ref(false), error: ref(null), refresh: vi.fn(), execute: vi.fn() }
    return Object.assign(Promise.resolve(result), result)
  }
  return {
    useAPIFetch: vi.fn(createFetch),
    useArchiveFetch: vi.fn(createFetch),
  }
})

// Mock Google Maps components used by the venue page
vi.mock('vue3-google-map', () => ({
  GoogleMap: { template: '<div class="google-map-stub" />' },
  AdvancedMarker: { template: '<div><slot name="content" /></div>' },
}))

import IndexPage from '~/pages/index.vue'
import AboutPage from '~/pages/about.vue'
import FaqPage from '~/pages/faq.vue'
import NewsPage from '~/pages/news.vue'
import PresentationsPage from '~/pages/presentations.vue'
import PrivacyPolicyPage from '~/pages/privacy-policy.vue'
import SpoinaPage from '~/pages/spoina.vue'
import TicketsPage from '~/pages/tickets.vue'
import VenuePage from '~/pages/venue.vue'
import WorkshopsPage from '~/pages/workshops.vue'
import SlugPage from '~/pages/[slug].vue'
import PartnersIndexPage from '~/pages/partners/index.vue'
import PartnersDetailPage from '~/pages/partners/[id].vue'
import SpeakersIndexPage from '~/pages/speakers/index.vue'
import SpeakersDetailPage from '~/pages/speakers/[id].vue'
import SchedulePage from '~/pages/schedule/[[dayId]].vue'
import ScheduleWorkshopsPage from '~/pages/schedule/workshops/[[dayId]].vue'

describe('pages render without errors', () => {
  it('renders the home page', async () => {
    const wrapper = await mountSuspended(IndexPage)
    expect(wrapper.find('.home__container').exists()).toBe(true)
  })

  it('renders the about page', async () => {
    const wrapper = await mountSuspended(AboutPage)
    expect(wrapper.find('.about__page').exists()).toBe(true)
  })

  it('renders the faq page', async () => {
    const wrapper = await mountSuspended(FaqPage)
    expect(wrapper.html()).toBeDefined()
  })

  it('renders the news page', async () => {
    // news page uses v-if="content" — with mocked null data the container is hidden,
    // but the component itself should mount without throwing
    const wrapper = await mountSuspended(NewsPage)
    expect(wrapper.html()).toBeDefined()
  })

  it('renders the presentations page', async () => {
    const wrapper = await mountSuspended(PresentationsPage)
    expect(wrapper.find('.presentations').exists()).toBe(true)
  })

  it('renders the privacy policy page', async () => {
    const wrapper = await mountSuspended(PrivacyPolicyPage)
    expect(wrapper.html()).toBeDefined()
  })

  it('renders the spoina page', async () => {
    const wrapper = await mountSuspended(SpoinaPage)
    expect(wrapper.html()).toBeDefined()
  })

  it('renders the tickets page', async () => {
    const wrapper = await mountSuspended(TicketsPage)
    expect(wrapper.find('.tickets').exists()).toBe(true)
  })

  it('renders the venue page', async () => {
    const wrapper = await mountSuspended(VenuePage)
    expect(wrapper.find('.venue__page').exists()).toBe(true)
  })

  it('renders the workshops page', async () => {
    const wrapper = await mountSuspended(WorkshopsPage)
    expect(wrapper.find('.workshops').exists()).toBe(true)
  })

  it('renders the catch-all slug page', async () => {
    const wrapper = await mountSuspended(SlugPage, {
      route: '/some-unknown-page',
    })
    expect(wrapper.html()).toBeDefined()
  })

  it('renders the partners listing page', async () => {
    const wrapper = await mountSuspended(PartnersIndexPage)
    expect(wrapper.find('.partners').exists()).toBe(true)
  })

  it('renders the partner detail page', async () => {
    const wrapper = await mountSuspended(PartnersDetailPage, {
      route: { params: { id: 'join-us' } },
    })
    expect(wrapper.find('.partners').exists()).toBe(true)
  })

  it('renders the speakers listing page', async () => {
    const wrapper = await mountSuspended(SpeakersIndexPage)
    expect(wrapper.find('.speakers').exists()).toBe(true)
  })

  it('renders the speaker detail page', async () => {
    const wrapper = await mountSuspended(SpeakersDetailPage, {
      route: { params: { id: '123' } },
    })
    expect(wrapper.find('.speaker').exists()).toBe(true)
  })

  it('renders the schedule page (default day)', async () => {
    const wrapper = await mountSuspended(SchedulePage)
    expect(wrapper.find('.agendaPage').exists()).toBe(true)
  })

  it('renders the schedule page for day-2', async () => {
    const wrapper = await mountSuspended(SchedulePage, {
      route: { params: { dayId: 'day-2' } },
    })
    expect(wrapper.find('.agendaPage').exists()).toBe(true)
  })

  it('renders the schedule workshops page (default day)', async () => {
    const wrapper = await mountSuspended(ScheduleWorkshopsPage)
    expect(wrapper.find('.agendaPage').exists()).toBe(true)
  })

  it('renders the schedule workshops page for day-2', async () => {
    const wrapper = await mountSuspended(ScheduleWorkshopsPage, {
      route: { params: { dayId: 'day-2' } },
    })
    expect(wrapper.find('.agendaPage').exists()).toBe(true)
  })
})
