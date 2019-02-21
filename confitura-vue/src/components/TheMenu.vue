<template>
    <div class="menu">
        <div class="menu-item" v-for="item in items" :key="item.label">
            <a class="menu-link" @click="navigateTo(item.url, $event)" href="#">{{ item.label }}</a>
        </div>
    </div>
</template>

<script lang="ts">
  import { Component, Prop, Vue } from 'vue-property-decorator';

  @Component({
    components: {},
  })
  export default class TheMenu extends Vue {

    public items = [
      { label: 'home', url: '/#home' },
      { label: 'about us', url: '/#about-us' },
      { label: 'numbers', url: '/#numbers' },
      { label: 'partners', url: '/#partners' },
      { label: 'contact', url: '/#contact' },
      { label: 'faq', url: '/faq' },
    ];

    public navigateTo(link: string, $event: Event) {
      $event.preventDefault();
      this.$emit('linkClicked', link);
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

        &:hover {
            text-decoration: underline;
        }
    }
</style>