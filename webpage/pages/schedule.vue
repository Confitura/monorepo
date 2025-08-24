<template>
  <div class="agendaPage">
    <PageHeader title="Schedule" type="peace" />

    <Box color="white" class="min-padding">
      <div class="agenda">
        <div class="agendaItem--empty"></div>
        <div v-for="room in rooms" :key="room.id" class="agendaItem__room">
          <span>{{ name(room.label) }}</span>
          <span class="room__subname">{{ subname(room.label) }}</span>
        </div>
        <template v-for="slot in slots"
                  :key="slot.id">
          <div
            class="agendaItem__slot"
            :class="{ 'agendaItem__slot--all': hasSingleEntryFor(slot) }"
          >
            <span>{{ slot.label }}</span>
          </div>
          <AgendaItem
            v-for="currentRoom in rooms"
            :key="`${currentRoom.id}-${slot.id}`"
            :entry="getEntryFor(currentRoom, slot)"
            @select="selectPresentation"
            @start-rating="startRatingPresentation"
            class="agendaItem__entry "
            :class="{
                      [`agendaItem__entry--${currentRoom.displayOrder}`]: true,
                      'agendaItem__entry--all': hasSingleEntryFor(slot)
                    }"
          ></AgendaItem>
        </template>
      </div>
    </Box>

    <Contact />
        <PresentationModal
          :presentationId="selectedPresentationId"
          @close="modalClosed()"
        ></PresentationModal>
    <!--    <PresentationRateModal-->
    <!--      :presentationRate="presentationRate"-->
    <!--      @close="modalClosed()"-->
    <!--    ></PresentationRateModal>-->
  </div>
</template>

<script setup lang="ts">

import { useAPIFetch } from '~/composables/useAPIFetch'

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

let { data: rooms } = useAPIFetch('/rooms.json', {
  key: 'rooms',
  transform: (data: EmbeddedRooms) => data._embedded.rooms.sort(sortByOrder)
})
let { data: slots } = useAPIFetch('/time-slots.json', {
  key: 'timeSlots',
  transform: (data: EmbeddedTimeSlots) => data._embedded.timeSlots.sort(sortByOrder)
})
let { data: agenda } = useAPIFetch('/agenda.json', {
  key: 'agenda',
  transform: (data: EmbeddedAgenda) => data._embedded.agendaEntries
})
const selectedPresentationId = useState('selectedPresentationId', () => null)
let presentationRate: PresentationRate | null = null

function getEntryFor(room: Room, slot: TimeSlot): AgendaEntry {
  const entry = agenda.value.find(
    it =>
      it.timeSlotId === slot.id &&
      (it.roomId === null || it.roomId === room.id)
  )
  return entry || { id: 'empty', label: 'empty' }
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

function startRatingPresentation(newRate: PresentationRate) {
  //TODO this.$store.commit(SET_PRESENTATION_UNDER_RATE, { presentationRate })
  // presentationRate = newRate;
}

function modalClosed() {
  selectedPresentationId.value = null
  presentationRate = null
}

let sortByOrder = (a: WithOrder, b: WithOrder) => a.displayOrder - b.displayOrder


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


const title = 'Schedule — Confitura 2025 Agenda';
const description = 'Explore the full Confitura 2025 agenda: time slots, rooms, and sessions. Plan your conference day in Warsaw.';
useHead({
  title,
  meta: [
    { name: 'description', content: description },
    { property: 'og:title', content: title },
    { property: 'og:description', content: description },
    { property: 'og:type', content: 'website' },
    { name: 'twitter:card', content: 'summary' },
    { name: 'twitter:title', content: title },
    { name: 'twitter:description', content: description }
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
    grid-template-columns: 120px 1fr 1fr 1fr 1fr 1fr;
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
  grid-row: auto / span 5;
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

  &:not(.agendaItem__entry--0) {
    display: none;
  }
}
</style>
