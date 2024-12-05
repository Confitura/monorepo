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
        {
          rel: 'stylesheet', href: 'https://use.fontawesome.com/releases/v5.6.3/css/solid.css',
          integrity: 'sha384-+0VIRx+yz1WBcCTXBkVQYIBVNEFH1eP6Zknm16roZCyeNg2maWEpk/l/KsyFKs7G',
          crossorigin: 'anonymous'
        }, {
          rel: 'stylesheet', href: 'https://use.fontawesome.com/releases/v5.6.3/css/brands.css',
          integrity: 'sha384-1KLgFVb/gHrlDGLFPgMbeedi6tQBLcWvyNUN+YKXbD7ZFbjX6BLpMDf0PJ32XJfX', crossorigin: 'anonymous'
        },
        {
          rel: 'stylesheet', href: 'https://use.fontawesome.com/releases/v5.6.3/css/fontawesome.css',
          integrity: 'sha384-jLuaxTTBR42U2qJ/pm4JRouHkEDHkVqH0T1nyQXn1mZ7Snycpf6Rl25VBNthU4z0', crossorigin: 'anonymous'
        },
        { rel: 'stylesheet', href: 'https://fonts.googleapis.com/icon?family=Material+Icons' }]
    }
  },
  modules: ['@pinia/nuxt'],

  routeRules: {
    '/api/**': { proxy: { to: 'https://2023.confitura.pl/api/**' } },
    '/json/**': { proxy: { to: 'https://2023.confitura.pl/json/**' } }
  }
})