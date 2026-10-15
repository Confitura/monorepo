<template>
  <div class="calendar__page">
    <PageHeader title="Add to calendar" type="planets"/>
    <Box color="white" :full="false">
      <div class="calendar__content">
        <p class="calendar__summary">
          <b>Confitura 2026</b><br/>
          Warsaw | September 25–26, 2026<br/>
          ADN Conference Center, Grzybowska 56
        </p>

        <div class="calendar__buttons">
          <a class="calendar__button" :href="googleUrl" target="_blank" rel="noopener">
            <i class="fab fa-google"></i> Google Calendar
          </a>
          <a class="calendar__button" :href="outlookUrl" target="_blank" rel="noopener">
            <i class="fas fa-envelope"></i> Outlook.com
          </a>
          <a class="calendar__button" :href="icsFile" download>
            <i class="fab fa-apple"></i> Apple Calendar
          </a>
          <a class="calendar__button" :href="icsFile" download>
            <i class="fas fa-download"></i> Download .ics
          </a>
        </div>

        <div class="calendar__agenda">
          <h2>Full agenda</h2>
          <p>
            Want every talk in your calendar too? Subscribe to the live agenda feed —
            it keeps all presentations up to date, separate from the single event above.
          </p>
          <a class="calendar__button" :href="agendaFeed">
            <i class="fas fa-calendar-alt"></i> Subscribe to all presentations
          </a>
        </div>
      </div>
    </Box>
    <Contact/>
  </div>
</template>

<script setup lang="ts">

const location = 'ADN Conference Center, Grzybowska 56, Warsaw'
const details = 'Confitura 2026 — Java Conference in Warsaw. https://confitura.pl'

const googleUrl =
    'https://calendar.google.com/calendar/render?action=TEMPLATE' +
    '&text=' + encodeURIComponent('Confitura 2026') +
    '&dates=20260925/20260927' +
    '&location=' + encodeURIComponent(location) +
    '&details=' + encodeURIComponent(details)

const outlookUrl =
    'https://outlook.live.com/calendar/0/action/compose?rru=addevent' +
    '&subject=' + encodeURIComponent('Confitura 2026') +
    '&allday=true' +
    '&startdt=2026-09-25&enddt=2026-09-27' +
    '&location=' + encodeURIComponent(location) +
    '&body=' + encodeURIComponent(details)

const icsFile = '/confitura-2026.ics'
const agendaFeed = 'webcal://api.confitura.pl/api/agenda/ical/subscribe'

const title = 'Add to calendar — Confitura 2026'
const description = 'Add Confitura 2026 (Warsaw, September 25–26) to Google, Apple, or Outlook calendars.'
useHead({
  title,
  meta: [
    {name: 'description', content: description},
    {property: 'og:title', content: title},
    {property: 'og:description', content: description},
    {property: 'og:type', content: 'website'},
    {name: 'twitter:card', content: 'summary'},
    {name: 'twitter:title', content: title},
    {name: 'twitter:description', content: description}
  ]
})

</script>

<style lang="scss" scoped>
@use "~/assets/colors" as *;
@use "~/assets/sizes" as *;
@use "~/assets/media" as *;
@use "~/assets/fonts" as *;

.calendar__content {
  max-width: 640px;
  margin: 0 auto;
}

.calendar__summary {
  font-size: 1.3rem;
  line-height: 1.6rem;
  margin-bottom: 2rem;
}

.calendar__buttons {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.calendar__button {
  display: inline-block;
  padding: 0.9rem 1.4rem;
  border: 2px solid $brand;
  border-radius: 6px;
  color: $brand;
  font-family: $font-bold;
  font-weight: bold;
  text-decoration: none;
  text-align: center;

  i {
    margin-right: 0.5rem;
  }

  &:hover {
    background-color: $brand;
    color: #ffffff;
  }
}

.calendar__agenda {
  margin-top: 3rem;

  h2 {
    color: $brand;
    font-size: 1.6rem;
    margin-bottom: 0.5rem;
  }

  p {
    font-size: 1.1rem;
    line-height: 1.5rem;
    margin-bottom: 1rem;
  }
}
</style>
