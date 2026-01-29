<template>
  <Box class="numbers" color="red" id="numbers">
    <div class="numbers-container">
      <div class="info">
        <h1 class="header">
          <span>confitura'25</span>
          <span> in numbers</span>
        </h1>
        <div class="subinfo">
          Everything started in 2007, when a group of passionate Java developers created a programming event for their local community.
<br />
          Today, Confitura brings together people from across Poland and beyond, with content in both Polish and English. We proudly mix talks from experienced experts and industry rockstars with presentations from new speakers who are just starting their journey and want to share something meaningful.
<br />
          We run Confitura as a non-profit, working year-round to deliver the best experience possible for our software community, because we believe it truly matters.
        </div>
      </div>
      <HomeNumbersPictogram
          :src="sessions"
          value="47"
          unit="presentations"
      />
      <HomeNumbersPictogram
        :src="speakers"
        value="65"
        unit="speakers"
      />
      <HomeNumbersPictogram
        :src="presentations"
        value="12"
        unit="workshops"
      />
      <HomeNumbersPictogram
        :src="views"
        value="1 mln +"
        unit="youtube channel views"
      />
      <HomeNumbersPictogram
        :src="videos"
        value="386"
        unit="youtube videos"
      />
      <HomeNumbersPictogram
        :src="participants"
        value="450"
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
