<template>
    <nav class="header" :class="theme">
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

  @Component
  export default class TheHeader extends Vue {
    public items = [
      { label: 'home', url: '/' },
      { label: 'about us', url: '/#about-us' },
      { label: 'numbers', url: '/#numbers' },
      { label: 'partners', url: '/#partners' },
      { label: 'contact', url: '/#contact' }];
    public theme: string = '';

    public mounted() {
      this.setTheme('transparent');
      window.addEventListener('scroll', this.handleScroll);
    }

    public beforeDestroy() {
      window.removeEventListener('scroll', this.handleScroll);
    }

    public handleScroll() {
      const scroll = window.scrollY;
      if (scroll > 20) {
        this.setTheme('black');
      } else {
        this.setTheme('transparent');
      }
    }

    private setTheme(theme: 'black' | 'transparent') {
      this.theme = `header--${theme}`;
    }
  }
</script>

<style scoped lang="scss">
    @import "../assets/colors";

    .header {
        position: fixed;
        padding: 37px 0 37px 0;
        width: 100%;
        z-index: 1000;
        box-sizing: border-box;

        &--black {
            background-color: #000000;
            padding: 15px 0 15px 0;
            transition: all 0.5s linear;
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
