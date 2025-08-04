<script setup lang="ts">
import { ref, computed, onMounted } from 'vue';
import { daysApi, agendaApi, presentationApi, roomsApi } from '@/utils/api';

// Define props
const props = defineProps<{
  dayId: string;
}>();

// Data for time slots, rooms, presentations, and agenda entries
const timeSlots = ref([]);
const rooms = ref([]);
const presentations = ref([]);
const agendaEntries = ref([]);
const day = ref(null);

// Load data from API
onMounted(async () => {
  try {
    // Load day information
    const dayResponse = await daysApi.getDayById(props.dayId);
    day.value = dayResponse.data;

    // Load time slots
    const timeSlotsResponse = await agendaApi.getAllTimeSlots();
    timeSlots.value = timeSlotsResponse.data;

    // Load rooms
    const roomsResponse = await agendaApi.getAllRooms1();
    rooms.value = roomsResponse.data;

    // Load presentations
    const presentationsResponse = await presentationApi.getAllPresentations();
    presentations.value = presentationsResponse.data;

    // Load agenda entries for the specified day
    const agendaEntriesResponse = await agendaApi.getAgendaEntriesByDay(props.dayId);
    agendaEntries.value = agendaEntriesResponse.data;
  } catch (error) {
    console.error('Error loading data:', error);
    // Fallback to mock data if API calls fail
    loadMockData();
    daysApi.saveDay(day.value);
  }
});

// Fallback to mock data if API calls fail
const loadMockData = () => {
  timeSlots.value = [
    { id: '1', start: '09:00', end: '10:00', displayOrder: 1 },
    { id: '2', start: '10:15', end: '11:15', displayOrder: 2 },
    { id: '3', start: '11:30', end: '12:30', displayOrder: 3 },
    { id: '4', start: '13:30', end: '14:30', displayOrder: 4 },
    { id: '5', start: '14:45', end: '15:45', displayOrder: 5 },
    { id: '6', start: '16:00', end: '17:00', displayOrder: 6 },
  ];

  rooms.value = [
    { id: '1', label: 'Main Hall', displayOrder: 1 },
    { id: '2', label: 'Workshop Room', displayOrder: 2 },
    { id: '3', label: 'Conference Room A', displayOrder: 3 },
    { id: '4', label: 'Conference Room B', displayOrder: 4 },
  ];

  presentations.value = [
    { id: '1', title: 'Keynote: Future of Technology', speakers: [{ name: 'John Doe', photo: 'https://randomuser.me/api/portraits/men/1.jpg' }] },
    { id: '2', title: 'Vue 3 and TypeScript', speakers: [{ name: 'Jane Smith', photo: 'https://randomuser.me/api/portraits/women/2.jpg' }] },
    { id: '3', title: 'Building Scalable Applications', speakers: [{ name: 'Bob Johnson', photo: 'https://randomuser.me/api/portraits/men/3.jpg' }] },
    { id: '4', title: 'DevOps Best Practices', speakers: [{ name: 'Alice Brown', photo: 'https://randomuser.me/api/portraits/women/4.jpg' }] },
    { id: '5', title: 'Machine Learning Fundamentals', speakers: [{ name: 'Charlie Wilson', photo: 'https://randomuser.me/api/portraits/men/5.jpg' }] },
    { id: '6', title: 'Web Security', speakers: [{ name: 'Diana Miller', photo: 'https://randomuser.me/api/portraits/women/6.jpg' }] },
    { id: '7', title: 'Microservices Architecture', speakers: [{ name: 'Edward Davis', photo: 'https://randomuser.me/api/portraits/men/7.jpg' }] },
    { id: '8', title: 'UI/UX Design Principles', speakers: [{ name: 'Fiona Clark', photo: 'https://randomuser.me/api/portraits/women/8.jpg' }] },
  ];

  agendaEntries.value = [
    { id: '1', dayId: props.dayId, timeSlotId: '1', roomId: '1', label: null, presentationId: '1' },
    { id: '2', dayId: props.dayId, timeSlotId: '1', roomId: '2', label: null, presentationId: '2' },
    { id: '3', dayId: props.dayId, timeSlotId: '2', roomId: '1', label: null, presentationId: '3' },
    { id: '4', dayId: props.dayId, timeSlotId: '2', roomId: '2', label: null, presentationId: '4' },
    { id: '5', dayId: props.dayId, timeSlotId: '3', roomId: null, label: 'Lunch Break', presentationId: null },
    { id: '6', dayId: props.dayId, timeSlotId: '4', roomId: '1', label: null, presentationId: '5' },
    { id: '7', dayId: props.dayId, timeSlotId: '4', roomId: '2', label: null, presentationId: '6' },
    { id: '8', dayId: props.dayId, timeSlotId: '5', roomId: '1', label: null, presentationId: '7' },
    { id: '9', dayId: props.dayId, timeSlotId: '5', roomId: '2', label: null, presentationId: '8' },
    { id: '10', dayId: props.dayId, timeSlotId: '6', roomId: null, label: 'Closing Remarks', presentationId: null },
  ];
};

