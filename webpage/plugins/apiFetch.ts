export default defineNuxtPlugin(async (nuxtApp) => {
    const config = useRuntimeConfig()
    const $apiFetch = async (request, options) => {
      const baseURL = config.public.baseURL

      const response = await fetcher.raw(request, {
        ...options,
        baseURL
      })

      return response._data
    }
  }
)
