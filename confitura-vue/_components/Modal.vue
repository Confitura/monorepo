<template>
  <div class="modal__container">
    <div class="modal">
      <div class="modal__header">
        <span class="closeButton" @click="close()"
          ><i class="fas fa-times"></i
        ></span>
      </div>
      <div class="modal__body">
        <slot></slot>
      </div>
    </div>
  </div>
</template>

<script lang="ts">
import { Component, Emit, Vue } from "vue-property-decorator";
import PresentationBox from "@/components/PresentationBox.vue";

@Component({
  components: { PresentationBox }
})
export default class Modal extends Vue {
  @Emit()
  public close() {
    return true;
  }
}
</script>

<style scoped lang="scss">
@import "../assets/sizes";
@import "../assets/colors";

.modal__container {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  max-height: 100vh;
  z-index: 1000;
  box-sizing: border-box;
  overflow: scroll;
  background-color: rgba(0, 0, 0, 0.2);
  @include md() {
    display: flex;
    justify-content: center;
    align-items: center;
  }
}

.modal {
  background-color: #ffffff;
  min-height: 100vh;
  box-shadow: 10px 10px 20px #767676;
  display: flex;
  flex-direction: column;

  @include md() {
    width: 800px;
    min-height: 500px;
    margin: auto;
  }
}

.modal__header {
  padding-right: 0.5rem;
  padding-top: 0.5rem;
}

.modal__body {
  padding: 0 2rem 2rem;
  display: flex;
  flex-direction: column;
  flex-grow: 1;
}

.closeButton {
  cursor: pointer;
  font-size: 2rem;
  float: right;

  &:hover {
    color: $brand;
  }
}
</style>
