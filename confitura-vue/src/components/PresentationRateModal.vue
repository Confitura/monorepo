<template>
  <Modal v-if="presentationRate" @close="close()">
    <div class="ratingModal__body">
      <h2 class="ratingModal__title">{{ this.presentation.title }}</h2>
      <PresentationRateStars
        v-model="presentationRate.rate"
        class="ratingModal__rate"
      />
      <div class="ratingModal__comment">
        <md-field class="ratingModal__commentFiled">
          <label>comment</label>
          <md-textarea
            class="ratingModal__commentInput"
            v-model="presentationRate.comment"
            maxlength="300"
          ></md-textarea>
        </md-field>
      </div>

      <div class="ratingModal__actions">
        <md-button class="md-raised" @click="close()">Cancel</md-button>
        <md-button class="md-raised" @click="save()"
          >Save my feedback
        </md-button>
      </div>
    </div>
  </Modal>
</template>

<script lang="ts">
import { Component, Emit, Vue } from "vue-property-decorator";
import Modal from "@/components/Modal.vue";
import { RATE, SET_PRESENTATION_UNDER_RATE } from "@/store/presentations";
import PresentationRateStars from "@/components/PresentationRateStars.vue";

@Component({
  components: { PresentationRateStars, Modal }
})
export default class PresentationRateModal extends Vue {
  @Emit()
  public close() {
    this.$store.commit(SET_PRESENTATION_UNDER_RATE, { presentationRate: null });
    return true;
  }

  public save() {
    this.$store.dispatch(RATE, this.presentationRate).then(() => this.close());
  }

  get presentationRate() {
    return this.$store.state.presentations.underRating;
  }

  get presentation() {
    return this.presentationRate.presentation;
  }
}
</script>

<style scoped lang="scss">
.ratingModal {
  &__body {
    display: flex;
    flex-direction: column;
    flex-grow: 1;
  }

  &__title {
    margin-top: 0;
  }

  &__rate {
    margin-bottom: 1rem;
  }

  &__comment {
    flex-grow: 1;
    display: flex;
  }

  &__commentFiled {
    flex-direction: column;
    align-items: stretch;
  }

  &__commentInput {
    resize: none !important;
    max-height: none;
  }

  &__actions {
    text-align: right;
  }
}
</style>
