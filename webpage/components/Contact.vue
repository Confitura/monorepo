<template>
  <Box class="contact" color="black" id="contact">
    <h1 class="header">stay connected</h1>
    <div class="contact__info">
      <div class="contact__mail">
        <a class="contact__link" href="mailto:confitura@confitura.pl">confitura@confitura.pl</a>
        <div class="contact__socials">
          <span>
            newsletter:
          <a title="newsletter" href="https://mailchi.mp/1811b15cffa4/confitura-newsletter" target="_blank"
             rel="noopener" class="link"> <i
              class="fas fa-envelope-square"></i> </a>
            </span>
          <span>
          socials:
          <a title="x" href="https://x.com/confiturapl" target="_blank" rel="noopener" class="link"> <i
              class="fa-brands fa-square-x-twitter"></i> </a>
          <a title="facebook" href="https://www.facebook.com/Confitura" target="_blank" rel="noopener" class="link"> <i
              class="fab fa-facebook-square"></i> </a>
          <a title="linkedin" href="https://www.linkedin.com/company/confitura" target="_blank" rel="noopener"
             class="link"> <i
              class="fab fa-linkedin"></i> </a>
          <a title="youtube" href="https://www.youtube.com/confiturapl" target="_blank" rel="noopener" class="link"> <i
              class="fab fa-youtube-square"></i> </a>
            </span>
        </div>
        <router-link to="privacy-policy" class="contact__privacy">Privacy policy</router-link>
        <SocialLinks class="mobile"/>
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

  .contact__socials {
    color: #ffffff;

    .link {
      padding-right: 0.5rem;
      color: #ffffff;

      :hover {
        color: $brand;
      }
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
