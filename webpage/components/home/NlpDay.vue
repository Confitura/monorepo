<template>
  <Box class="content" color="white">
    <img class="nlp-logo" src="~/assets/partners/2025/nlpday.svg">
    <p class="foreword">
      This year's edition of Confitura will be held in conjunction with Al & NLP Day 2025 - a two-day conference
      featuring inspiring sessions and hands-on workshops dedicated to the most important advances and applications in
      artificial intelligence, machine learning, and natural language processing.
    </p>
    <p class="foreword">
      <big class="main">EXPANDED PROGRAM:</big> Parallel to Confitura's main tracks, participants can attend specialized
      sessions
      practical workshops dedicated to the forefront of generativev artificial intelligence. The program features
      in-depth explorations of Polish large language models, emerging trends in GenAl, real-world applications of
      LLMs, and practical guidance on implementing generative Al solutions across diverse environments.
    </p>
    <p class="foreword">
      <big class="main">CROSS-DISCIPLINARY NETWORKING:</big> Meet and engage with both the thriving communityof Confitu
      ra and leading Al experts, academic researchers, and PolEval competition winners gathered for Al & NLP Day.
      This unique format offers even greater scope for knowledge sharing, recruitment, and brand visibility across
      intersecting sectors of IT, Al, and data science.
    </p>

  </Box>
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

.main {
  color: $brand;
}

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

.nlp-logo {
  height: 10em;
}
</style>
