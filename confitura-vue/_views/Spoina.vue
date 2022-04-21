<template>
  <div class="spoina__page">
    <PageHeader title="SPOINa" type="coder" />
    <Box color="white" :full="false">
      <PageFragment class="questions" name="spoina-page" />
    </Box>
    <Box color="white">
      <GmapMap
        :center="center"
        :zoom="16"
        :options="mapOptions"
        class="spoina__map"
      >
        <GmapMarker
          :position="center"
          :clickable="true"
          :draggable="false"
          :icon="require('../assets/marker.svg')"
        />
      </GmapMap>
    </Box>
    <Contact />
  </div>
</template>

<script lang="ts">
import { Component, Vue } from "vue-property-decorator";
import Box from "@/components/Box.vue";
import PageHeader from "@/components/PageHeader.vue";
import Contact from "@/components/Contact.vue";
import PageFragment from "@/components/PageFragment.vue";
import SocialLink from "@/components/SocialLink.vue";

@Component({
  components: { SocialLink, PageHeader, Box, Contact, PageFragment }
})
export default class Spoina extends Vue {
  public center = { lat: 52.188112, lng: 20.960616 };
  public mapOptions = {
    zoomControl: true,
    mapTypeControl: false,
    styles: [
      {
        featureType: "all",
        elementType: "labels.text.fill",
        stylers: [
          {
            color: "#ffffff"
          }
        ]
      },

      {
        featureType: "all",
        elementType: "labels.icon",
        stylers: [
          {
            visibility: "off"
          }
        ]
      },
      {
        featureType: "administrative",
        elementType: "geometry.fill",
        stylers: [
          {
            color: "#ee1f46"
          }
        ]
      },
      {
        featureType: "administrative",
        elementType: "geometry.stroke",
        stylers: [
          {
            color: "#c9323b"
          },
          {
            weight: 1.2
          }
        ]
      },
      {
        featureType: "administrative.locality",
        elementType: "geometry.fill",
        stylers: [
          {
            lightness: "-1"
          }
        ]
      },
      {
        featureType: "administrative.neighborhood",
        elementType: "labels.text.fill",
        stylers: [
          {
            lightness: "0"
          },
          {
            saturation: "0"
          }
        ]
      },
      {
        featureType: "administrative.neighborhood",
        elementType: "labels.text.stroke",
        stylers: [
          {
            weight: "0.01"
          }
        ]
      },
      {
        featureType: "administrative.land_parcel",
        elementType: "labels.text.stroke",
        stylers: [
          {
            weight: "0.01"
          }
        ]
      },
      {
        featureType: "landscape",
        elementType: "geometry",
        stylers: [
          {
            color: "#ee1f46"
          }
        ]
      },
      {
        featureType: "poi",
        elementType: "geometry",
        stylers: [
          {
            color: "#99282f"
          }
        ]
      },
      {
        featureType: "road",
        elementType: "geometry.stroke",
        stylers: [
          {
            visibility: "off"
          }
        ]
      },
      {
        featureType: "road.highway",
        elementType: "geometry.fill",
        stylers: [
          {
            color: "#99282f"
          }
        ]
      },
      {
        featureType: "road.highway.controlled_access",
        elementType: "geometry.stroke",
        stylers: [
          {
            color: "#99282f"
          }
        ]
      },
      {
        featureType: "road.arterial",
        elementType: "geometry",
        stylers: [
          {
            color: "#99282f"
          }
        ]
      },
      {
        featureType: "road.local",
        elementType: "geometry",
        stylers: [
          {
            color: "#99282f"
          }
        ]
      },
      {
        featureType: "transit",
        elementType: "geometry",
        stylers: [
          {
            color: "#99282f"
          }
        ]
      },
      {
        featureType: "water",
        elementType: "geometry",
        stylers: [
          {
            color: "#090228"
          }
        ]
      }
    ]
  };
}
</script>

<style lang="scss" scoped>
@import "../assets/colors";
@import "../assets/sizes";
@import "../assets/media";
@import "../assets/fonts";

.spoina__page {
  overflow: hidden;
}

.spoina__info {
  display: flex;
  flex-direction: column;
  @include md() {
    flex-direction: row;
  }

  .spoina__header {
    font-size: 2.5rem;
    color: $brand;
    @include md() {
      flex-basis: 50%;
      padding-right: 2rem;
    }
  }

  .spoina__infoContent {
    font-size: 1.2rem;
    line-height: 1.4rem;
    @include md() {
      flex-basis: 50%;
      font-size: 1.8rem;
      line-height: 2rem;
    }
  }
}

.spoina__map {
  width: 100vw;
  height: 100vh;
  margin-left: calc((100% - 100vw) / 2);
  margin-bottom: -$standard-padding;
}
</style>
