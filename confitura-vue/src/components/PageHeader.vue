<template>
  <header
    class="page-header__container"
    :class="{ 'page-header--crop': type === 'peace' }"
  >
    <div class="page-header" :class="{ 'page-header--small': small }">
      <h1 class="page-title">
        <template v-if="title">{{ title }}</template>
        <slot name="title"></slot>
      </h1>
      <slot v-if="!small && type === 'planets'">
        <img class="page-image" src="../assets/planety_faq.svg" alt="planets" />
      </slot>
      <slot v-if="type === 'coder'">
        <img
          src="../assets/small_planet.svg"
          class="header__planet"
          alt="small planet"
        />
        <img
          src="../assets/astronaut_comp.svg"
          class="header-img"
          alt="astronaut coder"
        />
      </slot>
      <slot v-if="type === 'peace'">
        <img src="../assets/astronaut.svg" class="header__peace" />
      </slot>
    </div>
  </header>
</template>
<script lang="ts">
import { Component, Prop, Vue } from "vue-property-decorator";
import { CHANGE_HEADER_THEME } from "@/types";

@Component({
  components: {}
})
export default class PageHeader extends Vue {
  @Prop(String)
  public title?: string;
  @Prop({ type: Boolean, default: false })
  public small?: boolean;
  @Prop({ default: "planets" })
  public type!: "planets" | "coder" | "peace";

  private threshold: number[] = [];

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
      const [entry] = entries;
      if (entry.isIntersecting) {
        if (entry.boundingClientRect.top < -100) {
          this.$store.commit(CHANGE_HEADER_THEME, { color: "white" });
        } else {
          this.$store.commit(CHANGE_HEADER_THEME, { color: "default" });
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
  background-image: url("../assets/stars.png");
}

.page-header--crop {
  overflow: hidden;
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

.header__planet {
  position: absolute;
  left: -40px;
  top: 60px;
  @include md() {
    position: unset;
    margin-left: 200px;
  }
}

.header-img {
  position: absolute;
  left: 170px;
  bottom: -50px;
  height: 280px;
  @include md() {
    position: unset;
    margin-top: 170px;
    margin-right: 150px;
    height: 350px;
  }
}

.header__peace {
  width: 500px;
  top: 50px;
  left: 60px;
  transform: rotate(-10deg);
  position: absolute;
  @include sm() {
    width: 500px;
    top: 0;
    position: unset;
    margin-top: 470px;
  }
}
</style>
