<template>
  <div class="pictogram">
    <div class="pictogram__icon-container">
      <img class="pictogram__icon" :src="src" alt="" />
    </div>
    <span class="pictogram__value">{{ value }}</span>
    <span class="pictogram__unit">{{ unit }}</span>
  </div>
</template>
<script setup lang="ts">

const { src, value, unit } = defineProps<{ src: string, value: string, unit: string }>()

</script>
<style scoped lang="scss">
@use "~/assets/colors" as *;
@use "~/assets/fonts" as *;

.pictogram {
  display: flex;
  flex-direction: column;
  align-items: center;

  &__icon-container {
    flex-grow: 1;
    display: flex;
    justify-content: center;
    align-items: center;
  }

  &__icon {
    margin-bottom: 31px;
  }

  &__value {
    font-size: 4rem;
    font-family: $font-bold;
  }

  &__unit {
    font-size: 1.125rem;
  }
}
</style>
