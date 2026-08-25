<script setup lang="ts">
import { ref, computed, onMounted, watch } from 'vue';
import type { Ref } from 'vue';
import { storeToRefs } from 'pinia';
import DialogConfirm from '@/components/DialogConfirm.vue';
import { useAgendaStore } from '@/stores/agenda';
import type {
  InlineAgendaEntry,
  InlineRoom,
  InlineTimeSlot,
} from "@/utils/api";

const props = defineProps<{
  dayId: string;
}>();

const agenda = useAgendaStore();
const { presentations } = storeToRefs(agenda);
const timeSlots = computed(() => agenda.timeSlotsForDay(props.dayId));
const rooms = computed(() => agenda.roomsForDay(props.dayId));
const entries = computed(() => agenda.entriesForDay(props.dayId));

const confirmDialog = useTemplateRef('confirmDialog');
const tab = ref('agenda');

async function refreshData() {
  try {
    await agenda.refreshData(props.dayId);
  } catch (error) {
    console.error('Error loading data:', error);
    Notify.error('Error loading data');
  }
}

onMounted(async () => {
  await refreshData();
});

// --- lookups -----------------------------------------------------------------

const getAgendaEntry = (timeSlot: InlineTimeSlot, room: InlineRoom | null) =>
  agenda.getAgendaEntry(timeSlot, room, props.dayId);

const getPresentation = (entry: InlineAgendaEntry | null) =>
  entry?.presentationId ? agenda.getPresentation(entry.presentationId) : null;

const entriesInTimeSlot = (timeSlot: InlineTimeSlot) =>
  entries.value.filter((entry) => entry.timeSlotIndex === timeSlot.displayOrder);

const entriesInRoom = (room: InlineRoom) =>
  entries.value.filter((entry) => entry.roomId === room.id);

const scheduledPresentationIds = computed(
  () => new Set(entries.value.map((entry) => entry.presentationId).filter(Boolean)),
);

const presentationItems = computed(() =>
  presentations.value.map((presentation) => ({
    value: presentation.id,
    title: presentation.title,
    props: {
      subtitle: [
        presentation.flatSpeakers,
        scheduledPresentationIds.value.has(presentation.id) ? 'already scheduled' : null,
      ]
        .filter(Boolean)
        .join(' — '),
    },
  })),
);

const timeSlotItems = computed(() =>
  timeSlots.value.map((timeSlot) => ({ value: timeSlot.displayOrder, title: timeSlot.label })),
);

const roomItems = computed(() => [
  ...rooms.value.map((room) => ({ value: room.id as string | null, title: room.label })),
  { value: null, title: 'All rooms' },
]);

const requiredRule = (value: string) => !!value || 'This field is required';
const entryCount = (count: number) => `${count} ${count === 1 ? 'entry' : 'entries'}`;

function addMinutes(time: string, minutes: number) {
  const [hours, mins] = time.split(':').map(Number);
  const total = (hours * 60 + mins + minutes) % (24 * 60);
  return `${String(Math.floor(total / 60)).padStart(2, '0')}:${String(total % 60).padStart(2, '0')}`;
}

function minutesBetween(start: string, end: string) {
  const toMinutes = (time: string) => {
    const [hours, mins] = time.split(':').map(Number);
    return hours * 60 + mins;
  };
  return toMinutes(end) - toMinutes(start);
}

// --- time slots --------------------------------------------------------------

interface TimeSlotForm {
  displayOrder: number | null;
  start: string;
  end: string;
  forAllRooms: boolean;
}

const DEFAULT_DURATION = 60;

const timeSlotDialog = ref(false);
const timeSlotFormValid = ref(false);
const editedTimeSlot: Ref<TimeSlotForm> = ref({
  displayOrder: null,
  start: '',
  end: '',
  forAllRooms: false,
});
const duration = ref(DEFAULT_DURATION);

const endTimeError = computed(() => {
  const form = editedTimeSlot.value;
  return form.start && form.end && form.end <= form.start
    ? 'End time must be after the start time'
    : '';
});
const timeSlotSavable = computed(() => timeSlotFormValid.value && !endTimeError.value);

