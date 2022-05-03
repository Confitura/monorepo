<template>
  <div class="presentations">
    <table>
      <thead>
        <tr>
          <td>id</td>
          <td>title</td>
          <td>language</td>
          <td>level</td>
          <td>speakers</td>
          <td>tags</td>
          <td>accepted</td>
        </tr>
      </thead>
      <tr v-for="presentation of presentations" :key="presentation.id">
        <td>
          <router-link
            :to="{
              name: 'presentation',
              params: {
                id: presentation.id,
                userId: presentation.speakers[0].id
              }
            }"
          >
            {{ presentation.id }}
          </router-link>
        </td>
        <td>{{ presentation.title }}</td>
        <td>{{ presentation.language }}</td>
        <td>{{ presentation.level }}</td>
        <td>
          <span v-for="speaker of presentation.speakers" :key="speaker">{{
            speaker.name
          }}</span>
        </td>
        <td>
          <span
            class="badge"
            v-for="tag of presentation.tags"
            :key="tag.name"
            >{{ tag.name }}</span
          >
        </td>
        <td>
          <button
            class="waves-effect waves-light btn-small grey"
            title="mark as volunteer"
            v-if="presentation.status !== 'accepted'"
            @click="changeStatus(presentation, true)"
          >
            <i class="material-icons ">check</i>
          </button>

          <button
            class="btn-small"
            v-bind:class="{
              green: presentation.status === 'accepted',
              red: presentation.status !== 'accepted'
            }"
          >
            <i class="material-icons">{{
              presentation.status === "accepted" ? "check" : "close"
            }}</i>
          </button>

          <button
            class="waves-effect waves-light btn-small grey"
            title="remove volunteer role"
            v-if="presentation.status === 'accepted'"
            @click="changeStatus(presentation, false)"
          >
            <i class="material-icons ">close</i>
          </button>
        </td>
      </tr>
    </table>
  </div>
</template>

<script lang="ts">
import { Component, Vue } from "vue-property-decorator";
import { Presentation} from "@/types";
import axios from "axios";
import { LOAD_ALL_PRESENTATIONS} from "@/store/admin";

@Component({
  components: {}
})
export default class Presentations extends Vue {
  get presentations() {
    return this.$store.state.admin.presentations;
  }

  changeStatus(presentation: Presentation, accepted: boolean) {
    console.log(presentation, accepted);
    const endpoint = accepted ? "accept" : "unaccept";
    if (confirm("are you sure?")) {
      axios
        .post(`/api/presentations/${presentation.id}/${endpoint}`, {})
        .then(() => this.$store.dispatch(LOAD_ALL_PRESENTATIONS));
    }
  }
}
</script>
