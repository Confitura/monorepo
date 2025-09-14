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
    <Modal v-if="user" @close="user = null">
      <div class="volunteer__modal">
        <div class="volunteers__member">
          <img
              :src="user.photo "
              :alt="user.name"
              class="volunteer__photo"
          />
          <div class="volunteer__info">
            <div class="volunteer__name">{{ user.name }}</div>
            <div class="volunteer__bio">{{ user.bio }}</div>
            <div class="volunteer__social">
              <SocialLink
                  type="twitter"
                  :id="user.twitter"
                  class="volunteer__socialLink"
              ></SocialLink>
              <SocialLink
                  type="github"
                  :id="user.github"
                  class="volunteer__socialLink"
              ></SocialLink>
              <SocialLink
                  type="www"
                  :id="user.www"
                  class="volunteer__socialLink"
              ></SocialLink>
            </div>
          </div>
        </div>
      </div>

    </Modal>
  </div>
</template>

<script setup lang="ts">
interface UserProfile {
  id?: string | number
  name: string
  photo?: string
  bio?: string
  twitter?: string
  github?: string
  www?: string
}

const {users = []} = defineProps<{ users: UserProfile[] }>()
let user = ref<UserProfile | null>(null)

function show(u: UserProfile) {
  user.value = u
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

.user__modal {
  flex-direction: row;
  justify-content: center;
  align-items: center;
  padding: 1rem;
  box-sizing: border-box;
  width: 50vw;
  height: 50vh;
}


.about__volunteers {
  //background-color: $brand;
  //color: #ffffff;
}

.volunteers__header,
.volunteers__header,
.bcc__header {
  font-weight: bold;
  font-size: 2rem;
  margin: 2rem;
  padding: 0;
  @include md() {
    font-size: 3rem;
  }
}

.volunteers__volunteers {
  display: grid;
  grid-template-columns: 1fr;
  grid-row-gap: 3rem;
  @include xl() {
    grid-template-columns: 1fr 1fr 1fr;
  }
}

.volunteers__volunteer {
  display: grid;
  grid-template-columns: 1fr;
  grid-template-areas: "photo" "info";
  @include md-to-xl() {
    grid-template-columns: 1fr 1fr;
    grid-template-areas: "photo info";
    &:nth-child(odd) {
      grid-template-areas: "info photo";
      text-align: end;
    }
  }
  @include xl() {
    grid-template-columns: 1fr 1fr;
    grid-template-areas: "photo info";
    &:nth-child(4n + 3),
    &:nth-child(4n + 4) {
      grid-template-areas: "info photo";
      text-align: end;
    }
  }
}


.volunteer__photo {
  width: 100%;
  height: 100%;
  aspect-ratio: 1/1;
  grid-area: photo;
  object-fit: cover;
}

.volunteer__info {
  padding: 1.5rem;
  width: 100%;
  grid-area: info;
  @include md() {
    padding: 1rem;
  }
}

.volunteer__name {
  font-size: 1.5rem;
  font-weight: bold;
  margin-bottom: 1rem;
}

.volunteer__bio {
  font-size: 1.3rem;
  line-height: 1.5rem;
  margin-bottom: 1rem;
  @include xl() {
    font-size: 1.1rem;
    line-height: 1.2rem;
  }
}

.volunteer__socialLink {
  color: black;
  font-size: 1.5rem;
  margin-right: 0.7rem;
}

.volunteer__modal {
  display: grid;
}

.volunteers__member {
  display: grid;
  grid-template-columns: 1fr;
  grid-template-areas: "photo" "info";
  @include md-to-xl() {
    grid-template-columns: 1fr 1fr;
    grid-template-areas: "photo info";
    &:nth-child(odd) {
      grid-template-areas: "info photo";
      text-align: end;
    }
  }
  @include xl() {
    grid-template-columns: 1fr 1fr;
    grid-template-areas: "photo info";
    &:nth-child(4n + 3),
    &:nth-child(4n + 4) {
      grid-template-areas: "info photo";
      text-align: end;
    }
  }
}
</style>
