<template>
  <div class="partners">
    <PageHeader title="Partners" type="peace"></PageHeader>
    <Box class="content" color="white">
      <template v-for="(items, type) in partners">
        <article v-if="items.length > 0" :key="type">
          <h2 class="type-header">
            {{ type }} <br class="type-header__breaker"/>partners
          </h2>
          <div class="logos">
            <router-link
                v-for="partner in items"
                :key="partner.id"
                :to="`/partners/${partner.id}`"
                class="logo-link"
                :class="`logo-link--${partner.type}`"
            >
              <img
                  :src="resolveImage(partner.logo)"
                  :alt="partner.name"
                  :class="{
                  ['logo--' + partner.type]: true,
                  ['logo--' + partner.orientation]: partner.orientation,
                  [partner.id]: true
                }"
              />
            </router-link>
          </div>
        </article>
      </template>
    </Box>
    <Contact/>
  </div>
</template>

<script setup lang="ts">
import {type Partners, usePartnersStore} from '~/stores/partnersStore'

let partners: Partners = usePartnersStore().partnersMap

const imgUrls = import.meta.glob('~/assets/partners/2023/*', {
  import: 'default',
  eager: true
})

function resolveImage(path: string) {
  return `${imgUrls[path]}`
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

.logos {
  display: flex;
  margin-bottom: 6rem;
  align-items: center;
  flex-direction: column;
  @include md() {
    flex-direction: row;
    flex-wrap: wrap;
  }
}

.logo-link {
  margin: 1.5rem 2rem;

  &--platinum,
  &--path {
    text-align: center;
  }
}

.logo--platinum,
.logo--path {
  width: 80%;

  @include md() {
    width: 250px;
  }
}

.logo--silver {
  width: 80px;

  &.logo--horizontal {
    width: 160px;
  }
}

.logo--bronze,
.logo--media,
.logo--tech {
  width: 120px;

  &.logo--horizontal {
    width: 160px;
  }
}

.logo--gold {
  width: 110px;

  &.logo--horizontal {
    width: 220px;
  }
}
</style>
