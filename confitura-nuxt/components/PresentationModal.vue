<template>
  <Modal @close="$emit('close')" v-if="presentation">
    <PresentationBox :presentation="presentation"></PresentationBox>
  </Modal>
</template>

<script setup lang="ts">

const { presentationId } = defineProps<{ presentationId: string }>()
const presentation = useState('presentation', () => null)

watch(() => presentationId, async () => {
  if (presentationId) {
    presentation.value = await $fetch(`/json/presentations/${presentationId}.json`)
  } else {
    presentation.value = null
  }
})
// @Watch("presentationId")
// function onChildChanged() {
//   if (this.presentationId) {
//     axios
//       .get<Presentation>(
//         `/json/presentations/${this.presentationId}.json`
//       )
//       .then(result => (this.presentation = result.data));
//   } else {
//     this.presentation = null;
//   }
// }
</script>

<style scoped lang="scss"></style>
