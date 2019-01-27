<template>
    <div class="illustration">
        <img src="../assets/orbit.svg" class="orbit">
        <img src="../assets/wire.svg" class="wire">
        <img src="../assets/planet-1.svg" class="planet-1">
        <img src="../assets/planet-2.svg" class="planet-2">
        <img src="../assets/planet-3.svg" class="planet-3">
        <img src="../assets/astronaut.svg" class="astronaut">
    </div>
</template>
<script lang="ts">
export default {
  name: 'TheIllustration',
};
</script>
<style scoped lang="scss">
    $levitate-speed: 3s;
    $levitate-offset: -4%;

    .illustration {
        width: 100vw;
        height: 100vw;
        @media all and (min-width: 1000px) {
            width: 48vw;
            height: 48vw;
        }

        position: relative;
        float: right;
        z-index: 20;
    }

    .astronaut {
        left: 17%;
        top: 21.5%;
        position: absolute;
        filter: drop-shadow(10px 10px 0 rgba(0, 0, 0, .5));
        width: 54%;
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
        left: 26%;
        top: 14.5%;
        width: 53.34%;
    }

    .planet-1 {
        position: absolute;
        left: 77%;
        top: 9%;
        z-index: 5;
        width: 10.7%;
    }

    .planet-2 {
        position: absolute;
        left: 0;
        top: 5%;
        width: 18.15%;
    }

    .planet-3 {
        position: absolute;
        left: 50%;
        top: 40%;
        width: 45%;
    }

    .orbit {
        position: absolute;
        left: 0;
        width: 87.6%;
        height: 96.54%;
    }


</style>