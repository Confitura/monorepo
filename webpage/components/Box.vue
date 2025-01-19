<template>
  <section class="box" :class="{ 'box--full': full }" ref="section">
    <div class="box__container">
      <slot />
    </div>
  </section>
</template>

<script setup lang="ts">
let section = ref(null)
const { full = true, color} = defineProps<{ full?: boolean, color?:string }>()

let threshold: number[] = []
for (let i = 0; i <= 1; i += 0.01) {
  threshold.push(i)
}

let observer: IntersectionObserver | null = null

const themeStore = useThemeStore()

onMounted(() => {
  const options = {
    threshold: threshold
  }

  const callback: IntersectionObserverCallback = entries => {
    const entry = entries[0]
    const height = themeStore.headerHeight
    if (entry.isIntersecting) {
      if (
        entry.boundingClientRect.top <= height &&
        entry.boundingClientRect.bottom > height
      ) {
        themeStore.changeHeaderTheme({ color: color })
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
@use "~/assets/sizes" as *;
@use "~/assets/media" as *;

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
