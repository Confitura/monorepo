<script setup lang="ts">
import type { RouteRecordRaw, RouteLocationRaw } from 'vue-router'

const { level = 0, item } = defineProps<{
  level?: number
  item: RouteRecordRaw
}>()
const visibleChildren = computed(() =>
  item.children
    ?.filter((child: RouteRecordRaw) => child.meta?.icon)
    .sort((a: RouteRecordRaw, b: RouteRecordRaw) => (a.meta?.drawerIndex ?? 99) - (b.meta?.drawerIndex ?? 98)),
)
const visibleChildrenNum = computed(() => visibleChildren.value?.length || 0)
const isItem = computed(() => !item.children || visibleChildrenNum.value <= 1)
const title = toRef(() => item.meta?.title)
const icon = toRef(() => item.meta?.icon)
const to = computed(() => ({
  name: item.name || visibleChildren.value?.[0].name,
}) as RouteLocationRaw)
</script>

<template>
  <v-list-item
    v-if="isItem && icon"
    :to="to"
    :prepend-icon="icon"
    active-class="text-primary"
    :title="title"
  />
  <v-list-group v-else-if="icon" :prepend-icon="icon" color="primary">
    <template #activator="{ props: vProps }">
      <v-list-item :title="title" v-bind="vProps" />
    </template>
    <AppDrawerItem
      v-for="child in visibleChildren"
      :key="child.name"
      :item="child"
      :level="level + 1"
    />
  </v-list-group>
</template>
