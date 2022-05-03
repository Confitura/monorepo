<template>
  <div>
    <template v-if="!entry">
      <ul>
        <li>
          <a
            class="waves-effect waves-light btn-small"
            @click="presentationModal = true"
            >Add Presentation</a
          >
        </li>
        <li>
          <a
            class="waves-effect waves-light btn-small"
            @click="labelModal = true"
            >Add Label</a
          >
        </li>
      </ul>
    </template>
    <template v-else-if="entry.presentation">
      <ul>
        <li>{{ entry.presentation.title }}</li>
        <li>
          <button
            class="waves-effect waves-light btn-small red"
            @click="remove()"
          >
            remove Presentation
          </button>
        </li>
      </ul>
    </template>
    <template v-else>
      <ul>
        <li>{{ entry.label }}</li>
        <li>
          <button
            class="waves-effect waves-light btn-small red"
            @click="remove()"
          >
            remove Label
          </button>
        </li>
      </ul>
    </template>

    <div v-if="labelModal">
      <Modal @close="close()">
        <div class="input-field col s12">
          <input id="label" type="text" maxlength="255" v-model="newLabel" />
          <label for="label">Label</label>
        </div>
        <button
          class="waves-effect waves-light btn-small green"
          @click="addLabel()"
        >
          add label
        </button>
      </Modal>
    </div>
    <div v-if="presentationModal">
      <Modal @close="close()">
        <ul class="collection">
          <li
            v-for="p of availablePresentations"
            :key="p.id"
            class="collection-item available-presentation"
            @click="addPresentation(p)"
          >
            <span class="title">{{ p.title }}</span>
            <span
              class="speaker "
              v-for="speaker of p.speakers"
              :key="speaker.id"
              >{{ speaker.name }}</span
            >
          </li>
        </ul>
      </Modal>
    </div>
  </div>
</template>

<script lang="ts">
import { Component, Prop, Vue } from "vue-property-decorator";
import { AgendaEntry, Room, TimeSlot } from "@/views/Agenda.vue";
import axios from "axios";
import { LOAD_SCHEDULE } from "@/store/admin";
import Modal from "@/components/Modal.vue";
import { Presentation } from "@/types";

@Component({
  components: { Modal }
})
export default class AgendaItem extends Vue {
  @Prop({ required: true })
  public entry!: AgendaEntry;

  @Prop({ required: true })
  public timeSlot!: TimeSlot;

  @Prop({ required: false })
  public room: Room;

  public labelModal: boolean = false;
  public newLabel = "";
  public presentationModal: boolean = false;

  public close() {
    this.labelModal = false;
    this.presentationModal = false;
  }

  addEntry(param: {
    timeSlot: string;
    room?: string | null;
    label?: string;
    presentation?: string;
  }) {
    axios.post(`/api/agenda/`, param).then(value => {
      this.$store.dispatch(LOAD_SCHEDULE);
      this.close();
    });
  }

  remove() {
    if (confirm("are you sure you want to remove it?")) {
      const id = this.entry.id;
      axios
        .delete(`/api/agenda/${id}`)
        .then(value => this.$store.dispatch(LOAD_SCHEDULE));
    }
  }

  addLabel() {
    if (this.newLabel) {
      const label = this.newLabel;
      const timeSlot = this.timeSlot._links.self.href;
      const room = this.room ? this.room._links.self.href : null;
      this.addEntry({ timeSlot, room, label });
    }
  }

  addPresentation(p: Presentation) {
    if (p) {
      const presentation = p._links.self.href;
      const timeSlot = this.timeSlot._links.self.href;
      const room = this.room ? this.room._links.self.href : null;
      this.addEntry({ timeSlot, room, presentation });
    }
  }

  get availablePresentations(): Presentation[] {
    return this.$store.getters.availablePresentations;
  }
}
</script>

<style scoped>
.btn-small {
  color: white !important;
}

.title {
  font-weight: bold;
}

.speaker {
  margin-left: 1em;
}

.available-presentation {
  cursor: pointer;
}

.available-presentation:hover {
  background-color: rgb(128, 217, 86);
}
</style>
