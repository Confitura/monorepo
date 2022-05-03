<template>
  <div>
    <table>
      <thead>
        <tr>
          <th>title</th>
          <th>presentationId</th>
          <th>positiveVotes</th>
          <th>negativeVotes</th>
          <th>totalVotes</th>
        </tr>
      </thead>
      <tr v-for="vote of votes" :key="vote.presentationId">
        <td>{{ vote.title }}</td>
        <td>{{ vote.presentationId }}</td>
        <td>{{ vote.positiveVotes }}</td>
        <td>{{ vote.negativeVotes }}</td>
        <td>{{ vote.totalVotes }}</td>
      </tr>
    </table>
  </div>
</template>

<script lang="ts">
import { Component, Vue } from "vue-property-decorator";
import { VoteStat } from "@/types";

@Component({
  components: {}
})
export default class Votes extends Vue {
  get votes(): VoteStat[] {
    return this.$store.state.admin.votes;
  }
}
</script>

<style scoped lang="scss"></style>
