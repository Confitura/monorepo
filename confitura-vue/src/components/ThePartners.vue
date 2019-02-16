<template>
  <Box class="partners" color="white">
    <h1 class="header">our partners</h1>
    <p class="info">If you want to become a partner of Confitura 2019, contact us:</p>
    <p class="info">
      <a class="contact-link" href="mailto:confitura@confitura.pl">confitura@confitura.pl</a>
    </p>
  </Box>
</template>

<script>
import Box from './Box';
export default {
  name: 'ThePartners',
  components: { Box },
};
</script>

<style scoped lang="scss">
@import '../assets/colors';
@import '../assets/fonts';
.partners {
  text-align: left;
}
.header {
  color: $brand;
  font-family: $font-bold;
  font-size: 3rem;
}
.info {
  font-size: 1.75rem;
}
.contact-link {
  color: #000000;
  text-decoration: none;
  &:hover {
    text-decoration: underline;
  }
}
</style>