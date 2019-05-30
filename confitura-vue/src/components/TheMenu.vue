<template>
    <div class="menu" :key="isLogin">
        <div class="menu-item" v-for="item in items" :key="item.label" v-if="isVisible(item)">
            <router-link v-if="item.url" :to="item.url" class="menu-link" @click.native="click()">{{ item.label }}</router-link>
            <span v-else class="menu-link" @click="item.action()">{{ item.label }}</span>
        </div>
    </div>
</template>

<script lang="ts">
  import { Component, Vue } from 'vue-property-decorator';
  import { LOGOUT } from '@/types';

  @Component({
    components: {},
  })
  export default class TheMenu extends Vue {

    public items: MenuItem[] = [
      { label: 'home', url: '/#home' },
      { label: 'about us', url: '/#about-us' },
      { label: 'numbers', url: '/#numbers' },
      { label: 'partners', url: '/partners' },
      { label: 'contact', url: '/#contact' },
      { label: 'workshop day', url: '/workshops' },
      { label: 'FAQ', url: '/faq' },
      { label: 'TICKETS', url: '/tickets' },
      { label: 'my profile', url: '/profile', visible: () => this.isLogin },
      { label: 'ADMIN', url: '/admin', visible: () => this.isAdmin },
      { label: 'logout', action: () => this.logout(), visible: () => this.isLogin },
      { label: 'login', url: '/login', visible: () => !this.isLogin },
    ];

    public isVisible(item: MenuItem): boolean {
      return item.visible === undefined || item.visible();
    }

    public click() {
      this.$emit('linkClicked');
    }

    public get isLogin() {
      return this.$store.getters.isLogin;
    }

    public get isAdmin() {
      return this.$store.getters.isAdmin;
    }

    public logout() {
      this.$store.dispatch(LOGOUT);
    }
  }

  interface MenuItem {
    label: string;
    url?: string;
    action?: () => void;
    visible?: () => boolean;
  }
</script>

<style scoped lang="scss">
    @import '../assets/colors';
    @import '../assets/media';

    .menu {
        flex-basis: 170px;
        align-self: flex-end;
        height: 100%;

        &.header--default {
            background-color: #000000;
        }

        @include lg() {
            flex-basis: unset;
            justify-content: right;
            display: unset;
        }
    }

    .menu-item {
        margin: 2rem;

        @include lg() {
            margin: 0.5rem;
            display: unset;
        }

    }

    .menu-link {
        text-decoration: none;
        color: $brand;
        font-size: 1.4rem;
        cursor: pointer;

        &:hover, &.router-link-active {
            color: #ffffff;
            border-bottom: 2px #ffffff solid;

            .header--red &, .header--black & {
                color: #000000;
                border-bottom: 2px #000000 solid;
            }
        }
    }
</style>
