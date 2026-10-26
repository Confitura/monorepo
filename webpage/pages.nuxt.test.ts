// @vitest-environment nuxt
import { describe, it, expect, vi, beforeEach, afterEach } from 'vitest'
import { mountSuspended } from '@nuxt/test-utils/runtime'

// Mock the archive/api fetch composable used by pages and PageFragment
const FAQ_ENTRIES = [
  { id: 'e1', category: 'Registration', question: 'How do I register?', answer: 'Buy a **ticket**.', displayOrder: 0, published: true },
  { id: 'e2', category: 'Venue', question: 'Where is it?', answer: 'At the venue.', displayOrder: 0, published: true },
]

const PARTNERS = [
  { id: 'uuid-1', slug: 'xtb', name: 'XTB', type: 'gold', www: 'https://xtb.com', logo: 'https://cdn/x.svg', description: 'x', orientation: 'horizontal', published: true },
  { id: 'uuid-2', slug: 'dpd', name: 'DPD', type: 'bronze', www: 'https://dpd.com', logo: 'https://cdn/d.svg', description: 'd', orientation: 'box', published: true },
]

// newest first, as the backend dumps it
const NEWS_ENTRIES = [
  { title: 'Agenda is live', slug: 'agenda-is-live', body: 'See the **agenda** & plan your day.\n\nMore soon.', publishedAt: '2026-09-01T10:00:00Z', cover: null },
  { title: 'Tickets', slug: 'tickets', body: '<p>Old <a href="https://x.pl">HTML</a> body</p>', publishedAt: '2026-08-20T19:12:42Z', cover: 'https://api.confitura.pl/api/resources/photos/n2.png' },
]
const NEWS = { latest: NEWS_ENTRIES[0], all: NEWS_ENTRIES }

// speaker '123' has one scheduled talk (p1) and one unscheduled talk (p2)
const SPEAKER = {
  id: '123', name: 'Jane Speaker', bio: 'Bio', photo: '',
  presentations: [
    { id: 'p1', name: 'Scheduled Talk', isWorkshop: false },
    { id: 'p2', name: 'Unscheduled Talk', isWorkshop: false },
  ],
}
const DAY1_AGENDA = { agendaEntries: [{ presentationId: 'p1' }] }

// one organizer/volunteer with a photo, one without (to exercise the placeholder)
const ADMINS = [
  { id: 'a1', name: 'With Photo', photo: 'https://cdn/a.jpg' },
  { id: 'a2', name: 'No Photo', photo: undefined },
]
const VOLUNTEERS = [
  { id: 'v1', name: 'Vol One', photo: 'https://cdn/v.jpg' },
  { id: 'v2', name: 'Vol Two', photo: undefined },
]

// edition.json as the backend dumps it; a test sets `edition` to null to simulate the file being missing
const EDITION = { year: 2026, name: 'Confitura 2026', conferenceStart: '2026-09-25T09:00:00+02:00', lastDay: '2026-09-26' }
const dumped = vi.hoisted(() => ({ edition: null as Record<string, unknown> | null }))
dumped.edition = EDITION

