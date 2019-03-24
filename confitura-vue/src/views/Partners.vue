<template>
    <div class="partners">
        <PageHeader title="Partners" class="header">
            <img src="../assets/astronaut.svg" class="header-img">
        </PageHeader>
        <Box class="content" color="white">
            <article v-for="(items, type) in partners ">
                <h2 class="type-header">{{type}} <br class="type-header__breaker"/>partners</h2>
                <div class="logos">
                    <a :href="partner.www" v-for="partner in items" target="_blank" rel="noopener" class="logo-link">
                        <img :src="partner.logo" :alt="partner.name" :class="'logo--'+partner.type">
                    </a>
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
    public platinum: Partner[] = [];
    public silver: Partner[] = [];
    public partners: { [key: string]: Partner[] } = {};

    private mounted() {
      this.$store.dispatch(LOAD_PARTNERS)
        .then(() => {
          this.partners = {
            platinum: this.$store.getters.platinum,
            silver: this.$store.getters.silver,

          };
        });
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

    .partners {
        overflow: hidden;
    }

    .header {
        overflow: hidden;
    }

    .header-img {
        width: 500px;
        top: 50px;
        left: 0;
        transform: rotate(-10deg);
        position: absolute;
        @include md(){
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
            @include md(){
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
    }

    .logo-link {
        margin-left: 2rem;
        margin-right: 2rem;
    }
    .logo--platinum {
        width: 80%;
        @include md(){
            width: unset;
        }
    }

    .logo--silver {
        width: 100px;
    }


</style>
