<template>
  <div class="home__container">
    <TheMainBanner id="home" />
    <TheAbout id="about-us" />
    <TheNumbers id="numbers" />
    <ThePartners id="partners" />
    <TheContact id="contact" />
  </div>
</template>

<script lang="ts">
import { Component, Vue } from "vue-property-decorator";
import TheMainBanner from "@/components/TheMainBanner.vue";
import TheAbout from "@/components/TheAbout.vue";
import TheSocialLinks from "@/components/TheSocialLinks.vue";
import TheNumbers from "@/components/TheNumbers.vue";
import ThePartners from "@/components/ThePartners.vue";
import TheContact from "@/components/TheContact.vue";

@Component({
  components: {
    TheContact,
    ThePartners,
    TheNumbers,
    TheSocialLinks,
    TheAbout,
    TheMainBanner
  }
})
export default class Home extends Vue {}
</script>

<style lang="scss">
@supports not (-webkit-overflow-scrolling: touch) {
  .home__container {
    max-height: 100vh;
    overflow-y: scroll;
    scroll-snap-type: y proximity;

    & > section {
      scroll-snap-align: start;
    }
  }
}
</style>
