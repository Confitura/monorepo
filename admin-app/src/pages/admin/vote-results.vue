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
  {title: 'None', value: 'NONE', color: 'grey'},
  {title: 'Pre-approved', value: 'PRE_APPROVED', color: 'green'},
  {title: 'Pre-rejected', value: 'PRE_REJECTED', color: 'red'},
  {title: 'In reserve', value: 'IN_RESERVE', color: 'amber-darken-2'},
]

const statusCounts = computed(() => {
  const counts: Record<string, number> = {NONE: 0, PRE_APPROVED: 0, PRE_REJECTED: 0, IN_RESERVE: 0}
  for (const r of results.value) {
    counts[r.preSelectionStatus] = (counts[r.preSelectionStatus] ?? 0) + 1
  }
  return counts
})

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
  {title: 'Comment', key: 'preSelectionComment', sortable: false},
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
  if (item.preSelectionStatus === 'IN_RESERVE') return 'in-reserve-row'
  return ''
}

function scoreColor(value: number | undefined): string {
  if (value === undefined || value === 0) return ''
  return value > 0 ? 'text-light-green-darken-2' : 'text-red-darken-1'
}

function savePreSelection(item: VoteResult) {
  return adminPresentationApi.setPreSelection(item.presentationId, {
    status: item.preSelectionStatus as any,
    comment: item.preSelectionComment ?? '',
  })
}

function setPreSelection(item: VoteResult, status: string) {
  const previous = item.preSelectionStatus
  item.preSelectionStatus = status as VoteResult['preSelectionStatus']
  savePreSelection(item)
    .then(() => Notify.success(`Updated ${item.title}`))
    .catch(() => {
      item.preSelectionStatus = previous
      Notify.error('Failed to update pre-selection')
    })
}

function saveComment(item: VoteResult) {
  savePreSelection(item)
    .then(() => Notify.success(`Saved comment for ${item.title}`))
    .catch(() => Notify.error('Failed to save comment'))
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
          <v-card-text class="d-flex ga-2 flex-wrap align-center">
            <v-chip
              v-for="option in preSelectionOptions"
              :key="option.value"
              :color="option.color"
              variant="flat"
              label
            >
              {{ option.title }}: {{ statusCounts[option.value] }}
            </v-chip>
            <v-chip color="primary" variant="tonal" label>Total: {{ results.length }}</v-chip>
          </v-card-text>
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
            <template #item.title="{ item }">
              <a
                :href="`/admin/presentation-preview/${item.presentationId}`"
                target="_blank"
                class="text-primary"
              >{{ item.title }}</a>
            </template>
            <template #item.flatSpeakers="{ item }">
              <template v-for="(speaker, i) in item.speakers" :key="speaker.id">
                <RouterLink :to="`/homepage/${speaker.id}`" class="text-primary">{{ speaker.name }}</RouterLink><span v-if="i < item.speakers.length - 1">, </span>
              </template>
            </template>
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
            <template #item.preSelectionComment="{ item }">
              <v-text-field
                v-model="item.preSelectionComment"
                density="compact"
                variant="plain"
                hide-details
                placeholder="add comment"
                style="min-width: 160px"
                @blur="saveComment(item)"
                @keyup.enter="saveComment(item)"
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

:deep(tr.in-reserve-row) > td {
  background-color: rgba(255, 193, 7, 0.16);
}
</style>
