<template>
  <div class="users">
    <table>
      <thead>
        <tr>
          <th>id</th>
          <th>name</th>
          <th>email</th>
          <th>gender</th>
          <th>size</th>
          <th>arrivalDate</th>
          <th>registeredBy</th>
          <th>ticketSendDate</th>
          <th></th>
          <th>surveySendDate</th>
        </tr>
      </thead>
      <tr v-for="participant of participants" :key="participant.id">
        <td>
          <router-link
            :to="{ name: 'participant', params: { id: participant.id } }"
            >{{ participant.id }}
          </router-link>
        </td>
        <td>{{ participant.name }}</td>
        <td>{{ participant.email }}</td>
        <td>{{ participant.gender }}</td>
        <td>{{ participant.size }}</td>
        <td>{{ participant.arrivalDate }}</td>
        <td>{{ participant.registeredBy }}</td>
        <td>{{ participant.ticketSendDate }}</td>
        <td>
          <button class="waves-effect waves-light btn"
                  @click="resendTicket(participant.id)">
            resend
          </button>
        </td>
        <td>{{ participant.surveySendDate }}</td>
      </tr>
    </table>
  </div>
</template>

<script lang="ts">
import { Component, Vue } from "vue-property-decorator";
import { Participant, UserProfile } from "@/types";
import axios from "axios";

@Component({
  components: {}
})
export default class Users extends Vue {
  get participants(): Participant[] {
    return this.$store.state.admin.participants;
  }

  public resendTicket(participantId: string) {
    if (confirm("sure to resend ticket?")) {
      axios.post(`/api/participants/${participantId}/resend-ticket`);
    }
  }
}
</script>
<style></style>
