<template>
    <section class="banner">
        <div class="twinkling"></div>
        <div class="container">
            <div class="info">
                <div class="slogan">
                    It is not a rocket science!
                </div>
                <TheTimer/>
                <div class="time-and-place">
                    29.06.2019, Warsaw Expo XXI
                </div>
            </div>
            <TheIllustration/>
        </div>
    </section>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import TheTimer from '@/components/TheTimer.vue';
import TheIllustration from '@/components/TheIllustration.vue';

@Component({
  components: { TheIllustration, TheTimer },
})
export default class TheMainBanner extends Vue {
}
</script>

<style scoped lang="scss">
    @import "../assets/fonts";

    .banner {
        height: calc(100vh - 50px);
        scroll-snap-align: start;
        background: #000000 url(../assets/stars.png);
        overflow: hidden;
        padding-top: 50px;
    }

    .twinkling {
         background:transparent url(http://www.script-tutorials.com/demos/360/images/twinkling.png) repeat top center;
        z-index: 1;
         animation:move-twink-back 800s linear infinite;
        position: absolute;
        top: 0;
        left: 0;
        right: 0;
        bottom: 0;
        width: 100%;
        height: 100%;
        display: block;
    }

    @keyframes move-twink-back {
       from {background-position:0 0;}
       to {background-position:-10000px 5000px;}
     }

    .container {
        max-width: 1400px;
        margin: auto;
        display: flex;
        justify-content: space-between;
        height: 100%;
        align-items: center;
        z-index: 20;
    }


    .info {
        margin-left: 50px;
        z-index: 20;
        text-align: left;
    }

    .slogan {
        font-family: $font-bold;
        color: #ffffff;
        font-size: 2rem;
    }

    .time-and-place {
        color: #ffffff;
        font-size: 1.5rem;
        margin-top: 4.5rem;
    }


</style>