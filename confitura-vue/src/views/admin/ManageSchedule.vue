<template>
  <div>
    <table>
      <thead>
        <tr>
          <th>
            <button class="waves-effect waves-light btn-small" @click="addRoom">
              Add Room
            </button>
          </th>
          <th v-for="room of rooms" :key="room.id" class="">
            <div class="input-field inline">
              <input
                class="validate"
                type="text"
                maxlength="255"
                v-model="room.label"
              />
            </div>
            <button
              class="waves-effect waves-light btn-small green"
              @click="updateRoom(room)"
            >
              <i class="material-icons">save</i>
            </button>
            <button
              class="waves-effect waves-light btn-small red"
              @click="deleteRoom(room)"
            >
              <i class="material-icons">delete_forever</i>
            </button>
          </th>
        </tr>

        <tr v-for="(slot, slotIndex) of timeSlots" :key="slot.id">
          <th style="width: 8em">
            <div class="input-field ">
              <input
                class="validate"
                type="text"
                maxlength="10"
                v-model="slot.start"
              />
              <label>From</label>
            </div>

            <div class="input-field ">
              <input
                class="validate"
                id="title"
                type="text"
                maxlength="10"
                v-model="slot.end"
              />
              <label>To</label>
            </div>
            <button
              class="waves-effect waves-light btn-small green"
              @click="updateTimeSlot(slot)"
            >
              <i class="material-icons">save</i>
            </button>

            <button
              class="waves-effect waves-light btn-small red"
              @click="deleteTimeSlot(slot)"
            >
              <i class="material-icons">delete_forever</i>
            </button>
          </th>

          <template v-if="slot.forAllRooms">
            <td :colspan="rooms.length" style="position: relative">
              <AgendaItem
                :entry="scheduleMatrix[slotIndex][0]"
                :time-slot="slot"
              ></AgendaItem>
            </td>
          </template>
          <template v-else>
            <td v-for="(room, roomIndex) of rooms" :key="room.id">
              <AgendaItem
                :entry="scheduleMatrix[slotIndex][roomIndex]"
                :time-slot="slot"
                :room="room"
              ></AgendaItem>
            </td>
          </template>
        </tr>
        <tr>
          <th>
            <button
              class="waves-effect waves-light btn-small"
              @click="addTimeSlot(false)"
            >
              Per Room
            </button>
            <button
              class="waves-effect waves-light btn-small"
              @click="addTimeSlot(true)"
            >
              For All
            </button>
          </th>
          <td :colspan="rooms.length"></td>
        </tr>
      </thead>
    </table>
  </div>
</template>

<script lang="ts">
import { Component, Vue } from "vue-property-decorator";
import { AgendaEntry, Room, TimeSlot } from "@/views/Agenda.vue";
import AgendaItem from "@/components/admin/AgendaItem.vue";
import axios from "axios";
import { LOAD_ROOMS, LOAD_TIME_SLOTS } from "@/store/admin";
import M from "materialize-css";

@Component({
  components: { AgendaItem }
})
export default class ManageSchedule extends Vue {
  mounted() {
    M.updateTextFields();
  }

  updated() {
    M.updateTextFields();
  }

  get rooms(): Room[] {
    return this.$store.state.admin.rooms;
  }

  get timeSlots(): TimeSlot[] {
    return this.$store.state.admin.timeSlots;
  }

  get scheduleMatrix(): AgendaEntry[][] {
    return this.$store.getters.scheduleMatrix;
  }

  deleteRoom(room: Room) {
    if (confirm(`are you sure you want remove room ${room.label}?`)) {
      axios
        .delete(`/api/rooms/${room.id}`)
        .then(() => this.$store.dispatch(LOAD_ROOMS))
        .catch(e => this.showError("cannot remove room: ", e));
    }
  }

  updateRoom(room: Room) {
    axios
      .put(`/api/rooms/${room.id}`, room)
      .then(() => this.$store.dispatch(LOAD_ROOMS))
      .then(() => this.showSuccess())
      .catch(e => this.showError("cannot remove room: ", e));
  }

  addRoom() {
    axios
      .post(`/api/rooms`, {
        label: "new room",
        displayOrder: this.rooms.length
      })
      .then(() => this.$store.dispatch(LOAD_ROOMS))
      .then(() => this.showSuccess())
      .catch(e => this.showError("cannot add room: ", e));
  }

  deleteTimeSlot(slot: TimeSlot) {
    if (confirm(`are you sure you want remove slot ${slot.label}?`)) {
      axios
        .delete(`/api/time-slots/${slot.id}`)
        .then(() => this.$store.dispatch(LOAD_TIME_SLOTS))
        .catch(e => this.showError("cannot remove time slot: ", e));
    }
  }

  updateTimeSlot(slot: TimeSlot) {
    axios
      .put(`/api/time-slots/${slot.id}`, slot)
      .then(() => this.$store.dispatch(LOAD_TIME_SLOTS))
      .then(() => this.showSuccess())
      .catch(e => this.showError("cannot update time slot: ", e));
  }

  addTimeSlot(forAllRooms: boolean) {
    axios
      .post("/api/time-slots/", {
        label: "new time slot",
        displayOrder: this.timeSlots.length,
        forAllRooms: forAllRooms
      })
      .then(() => this.$store.dispatch(LOAD_TIME_SLOTS))
      .then(() => this.showSuccess())
      .catch(e => this.showError("cannot add time slot: ", e));
  }

  private showError(prefix, e) {
    this.$toasted.error(prefix + e.message, {
      duration: 3000,
      className: "error",
      fullWidth: true
    });
  }

  private showSuccess() {
    this.$toasted.success("saved", {
      duration: 1000,
      fullWidth: true
    });
  }
}
</script>
<style>
table {
  height: 1px;
}
tr {
  height: 100%;
}
td {
  height: 100%;
}
td > div {
  height: 100%;
}
</style>
