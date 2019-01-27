<template>
    <nav class="header" :class="[theme]">
        <div class="header-container"><img
                class="header__logo"
                src="../assets/logo_white.svg"
                alt="confitura logo"
        />
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

    .header {
        padding-right: $standard-padding;
        padding-left: $standard-padding;
        position: fixed;
        width: 100%;
        z-index: 1000;
        box-sizing: border-box;
        transition: all 0.5s ease-out;
        &--default {
            background-color: transparent;
            padding-top: 37px ;
            padding-bottom: 37px ;
        }

        &--black, &--red {
            background-color: #ffffff;
            padding-top: 15px ;
            padding-bottom: 15px ;

        }
        &--white {
            background-color: #000000;
            padding-top: 15px ;
            padding-bottom: 15px ;
        }

        .header-container {
            max-width: 1400px;
            margin: auto;
            display: flex;
            align-items: center;
            font-weight: bold;
        }

        &__logo {
            width: 210px;
            justify-self: flex-start;
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
