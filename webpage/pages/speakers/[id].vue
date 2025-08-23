<template>
  <div class="speaker">
    <PageHeader :title="speaker?.name" type="coder"></PageHeader>
    <Box class="content" color="white" :full="false">
      <div v-if="speaker" class="speaker__container">
        <div class="speaker__left">
          <img :src="speaker.photo" class="speaker__photo"/>
          <div class="speaker__social">
            <SocialLink
                class="speaker__social-link"
                type="twitter"
                :id="speaker.twitter"
            ></SocialLink>
            <SocialLink
                class="speaker__social-link"
                type="facebook"
                :id="speaker.facebook"
            ></SocialLink>
            <SocialLink
                class="speaker__social-link"
                type="github"
                :id="speaker.github"
            ></SocialLink>
            <SocialLink
                class="speaker__social-link"
                type="www"
                :id="speaker.www"
            ></SocialLink>
          </div>
        </div>
        <div class="speaker__right">
          <div class="speaker__name">{{ speaker.name }}</div>
          <div class="speaker__bio">
            {{ speaker.bio }}
          </div>
          <div class="speaker__presentation" v-for="presentation in speaker.presentations">
            <nuxt-link :to="`/presentations#${presentation.id}`">
              <h2>
                <i class=" fas fa-hammer" title="workshop" v-if="presentation.isWorkshop"></i>
                <i class=" fas fa-microphone" title="presentation" v-else></i>
                {{ presentation.name }}
              </h2>
            </nuxt-link>
          </div>
        </div>
      </div>
    </Box>
    <Contact/>
  </div>
</template>

<script setup lang="ts">
import {useArchiveFetch} from '~/composables/useAPIFetch'
import { computed } from 'vue'

let route = useRoute()
let {data: speaker} = useArchiveFetch(`/users/${route.params.id}/public.json`)

// SEO: Dynamic head tags based on speaker data
const title = computed(() => {
  const name = (speaker as any)?.value?.name
  return name ? `${name} — Confitura 2025 Speaker` : 'Speaker — Confitura 2025'
})

const description = computed(() => {
  const base = ((speaker as any)?.value?.bio || 'Discover speakers and sessions at Confitura 2025.') as string
  const text = String(base)
  return text.length > 160 ? `${text.slice(0, 157)}...` : text
})

const image = computed(() => (speaker as any)?.value?.photo || '')

useHead({
  title,
  meta: [
    { name: 'description', content: description },
    { property: 'og:title', content: title },
    { property: 'og:description', content: description },
    { property: 'og:type', content: 'profile' },
    { property: 'og:image', content: image },
    { name: 'twitter:card', content: 'summary_large_image' },
    { name: 'twitter:title', content: title },
    { name: 'twitter:description', content: description },
    { name: 'twitter:image', content: image },
  ]
})
</script>

<style lang="scss" scoped>
@use "~/assets/colors" as *;
@use "~/assets/sizes" as *;
@use "~/assets/media" as *;
@use "~/assets/fonts" as *;

.speaker__presentation {
  margin-top: 2rem;

  a {
    text-decoration: none;
    color: inherit;

    &:hover {
      color: $brand;
    }
  }
}

.speaker {
  overflow: hidden;
}

.speaker__container {
  display: flex;
  flex-direction: column;
  @include md() {
    flex-direction: row;
  }
}

.speaker__left {
  flex-shrink: 0;
}

.speaker__right {
  @include md() {
    margin-left: 3rem;
  }
}

.speaker__photo {
  width: calc(100vw - 50px);
  height: calc(100vw - 50px);
  object-fit: cover;
  box-sizing: border-box;
  @include md() {
    width: 400px;
    height: 400px;
  }
}

.speaker__name {
  font-family: $font-bold;
  font-size: 2.5rem;
  color: $brand;
  margin-top: 2rem;
  @include md() {
    margin-top: 0;
    font-size: 3rem;
  }
}

.speaker__bio {
  font-size: 1.2rem;
  line-height: 1.7rem;
  margin-top: 2rem;
  @include md() {
    font-size: 1.5rem;
    line-height: 2rem;
  }
}

.speaker__social {
  margin-top: 2rem;
  font-size: 3rem;
  @include md() {
    text-align: center;
  }

  &-link {
    margin-right: 2rem;
  }
}
</style>
