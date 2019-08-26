<template>
  <div class="tickets">
    <PageHeader title="Tickets" />
    <Box class="content" color="white">
      <PageFragment name="registration-info" class="tickets__info" />
    </Box>
    <TheContact id="contact" />
  </div>
</template>

<script>
import PageFragment from "../components/PageFragment";
import PageHeader from "../components/PageHeader";
import TheContact from "../components/TheContact";
import Box from "../components/Box";

export default {
  name: "RegistrationInfoPage",
  components: { PageFragment, PageHeader, TheContact, Box }
};
</script>

<style lang="scss">
@import "../assets/colors";

.tickets__info {
  font-size: 1.5rem;
  line-height: 2rem;

  a {
    color: $brand;
    text-decoration: none;

    &:hover {
      text-decoration: underline;
    }
  }

  .table {
    width: 100%;
    border-collapse: collapse;

    td,
    th {
      padding: 0.75rem;
      border-bottom: 1px #dee2e6 solid;
      box-sizing: border-box;
    }

    tr {
      box-sizing: border-box;
    }
  }
}
</style>
