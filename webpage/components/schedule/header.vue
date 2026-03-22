<script setup lang="ts">
defineProps<{
  dayId: string
}>()
</script>

<template>

  <div class="agendaHeader">

    <span class="agendaHeader__day">Friday</span>
    <RouterLink to="/schedule/day-1" class="agendaHeader__button"
                :class="{'selected':'day-1' == dayId}">Presentations
    </RouterLink>
    <RouterLink to="/schedule/workshops/day-1" class="agendaHeader__button"
                :class="{'selected':'day-1-workshops' == dayId}">Workshops
    </RouterLink>
    <span class="agendaHeader__day">Saturday</span>
    <RouterLink to="/schedule/day-2" class="agendaHeader__button"
                :class="{'selected':'day-2' == dayId}">Presentations
    </RouterLink>
    <RouterLink to="/schedule/workshops/day-2" class="agendaHeader__button"
                :class="{'selected':'day-2-workshops' == dayId}">Workshops
    </RouterLink>
    <a class="agendaHeader__calendar"
       title="Use this url to add to your calendar"
       href="webcal://api.confitura.pl/api/agenda/ical/subscribe">
      <i class="fas fa-calendar-alt"></i>
    </a>
  </div>
</template>

<style scoped lang="scss">
@use "~/assets/colors" as *;
@use "~/assets/sizes" as *;
@use "~/assets/media" as *;
@use "~/assets/fonts" as *;

.agendaHeader {
  display: grid;

  grid-template-columns: 1fr 1fr;
  grid-template-areas: "day1 day1 cal"
                        "pres1 work1 cal"
                        "day2 day2 cal"
                        "pres2 work2 cal";
  width: 100%;
  gap: 0.5rem;
  margin-bottom: 1rem;

  .agendaHeader__day {
    text-align: center;
    padding: 0.5rem;
    font-weight: bold;
    text-decoration: none;
    color: inherit;

    &:nth-child(1) {
      grid-area: day1;
    }

    &:nth-child(4) {
      grid-area: day2;
    }
  }

  .agendaHeader__button {
    text-align: center;
    padding: 0.5rem;
    font-weight: bold;
    text-decoration: none;
    color: inherit;

    &:nth-child(2) {
      grid-area: pres1;
    }

    &:nth-child(3) {
      grid-area: work1;
    }

    &:nth-child(5) {
      grid-area: pres2;
    }

    &:nth-child(6) {
      grid-area: work2;
    }
  }

  .agendaHeader__button:hover {
    background-color: #ffb1bc;
    cursor: pointer;
  }

  .selected {
    background-color: $brand;
    color: white;
  }

  .agendaHeader__calendar {
    text-align: center;
    padding: 0.5rem;
    font-size: 1.5rem;
    color: inherit;
    text-decoration: none;
    cursor: pointer;
    grid-area: cal;

    &:hover {
      color: $brand;
    }
  }

  @include lg() {
    grid-template-columns: repeat(4, 1fr);
    grid-template-areas: "day1 day1 day2 day2 cal"
                       "pres1 work1 pres2 work2 cal";
  }
}

</style>