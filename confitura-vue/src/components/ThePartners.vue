<template>
    <Box class="partners" color="white">
        <h1 class="header">our partners</h1>
        <div class="partners-grid">
            <a href="https://softwareplant.com" class="link" rel="noopener" target="_blank">
                <img src="../assets/partners/softwareplant.png" alt="softwareplant" class="logo">
            </a>
            <span class="type">Platinum</span>
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
            display: grid;
            width: 100%;
            justify-items: center;

        }

        .type {
            font-size: 1.5rem;
            color: $brand;
            padding-top: 4rem;
        }

        .logo {
            width: 90%;
        }

        .link {
            text-align: center;
        }


    }
</style>
