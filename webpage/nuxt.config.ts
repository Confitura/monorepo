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