<script setup lang="ts">
import {usersApi, presentationApi} from "@/utils/api.ts";
import {ref} from 'vue'
import type {WorkshopRequest, Tag} from "@/utils/api-axios-client";
import {useAuthStore} from "@/stores/auth.ts";
import {onMounted} from 'vue'
import {useRoute} from "vue-router";
import router from "@/plugins/router.ts";

let user = useAuthStore().user
const route = useRoute()
const workshopId = route.params.id
const formValid = ref(false)

definePage({
  meta: {
    icon: 'mdi-monitor-dashboard',
    title: 'Submit Workshop',
    drawerIndex: 5,
    requiresAuth: true,
  },
})

const workshop = ref<WorkshopRequest>({
  title: '',
  shortDescription: '',
  description: '',
  level: '',
  language: 'Polish',
  tags: [],
  isFree: true,
  expectedPrice: 0,
  durationInMinutes: 45,
  maxGroupSize: 10,
})

let availableTags = ref<Tag[]>([])

// Validation rules
const requiredRule = (value: string) => !!value.trim() || 'This field is required'

onMounted(async () => {
  presentationApi.getAllTags()
    .then(response => response.data)
    .then(data => availableTags.value = data)
    .catch(error => console.error(error))

  if (workshopId) {
    usersApi.getWorkshop(user!.jti, workshopId)
      .then(response => response.data)
      .then(data => workshop.value = data)
  } else {
    console.log('no id')
  }

})

function doSubmit() {
  if (workshopId) {
    return usersApi.updateWorkshop(user!.jti, workshopId, workshop.value)
  } else {
    return usersApi.addWorkshopToUser(user!.jti, workshop.value);
  }
}

function handleSubmit() {
  if (!formValid.value) return;

  doSubmit()
    .then(_ => Notify.success(`Workshop ${workshop.value.title} saved`))
    .then(_ => router.back())
    .catch(error => console.error('Submission failed:', error))
}


</script>
<template>

  <v-form @submit.prevent="handleSubmit" v-model="formValid">
    <v-text-field
      v-model="workshop.title"
      label="Title"
      outlined
      required
      :rules="[requiredRule]"
    ></v-text-field>

    <v-text-field
      v-model="workshop.shortDescription"
      label="Short Description"
      outlined
      required
      :rules="[requiredRule]"
    ></v-text-field>

    <v-textarea
      v-model="workshop.description"
      label="Description"
      outlined
      rows="4"
      required
      :rules="[requiredRule]"
    ></v-textarea>

    <v-select
      v-model="workshop.level"
      :items="['Beginner', 'Intermediate', 'Advanced']"
      label="Level"
      outlined
      required
      :rules="[requiredRule]"
    ></v-select>

    <v-select
      v-model="workshop.language"
      :items="['English', 'Polish']"
      label="Language"
      outlined
      required
      :rules="[requiredRule]"
    ></v-select>


    <v-select
      v-model="workshop.tags"
      :items="availableTags"
      label="Tags"
      multiple
      item-title="name"
      item-value="id"
      outlined
    ></v-select>

    <v-switch
      v-model="workshop.isFree"
      label="Is the workshop free?"
      inset
      outlined
    ></v-switch>

    <v-text-field
      v-if="!workshop.isFree"
      v-model="workshop.expectedPrice"
      label="Expected Price (in PLN)"
      outlined
      required
      type="number"
    ></v-text-field>

    <v-text-field
      v-model="workshop.durationInMinutes"
      label="Duration (in minutes)"
      outlined
      required
      type="number"
    ></v-text-field>

    <v-text-field
      v-model="workshop.maxGroupSize"
      label="Maximum Group Size"
      outlined
      required
      type="number"
    ></v-text-field>

    <v-btn type="submit" color="primary" class="mt-4" :disabled="!formValid">
      Submit
    </v-btn>
  </v-form>


</template>

<style scoped lang="scss">

</style>
