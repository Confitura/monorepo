<script setup lang="ts">

import VueMarkdown from 'vue-markdown-render'
import {useRoute} from 'vue-router';

import {ref, onMounted} from 'vue';
import {getPage} from "@/utils/api.ts";

const route = useRoute();
console.log('Current route:', route);

const pageData = ref<string | null>(null);

onMounted(async () => {
  try {
    const routePath = (route.params as { all: string })["all"];
    const response = await getPage({ path: { id: routePath } });
    pageData.value = response.data || "";
  } catch (error) {
    console.error('Failed to fetch page data:', error);
  }
});

</script>

<template>

  <v-container v-if="pageData" fluid>
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
