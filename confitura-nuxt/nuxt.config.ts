// https://nuxt.com/docs/api/configuration/nuxt-config
export default defineNuxtConfig({
  compatibilityDate: '2024-11-01',
  components: {
    global: true,
    dirs: ['~/components']
  },
  devtools: { enabled: true },
  runtimeConfig: {
    public: {
      baseURL: process.env.BASE_URL || ''
    }
  },
  app: {
    rootAttrs: {
      id: 'app'
    },
    head: {
      link: [
        { rel: 'stylesheet', href: 'https://fonts.googleapis.com/icon?family=Material+Icons' },
        { rel: 'icon', type: 'image/x-icon', href: '/favicon.ico' },
      ],
      script:[{
        src: 'https://kit.fontawesome.com/bbf9190584.js',  crossorigin:'anonymous'
      }]
    }
  },
  modules: ['@pinia/nuxt'],

  routeRules: {
    '/api/**': { proxy: { to: 'https://2023.confitura.pl/api/**' } },
    '/json/**': { proxy: { to: 'https://2023.confitura.pl/json/**' } }
  }
})