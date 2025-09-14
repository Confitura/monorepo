<template>
  <Box color="red" class="about__committee no-padding">
    <h3 class="committee__header">organizers</h3>
    <div class="committee__members">
      <div
          class="committee__member"
          v-for="member in committee"
          :key="member.id"
      >
        <img
            :src="member.photo "
            :alt="member.name"
            class="member__photo"
        />
        <div class="member__info">
          <div class="member__name">{{ member.name }}</div>
          <div class="member__bio">{{ member.bio }}</div>
          <div class="member__social">
            <SocialLink
                type="twitter"
                :id="member.twitter"
                theme="white"
                class="member__socialLink"
            ></SocialLink>
            <SocialLink
                type="github"
                :id="member.github"
                theme="white"
                class="member__socialLink"
            ></SocialLink>
            <SocialLink
                type="www"
                :id="member.www"
                theme="white"
                class="member__socialLink"
            ></SocialLink>
          </div>
        </div>
      </div>
    </div>
  </Box>

</template>
<script setup lang="ts">
const {committee = []} = defineProps<{ committee: UserProfile[] }>()

interface UserProfile {
  id?: string | number
  name: string
  photo?: string
  bio?: string
  twitter?: string
  github?: string
  www?: string
}
</script>
<style scoped lang="scss">
@use "~/assets/colors" as *;
@use "~/assets/sizes" as *;
@use "~/assets/media" as *;
@use "~/assets/fonts" as *;



.about__committee {
  background-color: $brand;
  color: #ffffff;
}

.committee__header,
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

.committee__members {
  display: grid;
  grid-template-columns: 1fr;
  grid-row-gap: 3rem;
  @include xl() {
    grid-template-columns: 1fr 1fr;
  }
}

.committee__member {
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

.member__photo {
  width: 100%;
  height: auto;
  grid-area: photo;
  object-fit: cover;
}

.member__info {
  padding: 1.5rem;
  width: 100%;
  grid-area: info;
  @include md() {
    padding: 1rem;
  }
}

.member__name {
  font-size: 1.5rem;
  font-weight: bold;
  margin-bottom: 1rem;
}

.member__bio {
  font-size: 1.3rem;
  line-height: 1.5rem;
  margin-bottom: 1rem;
  @include xl() {
    font-size: 1.1rem;
    line-height: 1.2rem;
  }
}

.member__socialLink {
  color: #ffffff;
  font-size: 1.5rem;
  margin-right: 0.7rem;
}



</style>