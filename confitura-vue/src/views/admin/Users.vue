<template>
  <div class="users">
    <table>
      <thead>
        <tr>
          <th>id</th>
          <th>name</th>
          <th>email</th>
          <th>admin</th>
          <th>volunteer</th>
          <th>speaker</th>
          <th>Privacy Accepted</th>
        </tr>
      </thead>
      <tr v-for="user of users" :key="user.id">
        <td>
          <router-link :to="{ name: 'profile', params: { id: user.id } }">{{
            user.id
          }}</router-link>
        </td>
        <td>{{ user.name }}</td>
        <td>{{ user.email }}</td>
        <td>
          <button
            class="btn-small"
            v-bind:class="{ green: user.admin, red: !user.admin }"
          >
            <i class="material-icons">{{ user.admin ? "check" : "close" }}</i>
          </button>
        </td>
        <td>
          <button
            class="waves-effect waves-light btn-small grey"
            title="remove volunteer role"
            v-if="user.volunteer"
            @click="markAsVolunteer(user, false)"
          >
            <i class="material-icons ">close</i>
          </button>
          <button
            class="btn-small"
            v-bind:class="{ green: user.volunteer, red: !user.volunteer }"
          >
            <i class="material-icons">{{
              user.volunteer ? "check" : "close"
            }}</i>
          </button>
          <button
            class="waves-effect waves-light btn-small grey"
            title="mark as volunteer"
            v-if="!user.volunteer"
            @click="markAsVolunteer(user, true)"
          >
            <i class="material-icons ">check</i>
          </button>
        </td>
        <td>
          <button
            class="btn-small"
            v-bind:class="{ green: user.speaker, red: !user.speaker }"
          >
            <i class="material-icons">{{ user.speaker ? "check" : "close" }}</i>
          </button>
        </td>
        <td>
          <button
            class="btn-small"
            v-bind:class="{
              green: user.privacyPolicyAccepted,
              red: !user.privacyPolicyAccepted
            }"
          >
            <i class="material-icons">{{
              user.privacyPolicyAccepted ? "check" : "close"
            }}</i>
          </button>
        </td>
      </tr>
    </table>
  </div>
</template>

<script lang="ts">
import { Component, Vue } from "vue-property-decorator";
import { UserProfile } from "@/types";
import axios from "axios";
import { LOAD_USERS } from "@/store/admin";

@Component({
  components: {}
})
export default class Users extends Vue {
  public $refs!: Vue["$refs"] & {
    modal: Element;
  };

  get users() {
    return this.$store.state.admin.users;
  }

  public mounted() {}

  markAsVolunteer(user: UserProfile, volunteer: boolean) {
    console.log(user, volunteer);
    if (confirm("are you sure?")) {
      axios
        .post(`/api/users/${user.id}/volunteer/${volunteer}`, {})
        .then(() => this.$store.dispatch(LOAD_USERS));
    }
  }

  markAsAdmin(user: UserProfile, admin: boolean) {
    console.log(user, admin);
    if (confirm("are you sure?")) {
      axios
        .post(`/api/users/${user.id}/admin/${admin}`, {})
        .then(() => this.$store.dispatch(LOAD_USERS));
    }
  }
}
</script>
<style>
.btn-small {
  color: white !important;
}
</style>