/** The slot the edited one follows: the last one of the day when adding, the preceding one when editing. */
const previousTimeSlot = computed(() => {
  const displayOrder = editedTimeSlot.value.displayOrder;
  if (displayOrder === null) {
    return timeSlots.value[timeSlots.value.length - 1] ?? null;
  }
  const index = timeSlots.value.findIndex((it) => it.displayOrder === displayOrder);
  return index > 0 ? timeSlots.value[index - 1] : null;
});

function addTimeSlot() {
  // a new slot is appended at the end of the day, but it is displayed in chronological order,
  // so it lands wherever its times put it
  const last = timeSlots.value[timeSlots.value.length - 1];
  const start = last ? last.end.slice(0, 5) : '09:00';
  editedTimeSlot.value = {
    displayOrder: null,
    start,
    end: addMinutes(start, DEFAULT_DURATION),
    forAllRooms: false,
  };
  duration.value = DEFAULT_DURATION;
  timeSlotDialog.value = true;
}

function editTimeSlot(timeSlot: InlineTimeSlot) {
  editedTimeSlot.value = {
    displayOrder: timeSlot.displayOrder,
    start: timeSlot.start.slice(0, 5),
    end: timeSlot.end.slice(0, 5),
    forAllRooms: timeSlot.forAllRooms,
  };
  duration.value = minutesBetween(editedTimeSlot.value.start, editedTimeSlot.value.end);
  timeSlotDialog.value = true;
}

/** Start and duration drive the end time, so only the duration has to be typed in. */
function applyDuration() {
  const form = editedTimeSlot.value;
  if (form.start && duration.value > 0) {
    form.end = addMinutes(form.start, duration.value);
  }
}

function readDuration() {
  const form = editedTimeSlot.value;
  if (form.start && form.end) {
    duration.value = minutesBetween(form.start, form.end);
  }
}

// the three time fields stay in sync: changing the start or the duration moves the end,
// picking an end time updates the duration
watch(duration, applyDuration);
watch(() => editedTimeSlot.value.start, applyDuration);
watch(() => editedTimeSlot.value.end, readDuration);

function startAfterPreviousSlot() {
  const previous = previousTimeSlot.value;
  if (!previous) return;
  editedTimeSlot.value.start = previous.end.slice(0, 5);
  applyDuration();
}

async function saveTimeSlot(addAnother = false) {
  const form = editedTimeSlot.value;
  if (endTimeError.value) return;
  try {
    if (form.displayOrder === null) {
      await agenda.createTimeSlot(props.dayId, {
        start: form.start,
        end: form.end,
        forAllRooms: form.forAllRooms,
      });
    } else {
      await agenda.updateTimeSlot(props.dayId, form.displayOrder, {
        start: form.start,
        end: form.end,
        forAllRooms: form.forAllRooms,
      });
    }
    if (addAnother) {
      // keep the dialog open, ready for the slot that follows the one just saved
      form.displayOrder = null;
      form.start = form.end;
      applyDuration();
    } else {
      timeSlotDialog.value = false;
    }
  } catch (error) {
    console.error('Error saving time slot:', error);
    Notify.error('Error saving time slot');
  }
}

function deleteTimeSlot(timeSlot: InlineTimeSlot) {
  const count = entriesInTimeSlot(timeSlot).length;
  const scheduled = count > 0 ? ` with ${entryCount(count)} scheduled in it` : '';
  confirmDialog.value
    ?.open(`Are you sure you want to DELETE time slot ${timeSlot.label}${scheduled}?`)
    .then(async (confirmed: boolean) => {
      if (!confirmed) return;
      try {
        await agenda.deleteTimeSlot(props.dayId, timeSlot.displayOrder);
      } catch (error) {
        console.error('Error deleting time slot:', error);
        Notify.error('Error deleting time slot');
      }
    });
}

// --- rooms -------------------------------------------------------------------

interface RoomForm {
  id: string | null;
  label: string;
}

const roomDialog = ref(false);
const roomFormValid = ref(false);
const editedRoom: Ref<RoomForm> = ref({ id: null, label: '' });

function addRoom() {
  editedRoom.value = { id: null, label: '' };
  roomDialog.value = true;
}

function editRoom(room: InlineRoom) {
  editedRoom.value = { id: room.id, label: room.label };
  roomDialog.value = true;
}

async function saveRoom() {
  const form = editedRoom.value;
  try {
    if (form.id === null) {
      await agenda.createRoom(props.dayId, { label: form.label });
    } else {
      await agenda.updateRoom(props.dayId, form.id, { label: form.label });
    }
    roomDialog.value = false;
  } catch (error) {
    console.error('Error saving room:', error);
    Notify.error('Error saving room');
  }
}

