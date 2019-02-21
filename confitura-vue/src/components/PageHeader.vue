<template>
    <header class="page-header__container">
        <div class="page-header">
            <h1 class="page-title">frequently asked questions</h1>
            <img class="page-image" src="../assets/planety_faq.svg" alt="planets">
        </div>
    </header>
</template>
<script lang="ts">
  import { Component, Vue } from 'vue-property-decorator';
  import { CHANGE_HEADER_THEME } from '@/types';

  @Component({
    components: {},
  })
  export default class PageHeader extends Vue {
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
    .page-header {
        box-sizing: border-box;
        display: flex;
        align-items: center;
        justify-content: space-between;
        height: 300px;
        padding: $standard-padding;
        position: relative;
        max-width: $max-width;
        margin: auto;
        @include md() {
            height: 480px;
            padding: $standard-padding $standard-padding $standard-padding 80px;

        }

        .page-title {
            text-align: left;
            color: #ffffff;
            font-size: 2.3rem;
            font-family: $font-bold;
            font-weight: bold;
            text-transform: capitalize;
            max-width: 400px;
        }

        .page-image {
            position: absolute;
            bottom: -280px;
            left: 200px;

            @include md() {
                position: relative;
                bottom: -120px;
                left: 0;
            }
        }
    }
</style>
