<template>
  <Box color="white" :full="false" class="agenda-flow">
    <h2 class="agenda-flow__title">how we build the agenda</h2>
    <p class="agenda-flow__lead">You submit and vote. We take it from there.</p>

    <div class="agenda-flow__phases">
      <div
        v-for="phase in phases"
        :key="phase.id"
        class="agenda-flow__phase"
        :class="`agenda-flow__phase--${phase.id}`"
      >
        <p :id="`${uid}-${phase.id}`" class="agenda-flow__phase-label">{{ phase.label }}</p>
        <ol
          class="agenda-flow__steps"
          :start="phase.steps[0].number"
          :aria-labelledby="`${uid}-${phase.id}`"
        >
          <li v-for="step in phase.steps" :key="step.number" class="agenda-flow__step">
            <div class="agenda-flow__card">
              <span class="agenda-flow__number" aria-hidden="true">{{ step.number }}</span>
              <abbr v-if="step.short" class="agenda-flow__short" :title="step.title">{{ step.short }}</abbr>
              <h3 class="agenda-flow__step-title">{{ step.title }}</h3>
              <p class="agenda-flow__text">{{ step.text }}</p>
            </div>
          </li>
        </ol>
      </div>
    </div>
  </Box>
</template>

<script setup lang="ts">
interface Step {
  number: number
  title: string
  /** Acronym the community uses for the stage, e.g. C4P. */
  short?: string
  text: string
}

interface Phase {
  id: 'you' | 'we'
  label: string
  steps: Step[]
}

const uid = useId()

const phases: Phase[] = [
  {
    id: 'you',
    label: 'your part',
    steps: [
      { number: 1, short: 'C4P', title: 'Call for Papers', text: 'For a few weeks, you can submit your talks.' },
      { number: 2, short: 'V4P', title: 'Vote for Papers', text: 'You vote on the submitted talks: like or dislike.' },
    ],
  },
  {
    id: 'we',
    label: 'our part',
    steps: [
      { number: 3, title: 'Speaker preferences', text: 'We contact the speakers whose talks passed the vote and collect their preferences.' },
      { number: 4, title: 'The agenda', text: 'We build the agenda from the chosen talks and the speakers’ preferences.' },
    ],
  },
]
</script>

<style scoped lang="scss">
@use "~/assets/colors" as *;
@use "~/assets/fonts" as *;
@use "~/assets/media" as *;

// Confitura design system values the site has no variables for yet.
$line: #eaeaea;      // card borders
$ink-muted: #5f575a; // secondary text, 6.9:1 on white
$ink: #000000;

$lid-height: 8px;    // the jar's lid: a square bar 69% as wide as the card
$lid-gap: 4px;
$arrow-gap: 3rem;    // space between cards that holds an arrow
$stack-gap: 3.5rem;

// A square-ended arrow drawn with clip-path, pointing right; rotated for the stacked layout.
@mixin arrow {
  content: '';
  position: absolute;
  width: 40px;
  height: 16px;
  background: $ink;
  clip-path: polygon(0 6.5px, 27px 6.5px, 27px 1px, 40px 8px, 27px 15px, 27px 9.5px, 0 9.5px);
}

.agenda-flow__title {
  margin: 0;
  font-family: $font-bold;
  font-size: 2.25rem;
  line-height: 1.15;
  color: $brand;
  @include md() {
    font-size: 3rem;
  }
}

.agenda-flow__lead {
  margin: 1rem 0 0;
  font-size: 1.4rem;
  line-height: 1.2;
  color: $ink;
  @include md() {
    margin-top: 1.5rem;
    font-size: 1.75rem;
  }
}

// Stacked by default (phones, tablets); one row of four from xl.
.agenda-flow__phases {
  display: flex;
  flex-direction: column;
  gap: $stack-gap;
  max-width: 48rem;
  margin-top: 2.5rem;
  @include xl() {
    display: grid;
    grid-template-columns: repeat(2, minmax(0, 1fr));
    column-gap: $arrow-gap;
    max-width: none;
    margin-top: 4rem;
  }
}

