<script setup lang="ts">

import router from "@/plugins/router.ts";

definePage({
  meta: {
    breadcrumb: 'hidden',
    icon: 'mdi-home',
    title: 'Login',
    drawerIndex: 0,
    skipMenu: true,
    layout: 'whole-page',
  },
})


import {onMounted} from 'vue';

onMounted(() => {
  let accessToken = new URLSearchParams(window.location.hash.slice(1)).get('access_token');
  if (accessToken == null) {
    router.push('/login');
  } else {
    useAuthStore().login(accessToken);
  }
});
</script>

<template>
  <v-empty-state
    headline="Loading"
    title="authentication in progress"
  ></v-empty-state>
</template>

<style scoped lang="scss">

</style>
