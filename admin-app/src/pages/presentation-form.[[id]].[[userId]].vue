<script setup lang="ts">
import {getAllTags, getPresentation, updatePresentation, addPresentationToUser} from "@/utils/api.ts";
import {ref,onMounted} from 'vue'
import type {PresentationRequest, Tag} from "@/utils/api";
import {useAuthStore} from "@/stores/auth.ts";
import {useRoute} from "vue-router";
import router from "@/plugins/router.ts";

const user = useAuthStore().user
const route = useRoute()
const presentationId = (route.params as { id?: string; userId?: string }).id
const userId = (route.params as { id?: string; userId?: string }).userId
const formValid = ref(false)

const actualUserId = computed(() => {
  return userId || user?.jti;
})
// definePage({
//   meta: {
//     icon: 'mdi-monitor-dashboard',
//     title: 'Submit Presentation',
//     drawerIndex: 5,
//     requiresAuth: true,
//   },
// })

const presentation = ref<PresentationRequest>({
  title: '',
  shortDescription: '',
  description: '',
  level: '',
  language: 'Polish',
  tags: [],
})

const availableTags = ref<Tag[]>([])

// Validation rules
const requiredRule = (value: string) => !!value.trim() || 'This field is required'

onMounted(async () => {
  getAllTags()
    .then(response => response.data)
    .then(data => availableTags.value = data ?? [])
    .catch(error => console.error(error))

  if (presentationId && presentationId != 'new') {
    getPresentation({ path: { id: actualUserId.value!, presentationId: presentationId! } })
      .then(response => response.data)
      .then(data => { if (data) presentation.value = data })
  } else {
    console.log('no id')
  }

})

function doSubmit() {
  if (presentationId && presentationId != 'new') {
    return updatePresentation({ path: { userId: actualUserId.value!, presentationId: presentationId! }, body: presentation.value })
  } else {
    return addPresentationToUser({ path: { userId: actualUserId.value! }, body: presentation.value });
  }
}

function handleSubmit() {
  if (!formValid.value) return;

  doSubmit()
    .then(_ => Notify.success(`Presentation ${presentation.value.title} saved`))
    .then(_ => router.back())
    .catch(error => console.error('Submission failed:', error))
}


</script>
<template>

  <v-form v-model="formValid" @submit.prevent="handleSubmit">
    <v-text-field
      v-model="presentation.title"
      label="Title"
      outlined
      required
      :rules="[requiredRule]"
    />

    <v-text-field
      v-model="presentation.shortDescription"
      label="Short Description"
      outlined
      required
      :rules="[requiredRule]"
    />

    <v-textarea
      v-model="presentation.description"
      label="Description"
      outlined
      rows="4"
      required
      :rules="[requiredRule]"
    />

    <v-select
      v-model="presentation.level"
      :items="['Beginner', 'Intermediate', 'Advanced']"
      label="Level"
      outlined
      required
      :rules="[requiredRule]"
    />

    <v-select
      v-model="presentation.language"
      :items="['English', 'Polish']"
      label="Language"
      outlined
      required
      :rules="[requiredRule]"
    />


    <v-select
      v-model="presentation.tags"
      :items="availableTags"
      label="Tags"
      multiple
      item-title="name"
      item-value="id"
      outlined
    />

    <v-btn type="submit" color="primary" class="mt-4" :disabled="!formValid">
      Submit
    </v-btn>
  </v-form>


</template>

<style scoped lang="scss">

</style>
