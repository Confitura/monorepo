<script setup lang="ts">
import {presentationApi, usersApi} from "@/utils/api.ts";
import {ref} from 'vue'
import type {PresentationRequest, Tag} from "@/utils/api-axios-client";
import {useAuthStore} from "@/stores/auth.ts";
import {onMounted} from 'vue'
import {useRoute} from "vue-router";
import router from "@/plugins/router.ts";

let user = useAuthStore().user
const route = useRoute()
const presentationId = route.params.id

definePage({
  meta: {
    icon: 'mdi-monitor-dashboard',
    title: 'Submit Presentation',
    drawerIndex: 5,
    requiresAuth: true,
  },
})

const presentation = ref<PresentationRequest>({
  title: '',
  shortDescription: '',
  description: '',
  level: '',
  language: 'Polish',
  tags: [],
})

let availableTags = ref<Tag[]>([])

onMounted(async () => {
  presentationApi.getAllTags()
    .then(response => response.data)
    .then(data => availableTags.value = data)
    .catch(error => console.error(error))

  if (presentationId) {
    usersApi.getPresentation(user!.jti, presentationId)
      .then(response => response.data)
      .then(data => presentation.value = data)
  } else {
    console.log('no id')
  }

})

function doSubmit() {
  if (presentationId) {
    return usersApi.updatePresentation(user!.jti, presentationId, presentation.value)
  } else {
    return usersApi.addPresentationToUser(user!.jti, presentation.value);
  }
}

function handleSubmit() {
  doSubmit()
    .then(_ => Notify.success(`Presentation ${presentation.value.title} saved`))
    .then(_ => router.push('/homepage'))
    .catch(error => console.error('Submission failed:', error))
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
      :items="['English', 'Polish']"
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
