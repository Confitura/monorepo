<script setup lang="ts">
defineProps<{
  dayId: string
}>()
</script>

<template>
  <div class="agendaHeader">

    <RouterLink to="/schedule/day-1" class="agendaHeader__day"
                :class="{'selected':'day-1' == dayId}">Friday
    </RouterLink>
    <RouterLink to="/schedule/workshops/day-1" class="agendaHeader__day"
                :class="{'selected':'day-1-workshops' == dayId}">workshops
    </RouterLink>
    <RouterLink to="/schedule/day-2" class="agendaHeader__day"
                :class="{'selected':'day-2' == dayId}">Saturday
    </RouterLink>
    <RouterLink to="/schedule/workshops/day-2" class="agendaHeader__day"
                :class="{'selected':'day-2-workshops' == dayId}">workshops
    </RouterLink>
  </div>
</template>

<style scoped lang="scss">
@use "~/assets/colors" as *;
@use "~/assets/sizes" as *;
@use "~/assets/media" as *;
@use "~/assets/fonts" as *;

.agendaHeader {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(0, 1fr));
  width: 100%;
  gap: 1rem;
  margin-bottom: 1rem;

  .agendaHeader__day {
    text-align: center;
    padding: 0.5rem;
    font-weight: bold;
    text-decoration: none;
    color: inherit;
  }

  .agendaHeader__day:hover {
    background-color: #ffb1bc;
    cursor: pointer;
  }

  .selected {
    background-color: $brand;
    color: white;
  }
}

</style>