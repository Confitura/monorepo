<template>
    <section class="box">
        <div class="box__container">
            <slot/>
        </div>
    </section>
</template>

<script lang="ts">
  const HEADER_HEIGHT = 73;
  import { Component, Prop, Vue } from 'vue-property-decorator';
  import { CHANGE_HEADER_THEME } from '@/types';

  @Component({})
  export default class Box extends Vue {
    @Prop()
    public color?: string;
    private threshold: number[] = [];

    constructor() {
      super();
      for (let i = 0; i <= 1; i += 0.05) {
        this.threshold.push(i);
      }
    }

    public mounted(): void {
      const options = {
        threshold: this.threshold,
      };

      const callback: IntersectionObserverCallback = (entries) => {
        const entry = entries[0];
        if (entry.isIntersecting) {
          if (entry.boundingClientRect.top <= HEADER_HEIGHT && entry.boundingClientRect.bottom > HEADER_HEIGHT) {
            this.$store.commit(CHANGE_HEADER_THEME, { color: this.color });
          }
        }
      };
      const observer = new IntersectionObserver(callback, options);
      observer.observe(this.$el);
    }

  }
</script>

<style scoped lang="scss">
    @import "../assets/sizes";
    .box {
        min-height: 100vh;
        width: 100%;
        scroll-snap-align: start;
        display: flex;
        flex-direction: column;

        &__container {
            max-width: 1440px;
            width: 100%;
            margin: auto;
            padding: $standard-padding;
            box-sizing: border-box;
            text-align: left;
        }
    }
</style>