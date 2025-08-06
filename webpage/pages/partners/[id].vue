<template>
  <div class="partners">
    <PageFragment class="header" type="peace">
      <template #title>
        <span class="header__partner-type">{{ partner.type }} partner</span>
        {{ partner.name }}
      </template>
    </PageFragment>
    <Box class="content" color="white" :full="false">
      <article class="partner">
        <div class="partner__logo-container">
          <a :href="partner.www" target="_blank" rel="noopener">
            <img
              class="partner__logo"
              :class="{
                [`partner__logo--${partner.orientation}`]: partner.orientation
              }"
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
import { ref, onMounted, computed } from 'vue'
import Box from '@/components/Box.vue'
import Contact from '@/components/Contact.vue'
import { usePartnersStore, type Partner } from '@/stores/partnersStore'
import { useRoute } from 'vue-router'
import { marked } from 'marked'

const route = useRoute()
const partnersStore = usePartnersStore()
const partner = ref<Partner>({
  name: "",
  description: "",
  id: "",
  logo: "",
  type: "",
  www: ""
})

const description = computed(() => {
  return marked(partner.value.description)
})

const imgUrls = import.meta.glob('~/assets/partners/2025/*', {
  import: 'default',
  eager: true
})

function resolveImage(path: string): string {
  let resolved = `${imgUrls[path]}`;
  console.log('resolved path', resolved)
  return resolved
}

onMounted(() => {
  window.scrollTo(0, 0)
  const id = route.params.id as string
  const foundPartner = partnersStore.getPartnerById(id)
  if (foundPartner) {
    partner.value = foundPartner
  }
})
</script>

<style lang="scss" scoped>
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
  @media (max-width: 576px) {
    width: 500px;
    top: 0;
    position: unset;
    margin-top: 470px;
  }
}

.type-header {
  font-size: 2.7rem;
  color: var(--brand-color);
  margin-bottom: 3rem;
  font-weight: bold;

  &__breaker {
    @media (max-width: 576px) {
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
  @media (min-width: 768px) {
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
.partner {
  a {
    color: var(--brand-color);
  }
}
</style>