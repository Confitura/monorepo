<template>
  <div class="faq">
    <PageHeader title="Frequently Asked Questions" />
    <Box class="content" color="white">
      <p class="foreword">
        Here we have gathered the most common questions we are getting from the
        community. If you have not found an answer to the question that you
        have, please
        <a
          class="foreword__link"
          href="mailto:confitura@confitura.pl?subject=FAQ"
          >contact us</a
        >. We will update the list for the benefit of all.
      </p>
      <PageFragment class="questions" name="faq" />
    </Box>
    <Contact />
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

.questions section {
  display: grid;
  text-align: left;
  grid-template-columns: 1fr;
  @include md() {
    grid-template-columns: 1fr 1fr;
  }

  h2 {
    font-size: 2.2rem;
    color: $brand;
    @include md() {
      grid-column: 1 / 2;
      &,
      + h3 {
        margin-top: 4rem;
        margin-bottom: 1rem;
      }
    }
  }

  h3 {
    font-size: 1.5rem;
    font-weight: bold;
    align-self: center;
    margin: 0 0 1rem;
    @include md() {
      grid-column: 2 / 3;
    }
  }

  p,
  ul {
    font-size: 1.5rem;
    margin: 0;
    @include md() {
      grid-column: 2 / 3;
    }

    + h3 {
      margin-top: 2em;
    }
  }

  h2 + {
    p,
    ul {
      margin-top: 2em;
    }
  }
}
</style>
