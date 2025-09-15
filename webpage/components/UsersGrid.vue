<template>
  <div class="usersGrid">
    <div
      class="user"
      v-for="user in users"
      :key="user.id"
      @click="show(user)"
    >
      <img :src="user.photo" alt="" class="user__photo" loading="lazy"/>
      <div class="user__name">
        <span>{{ firstName(user.name) }}</span>
        <span>{{ lastName(user.name) }}</span>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
interface UserProfile {
  id?: string | number
  name: string
  photo?: string
}

const { users = [] } = defineProps<{ users: UserProfile[] }>()

function show({ id }: UserProfile) {
  if (id !== undefined && id !== null && id !== '') {
    navigateTo({ path: `/speakers/${id}` })
  }
}

function firstName(value: string) {
  const name = value || ''
  const idx = name.indexOf(' ')
  return idx === -1 ? name : name.substring(0, idx)
}

function lastName(value: string) {
  const name = value || ''
  const idx = name.indexOf(' ')
  return idx === -1 ? '' : name.substring(idx + 1)
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
        text-align: right;
        span {
            text-align: right;
        }
      }
    }
  }
  @include md-to-xl() {
    &:nth-child(4n + 3),
    &:nth-child(4n + 4) {
      flex-direction: row-reverse;

      .user__name {
        align-items: flex-end;
        text-align: right;
        span {
          text-align: right;
        }
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
        text-align: right;
        span {
          text-align: right;
        }
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