function deleteRoom(room: InlineRoom) {
  const count = entriesInRoom(room).length;
  const scheduled = count > 0 ? ` with ${entryCount(count)} scheduled in it` : '';
  confirmDialog.value
    ?.open(`Are you sure you want to DELETE room ${room.label}${scheduled}?`)
    .then(async (confirmed: boolean) => {
      if (!confirmed) return;
      try {
        await agenda.deleteRoom(props.dayId, room.id);
      } catch (error) {
        console.error('Error deleting room:', error);
        Notify.error('Error deleting room');
      }
    });
}

async function moveRoom(room: InlineRoom, direction: -1 | 1) {
  try {
    await agenda.moveRoom(props.dayId, room.id, direction);
  } catch (error) {
    console.error('Error moving room:', error);
    Notify.error('Error moving room');
  }
}

// --- agenda entries ----------------------------------------------------------

interface EntryForm {
  id: string | null;
  timeSlotIndex: number;
  roomId: string | null;
  label: string;
  presentationId: string | null;
}

const entryDialog = ref(false);
const editedEntry: Ref<EntryForm> = ref({
  id: null,
  timeSlotIndex: -1,
  roomId: null,
  label: '',
  presentationId: null,
});
const editedEntryOrigin = ref({ timeSlotIndex: -1, roomId: null as string | null });

function addAgendaEntry(timeSlot: InlineTimeSlot, room: InlineRoom | null) {
  editedEntry.value = {
    id: null,
    timeSlotIndex: timeSlot.displayOrder,
    roomId: room?.id ?? null,
    label: '',
    presentationId: null,
  };
  entryDialog.value = true;
}

function editAgendaEntry(entry: InlineAgendaEntry) {
  editedEntry.value = {
    id: entry.id,
    timeSlotIndex: entry.timeSlotIndex,
    roomId: entry.roomId ?? null,
    label: entry.label ?? '',
    presentationId: entry.presentationId ?? null,
  };
  editedEntryOrigin.value = {
    timeSlotIndex: entry.timeSlotIndex,
    roomId: entry.roomId ?? null,
  };
  entryDialog.value = true;
}

function entryOccupying(form: EntryForm) {
  const timeSlot = timeSlots.value.find((slot) => slot.displayOrder === form.timeSlotIndex);
  if (!timeSlot) return null;
  const room = rooms.value.find((it) => it.id === form.roomId) ?? null;
  const occupant = getAgendaEntry(timeSlot, room);
  return occupant && occupant.id !== form.id ? occupant : null;
}

async function saveAgendaEntry() {
  const form = editedEntry.value;
  if (entryOccupying(form)) {
    Notify.error('There is already an entry in that time slot and room');
    return;
  }
  try {
    if (form.id === null) {
      await agenda.saveAgendaEntry(
        {
          dayId: props.dayId,
          timeSlotIndex: form.timeSlotIndex,
          roomId: form.roomId ?? undefined,
          label: form.label,
          presentationId: form.presentationId ?? '',
        },
        props.dayId,
      );
    } else {
      const origin = editedEntryOrigin.value;
      if (origin.timeSlotIndex !== form.timeSlotIndex || origin.roomId !== form.roomId) {
        await agenda.moveAgendaEntry(
          form.id,
          {
            dayId: props.dayId,
            timeSlotIndex: form.timeSlotIndex,
            roomId: form.roomId ?? undefined,
          },
          props.dayId,
        );
      }
      await agenda.updateAgendaEntry(
        form.id,
        {
          label: form.label || undefined,
          presentationId: form.presentationId || undefined,
          roomId: form.roomId ?? undefined,
        },
        props.dayId,
      );
    }
    entryDialog.value = false;
  } catch (error) {
    console.error('Error saving agenda entry:', error);
    Notify.error('Error saving agenda entry');
  }
}

async function deleteAgendaEntry() {
  const id = editedEntry.value.id;
  if (!id) return;
  try {
    await agenda.deleteAgendaEntry(id, props.dayId);
    entryDialog.value = false;
  } catch (error) {
    console.error('Error deleting agenda entry:', error);
    Notify.error('Error deleting agenda entry');
  }
}
</script>

