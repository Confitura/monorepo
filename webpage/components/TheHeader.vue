<template>
  <div class="container">
    <nav class="header" :class="theme">
      <div class="header-container">
        <a href="/">
          <div class="header__logo"></div>
        </a>
        <span class="header__separator"></span>
        <a class="header__mobile-button" @click="toggleMenu($event)" href="#">
          menu
        </a>
        <TheMenu :class="theme" v-if="mobileBreakpoint" />
      </div>
    </nav>
    <TheMenu :class="theme" v-if="showMenu && !mobileBreakpoint" @linkClicked="hideMenu" />
  </div>
</template>

<script setup lang="ts">

import { useThemeStore } from '~/stores/themeStore'

const showMenu = useState('showMenu', () => false)
const themeStore = useThemeStore()
const theme = computed(() => `header--${themeStore.headerTheme}`)
const mobileBreakpoint = computed(() => themeStore.isLg)

onMounted(() => {
  window.addEventListener('scroll', handleScroll)
})

onUnmounted(() => {
  window.removeEventListener('scroll', handleScroll)
})

function handleScroll() {
  const scroll = window.scrollY
  if (scroll < 20) {
    themeStore.changeHeaderTheme({ color: 'default' })
  }
}

function hideMenu() {
  showMenu.value = false
}

function toggleMenu($event: Event) {
  $event.preventDefault()
  showMenu.value = !showMenu.value
}
</script>

<style scoped lang="scss">
@use "~/assets/colors" as *;
@use "~/assets/sizes" as *;
@use "~/assets/media" as *;

$big-menu-padding: 37px;
$small-menu-padding: 10px;

.container {
  display: flex;
  flex-direction: column;
  position: fixed;
  width: 100%;
  z-index: 1000;
  backdrop-filter: blur(10px);
}

.header {
  padding-right: 1rem;
  padding-left: 1rem;

  box-sizing: border-box;
  transition: all 0.5s linear;
  color: $brand;

  @include md() {
    padding-right: calc($standard-padding / 2);
    padding-left: calc($standard-padding / 2);
  }
  @include lg() {
    padding-right: $standard-padding;
    padding-left: $standard-padding;
  }

  &#{&}--default {
    background-color: transparent;
    padding-top: $small-menu-padding;
    padding-bottom: $small-menu-padding;

    @include md() {
      padding-top: $big-menu-padding;
      padding-bottom: $big-menu-padding;
    }
  }

  &#{&}--black,
  &#{&}--red {
    background-color: #ffffff;
    padding-top: $small-menu-padding;
    padding-bottom: $small-menu-padding;

    .header__logo {
      background-image: url("../assets/logo_black.svg");
    }
  }

  &#{&}--white {
    background-color: #000000;
    padding-top: $small-menu-padding;
    padding-bottom: $small-menu-padding;
  }

  .header-container {
    max-width: $max-width;
    margin: auto;
    display: flex;
    flex-wrap: wrap;
    align-items: center;
    font-weight: bold;
  }

  &__logo {
    width: 150px;
    height: 30px;
    background-image: url("../assets/_logo_white_x_nlpday.svg");
    background-position: center;
    background-repeat: no-repeat;
    background-size: contain;
    justify-self: flex-start;
    @include md() {
      width: 210px;
    }
  }

  &__separator {
    flex-grow: 1;
  }

  & &__mobile-button {
    &,
    :hover {
      cursor: pointer;
      font-size: 1.5rem;
      text-decoration: none;
      color: $brand;
      @include lg() {
        display: none;
      }
    }
  }
}

.menu-fade-enter-active,
.menu-fade-leave-active {
  transition: all 0.2s linear;

  @include md() {
    transition: none;
  }
}

.menu-fade-enter,
.menu-fade-leave-to {
  opacity: 0;
  transform: translateX(100px);
}
</style>
