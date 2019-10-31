<template>
  <section class="box" :class="{ 'box--full': full }">
    <div class="box__container">
      <slot />
    </div>
  </section>
</template>

<script lang="ts">
import { Component, Prop, Vue } from "vue-property-decorator";
import { CHANGE_HEADER_THEME } from "@/types";

@Component({})
export default class Box extends Vue {
  @Prop()
  public color?: string;
  @Prop({ default: true })
  public full!: boolean;
  private threshold: number[] = [];
  private observer: IntersectionObserver | null = null;

  constructor() {
    super();
    for (let i = 0; i <= 1; i += 0.01) {
      this.threshold.push(i);
    }
  }

  public mounted(): void {
    const options = {
      threshold: this.threshold
    };

    const callback: IntersectionObserverCallback = entries => {
      const entry = entries[0];
      const height = this.$store.state.headerHeight;
      if (entry.isIntersecting) {
        if (
          entry.boundingClientRect.top <= height &&
          entry.boundingClientRect.bottom > height
        ) {
          this.$store.commit(CHANGE_HEADER_THEME, { color: this.color });
        }
      }
    };
    this.observer = new IntersectionObserver(callback, options);
    this.observer.observe(this.$el);
  }

  protected beforeDestroy() {
    if (this.observer !== null) {
      this.observer.disconnect();
    }
  }
}
</script>

<style scoped lang="scss">
@import "../assets/sizes";
@import "../assets/media";

.box {
  width: 100%;
  display: flex;
  flex-direction: column;

  &--full {
    min-height: 100vh;
  }

  &.no-padding {
    & .box__container {
      padding: 0;
    }
  }

  &.min-padding {
    & .box__container {
      padding: 1rem;
    }
  }

  &__container {
    max-width: 1440px;
    width: 100%;
    margin: auto;
    box-sizing: border-box;
    text-align: left;
    @include padding();
    @include lg() {
      padding-top: $standard-padding;
    }
  }
}
</style>
