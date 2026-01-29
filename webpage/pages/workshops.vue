<template>
  <div class="workshops">
    <PageHeader title="Workshops" type="coder"/>
    <div
        v-for="(workshop, $index) in workshops"
        :key="workshop.id"
        :id="workshop.id"
    >
      <Box
          class="workshop"
          :class="{ 'workshop--odd': odd($index) }"
          color="white"
          :full="false"
      >
        <PresentationBox
            :presentation="workshop"
            :class="{ 'presentationBox--odd': odd($index) }"
        ></PresentationBox>
      </Box>
    </div>

    <Contact/>
  </div>
</template>

<script setup lang="ts">

import {useArchiveFetch} from '~/composables/useAPIFetch'
import {onMounted, watch, nextTick, onBeforeUnmount} from 'vue'
import {useRoute} from '#imports'

let {data: workshops} = await useArchiveFetch('/workshops/accepted.json', {
  transform: (data) => data
})

function odd($index: number) {
  return $index % 2 !== 0
}

const route = useRoute()

function scrollToHashIfAny() {
  if (typeof window === 'undefined') return
  const hash = route.hash
  if (!hash) return
  const id = decodeURIComponent(hash.substring(1))
  const el = document.getElementById(id)
  if (el) {
    setTimeout(() => {
      el.scrollIntoView({ behavior: 'smooth' })
    }, 200)
  }
}

// Watch for hash changes
watch(() => route.hash, (newHash) => {
  if (newHash) {
    nextTick(() => scrollToHashIfAny())
  }
}, { immediate: true })

// Watch for path changes (navigating from other pages)
watch(() => route.path, () => {
  nextTick(() => scrollToHashIfAny())
})

onMounted(() => {
  nextTick(() => scrollToHashIfAny())
  const handleHashChange = () => nextTick(() => scrollToHashIfAny())
  if (typeof window !== 'undefined') {
    window.addEventListener('hashchange', handleHashChange)
  }
  onBeforeUnmount(() => {
    if (typeof window !== 'undefined') {
      window.removeEventListener('hashchange', handleHashChange)
    }
  })
})

// If workshops are (re)loaded async, try again after render
watch(workshops, () => {
  nextTick(() => scrollToHashIfAny())
})



const title = 'Workshops — Confitura 2026';
const description = 'Browse all accepted workshops for Confitura 2026. Find sessions by topic, level, and format.';
useHead({
  title,
  meta: [
    { name: 'description', content: description },
    { property: 'og:title', content: title },
    { property: 'og:description', content: description },
    { property: 'og:type', content: 'website' },
    { name: 'twitter:card', content: 'summary' },
    { name: 'twitter:title', content: title },
    { name: 'twitter:description', content: description }
  ]
})

</script>

<style lang="scss" scoped>
@use "~/assets/colors" as *;
@use "~/assets/sizes" as *;
@use "~/assets/media" as *;
@use "~/assets/fonts" as *;

.workshops {
  overflow: hidden;
}

.workshop {
  &--odd {
    background-color: $brand;
    color: #ffffff;
  }

  &__header {
    color: $brand;

    &--odd {
      color: #ffffff;
    }
  }

  &__infoGroup {
    display: flex;
    flex-direction: column;
    @include md() {
      align-items: center;
      flex-direction: row;
    }
  }

  &__title {
    font-weight: bold;
    font-size: 2rem;
    margin-top: 0;
  }

  &__description {
    font-size: 1.2rem;
    line-height: 1.5rem;
    word-wrap: break-word;
    @include md() {
      font-size: 1.5rem;
      line-height: 1.7rem;
      padding-left: 4rem;
    }
  }
}
</style>
