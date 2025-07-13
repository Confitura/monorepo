<template>
  <Box class="partners" color="white" id="partners">
    <h1 class="header">our partners</h1>
    <div class="partners-grid">
      <div class="partners-main">
        <div class="platinum">
          <a
              v-for="item in partners.platinum"
              :key="item.id"
              :href="item.www"
              class="link"
              rel="noopener"
              target="_blank"
          >
            <img
                :src="resolveImage(item.logo)"
                :alt="item.name"
                class="logo__img--platinum"
                :class="item.id"
            />
          </a>
          <span class="type--platinum">Platinum</span>
        </div>
        <div class="path">
          <a
              v-for="item in partners.path"
              :key="item.id"
              :href="item.www"
              class="link"
              rel="noopener"
              target="_blank"
          >
            <img
                :src="resolveImage(item.logo)"
                :alt="item.name"
                class="logo__img--platinum"
                :class="item.id"
            />
          </a>
          <!--          <span class="type&#45;&#45;path">Path</span>-->
        </div>
      </div>
      <div class="other-types">
        <template v-for="type in types">
          <div class="logos" v-if="type === active" :key="type">
            <div
                v-for="item in partners[type]"
                :key="item.id"
                class="logo"
                :class="{
                  [`logo--${item.orientation}`]: item.orientation,
                  [`logo--${active}`]: active
                }"
            >
              <a :href="item.www" class="link" rel="noopener" target="_blank">
                <img
                    :src="resolveImage(item.logo)"
                    :alt="item.name"
                    class="logo__img"
                    :title="item.name"
                />
              </a>
            </div>
          </div>
        </template>

        <div class="type__selector">
          <div
              class="type"
              v-for="type in types"
              :key="type"
              @click="activate(type)"
              @mouseenter="pauseIfActive(type)"
              @mouseleave="startCarousel()"
              :class="{
              [`type--${type}`]: true,
              [`type--active`]: type === active
            }"
          >
            {{ type }}
          </div>
        </div>
      </div>
    </div>
  </Box>
</template>

<script setup lang="ts">

import {type PartnerType, usePartnersStore} from '~/stores/partnersStore'

const types: PartnerType[] = ['gold', 'silver', 'tech']
const active: Ref<PartnerType> = useState('active', () => 'gold')

let partners: Partners = usePartnersStore().partnersMap

let intervalId: any | undefined = undefined

function pauseIfActive(type: string) {
  if (active.value === type) {
    stopCarousel()
  }
}

function activate(type: PartnerType) {
  active.value = type
  stopCarousel()
}

function startCarousel() {
  if (intervalId === undefined) {
    intervalId = setInterval(() => {
      const currentIdx = types.indexOf(active.value)
      const newIdx = (currentIdx + 1) % types.length
      active.value = types[newIdx]
    }, 5000)
  }
}

onMounted(() => {
  startCarousel()
})

onDeactivated(() => {
  stopCarousel()
})

function stopCarousel() {
  clearInterval(intervalId)
  intervalId = undefined
}

const imgUrls = import.meta.glob('~/assets/partners/2023/*', {
  import: 'default',
  eager: true
})

function resolveImage(path: string): string {
  return `${imgUrls[path]}`
}

</script>

<style scoped lang="scss">
@use "~/assets/colors" as *;
@use "~/assets/fonts" as *;
@use "~/assets/media" as *;

.partners {
  text-align: left;

  .header {
    color: $brand;
    font-family: $font-bold;
    font-size: 3rem;
    margin: 0;
  }

  .info {
    font-size: 1.75rem;
  }

  &-grid {
    display: flex;
    width: 100%;
    justify-items: center;
    flex-direction: column;
    @include md() {
      flex-direction: row;
      min-height: 600px;
    }
  }

  .type--platinum {
    font-size: 2rem;
    color: $brand;
    padding-top: 1rem;
    @include md() {
      font-size: 1.5rem;
    }
  }

  .type--path {
    font-size: 2rem;
    color: $brand;
    padding-top: 2rem;
    @include md() {
      font-size: 1.5rem;
    }
  }

  .logos {
    display: flex;
    flex-wrap: wrap;
    padding-bottom: 1rem;
    padding-top: 1rem;
    justify-items: center;
    align-items: center;
    justify-content: center;
    flex-grow: 1;
    transition: all 0.3s linear;
    max-width: 1000px;
    @include md() {
      margin-left: 2rem;
    }
  }

  .logo {
    margin-top: 1.1rem;
    margin-bottom: 1.1rem;

    &.logo--horizontal {
      margin-top: 1.1rem;
      margin-bottom: 1.1rem;

      &.logo--gold {
        margin: 1.5rem;
      }
    }
  }

  .logo--silver {
    margin-left: 1.1rem;
    margin-right: 1.1rem;
  }

  .logo--tech {
    margin-left: 1.1rem;
    margin-right: 1.1rem;
  }

  .logo--gold {
    margin: 1.5rem;
  }

  .other-types {
    flex-grow: 1;
    display: flex;
    flex-direction: column-reverse;
    overflow: hidden;

    @include md() {
      flex-direction: column;
    }
  }

  .logo__img {
    width: 65px;
  }

  .logo--gold .logo__img {
    width: 130px;
  }

  .logo__img--platinum {
    width: 250px;

    &.ey,
    &.softwareplant {
      padding: 0 0 3rem 0;
      box-sizing: border-box;
    }
  }

  .logo--horizontal {
    grid-column-end: span 2;

    .logo__img {
      width: 100px;
    }

    &.logo--gold .logo__img {
      width: 230px;
    }
  }

  .link {
    text-align: center;
  }

  .type__selector {
    display: flex;
    justify-content: center;
    flex-shrink: 0;
  }

  .platinum,
  .path {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    padding-top: 2rem;
    row-gap: 2rem;
  }

  .type {
    cursor: pointer;
    transition: all 0.3s linear;

    &:hover {
      color: $brand;
      border-top-color: $brand;
    }
  }

  .type--gold,
  .type--silver,
  .type--bronze,
  .type--tech {
    font-size: 1.5rem;
    color: #eaeaea;
    margin: 5px;
    max-width: 230px;
    width: 33%;
    text-align: center;
    padding-top: 1.5rem;
    border-bottom: #eaeaea solid 8px;

    @include md() {
      border-bottom: none;
      border-top: #eaeaea solid 5px;
    }
  }

  .type--active {
    border-color: $brand;
    color: $brand;
  }

  .fade-enter-active,
  .fade-leave-active {
    transition: all 0.5s;
  }

  .fade-enter {
    opacity: 0;
  }

  .fade-leave-to {
    opacity: 0;
  }
}
</style>
