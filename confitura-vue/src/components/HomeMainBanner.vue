<template>
  <section class="banner" id="home">
    <HomeMainBannerStars />
    <div class="container">
      <div class="info">
        <div class="slogan">
          <h1 class="h1">It is not rocket science!</h1>
        </div>
        <!--        <div class="info__live">-->
        <!--          <div class="info__live-text">Thank you!</div>-->
        <!--        </div>-->
        <TheTimer />
        <div class="time-and-place">
          <div>{{ date }},&nbsp;</div>
          <div class="place">
            Warsaw Expo XXI
            <a
              class="place__link"
              href="https://www.google.com/maps/place/Warszawskie+Centrum+EXPO+XXI/@52.224893,20.9599152,17z/data=!3m1!4b1!4m5!3m4!1s0x471ecb60b93336e5:0xf7d1cb012bb9954d!8m2!3d52.224893!4d20.962104"
              target="_blank"
              rel="noopener"
            >
              <i class="fas fa-map-marker-alt"></i>
            </a>
          </div>
        </div>
      </div>
      <HomeMainBannerIllustration />
    </div>
    <img class="rocket-icon" src="../assets/rocket.svg" alt="rocket" />
  </section>
</template>

<script lang="ts">
import { Component, Vue } from "vue-property-decorator";
import TheTimer from "@/components/TheTimer.vue";
import HomeMainBannerIllustration from "@/components/HomeMainBannerIllustration.vue";
import { CHANGE_HEADER_THEME } from "@/types";
import dayjs from "dayjs";
import HomeMainBannerStars from "@/components/HomeMainBannerStars.vue";

@Component({
  components: { HomeMainBannerStars, HomeMainBannerIllustration, TheTimer }
})
export default class HomeMainBanner extends Vue {
  public date: string = dayjs(this.$store.state.date).format("DD.MM.YYYY");
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
      const [entry] = entries;
      if (entry.isIntersecting) {
        if (entry.boundingClientRect.top < -100) {
          this.$store.commit(CHANGE_HEADER_THEME, { color: "white" });
        } else {
          this.$store.commit(CHANGE_HEADER_THEME, { color: "default" });
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
@import "../assets/fonts";
@import "../assets/sizes";
@import "../assets/media";
@import "../assets/colors";

.banner {
  box-sizing: border-box;
  min-height: calc(100vh - 15vh);
  padding-top: 15vh;
  overflow: hidden;
  padding-left: $standard-padding;
  padding-right: $standard-padding;
  position: relative;
  @include md() {
    height: 100vh;
    padding-top: 0;
  }
}

.container {
  max-width: $max-width;
  margin: auto;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  height: 100%;
  align-items: center;
  z-index: 20;
  min-height: 80vh;
  position: relative;
  @include md() {
    flex-direction: row;
    min-height: unset;
  }
}

.info {
  z-index: 20;
  text-align: left;
  display: grid;
  grid-row-gap: 2rem;
  @include md() {
    margin-left: 50px;
    grid-row-gap: 4rem;
  }
}

.slogan {
  font-family: $font-bold;
  color: #ffffff;
  font-size: 2rem;
  @include xxl() {
    font-size: 3.75rem;
  }
}

.sub-slogan {
  font-family: $font-bold;
  color: #ffffff;
  font-size: 1.3rem;
  padding-top: 1rem;
}

.time-and-place {
  color: #ffffff;
  display: flex;
  flex-direction: column;
  font-size: 1.7rem;
  @include md() {
    flex-direction: row;
    font-size: 1.5rem;
  }
}

.place {
  display: flex;
  white-space: nowrap;
}

.place__link {
  margin-left: 10px;
  color: #ffffff;
}

.rocket-icon {
  display: none;
  @include md() {
    display: block;
    position: relative;
    bottom: 5rem;
    z-index: 2;
    animation: rocket-jump 2s 3;
    animation-delay: 5s;
    margin: auto;

    @keyframes rocket-jump {
      0% {
        transform: translateY(0);
      }
      50% {
        transform: translateY(-10px);
      }
      100% {
        transform: translateY(0px);
      }
    }
  }
}

.info__live {
  color: $brand;
}
.info__live-text {
  text-transform: uppercase;
  font-weight: bold;
  font-size: 2.5rem;
}
.info__live-link {
  font-size: 2rem;
}

.slogan {
  .h1 {
    font-size: 2rem;
  }
  .h2 {
    font-size: 1.7rem;
  }
}
</style>
