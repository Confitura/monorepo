<template>
  <div class="faq">
    <PageHeader title="Lean Coffee"/>
    <Box class="content" color="white">
      <PageFragment name="lean-coffee"/>
    </Box>
    <Contact/>
  </div>
</template>

<script setup lang="ts">
</script>

<style lang="scss" scoped>
@use "~/assets/colors" as *;
@use "~/assets/sizes" as *;
@use "~/assets/media" as *;
@use "~/assets/fonts" as *;

.faq {
  overflow: hidden;
}

.content {
  .foreword {
    text-align: left;
    color: #000000;
    font-size: 1.5rem;
    @include md() {
      width: 50%;
    }

    &__link {
      color: $brand;
    }
  }
}
</style>
<style lang="scss">
@use "~/assets/colors" as *;
@use "~/assets/sizes" as *;
@use "~/assets/media" as *;
@use "~/assets/fonts" as *;

</style>
