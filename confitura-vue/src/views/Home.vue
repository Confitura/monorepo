<template>
  <div class="home">
    <TheMainBanner/>
  </div>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import TheMainBanner from '@/components/TheMainBanner.vue';

@Component({
  components: {
    TheMainBanner,
  },
})
export default class Home extends Vue {}
</script>
