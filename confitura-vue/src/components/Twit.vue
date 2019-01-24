<template>
    <article class="twit">
        <div class="twit__avatar">
            <img class="twit__img" src="https://placehold.it/60x60?text=C" alt="">
        </div>
        <div class="twit__content">
            <div class="twit__metadata">
                <span class="twit__username">Confitura</span>
                <span class="twit__user"> @confitura</span>
                <span class="twit__date"> 19 Oct 2018</span>
            </div>
            <div class="twit__message">
                2018 - Piotr Wikiel & Robert Mroczkowski - Apache beam — promyk nadziei Data Engineera:
                http://youtu.be/TbUnCBKmeXc?a via @YouTube
            </div>
        </div>
    </article>
</template>
<script>
  export default {
    name: 'Twit',
  };
</script>
<style scoped lang="scss">
    @import "../assets/colors";
    @import "../assets/fonts";

    .twit {

        display: flex;
        flex-direction: row;
        font-size: 1rem;
        color: #ffffff;

        &__img {
            border-radius: 50px;
        }

        &__content {
            display: flex;
            flex-direction: column;
            margin-left: 2rem;
        }

        &__metadata {
            display: flex;
            flex-direction: row;
            margin-bottom: 2rem;
        }

        &__username {
            color: $brand;
        }
    }
</style>