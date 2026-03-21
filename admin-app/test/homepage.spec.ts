import HomepageId from '@/pages/homepage.[[id]].vue'
import { renderWithVuetify } from '@/../test/helpers'
import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(),
  routes: [{ path: '/:pathMatch(.*)*', component: HomepageId }],
})

describe('homepage', () => {
  it('renders workshops and presentations sections', async () => {
    const { getByText } = renderWithVuetify(HomepageId, {
      global: {
        plugins: [router],
      },
    })

    getByText('Workshops')
    getByText('Presentations')
  })
})
