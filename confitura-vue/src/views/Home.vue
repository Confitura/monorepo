<template>
    <div class="home__container">
        <TheSocialLinks/>
        <TheMainBanner/>
        <!--<TheAbout/>-->
        <!--<TheAbout/>-->
        <!--<TheAbout/>-->
        <!--<TheAbout/>-->
        <!--<TheAbout/>-->

    </div>
</template>

<script lang="ts">
  import { Component, Vue } from "vue-property-decorator";
  import TheMainBanner from "@/components/TheMainBanner.vue";
  import TheAbout from "@/components/TheAbout.vue";
  import TheSocialLinks from "@/components/TheSocialLinks.vue";

  @Component({
    components: {
      TheSocialLinks,
      TheAbout,
      TheMainBanner
    }
  })
  export default class Home extends Vue {
  }
</script>

<style  lang="scss">

    /*body {*/
        /*scroll-snap-type: y proximity;*/
    /*}*/
    section {
        /*border-bottom: 1px solid white;*/
        /*padding: 1rem;*/
        /*height: 100vh;*/
        /*scroll-snap-align: start;*/
        /*text-align: center;*/
        /*position: relative;*/
    }

</style>
