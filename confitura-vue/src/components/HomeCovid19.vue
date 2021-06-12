<template>
    <Box color="white" id="covid-19">
        <div class="about-us"><h2
                class="header">Confitura 2021 canceled</h2>
            <div class="main-info">
                We regret to inform you that Confitura 2021 has been canceled. The second year in a row, the conference will not take place due to the Corona Pandemic.
                We strongly believe that after those two tough years, we will back stronger than ever!
                Hope to see you in 2022! Stay safe and take the sentimental journey through the last eight editions of Confitura. Hop on our
                <a href="https://www.youtube.com/user/confiturapl"
                   target="_blank">
                    youtube channel
                </a>
                .
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
export default class HomeCovid19 extends Vue {}
</script>

<style scoped lang="scss">
@import "../assets/colors";
@import "../assets/fonts";
@import "../assets/media";

.about-us {
  display: grid;
  grid-row-gap: 2rem;
  grid-template-areas: "h " "mi";
  @include lg() {
    grid-column-gap: 8rem;
    grid-row-gap: 52px;
    align-content: center;
    grid-template-columns: 1fr
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
