<template>
  <div class="home__container">
    <HomeMainBanner/>
    <HomeLatestNews></HomeLatestNews>
    <HomeNumbers/>
    <HomeAbout/>
    <HomeInvitePartners/>
    <Contact/>
  </div>
</template>

<script setup lang="ts">

const title = 'Confitura 2026 — Java Conference in Warsaw';
const description = 'Join the largest Java community conference in Poland. Talks, workshops, and networking in Warsaw — Confitura 2026.';

useHead({
  title,
  meta: [
    { name: 'description', content: description },
    { property: 'og:title', content: title },
    { property: 'og:description', content: description },
    { property: 'og:type', content: 'website' },
    { name: 'twitter:card', content: 'summary' },
    { name: 'twitter:title', content: title },
    { name: 'twitter:description', content: description }
  ],
  script: [{
    type: 'application/ld+json',
    innerHTML: JSON.stringify({
      '@context': 'https://schema.org',
      '@type': 'Event',
      name: 'Confitura 2026',
      startDate: '2026-09-25T08:00:00+02:00',
      endDate: '2026-09-26T18:15:00+02:00',
      eventStatus: 'https://schema.org/EventScheduled',
      eventAttendanceMode: 'https://schema.org/OfflineEventAttendanceMode',
      location: {
        '@type': 'Place',
        name: 'ADN Centrum Konferencyjne',
        address: {
          '@type': 'PostalAddress',
          streetAddress: 'ul. Grzybowska 56',
          postalCode: '00-844',
          addressLocality: 'Warszawa',
          addressCountry: 'PL'
        },
        geo: {
          '@type': 'GeoCoordinates',
          latitude: 52.23522478133525,
          longitude: 20.987968556704143
        }
      },
      url: 'https://confitura.pl',
      organizer: {
        '@type': 'Organization',
        name: 'Confitura',
        url: 'https://confitura.pl'
      },
      description: description,
    })
  }]
});
</script>

<style lang="scss">
@supports not (-webkit-overflow-scrolling: touch) {
  .home__container {
    max-height: 100vh;
    overflow-y: scroll;
    scroll-snap-type: y proximity;

    & > section {
      scroll-snap-align: start;
    }
  }
}
</style>
