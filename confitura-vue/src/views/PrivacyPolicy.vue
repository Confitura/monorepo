<template>
    <div class="privacy-policy">
        <PageHeader title="Privacy Policy" class="header">
            <img src="../assets/astronaut.svg" class="header-img">
        </PageHeader>
        <Box class="content" color="white">
            <section class="privacy-policy__content" v-html="content"></section>
        </Box>
        <TheContact id="contact"/>
    </div>
</template>

<script lang="ts">
  import { Component, Vue } from 'vue-property-decorator';
  import Box from '@/components/Box.vue';
  import PageHeader from '@/components/PageHeader.vue';
  import TheContact from '@/components/TheContact.vue';
  import { Page } from '@/types';
  import axios from 'axios';
  import showdown from 'showdown';


  @Component({
    components: { PageHeader, Box, TheContact },
  })
  export default class PrivacyPolicy extends Vue {
    public content = '';
    private converter = new showdown.Converter();

    private mounted() {
      axios.get<Page>('/api/pages/privacy-policy')
        .then((response) => response.data.content)
        .then((content: string) => this.content = this.converter.makeHtml(content));

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

    .logos {
        display: flex;
        margin-bottom: 6rem;
        align-items: center;
    }

    .logo-link {
        margin-left: 2rem;
        margin-right: 2rem;

        &--platinum {
            text-align: center;
        }
    }

    .logo--platinum {
        width: 80%;
        @include md() {
            width: unset;
        }
    }

    .logo--silver {
        width: 100px;
    }

    .logo--horizontal {
        width: 200px;

    }


</style>
