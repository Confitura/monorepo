<template>
    <Box class="contact" color="black">
        <h1 class="header">stay connected</h1>
        <div class="contact__info">
            <div class="contact__mail">
                <a class="contact__link" href="mailto:confitura@confitura.pl">confitura@confitura.pl</a>
                <router-link to="privacy-policy" class="contact__privacy">Privacy policy</router-link>
                <TheSocialLinks class="mobile"/>
            </div>
            <div class="contact__tweet" v-for="tweet in tweets" :key="tweet.time">
                <Tweet :model="tweet"/>
            </div>
        </div>
    </Box>
</template>

<script lang="ts">
  import { Component, Vue } from 'vue-property-decorator';
  import Box from './Box.vue';
  import Tweet, { TweetItem } from './Tweet.vue';
  import TheSocialLinks from './TheSocialLinks.vue';
  import axios from 'axios';

  @Component({
    components: { Box, Tweet, TheSocialLinks },
  })
  export default class TheContact extends Vue {
    public tweets: TweetItem[] = [];

    protected mounted() {
      axios
        .get('/api/api/tweets')
        .then((response: { data: TweetItem[] }) => response.data)
        .then((tweets: TweetItem[]) => tweets.slice(0, 4))
        .then((tweets: TweetItem[]) => (this.tweets = tweets));
    }
  }


</script>

<style scoped lang="scss">
    @import '../assets/colors';
    @import '../assets/fonts';
    @import '../assets/media';

    .contact {
        background-color: #000000;

        .header {
            color: $brand;
            font-family: $font-bold;
            font-size: 3rem;
        }

        .contact__info {
            display: grid;
            grid-row-gap: 3rem;
            @include lg() {
                grid-template-columns: 1fr 1fr 1fr;
                grid-column-gap: 2rem;
            }
        }

        .contact__mail {
            grid-row: 1/ -3;
            display: flex;
            flex-direction: column;
        }

        .contact__link {
            font-size: 1.75rem;
            color: #ffffff;
            text-decoration: none;

            &:hover {
                text-decoration: underline;
            }
        }

        .contact__privacy {
            margin-top: 2rem;
            color: #ffffff;
            text-decoration: none;

            &:hover {
                text-decoration: underline;
            }
        }
    }
</style>
