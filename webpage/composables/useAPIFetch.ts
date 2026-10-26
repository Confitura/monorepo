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
    // Answer a server render 503 when its data failed to load (not a 404 of an unpublished feed, nor a
    // fetch cancelled for a newer one), because Nitro's SWR cache stores no response with a status >= 400.
    const event = useRequestEvent()
    options.middleware = [(next, {signal}) => next().catch(error => {
        if (event && !signal.aborted && error?.statusCode !== 404) setResponseStatus(event, 503)
        throw error
    })]
    return useFetch(path, options)
}