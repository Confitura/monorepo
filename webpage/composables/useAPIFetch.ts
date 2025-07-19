import {useFetch} from '#app'

type useFetchType = typeof useFetch

export const useAPIFetch: useFetchType = (path, options = {}) => {
    const config = useRuntimeConfig()
    options.baseURL = config.public.apiServer
    return useFetch(path, options)
}
type useArchiveFetch = typeof useFetch

export const useArchiveFetch: useArchiveFetch = (path, options = {}) => {
    const config = useRuntimeConfig()
    options.baseURL = config.public.archiveServer
    return useFetch(path, options)
}