export default defineSitemapEventHandler(async () => {
  const config = useRuntimeConfig()
  const speakers = await $fetch<{ id: string }[]>('/users/search/speakers.json', {
    baseURL: config.public.archiveServer,
  }).catch(() => [])

  return [
    '/schedule/day-1',
    '/schedule/day-2',
    '/schedule/workshops/day-1',
    '/schedule/workshops/day-2',
    ...speakers.map(speaker => `/speakers/${speaker.id}`),
  ]
})
