<template>
  <section class="banner" id="home" ref="section">
    <!--    <HomeMainBannerStars />-->
    <div class="container">
      <div class="info">
        <div class="slogan">
          <h1 class="h1">Confitura 2025 - Escape the Loop. Join the Revolution.</h1>
        </div>
        <div class="info__live">
          <div class="info__live-text">> Help us pick the presentations <</div>
          <a class="info__live-link" href="https://app.confitura.pl/vote-for-papers" target="_blank">vote here</a>
        </div>
        <div class="info__long">
          <p> Warsaw | September 19–20, 2025 </p>
          <TheTimer/>
          <p>ADN Conference Center, Grzybowska 56</p>
        </div>
        <div class="info__long">
          <small>
            Want to sponsor Confitura? Drop us a line:
            <a href="mailto:confitura[at]confitura[dot]pl">confitura[at]confitura[dot]pl</a>
            - let’s team up!
          </small>
        </div>
      </div>
      <HomeMainBannerYtVideo/>
    </div>
    <!--    <img class="scroll-indicator-icon" src="~/assets/rocket.svg" alt="rocket"/>-->
    <div class=" scroll-indicator-icon">
      <i class="fa-solid fa-arrow-down"></i>
    </div>
  </section>
</template>

<script setup lang="ts">
import dayjs from 'dayjs'

//TODO let date: string = dayjs(this.$store.state.date).format('DD.MM.YYYY')

let section = ref(null)
let date: string = dayjs('2025-06-24T09:00').format('DD.MM.YYYY')
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
  background-color: #000000;
  background-image: url("~/assets/confitura tlo 2.png");
  background-repeat: no-repeat;
  background-size: cover;
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
  backdrop-filter: blur(10px);
  padding: 2rem;
  z-index: 20;
  text-align: left;
  display: grid;
  grid-row-gap: 2rem;
  @include md() {
    margin-left: 50px;
    grid-row-gap: 4rem;
  }

  @include sm-only() {
    font-size: 0.8em;

    .slogan {
      font-size: 1.6rem;
    }

    .info__live-text {
      font-size: 2rem;
    }

    .info__live-link {
      font-size: 1.6rem;
    }

    .info__long {
      font-size: 1.36rem;
    }
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

.scroll-indicator-icon {
  font-size: 40px;
  width: 1em;
  color: white;
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

a {
  color: $brand;
}

.info__live-text {
  color: white;
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
