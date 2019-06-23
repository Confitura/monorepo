<template>
    <div class="presentations">
        <PageHeader title="Presentations" type="coder"/>
        <Box
                v-for="(presentation, $index) in presentations"
                :key="presentation.id"
                class="presentation"
                :class="{'presentation--odd': odd($index)}"
                color="white"
                :full="false">
            <PresentationBox :presentation="presentation" :class="{'presentationBox--odd': odd($index)}"></PresentationBox>
        </Box>

        <TheContact id="contact"/>
    </div>
</template>

<script lang="ts">
  import { Component, Vue } from 'vue-property-decorator';
  import Box from '@/components/Box.vue';
  import PageHeader from '@/components/PageHeader.vue';
  import TheContact from '@/components/TheContact.vue';
  import { LOAD_ACCEPTED_PRESENTATIONS } from '@/store/admin';
  import PresentationSpeakers from '@/components/PresentationSpeakers.vue';
  import PresentationMetadata from '@/components/PresentationMetadata.vue';
  import PresentationBox from '@/components/PresentationBox.vue';

  @Component({
    components: { PresentationBox, PresentationMetadata, PresentationSpeakers, PageHeader, Box, TheContact },
  })
  export default class AcceptedPresentations extends Vue {
    public mounted() {
      this.$store.dispatch(LOAD_ACCEPTED_PRESENTATIONS);
    }


    public get presentations() {
      return this.$store.state.admin.presentations;
    }

    public odd($index: number) {
      return $index % 2 !== 0;
    }
  }

</script>

<style lang="scss" scoped>
    @import "../assets/colors";
    @import "../assets/sizes";
    @import "../assets/media";
    @import "../assets/fonts";

    .presentations {
        overflow: hidden;
    }

    .presentation {
        &--odd {
            background-color: $brand;
            color: #ffffff;
        }

        &__header {
            color: $brand;

            &--odd {
                color: #ffffff;
            }
        }

        &__infoGroup {
            display: flex;
            flex-direction: column;
            @include md() {
                align-items: center;
                flex-direction: row;

            }
        }

        &__title {
            font-weight: bold;
            font-size: 2rem;
            margin-top: 0;
        }

        &__description {
            font-size: 1.2rem;
            line-height: 1.5rem;
            word-wrap: break-word;
            @include md() {
                font-size: 1.5rem;
                line-height: 1.7rem;
                padding-left: 4rem;
            }
        }
    }


</style>

