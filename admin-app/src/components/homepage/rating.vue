<script setup lang="ts">
import type {
  InlinePresentation,
  InlineWorkshop, User, ViewPresentationRate
} from "@/utils/api-axios-client";
import {presentationApi} from "@/utils/api.ts";

const {presentation} = defineProps<{
  presentation: InlinePresentation | InlineWorkshop;
}>();

let ratings = ref<ViewPresentationRate>()
let ratingsStats = ref([
  {'count': 0, 'label': 'TERRIBLE'},
  {'count': 0, 'label': 'BAD'},
  {'count': 0, 'label': 'IT_WAS_FINE'},
  {'count': 0, 'label': 'GREAT'},
  {'count': 0, 'label': 'AWESOME'},
])


function loadRates(presentation: InlinePresentation | InlineWorkshop) {
  presentationApi.rates1(presentation.id)
    .then((data) => {
      let value = data.data;
      ratings.value = value
      ratingsStats.value = [
        {'count': value.ratingCountTerrible!, 'label': 'TERRIBLE'},
        {'count': value.ratingCountBad!, 'label': 'BAD'},
        {'count': value.ratingCountItWasFine!, 'label': 'IT_WAS_FINE'},
        {'count': value.ratingCountGreat!, 'label': 'GREAT'},
        {'count': value.ratingCountAwesome!, 'label': 'AWESOME'},
      ]
    })
}

class Example {
}

onMounted(() => {
  loadRates(presentation);
})
</script>

<template>

  <v-card class="container" v-if="ratings">
    <v-card
      class="summary"
      elevation="10"
      height="500"
      width="360"
    >

      <div class="d-flex align-center flex-column my-auto">
        <div class="text-h2 mt-5">
          {{ ratings.avgRating }}
          <span class="text-h6 ml-n3">/5</span>
        </div>

        <v-rating
          :model-value="ratings.avgRating"
          color="yellow-darken-3"
          half-increments
        ></v-rating>
        <div class="px-3">{{ ratings.ratingCount }} ratings</div>
      </div>

      <v-list
        bg-color="transparent"
        class="d-flex flex-column-reverse"
        density="compact"
      >
        <v-list-item v-for="(rating,i) in 5" :key="i">
          <v-progress-linear
            :model-value="ratingsStats[i].count"
            :max="ratings.ratingCount"
            class="mx-n5"
            color="yellow-darken-3"
            height="20"
            rounded
          ></v-progress-linear>

          <template v-slot:prepend>
            <span class="rating-label">
              {{ ratingsStats[i].label }} ({{ rating }})
            </span>
          </template>

          <template v-slot:append>
            <div class="rating-values">
              <span class="d-flex justify-end"> {{
                  ratingsStats[i].count
                }} </span>
            </div>
          </template>
        </v-list-item>
      </v-list>
    </v-card>
    <div class="rates-list">

      <v-list lines="two">
        <v-list-item
          v-for="r in ratings.ratings"
          :key="r.id"
          :title="r.value"
          :subtitle="r.comment"
        ></v-list-item>
      </v-list>
    </div>
  </v-card>
</template>

<style scoped lang="scss">

.rating-label {
  width: 9em;
}


.rates-list {
  background-color: #36e4da;
}

.container {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.summary {
  order: 1;
}

.rates-list {
  order: 2;
}

@media (min-width: 768px) {
  .container {
    flex-direction: row;
    align-items: flex-start;
  }
  .summary {
    order: 1;
    flex: 1;
    margin-right: 1rem;
  }
  .rates-list {
    order: 2;
    flex: 2;
  }
}
</style>
