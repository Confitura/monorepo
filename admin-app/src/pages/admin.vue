<script setup lang="ts">
definePage({
  meta: {
    title: 'Admin',
    icon: 'mdi-view-list',
    drawerIndex: 2,
    breadcrumb: 'disabled',
    requiresAdmin: true
  },
})
</script>
<template>
  <RouterWrapper />
</template>
