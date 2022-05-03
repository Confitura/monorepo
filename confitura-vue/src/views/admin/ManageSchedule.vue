<template>
  <div>
    <table>
      <thead>
        <tr>
          <th></th>
          <th v-for="room of rooms" :key="room.id">{{ room.label }}</th>
        </tr>

        <tr v-for="(slot, slotIndex) of timeSlots" :key="slot.id">
          <td>{{ slot.start }} - {{ slot.end }}</td>

          <template v-if="slot.forAllRooms">
            <td :colspan="rooms.length">
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
      </thead>
    </table>
  </div>
</template>

<script lang="ts">
import { Component, Vue } from "vue-property-decorator";
import { AgendaEntry, Room, TimeSlot } from "@/views/Agenda.vue";
import AgendaItem from "@/components/admin/AgendaItem.vue";

@Component({
  components: { AgendaItem }
})
export default class ManageSchedule extends Vue {
  get rooms(): Room[] {
    return this.$store.state.admin.rooms;
  }

  get timeSlots(): TimeSlot[] {
    return this.$store.state.admin.timeSlots;
  }

  get scheduleMatrix(): AgendaEntry[][] {
    return this.$store.getters.scheduleMatrix;
  }
}
</script>
<style></style>
