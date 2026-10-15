import type { Plugin } from 'vue'
// Importing the api module registers the auth interceptor on the generated client at startup.
import '@/utils/api'

const plugin: Plugin = { install() {} }
export default plugin
