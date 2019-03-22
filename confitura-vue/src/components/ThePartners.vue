<template>
    <Box class="partners" color="white">
        <h1 class="header">our partners</h1>
        <div class="partners-grid">
            <div class="platinum">
                <a href="https://softwareplant.com" class="link" rel="noopener" target="_blank">
                    <img src="../assets/partners/softwareplant.svg" alt="softwareplant" class="logo__img--platinum">
                </a>
                <span class="type--platinum">Platinum</span>
            </div>
            <div class="other-types">
                <div class="logos">
                    <div class="logo--silver">
                        <a href="https://www.7n.com/" class="link" rel="noopener" target="_blank">
                            <img src="../assets/partners/7N.png" alt="7N" class="logo__img">
                        </a>
                    </div>
                </div>
                <div class="type__selector">
                    <div class="type--gold">gold</div>
                    <div class="type--silver type--active">silver</div>
                    <div class="type--bronze">bronze</div>
                </div>
            </div>
        </div>
    </Box>
</template>

<script>
  import Box from './Box';

  export default {
    name: 'ThePartners',
    components: { Box },
  };
</script>

<style scoped lang="scss">
    @import '../assets/colors';
    @import '../assets/fonts';
    @import '../assets/media';

    .partners {
        text-align: left;

        .header {
            color: $brand;
            font-family: $font-bold;
            font-size: 3rem;
        }

        .info {
            font-size: 1.75rem;
        }

        &-grid {
            display: flex;
            width: 100%;
            justify-items: center;
            flex-direction: column;
            @include md() {
                flex-direction: row;

            }

        }

        .type--platinum {
            font-size: 2rem;
            color: $brand;
            padding-top: 3rem;
            @include md(){
                font-size: 1.5rem;
            }
        }

        .logos {
            display: grid;
            grid-gap: 1rem;
            padding-bottom: 3rem;
            padding-top: 3rem;
            grid-template-columns: repeat(auto-fit, minmax(100px, 1fr));
        }



        .logo--silver {
            margin: auto;
        }

        .other-types {
            flex-grow: 1;
            display: flex;
            flex-direction: column-reverse;
            @include md(){
                flex-direction: column;
            }
        }

        .logo__img {
            width: 100px;
        }

        .logo__img--platinum {
            width: 300px;
        }

        .link {
            text-align: center;
        }

        .type__selector {
            display: flex;
            justify-content: center;
        }

        .platinum {
            display: flex;
            flex-direction: column;
            align-items: center;
            justify-content: center;
            padding-top: 2rem;
        }

        .type--gold, .type--silver, .type--bronze {
            font-size: 1.5rem;
            color: #EAEAEA;
            margin: 5px;
            max-width: 230px;
            width: 33%;
            text-align: center;
            padding-top: 2rem;
            border-bottom: #EAEAEA solid 8px;

            @include md() {
                border-bottom: none;
                border-top: #EAEAEA solid 5px;
            }
        }

        .type--active {
            border-color: $brand;
            color: $brand;
        }


    }
</style>
