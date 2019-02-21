<template>
    <div class="faq">
        <PageHeader/>
        <Box class="content" color="white">
            <p class="foreword">Everything has started in 2007, when the group of passionate Java developers decided to
                create a programming event for the community. Today we're running the conference for more than 1800
                attendees. 35 talks in 5 parallel sessions give an opportunity to learn new thin</p>
            <section class="questions" v-html="questions"></section>
        </Box>
        <TheContact id="contact"/>
    </div>
</template>

<script lang="ts">
  import { Component, Vue } from 'vue-property-decorator';
  import Box from '@/components/Box.vue';
  import PageHeader from '@/components/PageHeader.vue';
  import TheContact from '@/components/TheContact';
  import showdown from 'showdown';
  import axios from 'axios';

  @Component({
    components: { PageHeader, Box, TheContact },
  })
  export default class Faq extends Vue {
    public questions = '';
    private converter = new showdown.Converter();

    private mounted() {
      axios.get<Page>('/api/pages/faq')
        .then((response) => response.data.content)
        .then((content: string) => this.questions = this.converter.makeHtml(content));

    }
  }

  interface Page {
    content: string;
  }
</script>

<style lang="scss" scoped>
    @import "../assets/colors";
    @import "../assets/sizes";
    @import "../assets/media";
    @import "../assets/fonts";

    .content {
        .foreword {
            text-align: left;
            color: #000000;
            font-size: 1.75rem;
            @include md() {
                width: 50%;
            }
        }


    }
</style>
<style lang="scss">
    @import "../assets/colors";
    @import "../assets/sizes";
    @import "../assets/media";
    @import "../assets/fonts";

    .faq {
        overflow: hidden;
    }

    .questions {
        display: grid;
        text-align: left;
        grid-template-columns: 1fr;
        @include md() {
            grid-template-columns: 1fr 1fr;
        }

        h2 {
            font-size: 3rem;
            color: $brand;
            @include md() {
                grid-column: 1 / 2;
            }

            &, + h4 {
                margin-top: 4rem;
                margin-bottom: 1rem;
            }
        }

        h4 {
            font-size: 1.75rem;
            font-weight: bold;
            align-self: center;
            margin: 0 0 1rem;
            @include md() {
                grid-column: 2 / 3;
            }
        }

        p, ul {
            font-size: 1.75rem;
            margin: 0;
            @include md() {
                grid-column: 2 / 3;
            }

            + h4 {
                margin-top: 2em;
            }
        }

    }
</style>
