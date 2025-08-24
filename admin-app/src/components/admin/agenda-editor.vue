<script setup lang="ts">
import { ref, computed, onMounted } from 'vue';
import type { Ref } from 'vue';
import { storeToRefs } from 'pinia';
import { useAgendaStore } from '@/stores/agenda';
import type {
  AssignAgendaEntryRequest,
  InlineAgendaEntry,
  InlineTimeSlot,
  InlineRoom,
} from "@/utils/api-axios-client";
import { agendaApi } from '@/utils/api';

// Define props
const props = defineProps<{
  dayId: string;
}>();

// Store
const agenda = useAgendaStore();
const { presentations } = storeToRefs(agenda);
const timeSlots = computed(() => agenda.timeSlotsForDay(props.dayId));
const rooms = computed(() => agenda.roomsForDay(props.dayId));
// Local sorted aliases for template compatibility
const sortedTimeSlots = computed(() => timeSlots.value);
const sortedRooms = computed(() => rooms.value);

async function refreshData() {
  try {
    await agenda.refreshData(props.dayId);
  } catch (error) {
    console.error('Error loading data:', error);
    Notify.error('Error loading data');
  }
}

// Load data from API
onMounted(async () => {
  await refreshData();
});


const tab = ref('agenda');

// Dialog controls
const timeSlotDialog = ref(false);
const roomDialog = ref(false);
const agendaEntryDialog = ref(false);

// Form data
const editedTimeSlot: Ref<InlineTimeSlot> = ref({
  dayId: '',
  displayOrder: -1,
  label: '',
  start: '',
  end: ''
});

const editedRoom: Ref<InlineRoom> = ref({ id: '', label: '', displayOrder: 0 });
const editedAgendaEntryId: Ref<string | null> = ref(null);
const editedAgendaEntry: Ref<AssignAgendaEntryRequest> = ref({
  dayId: '',
  timeSlotIndex: -1,
  roomId: '',
  label: '',
  presentationId: ''
});

// Edit mode flag
const editMode = ref(false);

// Delegated lookups
const getAgendaEntry = (timeSlot: InlineTimeSlot, room: InlineRoom | null) =>
  agenda.getAgendaEntry(timeSlot, room, props.dayId);
const getPresentation = agenda.getPresentation;
const findPresentation = (timeSlot: InlineTimeSlot, room: InlineRoom | null) =>
  agenda.findPresentation(timeSlot, room, props.dayId);

const saveTimeSlot = async () => {
  try {
    if (!editMode.value) {
      // Creation of time slots is not handled by this issue
      Notify && (Notify as any).error ? (Notify as any).error('Creating time slots is not supported yet') : console.warn('Creating time slots is not supported yet');
      return;
    }
    // Use new endpoint to update the time slot
    await agendaApi.updateTimeSlot(
      props.dayId,
      editedTimeSlot.value.displayOrder!,
      {
        start: editedTimeSlot.value.start || undefined,
        end: editedTimeSlot.value.end || undefined,
      }
    );
    await refreshData();
  } catch (error) {
    console.error('Error saving time slot:', error);
    Notify.error && Notify.error('Error saving time slot');
  } finally {
    timeSlotDialog.value = false;
    editedTimeSlot.value = {
      dayId: '',
      displayOrder: -1,
      label: '',
      start: '',
      end: ''
    };
    editMode.value = false;
  }
};

// Add or update room
const saveRoom = async () => {
  try {
    if (!editMode.value) {
      // Creating rooms is not covered by this task
      Notify && (Notify as any).error ? (Notify as any).error('Creating rooms is not supported yet') : console.warn('Creating rooms is not supported yet');
      return;
    }
    // Use new endpoint to update the room
    await agendaApi.updateRoom(editedRoom.value.id!, {
      label: editedRoom.value.label || undefined,
      displayOrder: editedRoom.value.displayOrder ?? undefined,
    });
    await refreshData();
  } catch (error) {
    console.error('Error saving room:', error);
    Notify.error && Notify.error('Error saving room');
  } finally {
    roomDialog.value = false;
    editedRoom.value = { id: '', label: '', displayOrder: 0 };
    editMode.value = false;
  }
};

function closeAndCleanAgendaEntryEditor() {
  agendaEntryDialog.value = false;
  editedAgendaEntry.value = {
    dayId: '',
    timeSlotIndex: -1,
    roomId: '',
    label: '',
    presentationId: ''
  };
  editedAgendaEntryId.value = null;
  editMode.value = false;
}

// Add or update agenda entry
const saveAgendaEntry = async () => {
  try {
    // Make sure the day is set
    editedAgendaEntry.value.dayId = props.dayId;

    if (editMode.value && editedAgendaEntryId.value) {
      await agenda.updateAgendaEntry(editedAgendaEntryId.value, {
        label: editedAgendaEntry.value.label || undefined,
        presentationId: editedAgendaEntry.value.presentationId || undefined,
        roomId: editedAgendaEntry.value.roomId || undefined,
      }, props.dayId);
    } else {
      await agenda.saveAgendaEntry(editedAgendaEntry.value, props.dayId);
    }
  } catch (error) {
    console.error('Error saving agenda entry:', error);
    Notify.error('Error saving agenda entry');
  }
  closeAndCleanAgendaEntryEditor();
}

