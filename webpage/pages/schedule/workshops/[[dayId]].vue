<template>
  <div class="agendaPage">
    <PageHeader title="Schedule" type="peace"/>

    <Box color="white" class="min-padding">
      <ScheduleHeader :day-id="dayId"></ScheduleHeader>
      <div class="workshopList">
        <div
          v-for="entry in enrichedAgenda"
          :key="entry.id"
          class="workshopList__item"
        >
          <div class="workshopList__meta">
            <span class="workshopList__time">{{ entry.timeSlotLabel }}</span>
            <span v-if="entry.roomLabel" class="workshopList__sep"> · </span>
            <span v-if="entry.roomLabel" class="workshopList__room">{{ entry.roomLabel }}</span>
          </div>
          <AgendaItem
            :entry="entry"
            @select="selectPresentation"
          />
        </div>
      </div>
    </Box>

    <Contact/>
    <PresentationModal
        :presentationId="selectedPresentationId"
        @close="modalClosed()"
    ></PresentationModal>
  </div>
</template>

<script setup lang="ts">

import {useArchiveFetch} from '~/composables/useAPIFetch'

function name(room: string) {
  if (room.includes(' ')) {
    return room.split(' ')[0]
  }
  return room
}

function subname(room: string) {
  if (room.includes(' ')) {
    return room.substring(room.indexOf(' '))
  }
  return null
}

let sortByOrder = (a: WithOrder, b: WithOrder) => a.displayOrder - b.displayOrder

let days = ['day-1', 'day-2']
let dayId = (useRoute().params.dayId || 'day-1') + '-workshops';
let dayPath = '/agenda/' + dayId + '.json';

// Separate variable declarations from assignment
const rooms = ref<Room[]>([])
const slots = ref<TimeSlot[]>([])
const agenda = ref<AgendaEntry[]>([])
const presentations = ref<Presentation[]>([])

// Single call to useArchiveFetch to populate all variables
function loadDayAgenda() {
  const {data} = useArchiveFetch(dayPath, {
    key: `day-agenda-${dayId}`,
    transform: (data: DayAgenda) => data
  })

  watch(data, (value) => {
    if (!value) return

    // rooms
    rooms.value = (value.rooms || []).slice().sort(sortByOrder)

    // slots
    slots.value = (value.timeSlots || [])
        .map((slot: DayTimeSlot) => ({
          id: String(slot.displayOrder),
          label: slot.label,
          displayOrder: slot.displayOrder,
          forAllRooms: false,
          start: slot.start,
          end: slot.end
        }))
        .sort(sortByOrder) as any

    // agenda
    agenda.value = (value.agendaEntries || [])
        .map((entry: DayAgendaEntry) => ({
          ...entry,
          timeSlotId: String(entry.timeSlotIndex)
        })) as any

    // presentations
    presentations.value = (value.presentations || []) as any
  }, {immediate: true})
}

// initial load
loadDayAgenda()

const selectedPresentationId = useState('selectedPresentationId', () => null)

// Build a flat, enriched list of agenda entries for simplified list rendering
const enrichedAgenda = computed(() => {
  const list = (agenda.value || []).map((entry: any) => {
    const enriched: any = {...entry}

    // Ensure label is at least empty string when no presentation and label is null
    if (!enriched.presentationId && (enriched.label === null || enriched.label === undefined)) {
      enriched.label = ''
    }

    // Attach time slot label (and keep start/end if needed later)
    const slotObj: any = (slots.value || []).find((s: any) => String(s.id) === String(entry.timeSlotId))
    if (slotObj) {
      enriched.timeSlotLabel = slotObj.label
      enriched.start = slotObj.start
      enriched.end = slotObj.end
      enriched._slotOrder = slotObj.displayOrder
    } else {
      enriched.timeSlotLabel = ''
      enriched._slotOrder = Number.MAX_SAFE_INTEGER
    }

    // Attach room label
    const roomObj: any = entry.roomId ? (rooms.value || []).find((r: any) => r.id === entry.roomId) : null
    if (roomObj) {
      enriched.roomLabel = roomObj.label
      enriched._roomOrder = roomObj.displayOrder
    } else {
      enriched.roomLabel = ''
      enriched._roomOrder = Number.MAX_SAFE_INTEGER
    }

    // Attach presentation details if available
    if (entry.presentationId) {
      const pres: any = (presentations.value || []).find((p: any) => p.id === entry.presentationId)
      if (pres) {
        enriched.presentation = pres
        enriched.presentationId = pres.id
        enriched.speaker = pres.speakers || []
        enriched.tags = pres.tags || []
      }
    }

    return enriched
  })

  // Sort by slot order, then room order to make it readable
  return list.sort((a: any, b: any) => {
    if (a._slotOrder !== b._slotOrder) return a._slotOrder - b._slotOrder
    return a._roomOrder - b._roomOrder
  })
})

