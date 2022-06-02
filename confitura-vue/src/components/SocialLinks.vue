<template>
  <div class="social-links-container">
    <div class="social-links">
      <a
        class="link"
        v-for="link in links"
        :key="link.label"
        :class="{ 'link--black': withBackground }"
        :href="link.url"
        :title="link.label"
        target="_blank"
        rel="noopener"
      >
        <i :class="[link.icon, 'icon icon-' + link.label]"></i>
        <span class="icon__label">{{ link.label }}</span>
      </a>
    </div>
  </div>
</template>

<script lang="ts">
import { Component, Vue } from "vue-property-decorator";

@Component({
  components: {}
})
export default class SocialLinks extends Vue {
  public withBackground = true;
  public links = [
    {
      label: "twitter",
      icon: "fab fa-twitter-square",
      url: "https://twitter.com/confiturapl"
    },
    {
      label: "facebook",
      icon: "fab fa-facebook-square",
      url: "https://www.facebook.com/Confitura"
    },
    {
      label: "linkedin",
      icon: "fab fa-linkedin",
      url: "https://www.linkedin.com/company/confitura"
    },
    {
      label: "youtube",
      icon: "fab fa-youtube-square",
      url: "https://www.youtube.com/confiturapl"
    },
    {
      label: "email us",
      icon: "fas fa-envelope-square",
      url: "mailto:confitura@confitura.pl"
    }
  ];
}
</script>

<style scoped lang="scss">
@import "../assets/colors";
@import "../assets/media";

.social-links-container {
  &.mobile {
    @include md() {
      display: none;
    }
  }

  &.standard {
    display: none;
    @include md() {
      display: flex;
      position: fixed;
      right: 0;
      height: 100vh;
      align-items: center;
      z-index: 500;
    }
  }

  .social-links {
    display: grid;
    grid-template-columns: repeat(3, auto);
    transition: background-color 300ms linear;
    justify-content: start;
    white-space: nowrap;
    @include md() {
      display: grid;
      justify-items: end;
      grid-template-columns: 1fr;
    }
  }

  .link {
    display: flex;
    text-decoration: none;
    font-size: 2.5rem;
    color: #ffffff;
    overflow: hidden;
    padding-right: 2rem;
    padding-top: 2rem;
    box-sizing: content-box;
    @include md() {
      width: 1.5rem;
      transition: all 0.4s linear;
      font-size: 1.2rem;
      padding: 0.5rem;
    }

    &--black {
      background-color: #000000;
    }

    &:hover {
      color: $brand;
      background-color: #000000;
      padding-right: 6rem;

      .icon__label {
        display: block;
      }
    }

    .icon__label {
      padding-left: 1rem;
      color: #ffffff;
      display: none;
      @include md() {
        display: block;
      }
    }

    .icon-email {
      display: none;
      @include md() {
        display: unset;
      }
    }
  }
}
</style>
