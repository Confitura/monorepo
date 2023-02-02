<template>
  <Box class="numbers" color="red" id="numbers">
    <div class="numbers-container">
      <div class="info">
        <h1 class="header">
          <span>confitura'22</span>
          <span> in numbers</span>
        </h1>
        <div class="subinfo">
          Everything has started in 2007, when the group of passionate Java
          developers decided to create a programming event for the community.
          Today we're running the conference for more than 2000 attendees. 35
          talks in 5 parallel sessions give an opportunity to learn new things
          to everyone! Our live streams and recordings of talks gain enormous
          popularity on the web. That's why organizing this event gives
          satisfaction to more than 30 people from Confitura's team. That eagers
          us to organize another Confitura, this year it will be the 15th.
        </div>
      </div>
      <HomeNumbersPictogram
        :src="require('../assets/speakers.svg')"
        value="30"
        unit="speakers"
      />
      <HomeNumbersPictogram
        :src="require('../assets/sessions.svg')"
        value="4"
        unit="tracks"
      />
      <HomeNumbersPictogram
        :src="require('../assets/presentations.svg')"
        value="30"
        unit="presentations"
      />
      <HomeNumbersPictogram
        :src="require('../assets/views.svg')"
        value="over 1 mln"
        unit="youtube channel views"
      />
      <HomeNumbersPictogram
        :src="require('../assets/videos.svg')"
        value="323"
        unit="youtube videos"
      />
      <HomeNumbersPictogram
        :src="require('../assets/participants.svg')"
        value="1500"
        unit="participants"
      />
    </div>
  </Box>
</template>

<script lang="ts">
import HomeNumbersPictogram from "./HomeNumbersPictogram.vue";
import Box from "./Box.vue";

export default {
  name: "HomeNumbers",
  components: { Box, HomeNumbersPictogram }
};
</script>

<style scoped lang="scss">
@import "../assets/colors";
@import "../assets/fonts";
@import "../assets/media";

.numbers {
  background-color: $brand !important;
}

.numbers-container {
  display: grid;
  align-content: center;
  color: #ffffff;
  grid-row-gap: 4rem;
  @include lg() {
    grid-template-columns: 2fr 1fr 1fr 1fr;
    grid-row-gap: 3rem;
  }
}

.info {
  display: flex;
  flex-direction: column;
  justify-content: center;
  text-align: left;
  @include lg() {
    max-width: 450px;
    grid-row: 1 / span 2;
  }
}

.header {
  font-family: $font-bold;
  font-size: 3rem;
}

.subinfo {
  font-size: 1.75rem;
}
</style>
