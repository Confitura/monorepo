export const useCanonical = () => {
  const route = useRoute()
  useHead({
    link: computed(() => [
      { rel: 'canonical', href: `https://confitura.pl${route.path}` }
    ])
  })
}
