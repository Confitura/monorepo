<template>
  <Box color="white" id="about-us">
    <div class="about-us">
      <h2 class="header">about us</h2>
      <div class="main-info">
        Organized by a group of highly-skilled, passionate Java developers,
        architects, and affiliates gathers Polish and International leaders of
        the community to share their knowledge and experience both, during
        sessions and breaks. Low attending fee makes Confitura accessible for
        everyone, what's more money from sales goes straight to charity
        organizations.
      </div>
      <div class="additional-info">
        Our main goal is to promote JVM technologies and have fun doing so.
      </div>
    </div>
  </Box>
</template>

<script lang="ts">
import { Component, Vue } from "vue-property-decorator";
import Box from "@/components/Box.vue";

@Component({
  components: { Box }
})
export default class HomeAbout extends Vue {}
</script>

<style scoped lang="scss">
@import "../assets/colors";
@import "../assets/fonts";
@import "../assets/media";

.about-us {
  display: grid;
  grid-row-gap: 2rem;
  grid-template-areas: "h " "mi" "ai";
  @include lg() {
    grid-column-gap: 8rem;
    grid-row-gap: 52px;
    align-content: center;
    grid-template-columns: 1fr 1fr;
    grid-template-areas: "h  h" "mi ai";
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
</style>
