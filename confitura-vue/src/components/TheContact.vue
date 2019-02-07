<template>
    <Box class="contact" color="black">
        <h1 class="header">stay connected</h1>
        <div class="contact__info">
            <div class="contact__mail">
                <a class="contact__link" href="mailto:confitura@confitura.pl">confitura@confitura.pl</a>
            </div>
            <div class="contact__twit">
                <Twit/>
            </div>
            <div class="contact__twit">
                <Twit/>
            </div>
            <div class="contact__twit">
                <Twit/>
            </div>
            <div class="contact__twit">
                <Twit/>
            </div>
        </div>
    </Box>
</template>

<script>
  import Twit from './Twit';
  import Box from './Box';

  export default {
    name: 'TheContact',
    components: { Box, Twit },
  };
</script>

<style scoped lang="scss">
    @import "../assets/colors";
    @import "../assets/fonts";
    @import "../assets/media";

    .contact {
        background-color: #000000;
    }

    .header {
        color: $brand;
        font-family: $font-bold;
        font-size: 3rem;
    }

    .contact__info {
        display: grid;
        grid-row-gap: 3rem;
        @include lg() {
            grid-template-columns: 1fr 1fr 1fr;
            grid-column-gap: 2rem;
        }
    }

    .contact__mail {
        grid-row: 1/ -3;
    }

    .contact__link {
        font-size: 1.75rem;
        color: #ffffff;
        text-decoration: none;

        &:hover {
            text-decoration: underline;
        }
    }

</style>