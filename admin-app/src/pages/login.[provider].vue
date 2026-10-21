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
import {jwtDecode} from "jwt-decode";
import {finishSignInAttempt, providerLabel} from "@/utils/sign-in-attempt.ts";
import {cancelMerge, mergeUsers} from "@/utils/api.ts";

const params = useUrlSearchParams('hash-params')
const query = useUrlSearchParams('history')
const provider = providerLabel((useRoute().params as { provider?: string }).provider)
const rejected = ref(false)
// When adding a Login: the token of the User that Login signs in as, kept only until confirmed or cancelled.
const addedLoginToken = ref<string | null>(null)
const alreadyLinked = ref(false)

onMounted(() => {
  const accessToken = params.access_token as string
  if (accessToken == null) {
    router.push('/login');
    return
  }
  const intent = finishSignInAttempt(query.state as string)
  if (intent === 'sign-in') {
    useAuthStore().login(accessToken);
  } else if (intent === 'add-login') {
    // The token names the User the Login signs in as; the signed-in User's own means nothing to add.
    if (jwtDecode(accessToken).jti === useAuthStore().user?.jti) {
      alreadyLinked.value = true
    } else {
      addedLoginToken.value = accessToken
    }
  } else {
    // A callback this tab didn't start (e.g. a forged link): never sign in with its token.
    rejected.value = true
  }
});

// The backend merges the User the second token names into the signed-in one and answers with a
// fresh token for the signed-in User.
async function confirmLink() {
  try {
    const response = await mergeUsers({ body: { token: addedLoginToken.value! }, throwOnError: true })
    useAuthStore().replaceToken(response.data)
    Notify.success(`${provider} login added`)
    router.push('/profile-form')
  } catch (error) {
    const status = (error as { status?: number })?.status
    if (status === 403) {
      Notify.error(`This ${provider} sign-in is too old to use. Please add the login again.`)
    } else if (status === 409) {
      Notify.error(`This ${provider} login belongs to another account with its own profile, talks, roles or ticket. Merging such accounts is not available yet.`)
    } else {
      Notify.error(`Failed to add the ${provider} login`)
    }
  }
}

// Deletes the User the second sign-in created, so nothing is left behind. Whatever the backend answers,
// the signed-in User is unchanged, so the page just goes back.
async function cancelLink() {
  await cancelMerge({ body: { token: addedLoginToken.value! } })
  router.push('/profile-form')
}
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
    v-else-if="alreadyLinked"
    headline="Nothing to add"
    :title="`This ${provider} login already signs in to your account.`"
    action-text="Back to profile"
    to="/profile-form"
  ></v-empty-state>
  <v-empty-state
    v-else-if="addedLoginToken"
    :headline="`Add ${provider} login`"
    :title="`Add your ${provider} login to this account?`"
    :text="`From now on, signing in with ${provider} opens this account.`"
  >
    <template #actions>
      <v-btn @click="cancelLink">Cancel</v-btn>
      <v-btn color="primary" @click="confirmLink">Add login</v-btn>
    </template>
  </v-empty-state>
  <v-empty-state
    v-else
    headline="Loading"
    title="authentication in progress"
  ></v-empty-state>
</template>

<style scoped lang="scss">

</style>
