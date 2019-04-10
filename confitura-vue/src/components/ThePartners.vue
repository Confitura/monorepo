<template>
    <Box class="partners" color="white">
        <h1 class="header">our partners</h1>
        <div class="partners-grid">
            <div class="platinum">
                <a v-for="item in partners.platinum" :href="item.www" class="link" rel="noopener" target="_blank">
                    <img :src="item.logo" :alt="item.name" class="logo__img--platinum">
                </a>
                <span class="type--platinum">Platinum</span>
            </div>
            <div class="other-types">
                <transition name="fade" mode="out-in">
                    <template v-for="type in types">
                        <div class="logos" v-if="type === active">
                            <div v-for="item in partners[type]"
                                 :key="item.id"
                                 :class="{[`logo--${item.orientation}`]: item.orientation, [`logo--${active}`]: active}">
                                <a :href="item.www" class="link" rel="noopener" target="_blank">
                                    <img :src="item.logo" :alt="item.name" class="logo__img">
                                </a>
                            </div>
                        </div>
                    </template>
                </transition>
                <div class="type__selector">
                    <div class="type"
                         v-for="type in types"
                         :key="type"
                         @click="active = type"
                         :class="{[`type--${type}`]: true, [`type--active`]: type === active}">{{type}}
                    </div>
                </div>
            </div>
        </div>
    </Box>
</template>

<script lang="ts">
  import { Component, Vue } from 'vue-property-decorator';
  import Box from '@/components/Box.vue';
  import PageHeader from '@/components/PageHeader.vue';
  import TheContact from '@/components/TheContact.vue';
  import { LOAD_PARTNERS, Partner } from '@/types';

  @Component({
    components: { PageHeader, Box, TheContact },
  })
  export default class ThePartners extends Vue {
    public platinum: Partner[] = [];
    public silver: Partner[] = [];
    public gold: Partner[] = [];
    public types: PartnerType[] = ['gold', 'silver', 'bronze'];
    public active: PartnerType = 'gold';
    public partners: Partners = { platinum: [], gold: [], silver: [], bronze: [] };

    protected mounted() {
      this.$store.dispatch(LOAD_PARTNERS)
        .then(() => {
          this.partners.platinum = this.$store.getters.platinum;
          this.partners.silver = this.$store.getters.silver;
          this.partners.gold = this.$store.getters.gold;
          setInterval(() => {
            const currentIdx = this.types.indexOf(this.active);
            const newIdx = (currentIdx + 1) % 2;
            this.active = this.types[newIdx];
          }, 5000);
        });


    }

  }

  interface Partners {
    platinum: Partner[];
    gold: Partner[];
    silver: Partner[];
    bronze: Partner[];
  }

  type PartnerType = 'gold' | 'silver' | 'bronze';
</script>

<style scoped lang="scss">
    @import '../assets/colors';
    @import '../assets/fonts';
    @import '../assets/media';

    .partners {
        text-align: left;

        .header {
            color: $brand;
            font-family: $font-bold;
            font-size: 3rem;
        }

        .info {
            font-size: 1.75rem;
        }

        &-grid {
            display: flex;
            width: 100%;
            justify-items: center;
            flex-direction: column;
            @include md() {
                flex-direction: row;
                height: 400px;
            }

        }

        .type--platinum {
            font-size: 2rem;
            color: $brand;
            padding-top: 3rem;
            @include md() {
                font-size: 1.5rem;
            }
        }

        .logos {
            display: flex;
            flex-wrap: wrap;
            /*flex-direction: column;*/
            /*grid-row-gap: 3rem;*/
            /*grid-column-gap: 1rem;*/
            padding-bottom: 3rem;
            padding-top: 3rem;
            /*grid-template-columns: repeat(auto-fit, minmax(100px, 1fr));*/
            justify-items: center;
            align-items: center;
            justify-content: space-evenly;
            flex-grow: 1;
            transition: all 0.3s linear;

        }


        .logo--silver, .logo--gold {
            margin: 0.5rem;
        }

        .logo--horizontal {
            grid-column-end: span 2;

            .logo__img {
                width: 200px;
            }
        }

        .other-types {
            flex-grow: 1;
            display: flex;
            flex-direction: column-reverse;
            @include md() {
                flex-direction: column;
            }
        }

        .logo__img {
            width: 100px;
        }


        .logo__img--platinum {
            width: 300px;
        }

        .link {
            text-align: center;
        }

        .type__selector {
            display: flex;
            justify-content: center;
            flex-shrink: 0;
            flex-basis: 40%;
        }

        .platinum {
            display: flex;
            flex-direction: column;
            align-items: center;
            justify-content: center;
            padding-top: 2rem;
        }

        .type {
            cursor: pointer;
            transition: all 0.3s linear;

            &:hover {
                color: $brand;
                border-top-color: $brand;
            }
        }

        .type--gold, .type--silver, .type--bronze {
            font-size: 1.5rem;
            color: #EAEAEA;
            margin: 5px;
            max-width: 230px;
            width: 33%;
            text-align: center;
            padding-top: 2rem;
            border-bottom: #EAEAEA solid 8px;

            @include md() {
                border-bottom: none;
                border-top: #EAEAEA solid 5px;
            }
        }

        .type--active {
            border-color: $brand;
            color: $brand;
        }

        .fade-enter-active, .fade-leave-active {
            transition: opacity .3s;
        }
        .fade-enter, .fade-leave-to /* .fade-leave-active below version 2.1.8 */ {
            opacity: 0;
        }

    }
</style>
