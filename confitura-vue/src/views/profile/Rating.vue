<template>

  <table v-if="rating && rating.length > 0">
    <tr>
      <th colspan="2">Rating</th>
    </tr>
    <tr v-for="rate in rating">
      <td>{{ mapRate(rate.value) }}</td>
      <td>{{ rate.comment }}</td>
    </tr>
  </table>
</template>

<script lang="ts">
import {Component, Prop, Vue, Watch} from "vue-property-decorator";
import {EmbeddedRates, Presentation, Rate} from "@/types";
import axios from "axios";
import PresentationRateStars from "@/components/PresentationRateStars.vue";

@Component({
  components: {PresentationRateStars}
})
export default class Rating extends Vue {

  @Prop({required: false, default: null})
  public readonly presentation!: Presentation;

  rating: Rate[] = [];

  mounted() {
    this.onPresentationChanged()
  }

  @Watch("presentation")
  onPresentationChanged() {
    if (this.presentation && this.presentation.id) {
      axios
          .get<EmbeddedRates>(`/api/presentations/${this.presentation.id}/ratings`)
          .then(it => it.data)
          .then(it => this.rating = it._embedded.rates);
    }
  }

  mapRate(rate: string) {
    switch (rate) {
      case 'AWESOME':
        return 1.0
      case 'GREAT':
        return 2.0
      case 'IT_WAS_FINE':
        return 3.0
      case 'BAD':
        return 4.0
      case 'TERRIBLE':
        return 5.0
    }
  }
}
</script>

<style scoped>

</style>
