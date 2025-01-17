<template>
  <Box color="white" id="about-us">
    <div class="about-us">
      <h2 class="header">About Confitura
      </h2>
      <div class="main-info">
        <p>
          Confitura is one of Poland’s premier and longest-running software conferences. Organized by a dedicated team
          of Java developers, architects, and industry professionals, the conference features a comprehensive program of
          sessions and workshops led by both Polish and international experts. Covering a broad spectrum of topics,
          Confitura addresses everything from advanced coding practices to the latest trends in software development.
        </p>
        <p>
          The conference is renowned for its inclusive and welcoming atmosphere, ensuring accessibility for
          professionals at every stage of their career. With multiple tracks available, attendees can customize their
          experience based on their areas of interest and expertise. Additionally, Confitura remains committed to
          keeping its attendance fees affordable, fostering wider participation.
        </p>
        <p>
          Beyond the technical sessions, Confitura offers extensive networking opportunities during breaks and social
          events, promoting meaningful connections between attendees, speakers, and industry leaders. The conference’s
          unwavering focus on quality content and community engagement has established it as a cornerstone event in
          Poland’s IT sector.
        </p>
      </div>
      <div class="additional-info">
        Our main goal is to promote JVM technologies and have fun doing so.
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
