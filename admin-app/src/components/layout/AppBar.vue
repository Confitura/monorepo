<script setup lang="ts">
import {useToggle, useDark} from '@vueuse/core'
const theme = useTheme()
const {drawer} = storeToRefs(useAppStore())
const {user} = storeToRefs(useAuthStore())
const route = useRoute()
const breadcrumbs = computed(() => {
  return route!.matched
    .slice(1)
    .filter(
      (item) =>
        item.meta && item.meta.title && !(item.meta?.breadcrumb === 'hidden'),
    )
    .map((r) => ({
      title: r.meta.title!,
      disabled:
        r.meta?.breadcrumb === 'disabled' || r.path === route.path || false,
      to: r.path,
    }))
})
const isDark = useDark({
  onChanged(dark: boolean) {
    theme.global.name.value = dark ? 'dark' : 'light'
  },
})
const toggleDark = useToggle<true, false | null>(isDark)
function logout() {
  useAuthStore().logout()
}
</script>

<template>
  <v-app-bar flat>
    <v-app-bar-nav-icon @click="drawer = !drawer"/>
    <v-breadcrumbs :items="breadcrumbs"/>
    <v-spacer/>
    <div id="app-bar"/>

    <div>
      <v-chip v-if="user">{{ user?.sub }}</v-chip>
    </div>
    <div>
      <v-switch
        :model-value="isDark"
        color=""
        hide-details
        density="compact"
        inset
        false-icon="mdi-white-balance-sunny"
        true-icon="mdi-weather-night"
        style="opacity: 0.8"
        @update:model-value="toggleDark"
      />
    </div>
    <v-btn
      icon
      @click="logout"
      size="small"
      class="ml-2"
      target="_blank"
    >
      <v-icon size="30" icon="mdi-logout"/>
    </v-btn>
  </v-app-bar>
</template>

<style scoped>
:deep(.v-breadcrumbs-divider) {
  opacity: 0.5;
}
:deep(.v-switch__thumb .v-icon) {
  --v-icon-size-multiplier: 1.2 !important;
}
</style>
