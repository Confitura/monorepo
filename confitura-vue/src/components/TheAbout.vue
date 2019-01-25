<template>
    <Box>
        <div class="about-us">
            <h2 class="header">about us</h2>
            <div class="main-info">We are a group of highly-skilled, passionate Java developers, architects and affiliates organising the
                Confitura Java
                conference for the Polish Java community.
                It is the place where Polish and International leaders of the Java community share their Java knowledge and experience
                during
                sessions and breaks.
            </div>
            <div class="additional-info">Our main goal is to promote Java technologies within
                the Polish Java community
                and have fun doing so.
            </div>
        </div>
    </Box>
</template>

<script lang="ts">
  import { Component, Vue } from 'vue-property-decorator';
  import Box from '@/components/Box.vue';

  @Component({
    components: { Box },
  })
  export default class TheAbout extends Vue {

  }
</script>

<style scoped lang="scss">
    @import "../assets/colors";
    @import "../assets/fonts";

    .about-us {
        display: grid;
        grid-column-gap: 8rem;
        grid-row-gap: 52px;
        align-content: center;
        grid-template-columns: 1fr 1fr;
        grid-template-areas: "h  h" "mi ai";
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