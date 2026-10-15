// https://nuxt.com/docs/api/configuration/nuxt-config
export default defineNuxtConfig({
    compatibilityDate: '2024-11-01',
    components: {
        global: true,
        dirs: ['~/components']
    },
    devtools: {enabled: true},
    runtimeConfig: {
        public: {
            apiServer: "https://api.confitura.pl/api/resources/archive/2026/",
            archiveServer: "https://api.confitura.pl/api/resources/edition-2026/",
            fileServer: "https://api.confitura.pl",
            appUrl: "https://app.confitura.pl",
            googleMapsApiKey:  ""
        }
    },
    app: {
        rootAttrs: {
            id: 'app'
        },
        head: {
            htmlAttrs: { lang: 'en' },
            meta: [
                { charset: 'utf-8' },
                { name: 'viewport', content: 'width=device-width, initial-scale=1' },
                { property: 'og:site_name', content: 'Confitura' },
                { property: 'og:locale', content: 'en_US' },
                { property: 'og:image', content: 'https://confitura.pl/og-image.png' },
                { property: 'og:image:width', content: '1200' },
                { property: 'og:image:height', content: '630' },
            ],
            link: [
                {rel: 'stylesheet', href: 'https://fonts.googleapis.com/icon?family=Material+Icons'},
                {rel: 'icon', type: 'image/x-icon', href: '/favicon.ico'},
            ],
            script: [{
                src: 'https://kit.fontawesome.com/bbf9190584.js', crossorigin: 'anonymous'
            }]
        }
    },
    modules: ['@pinia/nuxt', '@nuxt/test-utils/module']
})