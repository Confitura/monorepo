<script setup lang="ts">

import VueMarkdown from 'vue-markdown-render'
import {useRoute} from 'vue-router';

const route = useRoute();
console.log('Current route:', route);

import {ref, onMounted} from 'vue';
import {pagesApi} from "@/utils/api.ts";

const pageData = ref(null);

onMounted(async () => {
  try {
    const routePath = route.params["all"];
    const response = await pagesApi.getPage(routePath);
    pageData.value = response.data || "";
  } catch (error) {
    console.error('Failed to fetch page data:', error);
  }
});

</script>

<template>

  <v-container fluid v-if="pageData">
    <v-row>
      <vue-markdown :source="pageData"/>
    </v-row>
  </v-container>
  <v-empty-state
    v-else
    headline="Whoops, 404"
    title="Page not found"
    text="The page you were looking for does not exist"
    icon="custom:confitura"
  />
</template>
