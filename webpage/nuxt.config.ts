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
            apiServer: "http://localhost:8080/api/resources/archive/2025/",
            archiveServer: "http://localhost:8080/api/resources/edition-2025/",
            fileServer: "http://localhost:8080"
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
    plausible: {
        // Prevent tracking on localhost
        ignoredHostnames: ['localhost'],
        apiHost: 'https://plausible.confitura.pl/'
    },
    umami: {
        id: '1d7ee786-0bb8-4ae4-ac1d-928296b7b44c',
        host: 'https://umami.confitura.pl',
        autoTrack: true,
        // proxy: 'cloak',
        // useDirective: true,
        ignoreLocalhost: true,
        // excludeQueryParams: false,
        // domains: ['cool-site.app', 'my-space.site'],
        // customEndpoint: '/my-custom-endpoint',
        // enabled: false,
        // logErrors: true,
    },
    modules: ['@pinia/nuxt', '@nuxt/test-utils/module', '@nuxtjs/plausible', 'nuxt-umami']
})