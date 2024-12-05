<template>
  <section class="banner" id="home" ref="section">
    <HomeMainBannerStars />
    <div class="container">
      <div class="info">
        <div class="slogan">
          <h1 class="h1">It is not rocket science!</h1>
        </div>
        <!--        <TheTimer />-->
        <div class="info__live">
          <div class="info__live-text">CONFITURA 2025</div>
          <!--          <a class="info__live-link" href="https://2023.confitura.pl/v4p" target="_blank">vote here</a>-->
        </div>

        <div class="info__long">
          We look forward to seeing you again in 2025!
        </div>
        <div class="info__long">
          If your company wants to join us for next year's edition, here is our contact mail:
          <a href="mailto:confitura[at]confitura[dot]pl"
          >confitura[at]confitura[dot]pl</a
          >; we will be happy to start a new friendship.
        </div>
      </div>
      <HomeMainBannerIllustration />
    </div>
    <img class="rocket-icon" src="~/assets/rocket.svg" alt="rocket" />
  </section>
</template>

<script setup lang="ts">
import dayjs from 'dayjs'

//TODO let date: string = dayjs(this.$store.state.date).format('DD.MM.YYYY')

let section = ref(null)
let date: string = dayjs("2025-06-24T09:00").format('DD.MM.YYYY')
let threshold: number[] = []
let observer: IntersectionObserver | null = null


for (let i = 0; i <= 1; i += 0.01) {
  threshold.push(i)
}


onMounted(() => {
  const options = {
    threshold: threshold
  }
  const callback: IntersectionObserverCallback = entries => {
    const [entry] = entries
    if (entry.isIntersecting) {
      if (entry.boundingClientRect.top < -100) {
        //TODO this.$store.commit(CHANGE_HEADER_THEME, { color: "white" });
      } else {
        //TODO this.$store.commit(CHANGE_HEADER_THEME, { color: "default" });
      }
    }
  }
  observer = new IntersectionObserver(callback, options)
  observer.observe(section.value)
})

onBeforeUnmount(() => {
  if (observer !== null) {
    observer.disconnect()
  }
})
</script>

<style scoped lang="scss">
@use "~/assets/fonts" as *;
@use "~/assets/sizes" as *;
@use "~/assets/media" as *;
@use "~/assets/colors" as *;

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

.info__long {
  color: #ffffff;
  font-size: 1.7rem;
  word-wrap: break-word;
  @include md() {
    max-width: 30vw;
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
