<script setup lang="ts">
import ChartUserTypes from "@/components/demo-charts/ChartUserTypes.vue";

import {onMounted} from 'vue';
import {dashboardApi} from "@/utils/api.ts";

definePage({
  meta: {
    icon: 'mdi-monitor-dashboard',
    title: 'Dashboard',
    drawerIndex: 1,
    requiresAuth: true,
    requiresAdmin: true,
  },
})


async function loadStats() {
  const {data: users} = await dashboardApi.usersStats();
  const {data: submissions} = await dashboardApi.submissionStats();
  stats.value = [
    {
      icon: 'mdi-account',
      title: 'Users',
      value: users.total!,
      unit: '',
      color: 'primary',
      caption: 'Registered',
    },
    {
      icon: 'mdi-presentation',
      title: 'Submissions',
      value: submissions.total!,
      color: 'primary',
      caption: `Workshops: ${submissions.workshops}, prestations: ${submissions.presentations}`,
    }]

}

onMounted(() => {
  loadStats();
});
const stats = ref([
  {
    icon: 'mdi-account',
    title: 'Users',
    value: 0,
    unit: '',
    color: 'primary',
    caption: 'Registered',
  },
  {
    icon: 'mdi-presentation',
    title: 'Submissions',
    value: 0,
    color: 'primary',
    caption: 'Workshops: 0, prestations: 0',
  },
  // {
  //   icon: 'mdi-send',
  //   title: 'Requests',
  //   value: 1238,
  //   color: 'warning',
  //   caption: 'Limit: 1320',
  // },
  // {
  //   icon: 'mdi-bell',
  //   title: 'Messages',
  //   value: 9042,
  //   color: 'primary',
  //   caption: 'Warnings: 300, erros: 47',
  // },
  // {
  //   icon: 'mdi-github',
  //   title: 'Github Stars',
  //   value: NaN,
  //   color: 'grey',
  //   caption: 'API has no response',
  // },
  // {
  //   icon: 'mdi-currency-cny',
  //   title: 'Total Fee',
  //   value: 2300,
  //   unit: '￥',
  //   color: 'error',
  //   caption: 'Upper Limit: 2000 ￥',
  // },
])
</script>

<template>
  <v-container fluid>
    <v-row>
      <v-col
        v-for="stat in stats"
        :key="stat.title"
        cols="12"
        sm="6"
        md="4"
        lg="2"
      >
        <StatsCard
          :title="stat.title"
          :unit="stat.unit"
          :color="stat.color"
          :icon="stat.icon"
          :value="stat.value"
        >
          <template #footer>
            {{ stat.caption }}
          </template>
        </StatsCard>
      </v-col>
    </v-row>
    <v-row>
      <!--      <v-col cols="12" md="6" lg="12">-->
      <!--        <v-card class="pa-2">-->
      <!--          <ChartLine />-->
      <!--        </v-card>-->
      <!--      </v-col>-->
      <!--      <v-col cols="12" md="6" lg="4">-->
      <!--        <v-card class="pa-2">-->
      <!--          <ChartRadar />-->
      <!--        </v-card>-->
      <!--      </v-col>-->
      <v-col cols="12" md="6" lg="4">
        <v-card class="pa-2">
          <ChartUserTypes/>
        </v-card>
      </v-col>
      <v-col cols="12" md="6" lg="4">
        <v-card class="pa-2">
          <ChartSubmissionTypes/>
        </v-card>
      </v-col>
      <!--      <v-col cols="12" md="6" lg="4">-->
      <!--        <v-card class="pa-2">-->
      <!--          <ChartBar />-->
      <!--        </v-card>-->
      <!--      </v-col>-->
    </v-row>
  </v-container>
</template>

<style scoped>

</style>
