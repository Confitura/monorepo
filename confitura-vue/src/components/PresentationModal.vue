<template>
  <Modal @close="close()" v-if="presentation">
    <PresentationBox :presentation="presentation"></PresentationBox>
  </Modal>
</template>

<script lang="ts">
import { Component, Emit, Prop, Vue, Watch } from "vue-property-decorator";
import PresentationBox from "@/components/PresentationBox.vue";
import { Presentation } from "@/types";
import axios from "axios";
import Modal from "@/components/Modal.vue";

@Component({
  components: { Modal, PresentationBox }
})
export default class PresentationModal extends Vue {
  public presentation: Presentation | null = null;

  @Prop({ required: true, default: null })
  public readonly presentationId!: string;

  @Emit()
  public close() {
    return true;
  }

  @Watch("presentationId")
  private onChildChanged() {
    if (this.presentationId) {
      axios
        .get<Presentation>(
          `/api/presentations/${this.presentationId}?projection=inlineSpeaker`
        )
        .then(result => (this.presentation = result.data));
    } else {
      this.presentation = null;
    }
  }
}
</script>

<style scoped lang="scss"></style>
