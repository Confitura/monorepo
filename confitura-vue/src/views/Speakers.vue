<template>
    <div class="speakers">
        <PageHeader title="Our speakers" type="coder"/>
        <Box class="content no-padding" color="white">
            <div class="speakers__grid">
                <div class="speakers__speaker speaker" v-for="speaker in speakers"
                     @click="show(speaker)">
                    <img :src="speaker.photo" alt="" class="speaker__photo">
                    <div class="speaker__name">
                        <span>{{speaker.name | firstName}}</span>
                        <span>{{speaker.name | lastName}}</span>

                    </div>
                </div>
            </div>
        </Box>
        <TheContact id="contact"/>
    </div>
</template>

<script lang="ts">
  import { Component, Vue } from 'vue-property-decorator';
  import Box from '@/components/Box.vue';
  import PageHeader from '@/components/PageHeader.vue';
  import TheContact from '@/components/TheContact.vue';
  import { LOAD_SPEAKERS } from '@/store/admin';
  import { UserProfile } from '@/types';

  @Component({
    components: { PageHeader, Box, TheContact },
  })
  export default class Speakers extends Vue {
    public mounted() {
      this.$store.dispatch(LOAD_SPEAKERS);
    }

    public show({ id }: UserProfile) {
      if (id) {
        this.$router.push({ name: 'speaker', params: { id } });
      }
    }

    public get speakers() {
      return this.$store.state.admin.speakers;
    }
  }

</script>

<style lang="scss" scoped>
    @import "../assets/colors";
    @import "../assets/sizes";
    @import "../assets/media";
    @import "../assets/fonts";

    .speakers {
        overflow: hidden;

        &__grid {
            display: flex;
            flex-direction: column;
            justify-content: center;
            margin-top: 4rem;
            @include md() {
                flex-direction: row;
                flex-wrap: wrap;
            }
        }
    }

    .speaker {
        display: flex;
        cursor: pointer;

        &:hover {
            background-color: $brand;
            color: #ffffff;
            transition: all 0.3s linear;
        }

        @include sm-only() {
            &:nth-child(odd) {
                flex-direction: row-reverse;

                .speaker__name {
                    align-items: flex-end;
                }


            }
        }
        @include md-to-xl() {
            &:nth-child(4n+3), &:nth-child(4n+4) {
                flex-direction: row-reverse;

                .speaker__name {
                    align-items: flex-end;
                }
            }
        }
        @include xl() {
            &:nth-child(6n+4), &:nth-child(6n+5), &:nth-child(6n+6) {
                flex-direction: row-reverse;

                .speaker__name {
                    align-items: flex-end;
                }
            }
        }


    }

    .speaker__photo {
        width: 50vw;
        height: 50vw;
        object-fit: cover;
        @include md() {
            width: 200px;
            height: 200px;
        }
    }

    .speaker__name {
        display: flex;
        flex-direction: column;
        justify-content: center;
        font-family: $font-bold;
        font-size: 1.7rem;
        padding: 1rem;
        box-sizing: border-box;
        @include md() {
            width: 200px;
            height: 200px;
        }
    }


</style>

