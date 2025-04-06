<script setup lang="ts">

import {useRoute} from 'vue-router'
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


const route = useRoute()
let accessToken = route.query["access_token"];
if (accessToken == null) {
  router.push('/login')
} else {
  useAuthStore().login(accessToken.toString())
}
</script>

<template>
  <v-empty-state
    headline="Loading"
    title="authentication in progress"
  ></v-empty-state>
</template>

<style scoped lang="scss">

</style>
