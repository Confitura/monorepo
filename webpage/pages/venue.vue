<template>
  <div class="venue__page">
    <PageHeader title="Venue" type="coder"/>
    <Box color="white" :full="false">
      <PageFragment class="questions" name="venue-page"/>
    </Box>
    <Box color="white">
      <GoogleMap
          :center="center"
          :zoom="16"
          class="venue__map"
          :api-key="mapApiKey"
          map-id="9b409aff2a4005d9633060b5"
      >
        <AdvancedMarker :options="markerOptions">
          <template #content>
            <img src="~/assets/marker.svg" alt="marker" class="marker__image"/>
          </template>
        </AdvancedMarker>

      </GoogleMap>

    </Box>
    <Contact/>
  </div>
</template>

<script setup lang="ts">

import {GoogleMap, AdvancedMarker} from 'vue3-google-map'

let center = {lat: 52.23522478133525, lng: 20.987968556704143}

let markerOptions = {
  position: center,
}


const config = useRuntimeConfig();
const mapApiKey = config.public.googleMapsApiKey;

const title = 'Venue — Confitura 2025 Location & Directions';
const description = 'Find all information about the Confitura 2025 venue: location, directions, and logistics for your visit.';
useHead({
  title,
  meta: [
    {name: 'description', content: description},
    {property: 'og:title', content: title},
    {property: 'og:description', content: description},
    {property: 'og:type', content: 'website'},
    {name: 'twitter:card', content: 'summary'},
    {name: 'twitter:title', content: title},
    {name: 'twitter:description', content: description}
  ]
})

</script>

<style lang="scss" scoped>
@use "~/assets/colors" as *;
@use "~/assets/sizes" as *;
@use "~/assets/media" as *;
@use "~/assets/fonts" as *;

.venue__page {
  overflow: hidden;
}

.venue__info {
  display: flex;
  flex-direction: column;
  @include md() {
    flex-direction: row;
  }

  .venue__header {
    font-size: 2.5rem;
    color: $brand;
    @include md() {
      flex-basis: 50%;
      padding-right: 2rem;
    }
  }

  .venue__infoContent {
    font-size: 1.2rem;
    line-height: 1.4rem;
    @include md() {
      flex-basis: 50%;
      font-size: 1.8rem;
      line-height: 2rem;
    }
  }
}

.venue__map {
  width: 100vw;
  height: 100vh;
  margin-left: calc((100% - 100vw) / 2);
  margin-bottom: -$standard-padding;
}
</style>
