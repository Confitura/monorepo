<template>
    <div class="menu">
        <div class="menu-item" v-for="item in items" :key="item.label">
            <router-link :to="item.url" class="menu-link" @click.native="click()">{{ item.label }}</router-link>
        </div>
    </div>
</template>

<script lang="ts">
  import { Component, Vue } from 'vue-property-decorator';

  @Component({
    components: {},
  })
  export default class TheMenu extends Vue {

    public items = [
      { label: 'home', url: '/#home' },
      { label: 'about us', url: '/#about-us' },
      { label: 'numbers', url: '/#numbers' },
      { label: 'partners', url: '/partners' },
      { label: 'contact', url: '/#contact' },
      { label: 'FAQ', url: '/faq' },
    ];

    public click() {
      this.$emit('linkClicked');
    }
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

        @include md() {
            flex-basis: unset;
            justify-content: right;
            display: unset;
        }
    }

    .menu-item {
        margin: 2rem;

        @include md() {
            margin: 0.5rem;
            display: unset;
        }

    }

    .menu-link {
        text-decoration: none;
        color: $brand;
        font-size: 1.4rem;

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
