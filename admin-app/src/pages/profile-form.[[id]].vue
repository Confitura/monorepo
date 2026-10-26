<script setup lang="ts">

import {storeUserProfilePicture, refreshToken, save1, getById, getUserLogins, unlinkLogin} from "@/utils/api.ts";
import type {LoginDto} from "@/utils/api.ts";
import {SIGN_IN_PROVIDERS, goToSignIn, providerLabel} from "@/utils/sign-in-attempt.ts";
import {ref, onMounted, computed} from 'vue';
import {storeToRefs} from 'pinia';
import {useAuthStore} from '@/stores/auth';
import { useRoute } from 'vue-router';

const store = useAuthStore();
const {user} = storeToRefs(store)
const route = useRoute()

const effectiveUserId = computed(() => {
  const routeId = (route.params as { id?: string })?.id
  return routeId || user.value?.jti
})

// Logins show on the signed-in User's own profile only: a new one is proven by signing in with it, so it
// can only be added there.
const isOwnProfile = computed(() => effectiveUserId.value === user.value?.jti)
const logins = ref<LoginDto[]>([])
const unlinkedProviders = computed(() =>
  SIGN_IN_PROVIDERS.filter((provider) => !logins.value.some((login) => login.provider === provider.id)))

function addedOn(login: LoginDto) {
  return login.createdDate ? `Added ${new Date(login.createdDate).toLocaleDateString()}` : ''
}

// The Login is deleted; adding it again later means signing in with it, as for any new Login.
async function removeLogin(login: LoginDto) {
  try {
    await unlinkLogin({ path: { id: effectiveUserId.value!, loginId: login.id! } })
    logins.value = logins.value.filter((other) => other.id !== login.id)
  } catch (error) {
    Notify.error(`Failed to remove the ${providerLabel(login.provider)} login`)
  }
}

const validationRules = {
  name: [(v: string) => !!v || 'Name is required'],
  email: [
    (v: string) => !!v || 'Email is required',
    (v: string) => /.+@.+\..+/.test(v) || 'Email must be valid',
  ],
  privacy: [(v: boolean) => v || 'You must accept the privacy policy'],
};

interface UserForm {
  id?: string | undefined;
  name: string;
  email: string;
  bio?: string;
  username?: string;
  twitter?: string;
  github?: string;
  www?: string;
  privacyPolicyAccepted: boolean;
}

const xForm = ref()
const profilePhoto = ref<File | null>(null)

const form = ref<UserForm>({
  id: undefined,
  name: '',
  email: '',
  bio: '',
  username: '',
  twitter: '',
  github: '',
  www: '',
  privacyPolicyAccepted: false
});

function uploadProfilePhoto() {
  if (profilePhoto.value && effectiveUserId.value) {
      return storeUserProfilePicture({
        path: { userId: effectiveUserId.value },
        body: { file: profilePhoto.value },
      });
  }
}

const onSubmit = async () => {
  const valid = await xForm.value.validate()

  if (valid.valid) {
    // ensure id is set to the effective user id before save
    if (!form.value.id && effectiveUserId.value) {
      form.value.id = effectiveUserId.value
    }
    save1({ body: form.value })
      .then(() => refreshToken())
      .then((res) => store.login(res.data))
      .then(() => uploadProfilePhoto())
      .catch((error) => {
        if(error.status == 413){
          Notify.error('Profile photo too large, max 3 MB')
        } else {
          Notify.error('Failed to save user')
          console.error('Failed to save user:', error)
        }
      })
  }
};

const fetchUserData = async (userId: string) => {
  try {
    const response = await getById({ path: { id: userId } });
    form.value = { ...response.data, name: response.data?.name ?? '', email: response.data?.email ?? '', privacyPolicyAccepted: response.data?.privacyPolicyAccepted ?? false };
  } catch (error) {
    console.error('Failed to fetch user data:', error);
  }
};


const fetchLogins = async (userId: string) => {
  const response = await getUserLogins({ path: { id: userId } });
  logins.value = response.data ?? [];
};

onMounted(() => {
  if (effectiveUserId.value) {
    fetchUserData(effectiveUserId.value);
    if (isOwnProfile.value) {
      fetchLogins(effectiveUserId.value);
    }
  }
});

</script>

<template>
  <v-form ref="xForm" @submit.prevent="onSubmit">
    <v-container>
      <v-text-field
        v-model="form.name"
        label="* Name"
        required
        :rules="validationRules.name"
      />

      <v-text-field
        v-model="form.email"
        label="* Email"
        type="email"
        :rules="validationRules.email"
        required
      />

      <v-textarea
        v-model="form.bio"
        label="Bio"
        rows="3"
      />

      <v-text-field
        v-model="form.username"
        label="Username"
      />

      <v-text-field
        v-model="form.twitter"
        label="Twitter"
        prefix="@"
      />

      <v-text-field
        v-model="form.github"
        label="GitHub"
        prefix="@"
      />

      <v-text-field
        v-model="form.www"
        label="Website"
        type="url"
        prefix="https://"
      />

      <v-file-input
        v-model="profilePhoto"
        label="Profile Photo"
        accept="image/*"
        prepend-icon="mdi-camera"
        show-size
        truncate-length="15"
      />

      <v-checkbox
v-model="form.privacyPolicyAccepted"
                  :rules="validationRules.privacy"
                  required>
        <template #label>
          <div>
            * I accept the <a href="/privacy-policy"> Privacy policy</a>
          </div>
        </template>
      </v-checkbox>

      <v-btn type="submit" color="primary">Submit</v-btn>
    </v-container>
  </v-form>
  <v-container v-if="isOwnProfile">
    <section aria-label="Logins">
      <h3 class="text-h6">Logins</h3>
      <v-list density="compact">
        <v-list-item
          v-for="login in logins"
          :key="login.id"
          :title="providerLabel(login.provider)"
          :subtitle="addedOn(login)"
        >
          <template #append>
            <!-- The last Login stays: without it the User could not sign in again. -->
            <v-btn
              variant="text"
              size="small"
              :disabled="logins.length === 1"
              :aria-label="`Remove ${providerLabel(login.provider)} login`"
              @click="removeLogin(login)"
            >Remove</v-btn>
          </template>
        </v-list-item>
      </v-list>
      <v-btn
        v-for="provider in unlinkedProviders"
        :key="provider.id"
        class="mr-2"
        variant="outlined"
        prepend-icon="mdi-plus"
        :aria-label="`Add ${provider.label} login`"
        @click="goToSignIn(provider.id, 'add-login')"
      >{{ provider.label }}</v-btn>
    </section>
  </v-container>
</template>
