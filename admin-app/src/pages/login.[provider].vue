<script setup lang="ts">

import {useRoute} from 'vue-router'
import api from "@/utils/api.ts";

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

let provider: string = route.params["provider"];
api.get(`login/${provider}/callback`, {params: route.query})
  .then(response => {
    useAuthStore().login(response.data)
  })

// router.push('/homepage')
</script>

<template>
  <v-empty-state
    headline="Loading"
    title="authentication in progress"
  ></v-empty-state>
</template>

<style scoped lang="scss">

</style>
