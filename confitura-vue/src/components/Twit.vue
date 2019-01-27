<template>
    <article class="twit">
        <div class="twit__avatar">
            <img class="twit__img" src="https://placehold.it/60x60?text=C" alt="">
        </div>
        <div class="twit__metadata">
            <span class="twit__username">Confitura</span>
            <span class="twit__user"> @confitura</span>
            <span class="twit__date"> 19 Oct 2018</span>
        </div>
        <div class="twit__message">
            2018 - Piotr Wikiel & Robert Mroczkowski - Apache beam — promyk nadziei Data Engineera:
            http://youtu.be/TbUnCBKmeXc?a via @YouTube
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

        display: grid;
        grid-template-columns: auto 1fr;
        grid-column-gap: 1rem;
        grid-row-gap: 1rem;
        font-size: 1rem;
        color: #ffffff;
        @media all and (min-width: 1000px) {
            grid-column-gap: 2rem;
            grid-row-gap: 2rem;
        }
        &__avatar {
            @media all and (min-width: 1000px) {
                grid-row: span 2;
            }
        }

        &__img {
            border-radius: 50px;
            width: 50px;
        }

        &__metadata {
            display: flex;
            flex-direction: row;
            align-items: center;
        }

        &__message {
            grid-column: span 2;
            @media all and (min-width: 1000px) {
                grid-column: span 1;

            }
            }

        &__username {
            color: $brand;
        }
    }
</style>