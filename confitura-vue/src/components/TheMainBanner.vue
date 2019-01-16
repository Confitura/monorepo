<template>
    <div class="banner">
        <div class="twinkling"></div>
        <div class="container">


            <div class="info">
                <div class="slogan">
                    It is not a rocket science!
                </div>
                <TheTimer/>

            </div>
            <div class="illustration">
                <img src="../assets/orbit.svg" class="orbit">
                <img src="../assets/wire.svg" class="wire">
                <img src="../assets/planet-1.svg" class="planet-1">
                <img src="../assets/planet-2.svg" class="planet-2">
                <img src="../assets/planet-3.svg" class="planet-3">
                <img src="../assets/astronaut.svg" class="astronaut">
            </div>
        </div>
    </div>
</template>

<script lang="ts">
    import {Component, Vue} from "vue-property-decorator";
    import TheTimer from "@/components/TheTimer.vue";

    @Component({
        components: {TheTimer}
    })
    export default class TheMainBanner extends Vue {
    }
</script>

<style scoped lang="scss">
    $levitate-speed: 5s;
    $levitate-offset: -20px;

    .banner {
        height: 100vh;
        background: #000000 url(../assets/stars.png);
    }

    .twinkling{
        background:transparent url(http://www.script-tutorials.com/demos/360/images/twinkling.png) repeat top center;
        z-index:1;
        animation:move-twink-back 800s linear infinite;
        position:absolute;
        top:0;
        left:0;
        right:0;
        bottom:0;
        width:100%;
        height:100%;
        display:block;
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

    .illustration {
        width: 750px;
        height: 750px;
        position: relative;
        float: right;
        z-index: 20;
    }

    .astronaut {
        left: 130px;
        top: 170px;
        position: absolute;
        filter: drop-shadow(10px 10px 0 rgba(0, 0, 0, .5));
        width: 400px;
        animation: levitate $levitate-speed alternate ease-in-out infinite;
    }

    @keyframes levitate {
        0% {
            transform: translate(0, 0)
        }
        100% {
            transform: translate(0, $levitate-offset)
        }
    }

    .wire {
        position: absolute;
        left: 200px;
        top: 110px;
        width: 400px;
    }

    .planet-1 {
        position: absolute;
        left: 570px;
        top: 70px;
        z-index: 5;
    }

    .planet-2 {
        position: absolute;
        left: 0;
        top: 39px;
    }

    .planet-3 {
        position: absolute;
        left: 370px;
        top: 295px;
    }

    .orbit {
        position: absolute;
        left: 0;
    }

    .info {
        margin-left: 50px;
        z-index: 20;
    }

    .slogan {
        color: #ffffff;
        font-size: 2rem;
        font-weight: bold;
    }


</style>