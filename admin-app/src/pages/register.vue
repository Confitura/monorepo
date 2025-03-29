<template>
  <v-form @submit.prevent="onSubmit">
    <v-container>
      <v-text-field
        v-model="form.name"
        label="Name"
        required
      ></v-text-field>

      <v-text-field
        v-model="form.email"
        label="Email"
        type="email"
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

      <v-checkbox
        v-model="form.privacyPolicyAccepted"
        label="I accept the privacy policy"
        required
      ></v-checkbox>

      <v-btn type="submit" color="primary">Submit</v-btn>
    </v-container>
  </v-form>
</template>

<script setup lang="ts">
import axios from "axios";
import api from "@/utils/api.ts";
let store = useAuthStore();
const {user} = storeToRefs(store)

interface UserForm {
  id: string | undefined;
  name: string;
  email: string;
  bio: string;
  username: string;
  twitter: string;
  github: string;
  www: string;
  privacyPolicyAccepted: boolean;
}

const form = ref<UserForm>({
  id: user?.value?.jti,
  name: '',
  email: '',
  bio: '',
  username: '',
  twitter: '',
  github: '',
  www: '',
  privacyPolicyAccepted: false
});

const onSubmit = () => {
  api.post('/users', form.value).then(
    () => {
      store.updateRegistered(form.value.name);
    },
    (error) => {
      console.log(error);
    });
};

api.get('/users/' + user.value.jti).then(
  it => {
    form.value = it.data;
  }
)
</script>
