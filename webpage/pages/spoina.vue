<template>
  <div class="faq">
    <PageHeader title="Spoina" />
    <Box class="content" color="white">
      <PageFragment class="spoina" name="spoina-page" />
    </Box>
    <Contact />
  </div>
</template>

<script setup lang="ts">

const title = 'Spoina - Integration Party — Confitura';
const description = 'Join us at Spoina - the integration party on the first day (Friday) of Confitura conference. Network and socialize with fellow attendees!';
useHead({
  title,
  meta: [
    { name: 'description', content: description },
    { property: 'og:title', content: title },
    { property: 'og:description', content: description },
    {property: 'og:type', content: 'event'},
    { name: 'twitter:card', content: 'summary' },
    { name: 'twitter:title', content: title },
    { name: 'twitter:description', content: description }
  ]
})
</script>

<style lang="scss" scoped>
@use "~/assets/colors" as *;
@use "~/assets/sizes" as *;
@use "~/assets/media" as *;
@use "~/assets/fonts" as *;

.faq {
  overflow: hidden;
}

.content {
  .foreword {
    text-align: left;
    color: #000000;
    font-size: 1.5rem;
    @include md() {
      width: 50%;
    }

    &__link {
      color: $brand;
    }
  }
}
</style>
<style lang="scss">
@use "~/assets/colors" as *;
@use "~/assets/sizes" as *;
@use "~/assets/media" as *;
@use "~/assets/fonts" as *;

.questions section {
  display: grid;
  text-align: left;
  grid-template-columns: 1fr;
  @include md() {
    grid-template-columns: 1fr 1fr;
  }

  h2 {
    font-size: 2.2rem;
    color: $brand;
    @include md() {
      grid-column: 1 / 2;
      &,
      + h3 {
        margin-top: 4rem;
        margin-bottom: 1rem;
      }
    }
  }

  h3 {
    font-size: 1.5rem;
    font-weight: bold;
    align-self: center;
    margin: 0 0 1rem;
    @include md() {
      grid-column: 2 / 3;
    }
  }

  p,
  ul {
    font-size: 1.5rem;
    margin: 0;
    @include md() {
      grid-column: 2 / 3;
    }

    + h3 {
      margin-top: 2em;
    }
  }

  h2 + {
    p,
    ul {
      margin-top: 2em;
    }
  }
}
</style>
