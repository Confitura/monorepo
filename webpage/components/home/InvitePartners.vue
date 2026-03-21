<script setup lang="ts">
</script>
<template>
  <Box color="white" class="partners">
    <div class="partners-container">
      <h2 class="header">Partner with us</h2>
      <div class="main-info">
        <div class="body">
          Confitura 2026 is coming on <b>September 25–26</b>, and we’re currently working on delivering another
          outstanding event for our growing community. Now is the perfect moment to join us as a partner and become part
          of something truly special.
          <br/>
          <br/>
          As a partner, you’ll have the opportunity to:
          <ul>
            <li> Promote your products and services to a highly technical audience</li>
            <li> Engage directly with software professionals and enthusiasts</li>
            <li> Strengthen your employer brand and support your hiring goals</li>
          </ul>
          Get in touch to receive our sponsorship packages and details:
          <b>confitura[at]confitura[dot]pl</b>


        </div>
      </div>
    </div>
  </Box>
</template>

<style scoped lang="scss">
@use "~/assets/colors" as *;
@use "~/assets/fonts" as *;
@use "~/assets/media" as *;


.partners-container {

  display: grid;
  grid-row-gap: 2rem;
  grid-template-areas: "h" "mi";
  align-content: center;
  @include lg() {
    grid-column-gap: 8rem;
    grid-row-gap: 52px;
    grid-template-columns: 1fr;
    grid-template-areas: "h" "mi";
  }
}

.header {
  grid-area: h;
  font-family: $font-bold;
  font-size: 3rem;
  color: $brand;
}

.main-info {
  grid-area: mi;
  font-size: 1.75rem;
}

.additional-info {
  grid-area: ai;
  font-family: $font-bold;
  font-size: 1.75rem;
  color: $brand;
}

.main-info {
  .body {
    :deep(a) {

      &:link, &:visited, &:hover, &:active, &:-webkit-any-link {
        color: $brand !important;
      }
    }
  }
}


a:-webkit-any-link {
  color: #ee1f46 !important;
}
</style>