const clearAgendaEntry = async () => {
  try {
    if (editedAgendaEntryId.value) {
      await agenda.deleteAgendaEntry(editedAgendaEntryId.value, props.dayId);
    }
    closeAndCleanAgendaEntryEditor()
  } catch (error) {
    console.error('Error clearing agenda entry:', error);
    Notify.error('Error clearing agenda entry');
  }
}

// Edit time slot
const editTimeSlot = (timeSlot: InlineTimeSlot) => {
  editedTimeSlot.value = { ...timeSlot };
  editMode.value = true;
  timeSlotDialog.value = true;

};

// Edit room
const editRoom = (room: InlineRoom) => {
  editedRoom.value = { ...room };
  editMode.value = true;
  roomDialog.value = true;
};

// Edit agenda entry
const editAgendaEntry = (entry: InlineAgendaEntry | null) => {
  // Make a copy of the entry and ensure dayId is set
  editedAgendaEntryId.value = entry?.id || null;
  editedAgendaEntry.value = {
    dayId: entry?.dayId!,
    timeSlotIndex: entry?.timeSlotIndex!,
    roomId: entry?.roomId,
    label: entry?.label || '',
    presentationId: entry?.presentationId || ''
  };
  editMode.value = true;
  agendaEntryDialog.value = true;
};


const deleteTimeSlot = (timeSlot: InlineTimeSlot) => {
  //TODO
};

const deleteRoom = (room: InlineRoom) => {
  //TODO
};

const deleteAgendaEntry = (entry: InlineAgendaEntry) => {
  //TODO
};

// Add new time slot
const addTimeSlot = () => {
  editedTimeSlot.value = {
    dayId: '',
    displayOrder: -1,
    label: '',
    start: '',
    end: ''
  };
  editMode.value = false;
  timeSlotDialog.value = true;
};

// Add new room
const addRoom = () => {
  editedRoom.value = {
    id: '',
    label: '',
    displayOrder: rooms.value.length + 1
  };
  editMode.value = false;
  roomDialog.value = true;
};

// Add new agenda entry
const addAgendaEntry = (timeSlotIndex: number, roomId: string | undefined) => {
  editedAgendaEntry.value = {
    dayId: props.dayId,
    timeSlotIndex: timeSlotIndex,
    roomId: roomId,
    label: '',
    presentationId: ''
  };
  editedAgendaEntryId.value = null;
  editMode.value = false;
  agendaEntryDialog.value = true;
};
</script>