vi.mock('~/composables/useAPIFetch', async () => {
  const { ref } = await import('vue')
  const createFetch = (path?: string) => {
    const data = ref(
      path === '/faq/entries.json' ? FAQ_ENTRIES
      : path === '/edition.json' ? dumped.edition
      : path === '/news.json' ? NEWS
      : path === '/partners/list.json' ? PARTNERS
      : path === '/users/123/public.json' ? SPEAKER
      : path === '/agenda/day-1.json' ? DAY1_AGENDA
      : path === '/users/search/admins.json' ? ADMINS
      : path === '/users/search/volunteers.json' ? VOLUNTEERS
      : null,
    )
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

import { useArchiveFetch } from '~/composables/useAPIFetch'
import IndexPage from '~/pages/index.vue'
import CalendarPage from '~/pages/calendar.vue'
import WorkshopSignupPage from '~/pages/workshop-signup.vue'
import AboutPage from '~/pages/about.vue'
import FaqPage from '~/pages/faq.vue'
import NewsPage from '~/pages/news/index.vue'
import NewsEntryPage from '~/pages/news/[slug].vue'
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
import AgendaFlowPreviewPage from '~/pages/preview/agenda-flow.vue'
import TheTimer from '~/components/TheTimer.vue'
import HomePartners from '~/components/home/Partners.vue'

describe('pages render without errors', () => {
  it('renders the home page', async () => {
    const wrapper = await mountSuspended(IndexPage)
    expect(wrapper.find('.home__container').exists()).toBe(true)
  })

  it('renders the calendar page', async () => {
    const wrapper = await mountSuspended(CalendarPage)
    expect(wrapper.find('.calendar__page').exists()).toBe(true)
  })

  it('renders the workshop sign-up page', async () => {
    const wrapper = await mountSuspended(WorkshopSignupPage)
    expect(wrapper.find('.workshop-signup').exists()).toBe(true)
    expect(wrapper.find('img[src="/workshop-signup/image7.png"]').exists()).toBe(true)
  })

  it('renders the about page', async () => {
    const wrapper = await mountSuspended(AboutPage)
    expect(wrapper.find('.about__page').exists()).toBe(true)
  })

  it('shows a placeholder for organizers/volunteers missing a photo', async () => {
    const wrapper = await mountSuspended(AboutPage)
    // organizer (a2) without a photo -> brand-square placeholder; with a photo -> <img>
    expect(wrapper.find('.member__photo--placeholder').exists()).toBe(true)
    expect(wrapper.find('img.member__photo').exists()).toBe(true)
    // the volunteers grid renders its members (section is enabled)
    expect(wrapper.find('.usersGrid').exists()).toBe(true)
    expect(wrapper.findAll('.usersGrid .user').length).toBe(2)
  })

  it('renders the faq page from structured entries grouped by category', async () => {
    const wrapper = await mountSuspended(FaqPage)
    const questions = wrapper.find('.questions')
    expect(questions.exists()).toBe(true)
    // the two-column grid CSS targets `.questions section`, so the rendered
    // markdown must live inside a nested <section>
    expect(wrapper.find('.questions section').exists()).toBe(true)
    const html = questions.html()
    // categories as h2, questions as h3, answer markdown rendered
    expect(html).toContain('Registration')
    expect(html).toContain('Venue')
    expect(html).toContain('How do I register?')
    expect(html).toContain('<strong>ticket</strong>')
  })

  it('lists news with titles linking to the entry pages and bodies rendered as Markdown', async () => {
    const wrapper = await mountSuspended(NewsPage)
    const links = wrapper.findAll('.latest-news h2 a')
    expect(links.map((l) => l.attributes('href'))).toEqual(['/news/agenda-is-live', '/news/tickets'])
    expect(links[0].text()).toBe('Agenda is live')
    expect(wrapper.html()).toContain('<strong>agenda</strong>')
    expect(wrapper.html()).toContain('<a href="https://x.pl">HTML</a>')
  })

  it('shows a news entry cover above its body in the list, only for entries that have one', async () => {
    const wrapper = await mountSuspended(NewsPage)
    const entries = wrapper.findAll('.latest-news')
    expect(entries[0].find('.news-cover').exists()).toBe(false)
    const cover = entries[1].find('.news-cover')
    expect(cover.attributes('src')).toBe('https://resources.confitura.pl/photos/n2.png?size=1200')
    expect(cover.attributes('alt')).toBe('Tickets')
    expect(entries[1].html().indexOf('news-cover')).toBeLessThan(entries[1].html().indexOf('class="body"'))
  })

  it('links the home page banner to the newest news entry', async () => {
    const wrapper = await mountSuspended(IndexPage)
    const link = wrapper.find('.latest-news h2 a')
    expect(link.attributes('href')).toBe('/news/agenda-is-live')
    expect(wrapper.find('.latest-news .body').html()).toContain('<strong>agenda</strong>')
  })

  it('renders a news entry page with its title, day and Markdown body', async () => {
    const wrapper = await mountSuspended(NewsEntryPage, { route: '/news/agenda-is-live' })
    expect(wrapper.find('.news-entry h2').text()).toBe('Agenda is live')
    expect(wrapper.find('.news-entry__date').text()).toBe('September 1, 2026')
    expect(wrapper.find('.news-entry__body').html()).toContain('<strong>agenda</strong>')
  })

  it('sets the page title and share tags for a news entry', async () => {
    await mountSuspended(NewsEntryPage, { route: '/news/agenda-is-live' })
    const meta = (key: string) => document.head.querySelector(`meta[property="${key}"]`)?.getAttribute('content')
    await vi.waitFor(() => expect(document.title).toBe('Agenda is live — Confitura'))
    expect(meta('og:title')).toBe('Agenda is live')
    expect(meta('og:description')).toBe('See the agenda & plan your day. More soon.')
    expect(meta('og:type')).toBe('article')
    expect(meta('og:url')).toBe('https://confitura.pl/news/agenda-is-live')
    // No cover: the site-wide default share image from nuxt.config stays.
    expect(meta('og:image')).toBe('https://confitura.pl/og-image.png')
  })

  it('shows the cover above the body on the news entry page, and nothing for an entry without one', async () => {
    const without = await mountSuspended(NewsEntryPage, { route: '/news/agenda-is-live' })
    expect(without.find('.news-cover').exists()).toBe(false)
    const withCover = await mountSuspended(NewsEntryPage, { route: '/news/tickets' })
    const cover = withCover.find('.news-entry .news-cover')
    expect(cover.attributes('src')).toBe('https://resources.confitura.pl/photos/n2.png?size=1200')
    expect(cover.attributes('alt')).toBe('Tickets')
    expect(withCover.find('article').html().indexOf('news-cover'))
      .toBeLessThan(withCover.find('article').html().indexOf('news-entry__body'))
  })

  it('shares the cover as the news entry image', async () => {
    await mountSuspended(NewsEntryPage, { route: '/news/tickets' })
    const content = (selector: string) => document.head.querySelector(selector)?.getAttribute('content')
    await vi.waitFor(() => expect(content('meta[property="og:image"]')).toBe('https://api.confitura.pl/api/resources/photos/n2.png'))
    expect(content('meta[name="twitter:image"]')).toBe('https://api.confitura.pl/api/resources/photos/n2.png')
    // the site-wide 1200x630 size tags don't describe the cover
    expect(document.head.querySelector('meta[property="og:image:width"]')).toBeNull()
  })

  it('answers 404 for an unknown news slug', async () => {
    await mountSuspended(NewsEntryPage, { route: '/news/nope' })
    expect(useError().value?.statusCode).toBe(404)
    clearError()
  })

  it('answers 503, not 404, when the news feed cannot be fetched', async () => {
    const data = ref(null)
    const failed = { data, pending: ref(false), error: ref(new Error('down')), refresh: vi.fn(), execute: vi.fn() }
    vi.mocked(useArchiveFetch).mockImplementationOnce((() => Object.assign(Promise.resolve(failed), failed)) as never)
    await mountSuspended(NewsEntryPage, { route: '/news/agenda-is-live' })
    expect(useError().value?.statusCode).toBe(503)
    clearError()
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

  it('renders the partners listing page from the backend dump, grouped by tier', async () => {
    const wrapper = await mountSuspended(PartnersIndexPage)
    expect(wrapper.find('.partners').exists()).toBe(true)
    // partners came from /partners/list.json, grouped by tier
    expect(wrapper.find('#gold').exists()).toBe(true)
    expect(wrapper.find('#bronze').exists()).toBe(true)
    expect(wrapper.html()).toContain('XTB')
    expect(wrapper.html()).toContain('DPD')
    expect(wrapper.find('a[href="/partners/xtb"]').exists()).toBe(true)
  })

  it('renders the partner detail page', async () => {
    const wrapper = await mountSuspended(PartnersDetailPage, {
      route: { params: { id: 'join-us' } },
    })
    expect(wrapper.find('.partners').exists()).toBe(true)
  })

  it('shows the partner type and name in the partner page header, without fetching a missing page', async () => {
    vi.mocked(useArchiveFetch).mockClear()
    const wrapper = await mountSuspended(PartnersDetailPage, {
      route: { params: { id: 'xtb' } },
    })
    expect(wrapper.find('h1').text()).toContain('gold partner')
    expect(wrapper.find('h1').text()).toContain('XTB')
    const paths = vi.mocked(useArchiveFetch).mock.calls.map(call => call[0])
    expect(paths).not.toContain('/pages/undefined.json')
  })

  describe('home partners carousel', () => {
    beforeEach(() => { vi.useFakeTimers({ toFake: ['setInterval', 'clearInterval'] }) })
    afterEach(() => { vi.useRealTimers() })

    it('links each partner by slug, like the partners list', async () => {
      const wrapper = await mountSuspended(HomePartners)
      const hrefs = wrapper.findAll('a.link').map(a => a.attributes('href'))
      expect(hrefs).toContain('/partners/xtb')
      expect(hrefs).not.toContain('/partners/uuid-1')
      wrapper.unmount()
    })

    it('leaves at most one timer after mounting and unmounting twice, and hover pauses it', async () => {
      const first = await mountSuspended(HomePartners)
      first.unmount()
      const second = await mountSuspended(HomePartners)
      expect(vi.getTimerCount()).toBe(1)
      await second.find('.type--active').trigger('mouseenter')
      expect(vi.getTimerCount()).toBe(0)
      await second.find('.type--active').trigger('mouseleave')
      expect(vi.getTimerCount()).toBe(1)
      second.unmount()
      expect(vi.getTimerCount()).toBe(0)
    })
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

  it('shows a calendar link only for the speaker\'s scheduled talk', async () => {
    const wrapper = await mountSuspended(SpeakersDetailPage, {
      route: { params: { id: '123' } },
    })
    const calendarLinks = wrapper.findAll('a.speaker__calendar')
    // p1 is scheduled (in day-1 agenda), p2 is not
    expect(calendarLinks.length).toBe(1)
    expect(calendarLinks[0].attributes('href')).toBe(
      'https://api.confitura.pl/api/agenda/ical/presentation/p1',
    )
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

  it('renders the agenda flow preview: four steps in order, split into your part and our part', async () => {
    const wrapper = await mountSuspended(AgendaFlowPreviewPage)
    expect(wrapper.find('.agenda-flow-preview').exists()).toBe(true)
    const titles = wrapper.findAll('.agenda-flow__step-title').map((t) => t.text())
    expect(titles).toEqual(['Call for Papers', 'Vote for Papers', 'Speaker preferences', 'The agenda'])
    const labels = wrapper.findAll('.agenda-flow__phase-label').map((l) => l.text())
    expect(labels).toEqual(['your part', 'our part'])
    // the second list continues the numbering, and each list is named by its phase label
    const lists = wrapper.findAll('ol.agenda-flow__steps')
    expect(lists[1].attributes('start')).toBe('3')
    const labelId = wrapper.find('.agenda-flow__phase-label').attributes('id')
    expect(lists[0].attributes('aria-labelledby')).toBe(labelId)
    expect(wrapper.find('abbr[title="Call for Papers"]').text()).toBe('C4P')
  })

  describe('with the Edition published as edition.json', () => {
    afterEach(() => { dumped.edition = EDITION })

    it('words the conference dates in the banner and the partners invitation', async () => {
      const wrapper = await mountSuspended(IndexPage)
      expect(wrapper.find('.banner .info__long p').text()).toBe('Warsaw | September 25–26, 2026')
      expect(wrapper.find('.partners .body').text()).toContain('Confitura 2026 is coming on September 25–26, and')
    })

    it('shows one date for a one-day conference', async () => {
      dumped.edition = { ...EDITION, lastDay: '2026-09-25' }
      const wrapper = await mountSuspended(IndexPage)
      expect(wrapper.find('.banner .info__long p').text()).toBe('Warsaw | September 25, 2026')
      expect(wrapper.find('.partners .body').text()).toContain('Confitura 2026 is coming on September 25, and')
    })

    it('counts down to the conference start', async () => {
      vi.useFakeTimers({ toFake: ['Date'] })
      vi.setSystemTime(new Date('2026-09-23T06:58:57Z')) // 2 days 1 minute 3 seconds before 09:00 in Warsaw (07:00Z)
      const wrapper = await mountSuspended(TheTimer)
      vi.useRealTimers()
      expect(wrapper.findAll('.timer-value').map((v) => v.text())).toEqual(['02', '00', '01', '03'])
    })
  })

  describe('without edition.json', () => {
    beforeEach(() => { dumped.edition = null })
    afterEach(() => { dumped.edition = EDITION })

    it('hides the banner date line but keeps the venue', async () => {
      const wrapper = await mountSuspended(IndexPage)
      expect(wrapper.findAll('.banner .info__long p').map((p) => p.text())).toEqual(['ADN Conference Center, Grzybowska 56'])
    })

    it('leaves the dates out of the partners invitation', async () => {
      const wrapper = await mountSuspended(IndexPage)
      const text = wrapper.find('.partners .body').text()
      expect(text).not.toContain('is coming on')
      expect(text).toContain('currently working on delivering')
    })

    it('hides the countdown', async () => {
      const wrapper = await mountSuspended(TheTimer)
      expect(wrapper.find('.timer').exists()).toBe(false)
    })
  })
})