function getEntryFor(room: Room | null, slot: TimeSlot): AgendaEntry {
  const entry = agenda.value?.find(
      it =>
          it.timeSlotId === slot.id &&
          (it.roomId === null || it.roomId === room?.id)
  )
  if (!entry) {
    return {id: 'empty', label: 'empty'}
  }

  // Build enriched entry with presentation and labels when available
  const enriched: any = {...entry}

  // Add labels
  const slotObj = slots.value?.find((s: any) => s.id === entry.timeSlotId)
  if (slotObj) {
    enriched.timeSlotLabel = slotObj.label
  }
  const roomObj = entry.roomId ? rooms.value?.find((r: any) => r.id === entry.roomId) : null
  if (roomObj) {
    enriched.roomLabel = roomObj.label
  }

  // Attach presentation details if presentationId present
  if (entry.presentationId) {
    const pres = presentations.value?.find((p: any) => p.id === entry.presentationId)
    if (pres) {
      enriched.presentation = pres
      enriched.presentationId = pres.id
      // AgendaItem expects `speaker` (singular property) listing speakers
      enriched.speaker = pres.speakers || []
      // Optional: provide tags for PresentationMetadata
      enriched.tags = pres.tags || []
    }
  }

  return enriched
}

function hasSingleEntryFor(slot: TimeSlot): boolean {
  const entry = agenda.value.find(it => it.timeSlotId === slot.id)
  return entry !== undefined && entry.roomId === null
}

function selectPresentation(presentation: Presentation) {
  console.log('selected presentation', presentation.id)
  if (presentation && presentation.id) {
    selectedPresentationId.value = presentation.id
  }
}


function modalClosed() {
  selectedPresentationId.value = null
}


export interface EmbeddedRooms {
  _embedded: { rooms: Room[] };
}

export interface EmbeddedTimeSlots {
  _embedded: { timeSlots: TimeSlot[] };
}

export interface EmbeddedAgenda {
  _embedded: { agendaEntries: AgendaEntry[] };
}

export interface Room extends WithOrder {
  label: string;
  id: string;
  _links: { self: { href: string } };
}

export interface TimeSlot extends WithOrder {
  label: string;
  id: string;
  forAllRooms: boolean;
  start: string;
  end: string;
  _links: { self: { href: string } };
}

interface WithOrder {
  displayOrder: number;
}

export interface AgendaEntry {
  id: string;
  label: string;
  presentation?: Presentation;
  presentationId?: string;
  roomId?: string | null;
  roomLabel?: string;
  timeSlotId?: string;
  timeSlotLabel?: string;
}

// Day agenda archive format (edition-2025)
interface DayTimeSlot {
  dayId: string;
  displayOrder: number;
  label: string;
  start: string;
  end: string;
}

interface DayAgendaEntry {
  id: string;
  dayId: string;
  timeSlotIndex: number;
  roomId: string | null;
  label: string | null;
  presentationId: string | null;
}

interface DayAgenda {
  timeSlots: DayTimeSlot[];
  rooms: Room[];
  presentations: Presentation[];
  agendaEntries: DayAgendaEntry[];
}


const title = 'Schedule — Confitura 2025 Agenda';
const description = 'Explore the full Confitura 2025 agenda: time slots, rooms, and sessions. Plan your conference day in Warsaw.';
useHead({
  title,
  meta: [
    {name: 'description', content: description},
    {property: 'og:title', content: title},
    {property: 'og:description', content: description},
    {property: 'og:type', content: 'website'},
    {name: 'twitter:card', content: 'summary'},
    {name: 'twitter:title', content: title},
    {name: 'twitter:description', content: description}
  ]
})

</script>

<style lang="scss" scoped>
@use "~/assets/colors" as *;
@use "~/assets/sizes" as *;
@use "~/assets/media" as *;
@use "~/assets/fonts" as *;

.agenda {
  display: grid;
  grid-template-columns: 60px 1fr;
  @include lg() {
    grid-template-columns: 120px 1fr 1fr 1fr;
    margin-bottom: 5rem;
  }
}

.agendaItem__room,
.agendaItem--empty {
  display: none;
  @include lg() {
    font-size: 1.5rem;
    line-height: 1.7rem;
    font-weight: bold;
    padding: 1.5rem;
    justify-self: stretch;
    align-self: stretch;
    text-align: center;
    border-bottom: 4px solid $brand;
    border-left: 1px solid #dfdfdf;
    display: flex;
    justify-content: center;
  }
}

.agendaItem__room {
  @include lg() {
    display: flex;
    flex-direction: column;
    justify-content: flex-start;
  }
}

.room__subname {
  font-size: 1.1rem;
  font-weight: normal;
}

.agendaItem--empty {
  border-left: none;
}


.agendaItem__slot {
  display: flex;
  justify-content: left;
  font-size: 1.2rem;
  line-height: 1.4rem;
  border-bottom: 2px solid #000000;
  padding-top: 0.7rem;
  padding-bottom: 0.7rem;
  grid-row: auto / span 3;
  @include lg() {
    grid-row: unset;
    padding-top: 1.5rem;
    padding-bottom: 1.5rem;
  }
}

.agendaItem__slot--all {
  grid-row: auto / span 1;
  @include lg() {
    grid-row: unset;
  }
}

.agendaItem__entry {
  justify-self: stretch;
  align-self: stretch;
  border-bottom: 2px solid #000000;
  border-left: 1px solid #dfdfdf;
}

.agendaItem__entry--all {
  grid-column: 2 / -1;

  &:not(.agendaItem__entry--1) {
    display: none;
  }
}


.workshopList {
  display: flex;
  flex-direction: column;
}

.workshopList__item {
  padding: 0.7rem 0.7rem 1rem;
  border-bottom: 2px solid #000000;
  @include md() {
    padding: 1rem 1.5rem 1.5rem;
  }
}

.workshopList__meta {
  font-size: 0.9rem;
  color: #767676;
  margin-bottom: 0.3rem;
}

.workshopList__time {
  font-weight: 600;
}

.workshopList__room {
  color: $brand;
}

</style>