<template>
  <v-container>
    <v-tabs v-model="tab" bg-color="primary" color="white">
      <v-tab value="agenda">Agenda</v-tab>
      <v-tab value="timeSlots">Time Slots</v-tab>
      <v-tab value="rooms">Rooms</v-tab>
    </v-tabs>

    <v-window v-model="tab" class="mt-5">
      <!-- Agenda Tab -->
      <v-window-item value="agenda">
        <v-card>
          <v-card-title class="d-flex justify-space-between align-center">
            <span>Conference Schedule</span>
            <div>
              <v-btn
class="mr-2" color="primary" variant="tonal"
                     prepend-icon="mdi-plus" @click="addTimeSlot">
                Time Slot
              </v-btn>
              <v-btn
color="primary" variant="tonal" prepend-icon="mdi-plus"
                     @click="addRoom">
                Room
              </v-btn>
            </div>
          </v-card-title>

          <v-card-text>
            <v-alert v-if="timeSlots.length === 0" type="info" variant="tonal">
              This day has no time slots yet. Add the first one to start building the agenda.
            </v-alert>

            <v-table v-else class="agendaGrid">
              <thead>
              <tr>
                <th class="text-left">Time</th>
                <th v-for="room in rooms" :key="room.id" class="text-left">
                  <div class="d-flex align-center justify-space-between">
                    <span>{{ room.label }}</span>
                    <span class="text-no-wrap">
                      <v-btn
icon="mdi-pencil" size="x-small" variant="text"
                             @click="editRoom(room)"/>
                      <v-btn
icon="mdi-delete" size="x-small" variant="text" color="error"
                             @click="deleteRoom(room)"/>
                    </span>
                  </div>
                </th>
                <th class="text-left">All Rooms</th>
              </tr>
              </thead>
              <tbody>
              <tr v-for="timeSlot in timeSlots" :key="timeSlot.displayOrder">
                <td>
                  <div class="d-flex align-center justify-space-between">
                    <div>
                      <div class="text-no-wrap">{{ timeSlot.label }}</div>
                      <v-chip v-if="timeSlot.forAllRooms" size="x-small" class="mt-1">
                        all rooms
                      </v-chip>
                    </div>
                    <span class="text-no-wrap">
                      <v-btn
icon="mdi-pencil" size="x-small" variant="text"
                             @click="editTimeSlot(timeSlot)"/>
                      <v-btn
icon="mdi-delete" size="x-small" variant="text" color="error"
                             @click="deleteTimeSlot(timeSlot)"/>
                    </span>
                  </div>
                </td>
                <td v-for="room in rooms" :key="room.id">
                  <v-card
                    v-if="getAgendaEntry(timeSlot, room)"
                    variant="outlined"
                    class="pa-2"
                    @click="editAgendaEntry(getAgendaEntry(timeSlot, room)!)"
                  >
                    <template v-if="getPresentation(getAgendaEntry(timeSlot, room))">
                      <div class="font-weight-bold">
                        {{ getPresentation(getAgendaEntry(timeSlot, room))!.title }}
                      </div>
                      <div class="text-caption">
                        {{ getPresentation(getAgendaEntry(timeSlot, room))!.flatSpeakers }}
                      </div>
                    </template>
                    <template v-else>
                      {{ getAgendaEntry(timeSlot, room)!.label || '(empty)' }}
                    </template>
                  </v-card>
                  <v-btn
                    v-else
                    variant="text"
                    size="small"
                    color="primary"
                    prepend-icon="mdi-plus"
                    :disabled="timeSlot.forAllRooms"
                    @click="addAgendaEntry(timeSlot, room)"
                  >
                    Add
                  </v-btn>
                </td>
                <td>
                  <v-card
                    v-if="getAgendaEntry(timeSlot, null)"
                    variant="outlined"
                    class="pa-2"
                    @click="editAgendaEntry(getAgendaEntry(timeSlot, null)!)"
                  >
                    <template v-if="getPresentation(getAgendaEntry(timeSlot, null))">
                      <div class="font-weight-bold">
                        {{ getPresentation(getAgendaEntry(timeSlot, null))!.title }}
                      </div>
                      <div class="text-caption">
                        {{ getPresentation(getAgendaEntry(timeSlot, null))!.flatSpeakers }}
                      </div>
                    </template>
                    <template v-else>
                      {{ getAgendaEntry(timeSlot, null)!.label || '(empty)' }}
                    </template>
                  </v-card>
                  <v-btn
                    v-else
                    variant="text"
                    size="small"
                    color="primary"
                    prepend-icon="mdi-plus"
                    :disabled="!timeSlot.forAllRooms"
                    @click="addAgendaEntry(timeSlot, null)"
                  >
                    Add
                  </v-btn>
                </td>
              </tr>
              </tbody>
            </v-table>
          </v-card-text>
        </v-card>
      </v-window-item>

      <!-- Time Slots Tab -->
      <v-window-item value="timeSlots">
        <v-card>
          <v-card-title class="d-flex justify-space-between align-center">
            <span>Time Slots</span>
            <v-btn color="primary" prepend-icon="mdi-plus" @click="addTimeSlot">
              Add Time Slot
            </v-btn>
          </v-card-title>

          <v-card-text>
            <v-table>
              <thead>
              <tr>
                <th class="text-left">Start Time</th>
                <th class="text-left">End Time</th>
                <th class="text-left">All Rooms</th>
                <th class="text-left">Entries</th>
                <th class="text-left">Actions</th>
              </tr>
              </thead>
              <tbody>
              <tr v-for="timeSlot in timeSlots" :key="timeSlot.displayOrder">
                <td>{{ timeSlot.start }}</td>
                <td>{{ timeSlot.end }}</td>
                <td>
                  <v-icon v-if="timeSlot.forAllRooms" icon="mdi-check"/>
                </td>
                <td>{{ entriesInTimeSlot(timeSlot).length }}</td>
                <td class="text-no-wrap">
                  <v-btn