<template>
  <v-container>
    <h1 class="text-h4 mb-6">Conference Agenda Management</h1>

    <!-- Tabs for different sections -->
    <v-tabs v-model="tab" bg-color="primary">
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
          </v-card-title>

          <v-card-text>
            <v-table>
              <thead>
              <tr>
                <th class="text-left">Time</th>
                <th v-for="room in rooms" :key="room.id"
                    class="text-left">
                  {{ room.label }}
                </th>
                <th class="text-left">All Rooms</th>
              </tr>
              </thead>
              <tbody>
              <tr v-for="timeSlot in timeSlots" :key="timeSlot.displayOrder">
                <td>{{ timeSlot.start }} - {{ timeSlot.end }}</td>
                <td v-for="room in rooms" :key="room.id">
                  <v-card
                    v-if="getAgendaEntry(timeSlot, room)"
                    variant="outlined"
                    class="pa-2 mb-2"
                    @click="editAgendaEntry(getAgendaEntry(timeSlot, room))"
                  >
                    <div
                      v-if="getAgendaEntry(timeSlot, room)!.presentationId">
                      <div class="font-weight-bold">
                        {{
                          findPresentation(timeSlot!, room)?.title
                        }}
                      </div>
                      <div
                        v-for="speaker in findPresentation(timeSlot!, room)!.speakers"
                        :key="speaker.name" class="d-flex align-center mt-2">
                        <v-avatar size="24" class="mr-2">
                          <v-img :src="speaker.photo"
                                 :alt="speaker.name"></v-img>
                        </v-avatar>
                        <span>{{ speaker.name }}</span>
                      </div>
                    </div>
                    <div
                      v-else-if="getAgendaEntry(timeSlot, room)?.label">
                      {{
                        getAgendaEntry(timeSlot, room)?.label
                      }}
                    </div>
                  </v-card>
                  <v-btn
                    v-else
                    variant="text"
                    size="small"
                    color="primary"
                    @click="addAgendaEntry(timeSlot.displayOrder!, room.id!)"
                  >
                    Add Entry
                  </v-btn>
                </td>
                <td>
                  <v-card
                    v-if="getAgendaEntry(timeSlot, null)"
                    variant="outlined"
                    class="pa-2 mb-2"
                    @click="editAgendaEntry(getAgendaEntry(timeSlot, null))"
                  >
                    {{ getAgendaEntry(timeSlot, null)?.label }}
                  </v-card>
                  <v-btn
                    v-else
                    variant="text"
                    size="small"
                    color="primary"
                    @click="addAgendaEntry(timeSlot.displayOrder!, undefined)"
                  >
                    Add Entry
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
            <v-btn color="primary" @click="addTimeSlot">Add Time Slot</v-btn>
          </v-card-title>

          <v-card-text>
            <v-table>
              <thead>
              <tr>
                <th class="text-left">Start Time</th>
                <th class="text-left">End Time</th>
                <th class="text-left">Display Order</th>
                <th class="text-left">Actions</th>
              </tr>
              </thead>
              <tbody>
              <tr v-for="timeSlot in sortedTimeSlots"
                  :key="timeSlot.displayOrder">
                <td>{{ timeSlot.start }}</td>
                <td>{{ timeSlot.end }}</td>
                <td>{{ timeSlot.displayOrder }}</td>
                <td>
                  <v-btn icon="mdi-pencil" size="small" class="mr-2"
                         @click="editTimeSlot(timeSlot)"></v-btn>
                  <v-btn icon="mdi-delete" size="small" color="error"
                         @click="deleteTimeSlot(timeSlot)"></v-btn>
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
            <v-btn color="primary" @click="addRoom">Add Room</v-btn>
          </v-card-title>

          <v-card-text>
            <v-table>
              <thead>
              <tr>
                <th class="text-left">Name</th>
                <th class="text-left">Display Order</th>
                <th class="text-left">Actions</th>
              </tr>
              </thead>
              <tbody>
              <tr v-for="room in sortedRooms" :key="room.id">
                <td>{{ room.label }}</td>
                <td>{{ room.displayOrder }}</td>
                <td>
                  <v-btn icon="mdi-pencil" size="small" class="mr-2"
                         @click="editRoom(room)"></v-btn>
                  <v-btn icon="mdi-delete" size="small" color="error"
                         @click="deleteRoom(room)"></v-btn>
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
          <span class="text-h5">{{
              editMode ? 'Edit Time Slot' : 'New Time Slot'
            }}</span>
        </v-card-title>

        <v-card-text>
          <v-container>
            <v-row>
              <v-col cols="12" sm="6">
                <v-text-field
                  v-model="editedTimeSlot.start"
                  label="Start Time"
                  type="time"
                ></v-text-field>
              </v-col>
              <v-col cols="12" sm="6">
                <v-text-field
                  v-model="editedTimeSlot.end"
                  label="End Time"
                  type="time"
                ></v-text-field>
              </v-col>
              <v-col cols="12">
                <v-text-field
                  v-model.number="editedTimeSlot.displayOrder"
                  label="Display Order"
                  type="number"
                ></v-text-field>
              </v-col>
            </v-row>
          </v-container>
        </v-card-text>

        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn color="blue-darken-1" variant="text"
                 @click="timeSlotDialog = false">Cancel
          </v-btn>
          <v-btn color="blue-darken-1" variant="text" @click="saveTimeSlot">
            Save
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>

    <!-- Room Dialog -->
    <v-dialog v-model="roomDialog" max-width="500px">
      <v-card>
        <v-card-title>
          <span class="text-h5">{{ editMode ? 'Edit Room' : 'New Room' }}</span>
        </v-card-title>

        <v-card-text>
          <v-container>
            <v-row>
              <v-col cols="12">
                <v-text-field
                  v-model="editedRoom.label"
                  label="Room Name"
                ></v-text-field>
              </v-col>
              <v-col cols="12">
                <v-text-field
                  v-model.number="editedRoom.displayOrder"
                  label="Display Order"
                  type="number"
                ></v-text-field>
              </v-col>
            </v-row>
          </v-container>
        </v-card-text>

        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn color="blue-darken-1" variant="text"
                 @click="roomDialog = false">Cancel
          </v-btn>
          <v-btn color="blue-darken-1" variant="text" @click="saveRoom">Save
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>

    <!-- Agenda Entry Dialog -->
    <v-dialog v-model="agendaEntryDialog" max-width="500px">
      <v-card>
        <v-card-title>
          <span class="text-h5">{{
              editMode ? 'Edit Agenda Entry' : 'New Agenda Entry'
            }}</span>
        </v-card-title>

        <v-card-text>
          <v-container>
            <v-row>
              <v-col cols="12">
                <v-select
                  v-model="editedAgendaEntry.presentationId"
                  :items="presentations"
                  item-title="title"
                  item-value="id"
                  label="Presentation"
                  clearable
                ></v-select>
              </v-col>
              <v-col cols="12">
                <v-text-field
                  v-model="editedAgendaEntry.label"
                  label="Label (for breaks, etc.)"
                  :disabled="!!editedAgendaEntry.presentationId"
                ></v-text-field>
              </v-col>
            </v-row>
          </v-container>
        </v-card-text>

        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn color="blue-darken-1" variant="text"
                 @click="clearAgendaEntry">Clear
          </v-btn>
          <v-btn color="blue-darken-1" variant="text"
                 @click="agendaEntryDialog = false">Cancel
          </v-btn>
          <v-btn color="blue-darken-1" variant="text" @click="saveAgendaEntry">
            Save
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
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
</style>
