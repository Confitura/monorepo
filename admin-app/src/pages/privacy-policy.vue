<script setup lang="ts">

definePage({
  meta: {
    icon: 'mdi-home',
    title: 'Privacy Policy',
    drawerIndex: 0,
    skipMenu: true,
    layout: 'whole-page',
  },
})

import VueMarkdown from 'vue-markdown-render'
import {useRoute} from 'vue-router';

const route = useRoute();
console.log('Current route:', route);

import {ref, onMounted} from 'vue';
import {pagesApi} from "@/utils/api.ts";

const pageData = ref(null);

const markdownOptions = {
  html: true,
  linkify: true,
  typographer: true,
  breaks: true
}

onMounted(async () => {
  try {
    const response = await pagesApi.getPage('privacy-policy');
    pageData.value = response.data || "";
  } catch (error) {
    console.error('Failed to fetch page data:', error);
  }
});

</script>

<template>

  <v-container fluid v-if="pageData">
    <v-row justify="center">
      <v-col cols="12" md="8" lg="6">
        <v-card class="pa-4">
          <vue-markdown :source="pageData"  :options="markdownOptions"/>
        </v-card>
      </v-col>
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