// Tab control
const tab = ref('agenda');

// Dialog controls
const timeSlotDialog = ref(false);
const roomDialog = ref(false);
const agendaEntryDialog = ref(false);

// Form data
const editedTimeSlot = ref({ id: '', start: '', end: '', displayOrder: 0 });
const editedRoom = ref({ id: '', label: '', displayOrder: 0 });
const editedAgendaEntry = ref({ id: '', timeSlotId: '', roomId: '', label: '', presentationId: '' });

// Edit mode flag
const editMode = ref(false);

// Get time slot by ID
const getTimeSlot = (id) => {
  return timeSlots.value.find(slot => slot.id === id) || null;
};

// Get room by ID
const getRoom = (id) => {
  return rooms.value.find(room => room.id === id) || null;
};

// Get presentation by ID
const getPresentation = (id) => {
  return presentations.value.find(presentation => presentation.id === id) || null;
};

// Get agenda entries for a specific time slot and room
const getAgendaEntry = (timeSlotId, roomId) => {
  return agendaEntries.value.find(entry =>
    entry.timeSlotId === timeSlotId &&
    (entry.roomId === roomId || (entry.roomId === null && roomId === null))
  ) || null;
};

// Sort time slots by display order
const sortedTimeSlots = computed(() => {
  return [...timeSlots.value].sort((a, b) => a.displayOrder - b.displayOrder);
});

// Sort rooms by display order
const sortedRooms = computed(() => {
  return [...rooms.value].sort((a, b) => a.displayOrder - b.displayOrder);
});

// Add or update time slot
const saveTimeSlot = () => {
  if (editMode.value) {
    const index = timeSlots.value.findIndex(slot => slot.id === editedTimeSlot.value.id);
    if (index !== -1) {
      timeSlots.value[index] = { ...editedTimeSlot.value };
    }
  } else {
    const newId = (Math.max(...timeSlots.value.map(slot => parseInt(slot.id)), 0) + 1).toString();
    timeSlots.value.push({ ...editedTimeSlot.value, id: newId });
  }
  timeSlotDialog.value = false;
  editedTimeSlot.value = { id: '', start: '', end: '', displayOrder: 0 };
  editMode.value = false;
};

// Add or update room
const saveRoom = () => {
  if (editMode.value) {
    const index = rooms.value.findIndex(room => room.id === editedRoom.value.id);
    if (index !== -1) {
      rooms.value[index] = { ...editedRoom.value };
    }
  } else {
    const newId = (Math.max(...rooms.value.map(room => parseInt(room.id)), 0) + 1).toString();
    rooms.value.push({ ...editedRoom.value, id: newId });
  }
  roomDialog.value = false;
  editedRoom.value = { id: '', label: '', displayOrder: 0 };
  editMode.value = false;
};

