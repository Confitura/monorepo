import { defineVitestConfig } from '@nuxt/test-utils/config'
import { fileURLToPath } from 'node:url'

export default defineVitestConfig({
  resolve: {
    alias: {
      '@plausible-analytics/tracker': fileURLToPath(
        new URL('./node_modules/@plausible-analytics/tracker/plausible.js', import.meta.url)
      )
    }
  }
})
