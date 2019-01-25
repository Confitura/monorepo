<template>
    <Box class="partners">
        <h1 class="header">our partners</h1>
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
    @import "../assets/colors";
    @import "../assets/fonts";
    .partners {
        text-align: left;
    }
    .header {
        color: $brand;
        font-family: $font-bold;
        font-size: 3rem;
    }

</style>