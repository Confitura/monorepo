<template>
    <div class="about__page">
        <PageHeader title="About Us" type="coder"/>
        <Box color="white">
            <div class="about__info">
                <h2 class="about__header">Confitura is created by Java community members since 2007.</h2>
                <PageFragment name="about-page" class="about__infoContent"/>
            </div>
        </Box>
        <Box color="red" class="about__committee no-padding">
            <h3 class="committee__header">organizers</h3>
            <div class="committee__members">
                <div class="committee__member" v-for="member in committee">
                    <img :src="member.photo | crop(500)" :alt="member.name" class="member__photo">
                    <div class="member__info">
                        <div class="member__name">{{member.name}}</div>
                        <div class="member__bio">{{member.bio}}</div>
                        <div class="member__social">
                            <SocialLink type="twitter" :id="member.twitter" theme="white" class="member__socialLink"></SocialLink>
                            <SocialLink type="github" :id="member.github" theme="white" class="member__socialLink"></SocialLink>
                            <SocialLink type="www" :id="member.www" theme="white" class="member__socialLink"></SocialLink>
                        </div>
                    </div>
                </div>
            </div>
            <div class="volunteers__headerContainer">
                <h3 class="volunteers__header">volunteers</h3>
            </div>
        </Box>
        <Box color="white" class="no-padding">
            <UsersGrid :users="volunteers"></UsersGrid>
        </Box>
        <Box color="white">
            <div class="bcc">
                <h3 class="bcc__header">Brain Change Continental</h3>
                <img src="../assets/bcc.png" alt="bcc" class="bcc__logo">
                <div class="bcc__info">
                    <PageFragment name="bcc"/>
                    <iframe class="bcc__video" width="560" height="315" src="https://www.youtube-nocookie.com/embed/NLD0btOtFbg?controls=0"
                            frameborder="0"
                            allow="accelerometer; autoplay; encrypted-media; gyroscope; picture-in-picture" allowfullscreen></iframe>
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
  import PageFragment from '@/components/PageFragment.vue';
  import axios from 'axios';
  import { EmbeddedUserProfiles, UserProfile } from '@/types';
  import SocialLink from '@/components/SocialLink.vue';
  import UsersGrid from '@/views/UsersGrid.vue';

  @Component({
    components: { UsersGrid, SocialLink, PageHeader, Box, TheContact, PageFragment },
  })
  export default class About extends Vue {
    public committee: UserProfile[] = [];
    public volunteers: UserProfile[] = [];

    public mounted(): void {
      this.fetch('admins')
        .then((users) => this.committee = users);

      this.fetch('volunteers')
        .then((users) => users.filter(user => {
          const photo = user.photo;
          return photo && !photo.includes('blank');
        }))
        .then((users) => this.volunteers = users);
    }

    private fetch(type: string) {
      return axios.get<EmbeddedUserProfiles>(`/api/users/search/${type}`)
        .then((it) => it.data._embedded.publicUsers)
        .then((it) => this.shuffle(it));
    }

    private shuffle<T>(array: T[]): T[] {
      return array.sort(() => 0.5 - Math.random());
    }
  }

</script>

<style lang="scss" scoped>
    @import "../assets/colors";
    @import "../assets/sizes";
    @import "../assets/media";
    @import "../assets/fonts";

    .about__page {
        overflow: hidden;
    }

    .about__info {
        display: flex;
        flex-direction: column;
        @include md() {
            flex-direction: row;
        }

        .about__header {
            font-size: 2.5rem;
            color: $brand;
            @include md() {
                flex-basis: 50%;
                padding-right: 2rem;
            }
        }

        .about__infoContent {
            font-size: 1.2rem;
            line-height: 1.4rem;
            @include md() {
                flex-basis: 50%;
                font-size: 1.8rem;
                line-height: 2rem;
            }
        }
    }

    .about__committee {
        background-color: $brand;
        color: #ffffff;
    }

    .committee__header,
    .volunteers__header,
    .bcc__header {
        font-weight: bold;
        font-size: 2rem;
        margin: 2rem;
        padding: 0;
        @include md() {
            font-size: 3rem;

        }
    }

    .committee__members {
        display: grid;
        grid-template-columns: 1fr;
        grid-row-gap: 3rem;
        @include xl() {
            grid-template-columns: 1fr 1fr;
        }
    }

    .committee__member {
        display: grid;
        grid-template-columns: 1fr;
        grid-template-areas: "photo" "info";
        @include md-to-xl() {
            grid-template-columns: 1fr 1fr;
            grid-template-areas: "photo info";
            &:nth-child(odd) {
                grid-template-areas: "info photo";
                text-align: end;
            }
        }
        @include xl() {
            grid-template-columns: 1fr 1fr;
            grid-template-areas: "photo info";
            &:nth-child(4n+3), &:nth-child(4n+4) {
                grid-template-areas: "info photo";
                text-align: end;

            }
        }
    }

    .member__photo {
        width: 100%;
        height: auto;
        grid-area: photo;
        object-fit: cover;
    }

    .member__info {
        padding: 1.5rem;
        width: 100%;
        grid-area: info;
        @include md() {
            padding: 1rem;
        }
    }

    .member__name {
        font-size: 1.5rem;
        font-weight: bold;
        margin-bottom: 1rem;
    }

    .member__bio {
        font-size: 1.3rem;
        line-height: 1.5rem;
        margin-bottom: 1rem;
        @include xl() {
            font-size: 1.1rem;
            line-height: 1.2rem;

        }

    }

    .member__socialLink {
        color: #ffffff;
        font-size: 1.5rem;
        margin-right: 0.7rem;
    }


    .volunteers__headerContainer {
        background-color: $brand;
        display: flex;
        color: #ffffff;
        padding-top: 2rem;
    }

    .bcc {
        display: grid;
        grid-template-columns: 1fr;
        grid-template-areas: "bcc-header" "bcc-logo" "bcc-info";
        @include md() {
            grid-column-gap: 2rem;
            grid-template-columns: 2fr 1fr;
            grid-template-areas: "bcc-info bcc-header" "bcc-info bcc-logo";
        }
    }

    .bcc__header {
        color: $brand;
        margin: 0 0 2rem;
        grid-area: bcc-header;
        font-weight: bold;
        @include md() {
            font-size: 3rem;
        }
    }

    .bcc__logo {
        width: 100%;
        grid-area: bcc-logo;
        justify-self: center;
        @include md() {
            width: 80%;

        }
    }

    .bcc__info {
        font-size: 1.4rem;
        line-height: 1.6rem;
        grid-area: bcc-info;
    }

    .bcc__video {
        height: 315px;
        grid-area: bcc-info;
    }
</style>

