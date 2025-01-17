<template>
  <div class="tickets">
    <PageHeader title="Tickets" />
    <Box class="content" color="white">
      <PageFragment name="registration-info" class="tickets__info" />
    </Box>
    <Contact />
  </div>
</template>

<script setup>
</script>

<style lang="scss">
@use "~/assets/colors" as *;

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
