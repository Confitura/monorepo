<template>
  <div class="ratingModal__rate">
    <star-rating
      v-model="rate"
      :star-size="30"
      :show-rating="false"
      :rounded-corners="true"
      :star-points="[
        23,
        2,
        14,
        17,
        0,
        19,
        10,
        34,
        7,
        50,
        23,
        43,
        38,
        50,
        36,
        34,
        46,
        19,
        31,
        17
      ]"
      @rating-selected="setStars()"
    ></star-rating>
  </div>
</template>
<script lang="ts">
import StarRating from "vue-star-rating";
import { Vue } from "vue-property-decorator";

export default Vue.extend({
  name: "PresentationRateStars",
  components: { StarRating },
  props: ["value"],
  data: function() {
    return {
      rate: this.value
    };
  },
  methods: {
    setStars() {
      this.$emit("input", this.rate);
    }
  }
});
</script>
