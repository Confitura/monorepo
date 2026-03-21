<template>
  <div class="timer" :key="seconds">
    <div class="timer-part">
      <span class="timer-value">{{ padValue(days) }}</span>
      <span class="timer-unit">days</span>
    </div>
    <span class="timer-part-separator"></span>
    <div class="timer-part">
      <span class="timer-value">{{ padValue(hours) }}</span>
      <span class="timer-unit">hours</span>
    </div>
    <span class="timer-part-separator"></span>
    <div class="timer-part">
      <span class="timer-value">{{ padValue(minutes) }}</span>
      <span class="timer-unit">minutes</span>
    </div>
    <span class="timer-part-separator"></span>
    <div class="timer-part">
      <span class="timer-value">{{ padValue(seconds) }}</span>
      <span class="timer-unit">seconds</span>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onBeforeUnmount } from 'vue'
import dayjs from 'dayjs'

// Target date for the countdown
const start = ref(dayjs('2026-09-25T09:00:00')) // Using the date from MainBanner.vue (June 24, 2025)
const now = ref(dayjs())
const days = ref(0)
const hours = ref(0)
const minutes = ref(0)
const seconds = ref(0)
let intervalHandle: number | null = null

// Pad single digit numbers with a leading zero
const padValue = (value: number): string => {
  return `${value}`.length === 1 ? `0${value}` : `${value}`
}

// Recalculate the time difference
const recalculate = () => {
  now.value = dayjs()
  days.value = start.value.diff(now.value, 'day')
  hours.value = start.value.subtract(days.value, 'day').diff(now.value, 'hour')
  minutes.value = start.value
    .subtract(days.value, 'day')
    .subtract(hours.value, 'hour')
    .diff(now.value, 'minute')
  seconds.value = start.value
    .subtract(days.value, 'day')
    .subtract(hours.value, 'hour')
    .subtract(minutes.value, 'minute')
    .diff(now.value, 'second')
}

onMounted(() => {
  recalculate()
  intervalHandle = window.setInterval(() => {
    recalculate()
  }, 1000)
})

onBeforeUnmount(() => {
  if (intervalHandle !== null) {
    clearInterval(intervalHandle)
  }
})
</script>

<style scoped lang="scss">
@use "~/assets/fonts" as *;
@use "~/assets/media" as *;
@use "~/assets/colors" as *;

.timer {
  color: $brand;
  display: flex;
  align-self: center;
}

.timer-part {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.timer-value {
  font-family: $font-bold;
  font-size: 3rem;
}

.timer-part-separator:after {
  content: ":";
  font-size: 3rem;
  font-weight: bold;
  margin-left: 10px;
  margin-right: 10px;
}

.timer-unit {
}
</style>
