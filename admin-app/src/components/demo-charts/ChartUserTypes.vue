<script setup lang="ts">
import type {ECOption} from '@/plugins/echarts'
import {dashboardApi} from "@/utils/api.ts";

dashboardApi.usersStats()
  .then(res => {
    let data = {
      admins: res.data.admins || 0,
      volunteers: res.data.volunteers || 0,
      total: res.data.total || 0,
    };
    option.value.series = [
      {
        name: 'User types',
        type: 'pie',
        roseType: 'radius',
        radius: [15, 95],
        center: ['50%', '38%'],
        data: [
          {value: data.admins, name: 'Admins'},
          {value: data.volunteers, name: 'Volunteers'},
          {value: data.total - data.admins - data.volunteers, name: 'Other'},
        ],
        animationEasing: 'cubicInOut',
      },
    ];
  })

const option = ref<ECOption>({
  backgroundColor: 'transparent',
  tooltip: {
    trigger: 'item',
  },
  legend: {
    left: 'center',
    bottom: '10',
    data: ['Admins', 'Volunteers', 'Other'],
  },
  series: [
    {
      name: 'User types',
      type: 'pie',
      roseType: 'radius',
      radius: [15, 95],
      center: ['50%', '38%'],
      data: [
        {value: 0, name: 'Admins'},
        {value: 0, name: 'Volunteers'},
        {value: 0, name: 'Other'},
      ],
      animationEasing: 'cubicInOut',
    },
  ],
})
</script>

<template>
  <v-chart :option="option" autoresize ref="chart"/>
</template>