// Add or update agenda entry
const saveAgendaEntry = async () => {
  try {
    // Make sure the day is set
    editedAgendaEntry.value.dayId = props.dayId;

    if (editMode.value) {
      // Update existing entry
      const updateResponse = await agendaApi.saveAgendaEntry(editedAgendaEntry.value);
      const index = agendaEntries.value.findIndex(entry => entry.id === editedAgendaEntry.value.id);
      if (index !== -1) {
        agendaEntries.value[index] = { ...editedAgendaEntry.value };
      }
    } else {
      // Create new entry
      const response = await agendaApi.saveAgendaEntry(editedAgendaEntry.value);
      agendaEntries.value.push(response.data);
    }
  } catch (error) {
    console.error('Error saving agenda entry:', error);
    // Fallback to local update if API call fails
    if (editMode.value) {
      const index = agendaEntries.value.findIndex(entry => entry.id === editedAgendaEntry.value.id);
      if (index !== -1) {
        agendaEntries.value[index] = { ...editedAgendaEntry.value };
      }
    } else {
      const newId = (Math.max(...agendaEntries.value.map(entry => parseInt(entry.id) || 0), 0) + 1).toString();
      agendaEntries.value.push({ ...editedAgendaEntry.value, id: newId, dayId: props.dayId });
    }
  }

  agendaEntryDialog.value = false;
  editedAgendaEntry.value = { id: '', dayId: props.dayId, timeSlotId: '', roomId: '', label: '', presentationId: '' };
  editMode.value = false;
};

// Edit time slot
const editTimeSlot = (timeSlot) => {
  editedTimeSlot.value = { ...timeSlot };
  editMode.value = true;
  timeSlotDialog.value = true;
};

// Edit room
const editRoom = (room) => {
  editedRoom.value = { ...room };
  editMode.value = true;
  roomDialog.value = true;
};

// Edit agenda entry
const editAgendaEntry = (entry) => {
  // Make a copy of the entry and ensure dayId is set
  editedAgendaEntry.value = {
    ...entry,
    dayId: entry.dayId || props.dayId // Use entry's dayId if available, otherwise use the prop
  };
  editMode.value = true;
  agendaEntryDialog.value = true;
};

// Delete time slot
const deleteTimeSlot = (id) => {
  const index = timeSlots.value.findIndex(slot => slot.id === id);
  if (index !== -1) {
    timeSlots.value.splice(index, 1);
    // Also delete any agenda entries associated with this time slot
    agendaEntries.value = agendaEntries.value.filter(entry => entry.timeSlotId !== id);
  }
};

// Delete room
const deleteRoom = (id) => {
  const index = rooms.value.findIndex(room => room.id === id);
  if (index !== -1) {
    rooms.value.splice(index, 1);
    // Also delete any agenda entries associated with this room
    agendaEntries.value = agendaEntries.value.filter(entry => entry.roomId !== id);
  }
};

// Delete agenda entry
const deleteAgendaEntry = (id) => {
  const index = agendaEntries.value.findIndex(entry => entry.id === id);
  if (index !== -1) {
    agendaEntries.value.splice(index, 1);
  }
};

// Add new time slot
const addTimeSlot = () => {
  editedTimeSlot.value = { id: '', start: '', end: '', displayOrder: timeSlots.value.length + 1 };
  editMode.value = false;
  timeSlotDialog.value = true;
};

// Add new room
const addRoom = () => {
  editedRoom.value = { id: '', label: '', displayOrder: rooms.value.length + 1 };
  editMode.value = false;
  roomDialog.value = true;
};

