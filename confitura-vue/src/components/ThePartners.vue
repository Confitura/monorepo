<template>
    <section class="partners" id="partners">
        <div class="partners-container"><h1 class="header">our partners</h1></div>
    </section>
</template>

<script>
  export default {
    name: 'ThePartners',
  };
</script>

<style scoped lang="scss">
    @import "../assets/colors";
    @import "../assets/fonts";
    .partners {
        width: 100%;
        min-height: 100vh;
        background-color: #ffffff;
        display: flex;
        flex-direction: column;
        justify-content: center;
        text-align: left;
    }
    .partners-container {
        max-width: 1440px;
        width: 100%;
        padding-left: 20px;
        padding-right: 20px;
        box-sizing: border-box;

        /*display: flex;*/
        /*flex-direction: column;*/
        /*justify-content: center;*/
    }
    .header {
        color: $brand;
        font-family: $font-bold;
        font-size: 3rem;
    }

</style>