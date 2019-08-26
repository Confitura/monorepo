<template xmlns:v-slot="http://www.w3.org/1999/XSL/Transform">
  <div class="partners">
    <PageHeader class="header" type="peace">
      <template v-slot:title>
        <span class="header__partner-type">{{ partner.type }} partner</span>
        {{ partner.name }}
      </template>
    </PageHeader>
    <Box class="content" color="white" :full="false">
      <article class="partner">
        <div class="partner__logo-container">
          <a :href="partner.www" target="_blank" rel="noopener">
            <img
              class="partner__logo"
              :class="{
                [`partner__logo--${partner.orientation}`]: partner.orientation
              }"
              :src="partner.logo"
              :alt="partner.name"
            />
          </a>
        </div>
        <div class="partner__description" v-html="description"></div>
      </article>
    </Box>
    <TheContact id="contact" />
  </div>
</template>

<script lang="ts">
import { Component, Vue } from "vue-property-decorator";
import Box from "@/components/Box.vue";
import PageHeader from "@/components/PageHeader.vue";
import TheContact from "@/components/TheContact.vue";
import { LOAD_PARTNER_BY_ID, Partner } from "@/types";
import marked from "marked";

@Component({
  components: { PageHeader, Box, TheContact }
})
export default class PartnerPage extends Vue {
  public partner: Partner = {
    name: "",
    description: "",
    id: "",
    logo: "",
    type: "",
    www: ""
  };

  public mounted() {
    window.scrollTo(0, 0);
    const { id } = this.$route.params;
    this.$store
      .dispatch(LOAD_PARTNER_BY_ID, id)
      .then(partner => (this.partner = partner));
  }

  public get description() {
    return marked(this.partner.description);
  }
}
</script>

<style lang="scss" scoped>
@import "../assets/colors";
@import "../assets/sizes";
@import "../assets/media";
@import "../assets/fonts";

.partners {
  overflow: hidden;
}

.header {
  overflow: hidden;

  &__partner-type {
    display: block;

    ::first-letter {
      text-transform: capitalize;
    }
  }
}

.header-img {
  width: 500px;
  top: 50px;
  left: 60px;
  transform: rotate(-10deg);
  position: absolute;
  @include sm() {
    width: 500px;
    top: 0;
    position: unset;
    margin-top: 470px;
  }
}

.type-header {
  font-size: 2.7rem;
  color: $brand;
  margin-bottom: 3rem;
  font-weight: bold;

  &__breaker {
    @include sm() {
      display: none;
    }
  }

  &:first-letter {
    text-transform: capitalize;
  }
}

.partner {
  display: grid;
  grid-template-columns: 1fr;
  grid-gap: 2rem;
  @include md() {
    grid-template-columns: 1fr 2fr;
  }

  & &__logo-container {
    padding: 1.5rem 1rem 1rem;
    text-align: center;

    .partner__logo {
      width: auto;
      height: auto;
      max-width: 500px;
      max-height: 200px;
      min-width: 190px;
      object-fit: contain;
    }

    .partner__logo--horizontal {
      min-width: 250px;
      max-width: 300px;
    }
  }

  & &__description {
    font-size: 1.5rem;
    line-height: 2rem;
  }
}
</style>
<style lang="scss">
@import "../assets/colors";

.partner {
  a {
    color: $brand;
  }
}
</style>
