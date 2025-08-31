<template>
  <div
      class="agendaItem"
      :class="{ 'agendaItem--withPresentation': entry.presentationId }"
  >
    <div v-if="entry.timeSlotSpan > 1" class="agendaItem__timeSlot">
      {{ entry.timeSlotLabel }}
    </div>
    <div v-if="entry.roomLabel" class="agendaItem__room">
      Room: {{ entry.roomLabel }}
    </div>
    <div
        class="agendaItem__presentation"
        v-if="entry.presentationId"
        @click="$emit('select',entry.presentation)"
    >
      <div class="agendaItem__title">{{ entry.presentation?.title }}</div>
      <div class="agendaItem__speakers">
        <span
            class="agendaItem__speaker"
            v-for="speaker in entry.speaker"
            :key="speaker.id"
        >{{ speaker.name }}</span
        >
      </div>
      <PresentationMetadata
          :presentation="entry.presentation"
          :showTags="false"
          class="agendaItem__metadata"
      ></PresentationMetadata>
      <div class="agendaItem__separator"></div>
    </div>
    <div v-else class="agendaItem__label">
      <span>{{ entry.label }}</span>
    </div>
  </div>
</template>

<script setup lang="ts">
const {entry} = defineProps<{ entry: AgendaEntry }>()

interface AgendaEntry {
  id: string;
  timeSlotSpan: number;
  timeSlotId: string;
  timeSlotLabel: string;
  roomId: string;
  roomLabel: string;
  label: string;
  presentationId: string;
  presentation: Presentation;
  tags: Tag[];
  speaker: any;
  cospeakers: any;
}
</script>

<style scoped lang="scss">
@use "~/assets/sizes" as *;
@use "~/assets/media" as *;
@use "~/assets/colors" as *;

.agendaItem {
  font-size: 1rem;
  padding: 0.7rem;
  display: flex;
  flex-direction: column;

  &--withPresentation {
    cursor: pointer;

    &:hover {
      background-color: #dfdfdf;
    }
  }

  @include md() {
    padding: 1.5rem;
  }

  &__room {
    color: $brand;
    font-size: 0.8rem;
    margin-bottom: 0.5rem;
    @include lg() {
      display: none;
    }
  }

  &__timeSlot {
    color: $brand;
    font-weight: bold;
    font-size: 0.8rem;
    margin-bottom: 0.5rem;
    @include lg() {
      display: none;
    }
  }

  &__entry--all,
  &__label {
    color: $brand;
    font-weight: bold;
    font-size: 1.2rem;
    text-transform: capitalize;
  }

  &__label {
    justify-self: center;
  }
}

.agendaItem__presentation {
  display: flex;
  flex-direction: column;
  height: 100%;
}

.agendaItem__speakers {
  font-size: 1rem;
  color: #767676;
  margin-top: 1rem;
}

.agendaItem__speaker {
  &:not(:last-child):after {
    content: ", ";
  }
}

.agendaItem__metadata {
  font-size: 1rem;
  line-height: 1.2rem;
  margin-top: 0.3rem;
}

.agendaItem__separator {
  flex-grow: 1;
}
</style>
