<template>
  <div class="agendaPage">
    <PageHeader title="Schedule" type="peace" />

    <Box color="white" class="min-padding">
      <div class="agenda">
        <div class="agendaItem--empty"></div>
        <div v-for="room in rooms" :key="room.id" class="agendaItem__room">
          <span>{{ room.label | name }}</span>
          <span class="room__subname">{{ room.label | subname }}</span>
        </div>
        <template v-for="slot in slots">
          <div
            :key="slot.id"
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
    <PresentationRateModal
      :presentationRate="presentationRate"
      @close="modalClosed()"
    ></PresentationRateModal>
  </div>
</template>

<script lang="ts">
import { Component, Vue } from "vue-property-decorator";
import Box from "@/components/Box.vue";
import PageHeader from "@/components/PageHeader.vue";
import Contact from "@/components/Contact.vue";
import PageFragment from "@/components/PageFragment.vue";
import axios from "axios";
import SocialLink from "@/components/SocialLink.vue";
import UsersGrid from "@/views/UsersGrid.vue";
import { Presentation, PresentationRate } from "@/types";
import AgendaItem from "@/components/AgendaItem.vue";
import PresentationModal from "@/components/PresentationModal.vue";
import PresentationRateModal from "@/components/PresentationRateModal.vue";
import { SET_PRESENTATION_UNDER_RATE } from "@/store/presentations";

@Component({
  components: {
    PresentationRateModal,
    AgendaItem,
    UsersGrid,
    SocialLink,
    PageHeader,
    Box,
    Contact,
    PageFragment,
    PresentationModal
  },
  filters: {
    name: (room: string) => {
      if (room.includes(" ")) {
        return room.split(" ")[0];
      }
      return room;
    },
    subname: (room: string) => {
      if (room.includes(" ")) {
        return room.substring(room.indexOf(" "));
      }
      return null;
    }
  }
})
export default class Agenda extends Vue {
  public rooms: Room[] = [];
  public slots: TimeSlot[] = [];
  public agenda: AgendaEntry[] = [];
  public selectedPresentationId: string | null = null;
  public presentationRate: PresentationRate | null = null;

  public mounted(): void {
    axios
      .get<EmbeddedRooms>(`/api/rooms`)
      .then(it => it.data._embedded.rooms)
      .then(rooms => rooms.sort(this.sortByOrder))
      .then(rooms => (this.rooms = rooms));

    axios
      .get<EmbeddedTimeSlots>(`/api/time-slots`)
      .then(it => it.data._embedded.timeSlots)
      .then(slots => slots.sort(this.sortByOrder))
      .then(slots => (this.slots = slots));

    axios
      .get<EmbeddedAgenda>(`/api/agenda`)
      .then(it => it.data._embedded.agendaEntries)
      .then(agenda => (this.agenda = agenda));
  }

  public getEntryFor(room: Room, slot: TimeSlot): AgendaEntry {
    const entry = this.agenda.find(
      it =>
        it.timeSlotId === slot.id &&
        (it.roomId === null || it.roomId === room.id)
    );
    return entry || { id: "empty", label: "empty" };
  }

  public hasSingleEntryFor(slot: TimeSlot): boolean {
    const entry = this.agenda.find(it => it.timeSlotId === slot.id);
    return entry !== undefined && entry.roomId === null;
  }

  public selectPresentation(presentation: Presentation) {
    if (presentation && presentation.id) {
      this.selectedPresentationId = presentation.id;
    }
  }

  public startRatingPresentation(presentationRate: PresentationRate) {
    this.$store.commit(SET_PRESENTATION_UNDER_RATE, { presentationRate });
    // this.presentationRate = presentationRate;
  }

  public modalClosed() {
    this.selectedPresentationId = null;
    this.presentationRate = null;
  }

  private sortByOrder = (a: WithOrder, b: WithOrder) =>
    a.displayOrder - b.displayOrder;
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
  roomId?: string;
  roomLabel?: string;
  timeSlotId?: string;
  timeSlotLabel?: string;
}
</script>

<style lang="scss" scoped>
@import "../assets/colors";
@import "../assets/sizes";
@import "../assets/media";
@import "../assets/fonts";

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
