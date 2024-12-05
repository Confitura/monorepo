import { useFetch } from '#app'

type useFetchType = typeof useFetch

export const useAPIFetch: useFetchType = (path, options = {}) => {
  const config = useRuntimeConfig()
  options.baseURL = config.public.baseURL
  return useFetch(path, options)
}