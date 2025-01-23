<template>
  <Box class="numbers" color="red" id="numbers">
    <div class="numbers-container">
      <div class="info">
        <h1 class="header">
          <span>confitura'23</span>
          <span> in numbers</span>
        </h1>
        <div class="subinfo">
          Everything has started in 2007, when the group of passionate Java
          developers decided to create a programming event for the community.
          Today we're running the conference for more than 1000 attendees. 30
          talks in 4 parallel sessions give an opportunity to learn new things
          to everyone! Our live streams and recordings of talks gain enormous
          popularity on the web. That's why organizing this event gives
          satisfaction to more than 30 people from Confitura's team. That eagers
          us to organize another Confitura, this year it will be the 15th.
        </div>
      </div>
      <HomeNumbersPictogram
        :src="speakers"
        value="32"
        unit="speakers"
      />
      <HomeNumbersPictogram
        :src="sessions"
        value="4"
        unit="tracks"
      />
      <HomeNumbersPictogram
        :src="presentations"
        value="30"
        unit="presentations"
      />
      <HomeNumbersPictogram
        :src="views"
        value="1 mln +"
        unit="youtube channel views"
      />
      <HomeNumbersPictogram
        :src="videos"
        value="338"
        unit="youtube videos"
      />
      <HomeNumbersPictogram
        :src="participants"
        value="1300"
        unit="participants"
      />
    </div>
  </Box>
</template>

<script lang="ts" setup>
import speakers from 'assets/speakers.svg'
import sessions from 'assets/sessions.svg'
import presentations from 'assets/presentations.svg'
import views from 'assets/views.svg'
import videos from 'assets/videos.svg'
import participants from 'assets/participants.svg'
</script>

<style scoped lang="scss">
@use "~/assets/colors" as *;
@use "~/assets/fonts" as *;
@use "~/assets/media" as *;

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
