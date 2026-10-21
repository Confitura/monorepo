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
import {useUrlSearchParams} from "@vueuse/core";
import {finishSignInAttempt} from "@/utils/sign-in-attempt.ts";

const params = useUrlSearchParams('hash-params')
const query = useUrlSearchParams('history')
const rejected = ref(false)

onMounted(() => {
  const accessToken = params.access_token as string
  if (accessToken == null) {
    router.push('/login');
  } else if (finishSignInAttempt('sign-in', query.state as string)) {
    useAuthStore().login(accessToken);
  } else {
    // A callback this tab didn't start (e.g. a forged link): never sign in with its token.
    rejected.value = true
  }
});
</script>

<template>
  <v-empty-state
    v-if="rejected"
    headline="Sign-in failed"
    title="This sign-in could not be verified"
    text="Please sign in again."
    action-text="Back to login"
    to="/login"
  ></v-empty-state>
  <v-empty-state
    v-else
    headline="Loading"
    title="authentication in progress"
  ></v-empty-state>
</template>

<style scoped lang="scss">

</style>
