<template>
  <header
    class="page-header__container"
    :class="{ 'page-header--crop': type === 'peace' }"
    ref="header"
  >
    <div class="page-header" :class="{ 'page-header--small': small }">
      <h1 class="page-title">
        <template v-if="title">{{ title }}</template>
        <slot name="title"></slot>
      </h1>
      <slot v-if="!small && type === 'planets'">
        <img class="page-image" src="../assets/planety_faq.svg" alt="planets" />
      </slot>
      <slot v-if="type === 'coder'">
        <img
          src="../assets/small_planet.svg"
          class="header__planet"
          alt="small planet"
        />
        <img
          src="../assets/astronaut_comp.svg"
          class="header-img"
          alt="astronaut coder"
        />
      </slot>
      <slot v-if="type === 'peace'">
        <img src="~/assets/astronaut.svg" class="header__peace" />
      </slot>
    </div>
  </header>
</template>
<script setup lang="ts">

import { useThemeStore } from '~/stores/themeStore'

const { title, type = 'planets', small } = defineProps<{
  title?: string,
  small?: boolean,
  type: 'planets' | 'coder' | 'peace'
}>()
const themeStore = useThemeStore()
const header = ref(null)
let threshold: number[] = []

for (let i = 0; i <= 1; i += 0.01) {
  threshold.push(i)
}

onMounted(() => {
  const options = { threshold }

  const callback: IntersectionObserverCallback = entries => {
    const [entry] = entries
    if (entry.isIntersecting) {
      if (entry.boundingClientRect.top < -100) {
        themeStore.changeHeaderTheme({ color: 'white' })
      } else {
        themeStore.changeHeaderTheme({ color: 'default' })
      }
    }
  }
  const observer = new IntersectionObserver(callback, options)
  console.log(header)
  observer.observe(header.value)
})
</script>
<style lang="scss" scoped>
@use "~/assets/colors" as *;
@use "~/assets/sizes" as *;
@use "~/assets/fonts" as *;
@use "~/assets/media" as *;

.page-header__container {
  width: 100%;
  background-color: #000000;
  background-image: url("../assets/stars.png");
}

.page-header--crop {
  overflow: hidden;
}

.page-header.page-header--small {
  height: 150px;
  @include md() {
    height: 250px;
  }
}

.page-header {
  box-sizing: border-box;
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 300px;
  padding: 2rem;
  position: relative;
  max-width: $max-width;
  margin: auto;
  @include md() {
    height: 400px;
    padding: $standard-padding $standard-padding $standard-padding 80px;
  }

  .page-title {
    text-align: left;
    color: #ffffff;
    font-size: 1.8rem;
    font-family: $font-bold;
    font-weight: bold;
    max-width: 400px;
    @include md() {
      font-size: 2.3rem;
    }
  }

  .page-image {
    position: absolute;
    bottom: -280px;
    right: -500px;
    @include sm() {
      right: -400px;
    }

    @include md() {
      position: relative;
      bottom: -90px;
      left: 0;
      width: 600px;
    }
  }
}

.header__planet {
  position: absolute;
  left: -40px;
  top: 60px;
  @include md() {
    position: unset;
    margin-left: 200px;
  }
}

.header-img {
  position: absolute;
  left: 170px;
  bottom: -50px;
  height: 280px;
  @include md() {
    position: unset;
    margin-top: 170px;
    margin-right: 150px;
    height: 350px;
  }
}

.header__peace {
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
</style>
