<script setup lang="ts">

import {resourcesApi, tokenAPi, usersApi} from "@/utils/api.ts";
import {ref, onMounted} from 'vue';
import {storeToRefs} from 'pinia';
import {useAuthStore} from '@/stores/auth';

let store = useAuthStore();
const {user} = storeToRefs(store)

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
  if (profilePhoto.value) {
      const formData = new FormData();
      formData.append('file', profilePhoto.value);
      const storeRequest = {
        data: formData,
        headers: {
          'Content-Type': 'multipart/form-data'
        }
      };
      return resourcesApi.storeUserProfilePicture(user?.value?.jti || '', storeRequest);
  }
}

const onSubmit = async () => {
  let valid = await xForm.value.validate()

  if (valid.valid) {
    usersApi.save(form.value)
      .then(() => tokenAPi.refreshToken())
      .then((res) => store.login(res.data))
      .then(() => uploadProfilePhoto())
      .catch((error) => {
        if(error.status == 413){
          Notify.error('Profile photo too large')
        } else {
          Notify.error('Failed to save user')
          console.error('Failed to save user:', error)
        }
      })
  }
};

const fetchUserData = async (userId: string) => {
  try {
    const response = await usersApi.getById(userId);
    form.value = response.data;
  } catch (error) {
    console.error('Failed to fetch user data:', error);
  }
};


onMounted(() => {
  if (user.value?.jti) {
    fetchUserData(user.value.jti);
  }
});

</script>

<template>
  <v-form @submit.prevent="onSubmit" ref="xForm">
    <v-container>
      <v-text-field
        v-model="form.name"
        label="* Name"
        required
        :rules="validationRules.name"
      ></v-text-field>

      <v-text-field
        v-model="form.email"
        label="* Email"
        type="email"
        :rules="validationRules.email"
        required
      ></v-text-field>

      <v-textarea
        v-model="form.bio"
        label="Bio"
        rows="3"
      ></v-textarea>

      <v-text-field
        v-model="form.username"
        label="Username"
      ></v-text-field>

      <v-text-field
        v-model="form.twitter"
        label="Twitter"
        prefix="@"
      ></v-text-field>

      <v-text-field
        v-model="form.github"
        label="GitHub"
        prefix="@"
      ></v-text-field>

      <v-text-field
        v-model="form.www"
        label="Website"
        type="url"
        prefix="https://"
      ></v-text-field>

      <v-file-input
        v-model="profilePhoto"
        label="Profile Photo"
        accept="image/*"
        prepend-icon="mdi-camera"
        show-size
        truncate-length="15"
      ></v-file-input>

      <v-checkbox v-model="form.privacyPolicyAccepted"
                  :rules="validationRules.privacy"
                  required>
        <template v-slot:label>
          <div>
            * I accept the <a href="/privacy-policy"> Privacy policy</a>
          </div>
        </template>
      </v-checkbox>

      <v-btn type="submit" color="primary">Submit</v-btn>
    </v-container>
  </v-form>
</template>
