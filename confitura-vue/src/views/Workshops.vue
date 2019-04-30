<template>
    <div class="workshops">
        <PageHeader title="Workshops" class="header">
            <img src="../assets/astronaut.svg" class="header-img">
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
                             :class="{['logo--'+partner.type]: true, ['logo--'+partner.orientation]: partner.orientation }">
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

  @Component({
    components: { PageHeader, Box, TheContact },
  })
  export default class Workshops extends Vue {

  }

</script>


<style lang="scss" scoped>
    @import "../assets/colors";
    @import "../assets/sizes";
    @import "../assets/media";
    @import "../assets/fonts";



</style>