icon="mdi-pencil" size="small" variant="text" class="mr-2"
                         @click="editTimeSlot(timeSlot)"/>
                  <v-btn
icon="mdi-delete" size="small" variant="text" color="error"
                         @click="deleteTimeSlot(timeSlot)"/>
                </td>
              </tr>
              </tbody>
            </v-table>
          </v-card-text>
        </v-card>
      </v-window-item>

      <!-- Rooms Tab -->
      <v-window-item value="rooms">
        <v-card>
          <v-card-title class="d-flex justify-space-between align-center">
            <span>Rooms</span>
            <v-btn color="primary" prepend-icon="mdi-plus" @click="addRoom">Add Room</v-btn>
          </v-card-title>

          <v-card-text>
            <v-table>
              <thead>
              <tr>
                <th class="text-left">Name</th>
                <th class="text-left">Entries</th>
                <th class="text-left">Actions</th>
              </tr>
              </thead>
              <tbody>
              <tr v-for="(room, index) in rooms" :key="room.id">
                <td>{{ room.label }}</td>
                <td>{{ entriesInRoom(room).length }}</td>
                <td class="text-no-wrap">
                  <v-btn
icon="mdi-arrow-up" size="small" variant="text"
                         :disabled="index === 0"
                         @click="moveRoom(room, -1)"/>
                  <v-btn
icon="mdi-arrow-down" size="small" variant="text"
                         :disabled="index === rooms.length - 1"
                         @click="moveRoom(room, 1)"/>
                  <v-btn
icon="mdi-pencil" size="small" variant="text" class="mr-2"
                         @click="editRoom(room)"/>
                  <v-btn
