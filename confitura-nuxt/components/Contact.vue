<template>
  <Box class="contact" color="black" id="contact">
    <h1 class="header">stay connected</h1>
    <div class="contact__info">
      <div class="contact__mail">
        <a class="contact__link" href="mailto:confitura@confitura.pl"
          >confitura@confitura.pl</a
        >
        <router-link to="privacy-policy" class="contact__privacy"
          >Privacy policy</router-link
        >
        <SocialLinks class="mobile" />
      </div>
    </div>
  </Box>
</template>

<script setup lang="ts">

</script>

<style scoped lang="scss">
@use "~/assets/colors" as *;
@use "~/assets/fonts" as *;
@use "~/assets/media" as *;

.contact {
  background-color: #000000;

  .header {
    color: $brand;
    font-family: $font-bold;
    font-size: 3rem;
  }

  .contact__info {
    display: grid;
    grid-row-gap: 3rem;
    @include lg() {
      grid-template-columns: 1fr 1fr 1fr;
      grid-column-gap: 2rem;
    }
  }

  .contact__mail {
    grid-row: 1/ -3;
    display: flex;
    flex-direction: column;
  }

  .contact__link {
    font-size: 1.75rem;
    color: #ffffff;
    text-decoration: none;

    &:hover {
      text-decoration: underline;
    }
  }

  .contact__privacy {
    margin-top: 2rem;
    color: #ffffff;
    text-decoration: none;

    &:hover {
      text-decoration: underline;
    }
  }
}
</style>
