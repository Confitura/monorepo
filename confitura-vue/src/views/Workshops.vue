<template>
    <div class="workshops">
        <PageHeader title="Workshops" class="header" type="coder">
        </PageHeader>
        <Box class="content" color="white">
            <div class="workshops__info">
                <div class="workshops__short-info">
                    <p>This year we are also offering
                        post-conference 4-hours workshops on Sunday, June 30.</p>
                </div>
                <div class="workshops__details">
                    <p>All topics will give you a huge dose of knowledge about Java or/and Big Data. The workshops will be conducted in
                        Polish.</p>

                    <p>Workshop Day is hosted by our partner Sages. Sages cooperates with experts from different areas of IT who share their
                        knowledge in conferences, community workshops and commercial trainings.</p>

                    <p>The number of seats is limited to 30 per workshop. Registration is required. Workshop fee includes the entrance to
                        Confitura conference.</p>

                    <p>If you have any questions please check our
                        <router-link class="link--red" to="faq">FAQ</router-link>
                        (for both trainers and trainees). If you did not find answer to your question there then please contact Maria from
                        Sages: <a class="link--red" href="mailto:m.nowakowska@sages.com.pl">m.nowakowska@sages.com.pl</a></p>
                </div>

            </div>
            <div class="workshops__scheduleContainer">
                <div class="workshops__schedule">
                    <div class="workshops__track">
                        <div class="workshops__trackTitle">Java Track</div>
                        <div class="workshops__workshop">
                            <h3 class="workshop__title">Architecture mini-workshop for busy developers</h3>
                            <div class="workshop__speaker">Jakub Nabrdalik</div>
                            <div class="workshop__signup">
                                <a
                                        href="https://evenea.pl/event/confituraarchitecture/"
                                        class="workshop__signupLink"
                                        target="_blank"
                                        rel="noopener">More info & Sign up</a>
                            </div>
                        </div>
                        <div class="workshops__workshop">
                            <h3 class="workshop__title">Jigsaw. Modules in (new) Java </h3>
                            <div class="workshop__speaker">Piotr Przybył</div>
                            <div class="workshop__signup">
                                <a
                                        href="https://evenea.pl/event/confiturajigsaw/"
                                        class="workshop__signupLink"
                                        target="_blank"
                                        rel="noopener">More info & Sign up</a>
                            </div>
                        </div>
                        <div class="workshops__workshop">
                            <h3 class="workshop__title">Pragmatic TDD</h3>
                            <div class="workshop__speaker">Michał Wojtyna</div>
                            <div class="workshop__signup">
                                <a
                                        href="https://evenea.pl/event/confiturapragmatictdd/"
                                        class="workshop__signupLink"
                                        target="_blank"
                                        rel="noopener">More info & Sign up</a>
                            </div>
                        </div>
                        <div class="workshops__workshop">
                            <h3 class="workshop__title">Spring Reactor</h3>
                            <div class="workshop__speaker">Tomasz Łabuz</div>
                            <div class="workshop__signup">
                                <a
                                        href="https://evenea.pl/event/confituraspringreactor/"
                                        class="workshop__signupLink"
                                        target="_blank"
                                        rel="noopener">More info & Sign up</a>
                            </div>
                        </div>
                    </div>
                    <div class="workshops__track">
                        <div class="workshops__trackTitle">Big Data Track</div>
                        <div class="workshops__workshop">
                            <h3 class="workshop__title">Introduction to Machine Learning with Scikit-learn - your first pipeline</h3>
                            <div class="workshop__speaker">Waldemar Kołodziejczyk</div>
                            <div class="workshop__signup">
                                <a
                                        href="https://evenea.pl/event/confituraintroml/"
                                        class="workshop__signupLink"
                                        target="_blank"
                                        rel="noopener">More info & Sign up</a>
                            </div>
                        </div>
                        <div class="workshops__workshop">
                            <h3 class="workshop__title">Natural language processing, support 100+ languages using polyglot</h3>
                            <div class="workshop__speaker">Paweł Konior</div>
                            <div class="workshop__signup">
                                <a
                                        href="https://evenea.pl/event/confituranlp/"
                                        class="workshop__signupLink"
                                        target="_blank"
                                        rel="noopener">More info & Sign up</a>
                            </div>
                        </div>
                        <div class="workshops__workshop">
                            <h3 class="workshop__title">Machine Learning in Apache Spark</h3>
                            <div class="workshop__speaker">Patryk Pilarski</div>
                            <div class="workshop__signup">
                                <a
                                        href="https://evenea.pl/event/confituraapachespark/"
                                        class="workshop__signupLink"
                                        target="_blank"
                                        rel="noopener">More info & Sign up</a>
                            </div>
                        </div>
                        <div class="workshops__workshop">
                            <h3 class="workshop__title">Autoencoder based generative models</h3>
                            <div class="workshop__speaker">Przemysław Spurek</div>
                            <div class="workshop__description"></div>
                            <div class="workshop__signup">
                                <a
                                        href="https://evenea.pl/event/confituragenerativemodels/"
                                        class="workshop__signupLink"
                                        target="_blank"
                                        rel="noopener">More info & Sign up</a>
                            </div>
                        </div>
                    </div>

                </div>
            </div>
            <div class="workshops__venue">
                <div class="workshops__venue-title">
                    workshops venue
                </div>
                <div class="workshops__venue-address">
                    <p>Post-conference workshops will take place in:</p>
                    Golden Floor Plaza,<br>
                    123A Jerozolimskie Avenue,<br>
                    Warsaw, Poland
                </div>
            </div>
            <GmapMap
                    :center="center"
                    :zoom="16"
                    :options="mapOptions"
                    class="workshops__map"
            >
                <GmapMarker
                        :position="center"
                        :clickable="true"
                        :draggable="false"
                        :icon="require('../assets/marker.svg')"
                />
            </GmapMap>
        </Box>
        <TheContact id="contact"/>
    </div>
