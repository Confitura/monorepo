<template>
  <div class="usersGrid">
    <div class="user" v-for="user in users" :key="user.id" @click="show(user)">
      <img :src="user.photo " alt="" class="user__photo" />
      <div class="user__name">
        <span>{{ firstName(user.name) }}</span>
        <span>{{ lastName(user.name) }}</span>
      </div>
    </div>
  </div>
</template>
<script setup lang="ts">

const { users = [] } = defineProps<{ users?: [] }>()

function show({ id }) {
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
<style lang="scss" scoped>
@use "~/assets/colors" as *;
@use "~/assets/sizes" as *;
@use "~/assets/media" as *;
@use "~/assets/fonts" as *;

.usersGrid {
  display: flex;
  flex-direction: column;
  justify-content: center;
  @include md() {
    flex-direction: row;
    flex-wrap: wrap;
  }
}

.user {
  display: flex;
  cursor: pointer;

  &:hover {
    background-color: $brand;
    color: #ffffff;
    transition: all 0.3s linear;
  }

  @include sm-only() {
    &:nth-child(odd) {
      flex-direction: row-reverse;

      .user__name {
        align-items: flex-end;
      }
    }
  }
  @include md-to-xl() {
    &:nth-child(4n + 3),
    &:nth-child(4n + 4) {
      flex-direction: row-reverse;

      .user__name {
        align-items: flex-end;
      }
    }
  }
  @include xl() {
    &:nth-child(6n + 4),
    &:nth-child(6n + 5),
    &:nth-child(6n + 6) {
      flex-direction: row-reverse;

      .user__name {
        align-items: flex-end;
      }
    }
  }
}

.user__photo {
  width: 50vw;
  height: 50vw;
  object-fit: cover;
  background-color: $brand;
  @include md() {
    width: 200px;
    height: 200px;
  }
}

.user__name {
  display: flex;
  flex-direction: column;
  justify-content: center;
  font-family: $font-bold;
  font-size: 1.7rem;
  padding: 1rem;
  box-sizing: border-box;
  @include md() {
    width: 200px;
    height: 200px;
  }
}
</style>
