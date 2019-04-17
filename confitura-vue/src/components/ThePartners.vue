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
                        <div class="logos" v-if="type === active" :key="type">
                            <div v-for="item in partners[type]"
                                 :key="item.id"
                                 class="logo"
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
                         @click="activate(type)"
                         @mouseenter="pauseIfActive(type)"
                         @mouseleave="start()"
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
    public partners: Partners = { platinum: [], gold: [], silver: [], bronze: [] };
    public active: PartnerType = 'gold';
    private pause = false;

    public pauseIfActive(type: string) {
      if (this.active === type) {
        this.pause = true;
      }
    }

    public start() {
      this.pause = false;
    }

    public activate(type: PartnerType) {
      this.active = type;
      this.pause = true;
    }


    protected mounted() {
      this.$store.dispatch(LOAD_PARTNERS)
        .then(() => {
          this.partners.platinum = this.$store.getters.platinum;
          this.partners.silver = this.$store.getters.silver;
          this.partners.gold = this.$store.getters.gold;
          setInterval(() => {
            if (!this.pause) {
              const currentIdx = this.types.indexOf(this.active);
              const newIdx = (currentIdx + 1) % 2;
              this.active = this.types[newIdx];
            }
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
                min-height: 600px;
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
            padding-bottom: 1.5rem;
            padding-top: 1.5rem;
            justify-items: center;
            align-items: center;
            justify-content: center;
            flex-grow: 1;
            transition: all 0.3s linear;
            max-width: 1000px;
        }
        .logo {
            margin-top: 1rem;
            margin-bottom: 1rem;
            &.logo--horizontal {
                margin-top: 1.5rem;
                margin-bottom: 1.5rem;
            }

        }

        .logo--silver, .logo--gold {
            margin-left: 2rem;
            margin-right: 2rem;
        }



        .other-types {
            flex-grow: 1;
            display: flex;
            flex-direction: column-reverse;
            overflow: hidden;


            @include md() {
                flex-direction: column;

            }
        }

        .logo__img {
            width: 80px;
        }

        .logo__img--gold {
            width: 160px;
        }

        .logo__img--platinum {
            width: 300px;
        }

        .logo--horizontal {
            grid-column-end: span 2;

            .logo__img {
                width: 160px;
            }

            &.logo--gold .logo__img {
                width: 230px;
            }
        }


        .link {
            text-align: center;
        }

        .type__selector {
            display: flex;
            justify-content: center;
            flex-shrink: 0;
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
            transition: all .5s;
        }

        .fade-enter {
            opacity: 0;
        }

        .fade-leave-to {
            opacity: 0;
        }

    }
</style>