</template>

<script lang="ts">
  import { Component, Vue } from 'vue-property-decorator';
  import Box from '@/components/Box.vue';
  import PageHeader from '@/components/PageHeader.vue';
  import TheContact from '@/components/TheContact.vue';

  @Component({
    components: { PageHeader, Box, TheContact },
  })
  export default class Workshops extends Vue {
    public center = { lat: 52.225045, lng: 20.990859 };
    public mapOptions = {
      zoomControl: true,
      mapTypeControl: false,
      styles: [
        {

          featureType: 'all',
          elementType: 'labels.text.fill',
          stylers: [
            {
              color: '#ffffff',
            },
          ],
        },
        {
          featureType: 'all',
          elementType: 'labels.text.stroke',
          stylers: [
            {
              visibility: 'off',
            },
          ],
        },
        {
          featureType: 'all',
          elementType: 'labels.icon',
          stylers: [
            {
              visibility: 'off',
            },
          ],
        },
        {
          featureType: 'administrative',
          elementType: 'geometry.fill',
          stylers: [
            {
              color: '#ee1f46',
            },
          ],
        },
        {
          featureType: 'administrative',
          elementType: 'geometry.stroke',
          stylers: [
            {
              color: '#c9323b',
            },
            {
              weight: 1.2,
            },
          ],
        },
        {
          featureType: 'administrative.locality',
          elementType: 'geometry.fill',
          stylers: [
            {
              lightness: '-1',
            },
          ],
        },
        {
          featureType: 'administrative.neighborhood',
          elementType: 'labels.text.fill',
          stylers: [
            {
              lightness: '0',
            },
            {
              saturation: '0',
            },
          ],
        },
        {
          featureType: 'administrative.neighborhood',
          elementType: 'labels.text.stroke',
          stylers: [
            {
              weight: '0.01',
            },
          ],
        },
        {
          featureType: 'administrative.land_parcel',
          elementType: 'labels.text.stroke',
          stylers: [
            {
              weight: '0.01',
            },
          ],
        },
        {
          featureType: 'landscape',
          elementType: 'geometry',
          stylers: [
            {
              color: '#ee1f46',
            },
          ],
        },
        {
          featureType: 'poi',
          elementType: 'geometry',
          stylers: [
            {
              color: '#99282f',
            },
          ],
        },
        {
          featureType: 'road',
          elementType: 'geometry.stroke',
          stylers: [
            {
              visibility: 'off',
            },
          ],
        },
        {
          featureType: 'road.highway',
          elementType: 'geometry.fill',
          stylers: [
            {
              color: '#99282f',
            },
          ],
        },
        {
          featureType: 'road.highway.controlled_access',
          elementType: 'geometry.stroke',
          stylers: [
            {
              color: '#99282f',
            },
          ],
        },
        {
          featureType: 'road.arterial',
          elementType: 'geometry',
          stylers: [
            {
              color: '#99282f',
            },
          ],
        },
        {
          featureType: 'road.local',
          elementType: 'geometry',
          stylers: [
            {
              color: '#99282f',
            },
          ],
        },
        {
          featureType: 'transit',
          elementType: 'geometry',
          stylers: [
            {
              color: '#99282f',
            },
          ],
        },
        {
          featureType: 'water',
          elementType: 'geometry',
          stylers: [
            {
              color: '#090228',
            },
          ],
        },
      ],
    };
  }