.agenda-flow__phase {
  --phase-color: #{$brand};
  display: flex;
  flex-direction: column;
  gap: 1rem;

  &--we {
    --phase-color: #{$ink};
  }

  @include xl() {
    gap: 1.75rem;
  }
}

.agenda-flow__phase-label {
  margin: 0;
  font-family: $font-bold;
  font-weight: bold;
  font-size: 1.4rem;
  line-height: 1.15;
  color: var(--phase-color);

  // In the row layout the label sits under its two cards, below a bar spanning them.
  @include xl() {
    order: 1;
    display: flex;
    flex-direction: column;
    gap: 0.625rem;

    &::before {
      content: '';
      height: $lid-height;
      background: var(--phase-color);
    }
  }
}

.agenda-flow__steps {
  list-style: none;
  margin: 0;
  padding: 0;
  display: flex;
  flex-direction: column;
  gap: $stack-gap;
  @include xl() {
    flex: 1 1 auto;
    display: grid;
    grid-template-columns: repeat(2, minmax(0, 1fr));
    column-gap: $arrow-gap;
  }
}

.agenda-flow__step {
  position: relative;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: $lid-gap;
  min-width: 0;

  // The lid, in the phase's colour.
  &::before {
    content: '';
    width: 69%;
    height: $lid-height;
    background: var(--phase-color);
  }

  // Arrow to the next step: below in the stack, to the right in the row.
  &::after {
    @include arrow;
    top: calc(100% + #{$stack-gap} / 2 - 8px);
    left: calc(50% - 20px);
    transform: rotate(90deg);
    @include xl() {
      top: calc(50% - 8px);
      left: calc(100% + (#{$arrow-gap} - 40px) / 2);
      transform: none;
    }
  }
}

.agenda-flow__phase:last-child .agenda-flow__step:last-child::after {
  display: none;
}

.agenda-flow__card {
  box-sizing: border-box;
  align-self: stretch;
  flex: 1 1 auto;
  display: grid;
  grid-template-columns: auto minmax(0, 1fr) auto;
  grid-template-areas:
    "number title short"
    "number text  text";
  column-gap: 1.25rem;
  row-gap: 0.5rem;
  align-items: baseline;
  padding: 1.25rem 1.25rem 1.5rem;
  border: 1px solid $line;
  background: #ffffff;
  @include md() {
    column-gap: 1.75rem;
    padding: 1.5rem 1.75rem;
  }

  @include xl() {
    grid-template-columns: minmax(0, 1fr) auto;
    grid-template-rows: auto auto 1fr;
    grid-template-areas:
      "number short"
      "title  title"
      "text   text";
    align-items: center;
    row-gap: 0.75rem;
    padding: 1.5rem 1.5rem 1.75rem;
  }
}

.agenda-flow__number {
  grid-area: number;
  align-self: start;
  min-width: 1.75rem;
  font-family: $font-bold;
  font-weight: bold;
  font-size: 3rem;
  line-height: 1;
  font-variant-numeric: tabular-nums;
  color: $ink;
  @include md() {
    min-width: 2.25rem;
    font-size: 4rem;
  }
}

.agenda-flow__short {
  grid-area: short;
  font-family: $font-bold;
  font-weight: bold;
  font-size: 1.1rem;
  letter-spacing: 0.06em;
  color: $ink-muted;
  text-decoration: none;
  cursor: help;
  @include md() {
    font-size: 1.25rem;
  }
}

.agenda-flow__step-title {
  grid-area: title;
  margin: 0;
  font-family: $font-bold;
  font-size: 1.4rem;
  line-height: 1.15;
  color: $ink;
  @include md() {
    font-size: 1.75rem;
  }
  @include xl() {
    font-size: 1.5rem;
  }
}

.agenda-flow__text {
  grid-area: text;
  align-self: start;
  margin: 0;
  font-size: 1.15rem;
  line-height: 1.4;
  color: $ink;
  @include md() {
    font-size: 1.35rem;
  }
  @include xl() {
    font-size: 1.25rem;
    line-height: 1.45;
  }
}
</style>
