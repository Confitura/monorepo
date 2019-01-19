<template>
    <section class="about-us">
ABOUT
    </section>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';

@Component
export default class TheAbout extends Vue {

}
</script>

<style scoped lang="scss">
    .about-us {
        min-height: 100vh;
        background-color: #ffffff;
        width: 100%;
        scroll-snap-align: start;
    }
</style>