</script>


<style lang="scss" scoped>
    @import "../assets/colors";
    @import "../assets/sizes";
    @import "../assets/media";
    @import "../assets/fonts";

    .header__planet {
        position: absolute;
        left: -40px;
        top: 60px;
        @include md() {
            position: unset;
            margin-left: 200px;
        }
    }

    .header-img {
        position: absolute;
        left: 170px;
        bottom: -50px;
        height: 280px;
        @include md() {
            position: unset;
            margin-top: 170px;
            margin-right: 150px;
            height: 350px;

        }

    }

    .workshops {
        overflow: hidden;

        &__info, &__venue {
            display: flex;
            margin-bottom: 4rem;
            flex-direction: column;
            @include lg() {
                flex-direction: row;
            }
        }

        &__short-info, &__venue-title {
            flex-basis: 35%;
            flex-shrink: 0;
            margin-bottom: 1rem;
            color: $brand;
            font-size: 2rem;
            font-weight: bold;
        }

        &__details, &__venue-address {
            font-size: 1.5rem;
            line-height: 2rem;
            @include lg() {
                margin-left: 4rem;
            }
        }

        &__map {
            width: 100vw;
            height: 100vh;
            margin-left: calc((100% - 100vw) / 2);
            margin-bottom: -$standard-padding;
        }


    }

    .workshops__schedule {
        display: flex;
        flex-direction: column;


        @include md(){
        flex-direction: row;

        }
        .workshops__track {
            flex-basis: 50%;
            display: flex;
            flex-direction: column;
            flex-grow: 0;
            @include md(){
                margin-bottom: 3rem;
            }
        }

        .workshops__trackTitle {
            margin-bottom: 1rem;
            color: $brand;
            font-size: 2rem;
            font-weight: bold;
            border-bottom: 1px solid #979797;
            padding-bottom: 2rem;
        }

        .workshops__workshop {
            &:not(:last-child) {
                border-bottom: 1px solid #979797;
            }
            padding-bottom: 2rem;
            min-height: 270px;
            padding-right: 1rem;
            padding-left: 1rem;
        }

        .workshop__title {
            font-weight: bold;
            font-size: 1.7rem;
        }

        .workshop__speaker {
            font-size: 1.5rem;
        }

        .workshop__signup {
            margin-top: 2rem;
        }

        .workshop__signupLink {
            color: $brand;
            font-size: 1.2rem;
            font-weight: bold;
        }
    }


</style>
