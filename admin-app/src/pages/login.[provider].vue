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
import {cancelMerge, mergeUsers, previewMerge} from "@/utils/api.ts";
import type {MergePreview} from "@/utils/api.ts";

const params = useUrlSearchParams('hash-params')
const query = useUrlSearchParams('history')
const provider = providerLabel((useRoute().params as { provider?: string }).provider)
const rejected = ref(false)
// When adding a Login: the token of the User that Login signs in as, kept only until confirmed or cancelled.
const addedLoginToken = ref<string | null>(null)
const alreadyLinked = ref(false)
// What the signed-in User would be after the merge, or why the merge is refused.
const preview = ref<MergePreview | null>(null)
const refusal = ref<string | null>(null)

const PROFILE_FIELDS = [
  ['name', 'Name'], ['email', 'Email'], ['bio', 'Bio'], ['username', 'Username'],
  ['twitter', 'Twitter'], ['github', 'GitHub'], ['www', 'Website'],
] as const
const previewFields = computed(() => PROFILE_FIELDS.filter(([key]) => preview.value?.[key]))
const gainedRoles = computed(() =>
  [preview.value?.becomesAdmin && 'an admin', preview.value?.becomesVolunteer && 'a volunteer'].filter(Boolean))

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
      loadPreview()
    }
  } else {
    // A callback this tab didn't start (e.g. a forged link): never sign in with its token.
    rejected.value = true
  }
});

// The backend refuses the preview exactly when it would refuse the merge.
async function loadPreview() {
  try {
    const response = await previewMerge({ body: { token: addedLoginToken.value! }, throwOnError: true })
    preview.value = response.data
  } catch (error) {
    refusal.value = refusalMessage(error)
  }
}

function refusalMessage(error: unknown) {
  const { status, response } = error as { status?: number, response?: { data?: unknown } }
  if (status === 403) {
    return `This ${provider} sign-in is too old to use. Please add the login again.`
  }
  // A 409 says why the Users cannot be merged, e.g. both hold a ticket.
  return status === 409 && typeof response?.data === 'string' ? response.data : `Failed to add the ${provider} login`
}

// The backend merges the User the second token names into the signed-in one and answers with a
// fresh token for the signed-in User.
async function confirmLink() {
  try {
    const response = await mergeUsers({ body: { token: addedLoginToken.value! }, throwOnError: true })
    useAuthStore().replaceToken(response.data)
    Notify.success(`${provider} login added`)
    router.push('/profile-form')
  } catch (error) {
    Notify.error(refusalMessage(error))
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
    v-else-if="refusal"
    :headline="`Cannot add ${provider} login`"
    :text="refusal"
    action-text="Back to profile"
    to="/profile-form"
  ></v-empty-state>
  <v-empty-state
    v-else-if="preview"
    :headline="`Add ${provider} login`"
    :title="`Add your ${provider} login to this account?`"
    :text="`From now on, signing in with ${provider} opens this account, and anything you saved under that login is merged in. Your profile will be:`"
  >
    <v-avatar v-if="preview.photo" :image="preview.photo" size="64" />
    <v-list density="compact">
      <v-list-item v-for="[key, label] in previewFields" :key="key" :subtitle="label" :title="preview[key]" />
    </v-list>
    <p v-if="gainedRoles.length">You also become {{ gainedRoles.join(' and ') }}.</p>
    <template v-if="preview.talksToMove?.length">
      <p>These talks move to this account:</p>
      <v-list density="compact">
        <v-list-item v-for="(title, index) in preview.talksToMove" :key="index" :title="title" />
      </v-list>
    </template>
    <v-alert type="warning" variant="tonal" text="This cannot be undone." />
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
