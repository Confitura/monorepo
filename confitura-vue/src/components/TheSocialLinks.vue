<template>
    <div class="social-links-container">
        <div class="social-links" :class="{'social-links--black': withBackground}">
            <a v-for="link in links"
               class="link"
               :href="link.url"
               :title="link.label"
               target="_blank"
               rel="noopener">
                <i :class="link.icon"></i>
            </a>
        </div>
    </div>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';

@Component({
  components: {},
})
export default class TheSocialLinks extends Vue {
  public withBackground = false;
  public links = [
    {
      label: 'twitter',
      icon: 'fab fa-twitter-square',
      url: 'https://twitter.com/confiturapl',
    },
    {
      label: 'facebook',
      icon: 'fab fa-facebook-square',
      url: 'https://www.facebook.com/Confitura',
    },
    {
      label: 'youtube',
      icon: 'fab fa-youtube-square',
      url: 'https://www.youtube.com/confiturapl',
    },
    {
      label: 'email',
      icon: 'fas fa-envelope-square',
      url: 'mailto:confitura@confitura.pl',
    },
  ];

  public mounted() {
    window.addEventListener('scroll', this.handleScroll);
  }

  public beforeDestroy() {
    window.removeEventListener('scroll', this.handleScroll);
  }

  public handleScroll() {
    const scroll = window.scrollY;
    this.withBackground = scroll > 20;
  }
}
</script>

<style scoped lang="scss">
    @import "../assets/colors";
    @import "../assets/media";


    .social-links-container {

        display: none;
        @include md(){
            display: flex;
            position: fixed;
            right: 0;
            height: 100vh;
            align-items: center;
            z-index: 10000;
        }
    }

    .social-links {
        display: grid;
        grid-gap: 1.2rem;
        padding: 20px 11px 20px 11px;
        transition: background-color 300ms linear;

        &--black {
            background-color: #000000;
        }
    }

    .link {
        display: flex;
        font-size: 1.2rem;
        color: #ffffff;
        transition: color 300ms linear;

        &:hover {
            color: $brand;
        }
    }

</style>