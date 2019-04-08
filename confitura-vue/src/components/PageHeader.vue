<template xmlns:slot="http://www.w3.org/1999/xhtml">
    <header class="page-header__container">
        <div class="page-header" :class="{'page-header--small': small}">
            <h1 class="page-title">
                <template v-if="title">{{title}}</template>
                <slot name="title"></slot>
            </h1>
            <slot v-if="!small">
                <img class="page-image" src="../assets/planety_faq.svg" alt="planets">
            </slot>
        </div>
    </header>
</template>
<script lang="ts">
  import { Component, Prop, Vue } from 'vue-property-decorator';
  import { CHANGE_HEADER_THEME } from '@/types';

  @Component({
    components: {},
  })
  export default class PageHeader extends Vue {
    @Prop(String)
    public title?: string;
    @Prop({ type: Boolean, default: false })
    public small?: boolean;

    private threshold: number[] = [];

    constructor() {
      super();
      for (let i = 0; i <= 1; i += 0.01) {
        this.threshold.push(i);
      }
    }

    public mounted(): void {
      const options = {
        threshold: this.threshold,
      };

      const callback: IntersectionObserverCallback = (entries) => {
        const [entry] = entries;
        if (entry.isIntersecting) {
          if (entry.boundingClientRect.top < -100) {
            this.$store.commit(CHANGE_HEADER_THEME, { color: 'white' });
          } else {
            this.$store.commit(CHANGE_HEADER_THEME, { color: 'default' });
          }
        }
      };
      const observer = new IntersectionObserver(callback, options);
      observer.observe(this.$el);
    }

  }
</script>
<style lang="scss" scoped>
    @import "../assets/colors";
    @import "../assets/sizes";
    @import "../assets/fonts";
    @import "../assets/media";

    .page-header__container {
        width: 100%;
        background-color: #000000;
        background-image: url('../assets/stars.png');
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
</style>
