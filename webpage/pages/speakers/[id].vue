<template>
  <div class="speaker">
    <PageHeader title="Speakers" type="coder"> </PageHeader>
    <Box class="content" color="white" :full="false">
      <div v-if="speaker" class="speaker__container">
        <div class="speaker__left">
          <img :src="speaker.photo" class="speaker__photo" />
          <div class="speaker__social">
            <SocialLink
              class="speaker__social-link"
              type="twitter"
              :id="speaker.twitter"
            ></SocialLink>
            <SocialLink
              class="speaker__social-link"
              type="facebook"
              :id="speaker.facebook"
            ></SocialLink>
            <SocialLink
              class="speaker__social-link"
              type="github"
              :id="speaker.github"
            ></SocialLink>
            <SocialLink
              class="speaker__social-link"
              type="www"
              :id="speaker.www"
            ></SocialLink>
          </div>
        </div>
        <div class="speaker__right">
          <div class="speaker__name">{{ speaker.name }}</div>
          <div class="speaker__bio">
            {{ speaker.bio }}
          </div>
        </div>
      </div>
    </Box>
    <Contact />
  </div>
</template>

<script setup lang="ts">
import { useAPIFetch } from '~/composables/useAPIFetch'

let route = useRoute()
let { data: speaker } = useAPIFetch(`/users/${route.params.id}/public.json`)
</script>

<style lang="scss" scoped>
@use "~/assets/colors" as *;
@use "~/assets/sizes" as *;
@use "~/assets/media" as *;
@use "~/assets/fonts" as *;

.speaker {
  overflow: hidden;
}

.speaker__container {
  display: flex;
  flex-direction: column;
  @include md() {
    flex-direction: row;
  }
}
.speaker__left {
  flex-shrink: 0;
}
.speaker__right {
  @include md() {
    margin-left: 3rem;
  }
}

.speaker__photo {
  width: calc(100vw - 50px);
  height: calc(100vw - 50px);
  object-fit: cover;
  box-sizing: border-box;
  @include md() {
    width: 400px;
    height: 400px;
  }
}

.speaker__name {
  font-family: $font-bold;
  font-size: 2.5rem;
  color: $brand;
  margin-top: 2rem;
  @include md() {
    margin-top: 0;
    font-size: 3rem;
  }
}

.speaker__bio {
  font-size: 1.2rem;
  line-height: 1.7rem;
  margin-top: 2rem;
  @include md() {
    font-size: 1.5rem;
    line-height: 2rem;
  }
}

.speaker__social {
  margin-top: 2rem;
  font-size: 3rem;
  @include md() {
    text-align: center;
  }

  &-link {
    margin-right: 2rem;
  }
}
</style>
