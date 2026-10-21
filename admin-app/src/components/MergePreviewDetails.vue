<script setup lang="ts">
import type {MergePreview} from "@/utils/api.ts";

// What a merge makes of the User that is kept, shown before it is confirmed: the resulting profile, the roles it
// gains and the talks that move to it. The two phrases name that User the way the page speaks to the reader.
const {preview, rolesLead, talksHeading} = defineProps<{
  preview: MergePreview
  rolesLead: string
  talksHeading: string
}>()

const PROFILE_FIELDS = [
  ['name', 'Name'], ['email', 'Email'], ['bio', 'Bio'], ['username', 'Username'],
  ['twitter', 'Twitter'], ['github', 'GitHub'], ['www', 'Website'],
] as const
const fields = computed(() => PROFILE_FIELDS.filter(([key]) => preview[key]))
const gainedRoles = computed(() =>
  [preview.becomesAdmin && 'an admin', preview.becomesVolunteer && 'a volunteer'].filter(Boolean))
</script>

<template>
  <v-avatar v-if="preview.photo" :image="preview.photo" size="64" />
  <v-list density="compact">
    <v-list-item v-for="[key, label] in fields" :key="key" :subtitle="label" :title="preview[key]" />
  </v-list>
  <p v-if="gainedRoles.length">{{ rolesLead }} {{ gainedRoles.join(' and ') }}.</p>
  <template v-if="preview.talksToMove?.length">
    <p>{{ talksHeading }}</p>
    <v-list density="compact">
      <v-list-item v-for="(title, index) in preview.talksToMove" :key="index" :title="title" />
    </v-list>
  </template>
</template>
