<template>
  <div>
    <button class="edit-button" v-if="isEditable" @click="edit()">Edit</button>
    <button class="save-button" v-if="isEditing" @click="save()">Save</button>
    <button class="cancel-button" v-if="isEditing" @click="cancel()">
      Cancel
    </button>
    <section v-if="isViewing" v-html="html"></section>
    <section v-if="isEditing">
      <textarea class="editor" v-model="contentInEdit"></textarea>
    </section>
  </div>
</template>

<script lang="ts">
import { Component, Prop, Vue } from "vue-property-decorator";
import axios from "axios";
import { Page } from "@/types";
import marked from "marked";

@Component({
  components: {}
})
export default class PageFragment extends Vue {
  @Prop({ required: true })
  public name!: string;

  public content: string = "";
  public contentInEdit = "";
  public editMode = false;

  public mounted() {
    axios
      .get<Page>(`/json/pages/${this.name}.json`)
      .then(response => response.data.content)
      .then((content: string) => (this.content = content));
  }

  public edit() {
    this.editMode = true;
    this.contentInEdit = this.content;
  }

  public cancel() {
    this.editMode = false;
  }

  public save() {
    axios
      .post<void>(`/api/pages`, { id: this.name, content: this.contentInEdit })
      .then(() => (this.content = this.contentInEdit))
      .then(() => this.cancel());
  }

  public get isEditable(): boolean {
    return this.$store.getters.isAdmin && this.isViewing;
  }

  public get isViewing() {
    return !this.editMode;
  }

  public get isEditing() {
    return this.editMode;
  }

  public get html() {
    return marked(this.content);
  }
}
</script>

<style scoped>
.editor {
  width: 100%;
  height: 100vh;
}

.edit-button,
.save-button,
.cancel-button {
  margin: 0 1rem 1rem 0;
}
</style>
