<script setup lang="ts">
import type {DataTableHeaders} from '@/plugins/vuetify'
import {adminPresentationApi, adminVoteApi} from '@/utils/api.ts'
import type {VoteResult} from '@/utils/api-axios-client'

definePage({
  meta: {
    title: 'Vote Results',
    icon: 'mdi-poll',
  },
})

interface Voter {
  name: string
  token: string
}

const VOTERS_STORAGE_KEY = 'vote-results-voters'

const search = ref('')
const results = ref<VoteResult[]>([])
const voters = ref<Voter[]>(loadVoters())
const votersText = ref(voters.value.map(v => `${v.name}=${v.token}`).join('\n'))

const preSelectionOptions = [
  {title: 'None', value: 'NONE'},
  {title: 'Pre-approved', value: 'PRE_APPROVED'},
  {title: 'Pre-rejected', value: 'PRE_REJECTED'},
]

const baseHeaders: DataTableHeaders = [
  {title: 'Title', key: 'title'},
  {title: 'Speakers', key: 'flatSpeakers'},
  {title: 'Total', key: 'total', align: 'end'},
  {title: 'Score', key: 'score', align: 'end'},
  {title: 'Positive', key: 'positive', align: 'end'},
  {title: 'Negative', key: 'negative', align: 'end'},
  {title: 'Neutral', key: 'neutral', align: 'end'},
  {title: 'Positive %', key: 'positivePercent', align: 'end'},
  {title: 'Negative %', key: 'negativePercent', align: 'end'},
  {title: 'Neutral %', key: 'neutralPercent', align: 'end'},
  {title: 'Workshop', key: 'workshop'},
  {title: 'Tags', key: 'flatTags'},
  {title: 'Pre-selection', key: 'preSelectionStatus', sortable: false},
]

const headers = computed<DataTableHeaders>(() => [
  ...baseHeaders,
  ...voters.value.map(voter => ({
    title: voter.name || voter.token,
    key: voter.token,
    align: 'end' as const,
    value: (item: VoteResult) => item.voterScores?.[voter.token] ?? 0,
  })),
])

function loadVoters(): Voter[] {
  try {
    return JSON.parse(localStorage.getItem(VOTERS_STORAGE_KEY) || '[]')
  } catch {
    return []
  }
}

function applyVoters() {
  voters.value = votersText.value
    .split('\n')
    .map(line => line.trim())
    .filter(Boolean)
    .map(line => {
      const [name, token] = line.split('=').map(part => part.trim())
      return token ? {name, token} : {name: '', token: name}
    })
    .filter(v => v.token)
  localStorage.setItem(VOTERS_STORAGE_KEY, JSON.stringify(voters.value))
  reload()
}

function reload() {
  const tokens = voters.value.map(v => v.token)
  adminVoteApi.results(tokens.length ? tokens : undefined)
    .then(res => results.value = res.data)
    .catch(e => console.error(e))
}

function rowClass(item: VoteResult): string {
  if (item.preSelectionStatus === 'PRE_APPROVED') return 'pre-approved-row'
  if (item.preSelectionStatus === 'PRE_REJECTED') return 'pre-rejected-row'
  return ''
}

function scoreColor(value: number | undefined): string {
  if (value === undefined || value === 0) return ''
  return value > 0 ? 'text-light-green-darken-2' : 'text-red-darken-1'
}

function setPreSelection(item: VoteResult, status: string) {
  const previous = item.preSelectionStatus
  item.preSelectionStatus = status as VoteResult['preSelectionStatus']
  adminPresentationApi.setPreSelection(item.presentationId, {status: status as any})
    .then(() => Notify.success(`Updated ${item.title}`))
    .catch(() => {
      item.preSelectionStatus = previous
      Notify.error('Failed to update pre-selection')
    })
}

onMounted(reload)
</script>

<template>
  <v-container fluid>
    <v-row>
      <v-col>
        <teleport to="#app-bar">
          <v-text-field
            v-model="search"
            prepend-inner-icon="mdi-magnify"
            label="Search"
            single-line
            hide-details
            density="compact"
            class="mr-2"
            rounded="xl"
            flat
            variant="solo"
            style="width: 250px"
          />
        </teleport>

        <v-card class="mb-4">
          <v-card-title class="text-subtitle-1">Voter columns</v-card-title>
          <v-card-text>
            <v-textarea
              v-model="votersText"
              label="One voter per line, format: Name=token"
              placeholder="Dominik=eb2d7dc7-c5f1-417f-876f-7706ed3610ec"
              rows="3"
              auto-grow
              hide-details
            />
          </v-card-text>
          <v-card-actions>
            <v-btn color="primary" variant="tonal" @click="applyVoters">Apply</v-btn>
          </v-card-actions>
        </v-card>

        <v-card>
          <v-data-table
            :headers="headers"
            :items="results"
            items-per-page="100"
            item-value="presentationId"
            :filter-keys="['title', 'flatSpeakers', 'flatTags']"
            :search="search"
            :sort-by="[{ key: 'score', order: 'desc' }]"
            :row-props="({ item }) => ({ class: rowClass(item) })"
          >
            <template #item.workshop="{ item }">
              <v-chip v-if="item.workshop" color="purple" label size="small">Workshop</v-chip>
            </template>
            <template #item.positivePercent="{ item }">{{ item.positivePercent }}%</template>
            <template #item.negativePercent="{ item }">{{ item.negativePercent }}%</template>
            <template #item.neutralPercent="{ item }">{{ item.neutralPercent }}%</template>
            <template #item.preSelectionStatus="{ item }">
              <v-select
                :model-value="item.preSelectionStatus"
                :items="preSelectionOptions"
                density="compact"
                variant="plain"
                hide-details
                style="min-width: 130px"
                @update:model-value="status => setPreSelection(item, status)"
              />
            </template>
            <template
              v-for="voter in voters"
              :key="voter.token"
              #[`item.${voter.token}`]="{ item }"
            >
              <span :class="scoreColor(item.voterScores?.[voter.token])">
                {{ item.voterScores?.[voter.token] ?? 0 }}
              </span>
            </template>
          </v-data-table>
        </v-card>
      </v-col>
    </v-row>
  </v-container>
</template>

<style scoped>
:deep(tr.pre-approved-row) > td {
  background-color: rgba(76, 175, 80, 0.14);
}

:deep(tr.pre-rejected-row) > td {
  background-color: rgba(244, 67, 54, 0.12);
}
</style>
