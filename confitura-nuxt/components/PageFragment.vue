<template>
  <div>
    <!--    <button class="edit-button" v-if="isEditable" @click="edit()">Edit</button>-->
    <!--    <button class="save-button" v-if="isEditing" @click="save()">Save</button>-->
    <!--    <button class="cancel-button" v-if="isEditing" @click="cancel()">-->
    <!--      Cancel-->
    <!--    </button>-->
    <section v-if="isViewing" v-html="html"></section>
    <!--    <section v-if="isEditing">-->
    <!--      <textarea class="editor" v-model="contentInEdit"></textarea>-->
    <!--    </section>-->
  </div>
</template>

<script setup lang="ts">
import { marked } from 'marked'

const { name } = defineProps<{ name: string }>()
const contentInEdit = useState('contentInEdit', () => '')
const editMode = useState('editMode', () => false)
const { data: content } = useAPIFetch(`/json/pages/${name}.json`, {
  key: `content-${name}`,
  transform: (response) => response.content
})

function edit() {
  editMode.value = true
  contentInEdit.value = content.value
}

function cancel() {
  editMode.value = false
}

function save() {
  // axios
  //   .post<void>(`/api/pages`, { id: this.name, content: this.contentInEdit })
  //   .then(() => (this.content = this.contentInEdit))
  //   .then(() => this.cancel());
}

const isEditable = false
//TODO public get isEditable(): boolean {
//   return this.$store.getters.isAdmin && this.isViewing;
// }

const isViewing = true
// public get isViewing() {
//   return !this.editMode;
// }
const isEditing = false
// public get isEditing() {
//   return this.editMode;
// }

const html = computed(() => content.value ? marked(content.value) : '')
// public get html() {
//   return marked(this.content);
// }
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
