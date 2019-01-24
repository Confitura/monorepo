<template>
    <section class="contact" id="contact">
        <div class="contact-container">
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
        </div>
    </section>
</template>

<script>
  import Twit from './Twit';

  export default {
    name: 'TheContact',
    components: { Twit },
  };
</script>

<style scoped lang="scss">
    @import "../assets/colors";
    @import "../assets/fonts";

    .contact {
        background-color: #000000;
        width: 100%;
        min-height: 100vh;
        display: flex;
        flex-direction: column;
        justify-content: center;
        text-align: left;
    }

    .contact-container {
        max-width: 1440px;
        width: 100%;
        padding-left: 140px;
        padding-right: 140px;
        box-sizing: border-box;
    }

    .header {
        color: $brand;
        font-family: $font-bold;
        font-size: 3rem;
    }

    .contact__info {
        display: grid;
        grid-template-columns: 1fr 1fr 1fr;
        grid-row-gap: 50px;
        grid-column-gap: 30px;
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