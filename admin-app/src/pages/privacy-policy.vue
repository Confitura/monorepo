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
        <v-card class="pa-4 markdown">
          <vue-markdown :source="pageData" :options="markdownOptions"/>
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

<style >
.markdown h1,
.markdown h2,
.markdown h3,
.markdown h4,
.markdown h5,
.markdown h6 {
  margin: 1.5em 0 0.5em !important;
  padding-inline-start: 30px;
}

.markdown p {
  margin: 1em 0 !important;
  padding-inline-start: 30px;
}

.markdown blockquote {
  margin: 1em 0 !important;
  padding: 0.5em 1em !important;
  border-left: 4px solid rgba(var(--v-theme-primary), 0.5) !important;
  background-color: rgba(var(--v-theme-primary), 0.05);
}

.markdown ul,
.markdown ol {
  margin: 1em 0 !important;
  padding-left: 30px !important;
}

.markdown li {
  padding-inline-start: 10px !important;
  margin-bottom: 0.5em !important;
}

.markdown ul ul,
.markdown ol ol,
.markdown ul ol,
.markdown ol ul {
  margin: 0.5em 0 !important;
}

.markdown pre,
.markdown code {
  background-color: rgba(var(--v-theme-surface), 0.12);
  padding: 0.2em 0.4em;
  border-radius: 4px;
  font-family: monospace;
}

.markdown pre code {
  padding: 1em;
  display: block;
  overflow-x: auto;
}
</style>
