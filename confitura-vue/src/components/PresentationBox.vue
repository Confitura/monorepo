<template>
  <div class="presentationBox" v-if="presentation">
    <div class="presentationBox__header">
      <h2 class="presentationBox__title">{{ presentation.title }}</h2>
      <div class="presentationBox__infoGroup">
        <PresentationSpeakers
            :speakers="presentation.speakers"
        ></PresentationSpeakers>
        <PresentationMetadata
            :presentation="presentation"
        ></PresentationMetadata>
      </div>
    </div>
    <div class="presentationBox__description" v-html="description">
    </div>
  </div>
</template>

<script lang="ts">
import { Component, Prop, Vue } from "vue-property-decorator";
import PresentationSpeakers from "@/components/PresentationSpeakers.vue";
import PresentationMetadata from "@/components/PresentationMetadata.vue";
import { Presentation } from "@/types";
import marked from "marked";

@Component({
  components: { PresentationMetadata, PresentationSpeakers }
})
export default class AcceptedPresentations extends Vue {
  @Prop({ required: false, default: null })
  public readonly presentation!: Presentation;

  public get description() {
    return marked(this.presentation.description);
  }
}
</script>

<style scoped lang="scss">
@import "../assets/colors";
@import "../assets/sizes";
@import "../assets/media";
@import "../assets/fonts";

.presentationBox--odd {
  .presentationBox__header {
    color: #ffffff;
  }
}

.presentationBox {
  &__header {
    color: $brand;
  }

  &__infoGroup {
    display: flex;
    flex-direction: column;
    @include md() {
      align-items: center;
      flex-direction: row;
    }
  }

  &__title {
    font-weight: bold;
    font-size: 2rem;
    margin-top: 0;
  }

  &__description {
    font-size: 1.2rem;
    line-height: 1.5rem;
    word-wrap: break-word;
    @include md() {
      padding-left: 4rem;
      line-height: 1.7rem;
    }
  }
}
</style>
