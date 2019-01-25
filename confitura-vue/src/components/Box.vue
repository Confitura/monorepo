<template>
    <section class="box">
        <div class="box__container">
            <slot/>
        </div>
    </section>
</template>

<script>
  export default {
    name: 'Box',
  };
</script>

<style scoped lang="scss">
    .box {
        min-height: 100vh;
        width: 100%;
        scroll-snap-align: start;
        display: flex;
        flex-direction: column;

    &__container {
         max-width: 1440px;
         width: 100%;
         margin: auto;
         box-sizing: border-box;
         text-align: left;
         padding-right: 20px;
         padding-left: 20px;
     }
    }
</style>