// Add new agenda entry
const addAgendaEntry = (timeSlotId, roomId) => {
  editedAgendaEntry.value = {
    id: '',
    dayId: props.dayId,
    timeSlotId: timeSlotId,
    roomId: roomId,
    label: '',
    presentationId: ''
  };
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
                  <th v-for="room in sortedRooms" :key="room.id" class="text-left">
                    {{ room.label }}
                  </th>
                  <th class="text-left">All Rooms</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="timeSlot in sortedTimeSlots" :key="timeSlot.id">
                  <td>{{ timeSlot.start }} - {{ timeSlot.end }}</td>
                  <td v-for="room in sortedRooms" :key="room.id">
                    <v-card
                      v-if="getAgendaEntry(timeSlot.id, room.id)"
                      variant="outlined"
                      class="pa-2 mb-2"
                      @click="editAgendaEntry(getAgendaEntry(timeSlot.id, room.id))"
                    >
                      <div v-if="getAgendaEntry(timeSlot.id, room.id).presentationId">
                        <div class="font-weight-bold">
                          {{ getPresentation(getAgendaEntry(timeSlot.id, room.id).presentationId).title }}
                        </div>
                        <div v-for="speaker in getPresentation(getAgendaEntry(timeSlot.id, room.id).presentationId).speakers" :key="speaker.name" class="d-flex align-center mt-2">
                          <v-avatar size="24" class="mr-2">
                            <v-img :src="speaker.photo" :alt="speaker.name"></v-img>
                          </v-avatar>
                          <span>{{ speaker.name }}</span>
                        </div>
                      </div>
                      <div v-else-if="getAgendaEntry(timeSlot.id, room.id).label">
                        {{ getAgendaEntry(timeSlot.id, room.id).label }}
                      </div>
                    </v-card>
                    <v-btn
                      v-else
                      variant="text"
                      size="small"
                      color="primary"
                      @click="addAgendaEntry(timeSlot.id, room.id)"
                    >
                      Add Entry
                    </v-btn>
                  </td>
                  <td>
                    <v-card
                      v-if="getAgendaEntry(timeSlot.id, null)"
                      variant="outlined"
                      class="pa-2 mb-2"
                      @click="editAgendaEntry(getAgendaEntry(timeSlot.id, null))"
                    >
                      {{ getAgendaEntry(timeSlot.id, null).label }}
                    </v-card>
                    <v-btn
                      v-else
                      variant="text"
                      size="small"
                      color="primary"
                      @click="addAgendaEntry(timeSlot.id, null)"
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
                <tr v-for="timeSlot in sortedTimeSlots" :key="timeSlot.id">
                  <td>{{ timeSlot.start }}</td>
                  <td>{{ timeSlot.end }}</td>
                  <td>{{ timeSlot.displayOrder }}</td>
                  <td>
                    <v-btn icon="mdi-pencil" size="small" class="mr-2" @click="editTimeSlot(timeSlot)"></v-btn>
                    <v-btn icon="mdi-delete" size="small" color="error" @click="deleteTimeSlot(timeSlot.id)"></v-btn>
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
                    <v-btn icon="mdi-pencil" size="small" class="mr-2" @click="editRoom(room)"></v-btn>
                    <v-btn icon="mdi-delete" size="small" color="error" @click="deleteRoom(room.id)"></v-btn>
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
          <span class="text-h5">{{ editMode ? 'Edit Time Slot' : 'New Time Slot' }}</span>
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
          <v-btn color="blue-darken-1" variant="text" @click="timeSlotDialog = false">Cancel</v-btn>
          <v-btn color="blue-darken-1" variant="text" @click="saveTimeSlot">Save</v-btn>
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
          <v-btn color="blue-darken-1" variant="text" @click="roomDialog = false">Cancel</v-btn>
          <v-btn color="blue-darken-1" variant="text" @click="saveRoom">Save</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>

    <!-- Agenda Entry Dialog -->
    <v-dialog v-model="agendaEntryDialog" max-width="500px">
      <v-card>
        <v-card-title>
          <span class="text-h5">{{ editMode ? 'Edit Agenda Entry' : 'New Agenda Entry' }}</span>
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
                  :disabled="editedAgendaEntry.presentationId"
                ></v-text-field>
              </v-col>
            </v-row>
          </v-container>
        </v-card-text>

        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn color="blue-darken-1" variant="text" @click="agendaEntryDialog = false">Cancel</v-btn>
          <v-btn color="blue-darken-1" variant="text" @click="saveAgendaEntry">Save</v-btn>
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
