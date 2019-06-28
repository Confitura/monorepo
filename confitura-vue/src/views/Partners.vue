<template>
    <div class="partners">
        <PageHeader title="Partners" type="peace">

        </PageHeader>
        <Box class="content" color="white">
            <article v-for="(items, type) in partners ">
                <h2 class="type-header">{{type}} <br class="type-header__breaker"/>partners</h2>
                <div class="logos">

                    <router-link v-for="partner in items"
                                 :key="partner.id"
                                 :to="`/partners/${partner.id}`"
                                 class="logo-link"
                                 :class="`logo-link--${partner.type}`">
                        <img :src="partner.logo" :alt="partner.name"
                             :class="{['logo--'+partner.type]: true, ['logo--'+partner.orientation]: partner.orientation, [partner.id]: true }">
                    </router-link>
                </div>
            </article>
        </Box>
        <TheContact id="contact"/>
    </div>
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
  export default class Partners extends Vue {
    public partners: { [key: string]: Partner[] } = {};

    public mounted() {
      this.$store.dispatch(LOAD_PARTNERS)
        .then(() => {
          this.partners = {
            platinum: this.$store.getters.platinum,
            path: this.$store.getters.path,
            gold: this.$store.getters.gold,
            silver: this.$store.getters.silver,
            bronze: this.$store.getters.bronze,
          };
        });
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
        flex-direction: column;
        @include md() {
            flex-direction: row;
            flex-wrap: wrap;
        }
    }

    .logo-link {
        margin: 1.5rem 2rem;


        &--platinum, &--path {
            text-align: center;
        }
    }

    .logo--platinum, .logo--path{
        width: 80%;


        @include md() {
            width: 250px;
        }
    }

    .logo--silver {
        width: 80px;

        &.logo--horizontal {
            width: 160px;
        }
    }

    .logo--bronze {
        width: 80px;

        &.logo--horizontal {
            width: 160px;
        }
    }

    .logo--gold {
        width: 110px;

        &.logo--horizontal {
            width: 220px;
        }
    }


</style>
