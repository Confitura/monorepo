<template>
    <div class="container">
        <nav class="header" :class="theme">
            <div class="header-container">
                <a href="/">
                    <div class="header__logo"></div>
                </a>
                <span class="header__separator"></span>
                <a class="header__mobile-button" @click="toggleMenu($event)" href="#">menu</a>
                <TheMenu v-if="mobileBreakpoint"/>
            </div>
        </nav>
        <transition name="menu-fade">
            <TheMenu :class="theme" v-if="showMenu && !mobileBreakpoint" @linkClicked="hideMenu"/>
        </transition>
    </div>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import { CHANGE_HEADER_THEME } from '@/types';
import TheMenu from '@/components/TheMenu.vue';

@Component({
  components: { TheMenu },
})
export default class TheHeader extends Vue {
  public showMenu = false;

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

  public hideMenu() {
    this.showMenu = false;
  }

  public toggleMenu($event: Event) {
    $event.preventDefault();
    this.showMenu = !this.showMenu;
  }

  get theme() {
    return `header--${this.$store.state.headerTheme}`;
  }

  get mobileBreakpoint() {
    return this.$store.getters.isLg;
  }
}
</script>

<style scoped lang="scss">
    @import '../assets/colors';
    @import '../assets/sizes';
    @import '../assets/media';

    $big-menu-padding: 37px;
    $small-menu-padding: 10px;

    .container {
        display: flex;
        flex-direction: column;
        position: fixed;
        width: 100%;
        z-index: 1000;
    }

    .header {
        padding-right: 1rem;
        padding-left: 1rem;

        box-sizing: border-box;
        transition: all 0.5s linear;
        color: $brand;

        @include md() {
            padding-right: $standard-padding / 2;
            padding-left: $standard-padding / 2;
        }
        @include lg() {
            padding-right: $standard-padding;
            padding-left: $standard-padding;
        }

        &--default {
            background-color: transparent;
            padding-top: $small-menu-padding;
            padding-bottom: $small-menu-padding;

            @include md() {
                padding-top: $big-menu-padding;
                padding-bottom: $big-menu-padding;
            }
        }

        &--black,
        &--red {

            background-color: #ffffff;
            padding-top: $small-menu-padding;
            padding-bottom: $small-menu-padding;

            .header__logo {
                background-image: url('../assets/logo_black.svg');
            }

        }

        &--white {
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
            background-image: url('../assets/logo_white.svg');
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

        &__mobile-button {
            cursor: pointer;
            font-size: 1.5rem;
            text-decoration: none;
            color: $brand;
            @include lg() {
                display: none;
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
