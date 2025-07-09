<template>
  <div class="speakers">
    <div
      v-for="speaker in speakers"
      :key="speaker.id"
      class="speaker"
      @click="show(speaker)"
    >
      <EventualImage :src="speaker.photo" class="speaker__photo" />
      <div class="speaker__name">
        <span>{{ firstName(speaker.name) }}</span>
        <span>{{ lastName(speaker.name) }}</span>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
const { speakers = [] } = defineProps<{ speakers: [] }>()

function show({ id }: UserProfile) {
  if (id) {
    navigateTo({ path: `speakers/${id}` })
  }
}

function firstName(value: string) {
  const name = value || ''
  const idx = name.indexOf(' ')
  return name.substring(0, idx)
}

function lastName(value: string) {
  const name = value || ''
  const idx = name.indexOf(' ')
  return name.substring(idx)
}
</script>

<style scoped lang="scss">
@use "~/assets/colors" as *;
@use "~/assets/sizes" as *;
@use "~/assets/media" as *;
@use "~/assets/fonts" as *;

.speakers {
  display: flex;
  flex-direction: column;
  @include md() {
    flex-direction: row;
  }
}

.speaker {
  display: flex;
  justify-items: center;
  align-items: center;
  margin-right: 3rem;
  margin-bottom: 2rem;
  cursor: pointer;

  &__photo {
    flex-grow: 0;
    width: 45px;
    height: 45px;
    object-fit: cover;
    margin-right: 1rem;
    background-color: #000000;
  }

  &__name {
    display: flex;
    flex-direction: column;
    font-size: 1rem;
    font-weight: bold;
  }
}
</style>
