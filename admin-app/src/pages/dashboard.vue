<script setup lang="ts">
import ChartUserTypes from "@/components/demo-charts/ChartUserTypes.vue";

import {onMounted, ref, computed} from 'vue';
import {dashboardApi, adminTasksApi} from "@/utils/api.ts";

definePage({
  meta: {
    icon: 'mdi-monitor-dashboard',
    title: 'Dashboard',
    drawerIndex: 1,
    requiresAuth: true,
    requiresAdmin: true,
  },
})

// Last webpage update date (fetched from backend)
const lastWebpageUpdate = ref<Date | null>(null);

const formattedLastUpdate = computed(() => {
  if (!lastWebpageUpdate.value) return 'N/A';
  try {
    return lastWebpageUpdate.value.toLocaleString();
  } catch (e) {
    return String(lastWebpageUpdate.value);
  }
});

async function loadLastWebpageDump() {
  try {
    const { data } = await adminTasksApi.getLastWebpageDump();
    lastWebpageUpdate.value = data.lastDumpAt ? new Date(data.lastDumpAt) : null;
  } catch (e) {
    console.error('Failed to load last webpage dump info', e);
  }
}

async function triggerWebpageUpdate() {
  try {
    await adminTasksApi.triggerWebpageDump();
    await loadLastWebpageDump();
  } catch (e) {
    console.error('Failed to trigger webpage update', e);
  }
}

async function loadStats() {
  const {data: users} = await dashboardApi.usersStats();
  const {data: submissions} = await dashboardApi.submissionStats();
  const {data: newsletter} = await dashboardApi.newsletterStat();

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
    }, {
      icon: 'mdi-mail',
      title: 'Newsletter',
      value: newsletter.subscribersCount || 0,
      color: 'primary',
      caption: 'subscribers',
    }]

}

onMounted(() => {
  loadStats();
  loadLastWebpageDump();
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
  }, {
    icon: 'mdi-presentation',
    title: 'Newsletter subscribers',
    value: 0,
    color: 'primary',
    caption: '',
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
const v4pToken = ref<string | null>(null);
const displayV4PToken = computed(() => v4pToken.value ?? 'N/A');
const copySnackbar = ref(false);

onMounted(() => {
  v4pToken.value = localStorage.getItem('v4p-token');
});

async function copyV4PToken() {
  const token = v4pToken.value;
  if (!token) return;
  try {
    await navigator.clipboard.writeText(token);
    copySnackbar.value = true;
  } catch (e) {
    try {
      const ta = document.createElement('textarea');
      ta.value = token;
      ta.style.position = 'fixed';
      ta.style.opacity = '0';
      document.body.appendChild(ta);
      ta.focus();
      ta.select();
      document.execCommand('copy');
      document.body.removeChild(ta);
      copySnackbar.value = true;
    } catch (e2) {
      console.error('Failed to copy token', e2);
    }
  }
}
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
      <v-col cols="12" md="6" lg="4">
        <v-card class="pa-4 d-flex flex-column justify-space-between">
          <div>
            <div class="text-subtitle-1 font-weight-medium mb-2">Last webpage update</div>
            <div class="text-body-2">{{ formattedLastUpdate }}</div>
          </div>
          <div class="mt-4">
            <v-btn color="primary" @click="triggerWebpageUpdate" prepend-icon="mdi-reload">
              Trigger update
            </v-btn>
          </div>
        </v-card>
      </v-col>
      <v-col cols="12" md="6" lg="4">
        <v-card class="pa-4">
          <div class="text-subtitle-1 font-weight-medium mb-2">Vote-for-papers token</div>
          <div
            class="text-body-2"
            style="word-break: break-all; font-family: monospace; cursor: pointer;"
            @click="copyV4PToken"
            title="Click to copy to clipboard"
          >{{ displayV4PToken }}</div>
          <v-snackbar v-model="copySnackbar" timeout="2000" color="success">
            Token copied to clipboard
          </v-snackbar>
          <div class="mt-4">
            <v-btn color="primary" to="/vote-for-papers" prepend-icon="mdi-arrow-right-bold">
              Go to Vote for Papers
            </v-btn>
          </div>
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
.v-card:not(.stats-card) {
  height: 340px;
}
</style>
