<template xmlns:v-slot="http://www.w3.org/1999/XSL/Transform">
  <div class="partners">
    <PageHeader class="header" type="peace">
      <template v-slot:title>
        <span class="header__partner-type">{{ partner.type }} partner</span>
        {{ partner.name }}
      </template>
    </PageHeader>
    <Box class="content" color="white" :full="false">
      <article class="partner">
        <div class="partner__logo-container">
          <a :href="partner.www" target="_blank" rel="noopener">
            <img
              class="partner__logo"
              :class="{ [`partner__logo--${partner.orientation}`]: partner.orientation }"
              :src="resolveImage(partner.logo)"
              :alt="partner.name"
            />
          </a>
        </div>
        <div class="partner__description" v-html="description"></div>
      </article>
    </Box>
    <Contact />
  </div>
</template>

<script setup lang="ts">
import { marked } from 'marked'
import { usePartnersStore } from '~/stores/partnersStore'

const route = useRoute()
const partnerStore = usePartnersStore()
const partner: Partner = computed(() => partnerStore.getPartnerById(route.params.partner))
const description = computed(() => partner.value.description ? marked(partner.value.description) : '')

onMounted(() => {
  window.scrollTo(0, 0)
})


const imgUrls = import.meta.glob('~/assets/partners/2023/*', {
  import: 'default',
  eager: true
})

function resolveImage(path: string) {
  return imgUrls[path]
}
</script>

<style lang="scss" scoped>
@use "~/assets/colors" as *;
@use "~/assets/sizes" as *;
@use "~/assets/media" as *;
@use "~/assets/fonts" as *;

.partners {
  overflow: hidden;
}

.header {
  overflow: hidden;

  &__partner-type {
    display: block;

    ::first-letter {
      text-transform: capitalize;
    }
  }
}

.header-img {
  width: 500px;
  top: 50px;
  left: 60px;
  transform: rotate(-10deg);
  position: absolute;
  @include sm() {
    width: 500px;
    top: 0;
    position: unset;
    margin-top: 470px;
  }
}

.type-header {
  font-size: 2.7rem;
  color: $brand;
  margin-bottom: 3rem;
  font-weight: bold;

  &__breaker {
    @include sm() {
      display: none;
    }
  }

  &:first-letter {
    text-transform: capitalize;
  }
}

.partner {
  display: grid;
  grid-template-columns: 1fr;
  grid-gap: 2rem;
  @include md() {
    grid-template-columns: 1fr 2fr;
  }

  & &__logo-container {
    padding: 1.5rem 1rem 1rem;
    text-align: center;

    .partner__logo {
      width: auto;
      height: auto;
      max-width: 500px;
      max-height: 200px;
      min-width: 190px;
      object-fit: contain;
    }

    .partner__logo--horizontal {
      min-width: 250px;
      max-width: 300px;
    }
  }

  & &__description {
    font-size: 1.5rem;
    line-height: 2rem;
  }
}
</style>
<style lang="scss">
@use "~/assets/colors" as *;

.partner {
  a {
    color: $brand;
  }
}
</style>