icon="mdi-delete" size="small" variant="text" color="error"
                         @click="deleteRoom(room)"/>
                </td>
              </tr>
              </tbody>
            </v-table>
          </v-card-text>
        </v-card>
      </v-window-item>
    </v-window>

    <!-- Time Slot Dialog -->
    <v-dialog v-model="timeSlotDialog" max-width="500px">
      <v-card>
        <v-card-title>
          <span class="text-h5">
            {{ editedTimeSlot.displayOrder === null ? 'New Time Slot' : 'Edit Time Slot' }}
          </span>
        </v-card-title>

        <v-card-text>
          <v-form v-model="timeSlotFormValid">
            <v-row>
              <v-col cols="12" sm="4">
                <v-text-field
                  v-model="editedTimeSlot.start"
                  label="Start Time"
                  type="time"
                  :rules="[requiredRule]"
                />
              </v-col>
              <v-col cols="12" sm="4">
                <v-text-field
                  v-model.number="duration"
                  label="Duration (min)"
                  type="number"
                  min="1"
                  hint="Sets the end time"
                />
              </v-col>
              <v-col cols="12" sm="4">
                <v-text-field
                  v-model="editedTimeSlot.end"
                  label="End Time"
                  type="time"
                  :rules="[requiredRule]"
                  :error-messages="endTimeError"
                />
              </v-col>
              <v-col cols="12" class="pt-0">
                <v-btn
                  variant="text"
                  size="small"
                  color="primary"
                  prepend-icon="mdi-ray-start-arrow"
                  :disabled="!previousTimeSlot"
                  @click="startAfterPreviousSlot"
                >
                  Start when previous slot ends{{
                    previousTimeSlot ? ` (${previousTimeSlot.end.slice(0, 5)})` : ''
                  }}
                </v-btn>
              </v-col>
              <v-col cols="12">
                <v-switch
                  v-model="editedTimeSlot.forAllRooms"
                  label="Spans all rooms (break, keynote, ...)"
                  color="primary"
                  hide-details
                />
              </v-col>
            </v-row>
          </v-form>
        </v-card-text>

        <v-card-actions>
          <v-spacer/>
          <v-btn color="blue-darken-1" variant="text" @click="timeSlotDialog = false">
            Cancel
          </v-btn>
          <v-btn
v-if="editedTimeSlot.displayOrder === null" color="blue-darken-1" variant="text"
                 :disabled="!timeSlotSavable" @click="saveTimeSlot(true)">
            Save & add next
          </v-btn>
          <v-btn
color="blue-darken-1" variant="text" :disabled="!timeSlotSavable"
                 @click="saveTimeSlot()">
            Save
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>

    <!-- Room Dialog -->
    <v-dialog v-model="roomDialog" max-width="500px">
      <v-card>
        <v-card-title>
          <span class="text-h5">{{ editedRoom.id === null ? 'New Room' : 'Edit Room' }}</span>
        </v-card-title>

        <v-card-text>
          <v-form v-model="roomFormValid">
            <v-text-field
              v-model="editedRoom.label"
              label="Room Name"
              :rules="[requiredRule]"
            />
          </v-form>
        </v-card-text>

        <v-card-actions>
          <v-spacer/>
          <v-btn color="blue-darken-1" variant="text" @click="roomDialog = false">Cancel</v-btn>
          <v-btn
color="blue-darken-1" variant="text" :disabled="!roomFormValid"
                 @click="saveRoom">
            Save
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>

    <!-- Agenda Entry Dialog -->
    <v-dialog v-model="entryDialog" max-width="500px">
      <v-card>
        <v-card-title>
          <span class="text-h5">
            {{ editedEntry.id === null ? 'New Agenda Entry' : 'Edit Agenda Entry' }}
          </span>
        </v-card-title>

        <v-card-text>
          <v-row>
            <v-col cols="12" sm="6">
              <v-select
                v-model="editedEntry.timeSlotIndex"
                :items="timeSlotItems"
                label="Time Slot"
              />
            </v-col>
            <v-col cols="12" sm="6">
              <v-select
                v-model="editedEntry.roomId"
                :items="roomItems"
                label="Room"
              />
            </v-col>
            <v-col cols="12">
              <v-autocomplete
                v-model="editedEntry.presentationId"
                :items="presentationItems"
                label="Presentation"
                clearable
              />
            </v-col>
            <v-col cols="12">
              <v-text-field
                v-model="editedEntry.label"
                label="Label (for breaks, etc.)"
                :disabled="!!editedEntry.presentationId"
              />
            </v-col>
          </v-row>
        </v-card-text>

        <v-card-actions>
          <v-btn
v-if="editedEntry.id" color="error" variant="text"
                 @click="deleteAgendaEntry">
            Delete
          </v-btn>
          <v-spacer/>
          <v-btn color="blue-darken-1" variant="text" @click="entryDialog = false">Cancel</v-btn>
          <v-btn color="blue-darken-1" variant="text" @click="saveAgendaEntry">Save</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>

    <DialogConfirm ref="confirmDialog"/>
  </v-container>
</template>

<style scoped lang="scss">
.v-table {
  table {
    border-collapse: collapse;
    width: 100%;
  }

  th, td {
    border: 1px solid rgba(0, 0, 0, 0.12);
    padding: 8px;
  }
}

.agendaGrid {
  td {
    vertical-align: top;
  }
}
</style>
