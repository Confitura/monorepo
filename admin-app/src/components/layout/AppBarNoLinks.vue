<script setup lang="ts">
import {useToggle, useDark} from '@vueuse/core'

const theme = useTheme()
const {user} = storeToRefs(useAuthStore())
const route = useRoute()
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
    <template #prepend>
      <v-list>
        <v-list-item class="pa-1" href="https://confitura.pl" link>
          <template #prepend>
            <v-icon
              icon="custom:confitura"
              size="x-large"
              class="drawer-header-icon"
              color="primary"
            />
          </template>
          <v-list-item-title
            class="text-h5 font-weight-bold"
            style="line-height: 2rem; text-overflow: clip"
          >
            <span class="text-primary">Welcome</span>
          </v-list-item-title>
        </v-list-item>
      </v-list>
    </template>
    <v-spacer/>

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

.v-list {
  background: transparent !important;
}

</style>
