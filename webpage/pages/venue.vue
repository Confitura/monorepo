<template>
  <div class="venue__page">
    <PageHeader title="Venue" type="coder" />
    <Box color="white" :full="false">
      <PageFragment class="questions" name="venue-page" />
    </Box>
    <Box color="white">
<!--      <GmapMap-->
<!--        :center="center"-->
<!--        :zoom="16"-->
<!--        :options="mapOptions"-->
<!--        class="venue__map"-->
<!--      >-->
<!--        <GmapMarker-->
<!--          :position="center"-->
<!--          :clickable="true"-->
<!--          :draggable="false"-->
<!--          icon="~/assets/marker.svg" />-->
<!--      </GmapMap>-->

    </Box>
    <Contact />
  </div>
</template>

<script setup lang="ts">
let center= {lat: 51.093048, lng: 6.842120}
let markers= [
  {
    position: {
      lat: 51.093048, lng: 6.842120
    },
  }
]
// let center = { lat: 52.2248963, lng: 20.9599152 }
let mapOptions = {
  zoomControl: true,
  mapTypeControl: false,
  styles: [
    {
      featureType: 'all',
      elementType: 'labels.text.fill',
      stylers: [
        {
          color: '#ffffff'
        }
      ]
    },

    {
      featureType: 'all',
      elementType: 'labels.icon',
      stylers: [
        {
          visibility: 'off'
        }
      ]
    },
    {
      featureType: 'administrative',
      elementType: 'geometry.fill',
      stylers: [
        {
          color: '#ee1f46'
        }
      ]
    },
    {
      featureType: 'administrative',
      elementType: 'geometry.stroke',
      stylers: [
        {
          color: '#c9323b'
        },
        {
          weight: 1.2
        }
      ]
    },
    {
      featureType: 'administrative.locality',
      elementType: 'geometry.fill',
      stylers: [
        {
          lightness: '-1'
        }
      ]
    },
    {
      featureType: 'administrative.neighborhood',
      elementType: 'labels.text.fill',
      stylers: [
        {
          lightness: '0'
        },
        {
          saturation: '0'
        }
      ]
    },
    {
      featureType: 'administrative.neighborhood',
      elementType: 'labels.text.stroke',
      stylers: [
        {
          weight: '0.01'
        }
      ]
    },
    {
      featureType: 'administrative.land_parcel',
      elementType: 'labels.text.stroke',
      stylers: [
        {
          weight: '0.01'
        }
      ]
    },
    {
      featureType: 'landscape',
      elementType: 'geometry',
      stylers: [
        {
          color: '#ee1f46'
        }
      ]
    },
    {
      featureType: 'poi',
      elementType: 'geometry',
      stylers: [
        {
          color: '#99282f'
        }
      ]
    },
    {
      featureType: 'road',
      elementType: 'geometry.stroke',
      stylers: [
        {
          visibility: 'off'
        }
      ]
    },
    {
      featureType: 'road.highway',
      elementType: 'geometry.fill',
      stylers: [
        {
          color: '#99282f'
        }
      ]
    },
    {
      featureType: 'road.highway.controlled_access',
      elementType: 'geometry.stroke',
      stylers: [
        {
          color: '#99282f'
        }
      ]
    },
    {
      featureType: 'road.arterial',
      elementType: 'geometry',
      stylers: [
        {
          color: '#99282f'
        }
      ]
    },
    {
      featureType: 'road.local',
      elementType: 'geometry',
      stylers: [
        {
          color: '#99282f'
        }
      ]
    },
    {
      featureType: 'transit',
      elementType: 'geometry',
      stylers: [
        {
          color: '#99282f'
        }
      ]
    },
    {
      featureType: 'water',
      elementType: 'geometry',
      stylers: [
        {
          color: '#090228'
        }
      ]
    }
  ]
}
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
