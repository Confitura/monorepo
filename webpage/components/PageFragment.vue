<template>
  <div>
    <section v-html="html"></section>
  </div>
</template>

<script setup lang="ts">
import {marked} from 'marked'

const {name} = defineProps<{ name: string }>()
const content = await useArchiveFetch(`/pages/${name}.json`, {
  key: `content-${name}`,
}).then(response => response.data.value as string)


const html = computed(() => {
  return content ? marked(content) : ''
})
</script>

<style scoped>
</style>
