<template>
  <div class="modal__container" @click.self="$emit('close')">
    <div class="modal" @keydown.esc.stop.prevent="$emit('close')" tabindex="-1" ref="modalRef">
      <div class="modal__header">
        <span class="closeButton" @click="$emit('close')">
          <i class="fas fa-times"></i>
        </span>
      </div>
      <div class="modal__body">
        <slot></slot>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, onBeforeUnmount, ref, nextTick } from 'vue'

const modalRef = ref<HTMLElement | null>(null)

const onKeydown = (e: KeyboardEvent) => {
  if (e.key === 'Escape' || e.key === 'Esc') {
    e.preventDefault()
    e.stopPropagation()
    // emit close
    // In <script setup>, use `defineEmits`
    emitClose()
  }
}

const emit = defineEmits<{ (e: 'close'): void }>()
const emitClose = () => emit('close')

onMounted(async () => {
  document.addEventListener('keydown', onKeydown)
  await nextTick()
  // Focus the modal so keydown works even without global listener
  modalRef.value?.focus()
})

onBeforeUnmount(() => {
  document.removeEventListener('keydown', onKeydown)
})
</script>

<style scoped lang="scss">
@use "~/assets/sizes" as *;
@use "~/assets/colors" as *;

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
  //@include md() {
    display: flex;
    justify-content: center;
    align-items: center;
  //}
}

.modal {
  background-color: #ffffff;
  //min-height: 100vh;
  box-shadow: 10px 10px 20px #767676;
  display: flex;
  flex-direction: column;

  //@include md() {
    width: 800px;
    min-height: 500px;
    margin: auto;
  //}
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
