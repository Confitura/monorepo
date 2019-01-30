<template>
    <nav class="header" :class="[theme]">
        <div class="header-container">
            <div class="header__logo">
            </div>
            <span class="header__separator"></span>
            <div class="menu-item" v-for="item in items" :key="item.label">
                <a class="menu-link" :href="item.url">{{ item.label }}</a>
            </div>
        </div>
    </nav>
</template>

<script lang="ts">
  import { Component, Vue } from 'vue-property-decorator';
  import { CHANGE_HEADER_THEME } from '@/types';

  @Component
  export default class TheHeader extends Vue {
    public items = [
      { label: 'home', url: '/' },
      { label: 'about us', url: '/#about-us' },
      { label: 'numbers', url: '/#numbers' },
      { label: 'partners', url: '/#partners' },
      { label: 'contact', url: '/#contact' }];

    public mounted() {
      window.addEventListener('scroll', this.handleScroll);
    }

    public beforeDestroy() {
      window.removeEventListener('scroll', this.handleScroll);
    }

    public handleScroll() {
      const scroll = window.scrollY;
      if (scroll < 20) {
        this.$store.commit(CHANGE_HEADER_THEME, { color: 'default' });
      }
    }

    get theme() {
      return `header--${this.$store.state.headerTheme}`;
    }
  }
</script>

<style scoped lang="scss">
    @import "../assets/colors";
    @import "../assets/sizes";
    @import "../assets/media";

    .header {
        padding-right: 15px;
        padding-left: 15px;
        position: fixed;
        width: 100%;
        z-index: 1000;
        box-sizing: border-box;
        transition: all 0.5s ease-out;
        @include md() {
            padding-right: $standard-padding;
            padding-left: $standard-padding;
        }

        &--default {
            background-color: transparent;
            padding-top: 15px;
            padding-bottom: 15px;
            @include md() {
                padding-top: 37px;
                padding-bottom: 37px;
            }
        }

        &--black, &--red {
            background-color: #ffffff;
            padding-top: 15px;
            padding-bottom: 15px;

            .header__logo {
                background-image: url("../assets/logo_black.svg");
            }

        }

        &--white {
            background-color: #000000;
            padding-top: 15px;
            padding-bottom: 15px;
        }

        .header-container {
            max-width: 1400px;
            margin: auto;
            display: flex;
            align-items: center;
            font-weight: bold;
        }

        &__logo {
            width: 150px;
            height: 30px;
            background-image: url("../assets/logo_white.svg");
            background-position: center;
            background-repeat: no-repeat;
            background-size: contain;
            /*height: 100%    ;*/
            justify-self: flex-start;
            align-self: stretch;
            @include md() {
                width: 210px;
            }
        }

        &__separator {
            flex-grow: 1;
        }

        .menu-item {
            margin: 0.5rem;
            display: none;
            @media all and (min-width: 1000px) {
                display: unset;
            }
        }

        .menu-link {
            text-decoration: none;
            color: $brand;
            font-size: 1.5rem;

            &:hover {
                text-decoration: underline;
            }
        }
    }
</style>
