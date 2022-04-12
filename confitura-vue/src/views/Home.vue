<template>
  <div class="home__container">
    <HomeMainBanner />
    <HomeAbout />
    <HomeNumbers />
<!--        <HomePartners />-->
    <Contact />
  </div>
</template>

<script lang="ts">
import { Component, Vue } from "vue-property-decorator";
import HomeMainBanner from "@/components/HomeMainBanner.vue";
import HomeAbout from "@/components/HomeAbout.vue";
import HomeCovid19 from "@/components/HomeCovid19.vue";
import SocialLinks from "@/components/SocialLinks.vue";
import HomePartners from "@/components/HomePartners.vue";
import Contact from "@/components/Contact.vue";
import HomeNumbers from "@/components/HomeNumbers.vue";

@Component({
  components: {
    Contact,
    HomePartners,
    HomeNumbers,
    SocialLinks,
    HomeAbout,
    HomeCovid19,
    HomeMainBanner
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
