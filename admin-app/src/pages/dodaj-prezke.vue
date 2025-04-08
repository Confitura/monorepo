<script setup lang="ts">
import {presentationApi, usersApi} from "@/utils/api.ts";
import {ref} from 'vue'
import type {PresentationRequest, Tag} from "@/utils/api-axios-client";
import {useAuthStore} from "@/stores/auth.ts";

let user = useAuthStore().user

definePage({
  meta: {
    icon: 'mdi-monitor-dashboard',
    title: 'Dodaj prezke',
    drawerIndex: 5,
    requiresAuth: true,
  },
})

const presentation = ref<PresentationRequest>({
  title: '',
  shortDescription: '',
  description: '',
  level: '',
  language: '',
  tags: [],
})

import {onMounted} from 'vue'

let availableTags = ref<Tag[]>([])
onMounted(async () => {
  presentationApi.getAllTags()
    .then(response => response.data)
    .then(data => availableTags.value = data)
    .catch(error => console.error(error))

})

function handleSubmit() {
  console.log('Form Submitted:', presentation.value)
  // Example API call
  usersApi.addPresentationToUser(user!.jti, presentation.value).then(response => {
    console.log('Submission successful:', response)
  }).catch(error => {
    console.error('Submission failed:', error)
  })
}


</script>
<template>

  <v-form @submit.prevent="handleSubmit">
    <v-text-field
      v-model="presentation.title"
      label="Title"
      outlined
      required
    ></v-text-field>

    <v-text-field
      v-model="presentation.shortDescription"
      label="Short Description"
      outlined
      required
    ></v-text-field>

    <v-textarea
      v-model="presentation.description"
      label="Description"
      outlined
      rows="4"
      required
    ></v-textarea>

    <v-select
      v-model="presentation.level"
      :items="['Beginner', 'Intermediate', 'Advanced']"
      label="Level"
      outlined
      required
    ></v-select>

    <v-select
      v-model="presentation.language"
      :items="['English', 'French', 'Spanish', 'German']"
      label="Language"
      outlined
      required
    ></v-select>


    <v-select
      v-model="presentation.tags"
      :items="availableTags"
      label="Tags"
      multiple
      item-title="name"
      item-value="id"
      outlined
    ></v-select>

    <v-btn type="submit" color="primary" class="mt-4">
      Submit
    </v-btn>
  </v-form>


</template>

<style scoped lang="scss">

</style>
