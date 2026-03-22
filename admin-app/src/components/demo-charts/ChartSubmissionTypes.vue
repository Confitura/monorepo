<script setup lang="ts">
import type {ECOption} from '@/plugins/echarts'
import {dashboardApi} from "@/utils/api.ts";

dashboardApi.submissionStats()
  .then(res => {
    let data = {
      workshops: res.data.workshops || 0,
      presentations: res.data.presentations || 0,
    };
    option.value.series = [
      {
        name: 'Submitted workshops/presentations count',
        type: 'pie',
        roseType: 'radius',
        radius: [15, 95],
        center: ['50%', '38%'],
        data: [
          {value: data.workshops, name: 'workshops'},
          {value: data.presentations, name: 'presentations'},
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
    data: ['workshops', 'presentations'],
  },
  series: [
    {
      name: 'Submitted workshops/presentations count',
      type: 'pie',
      roseType: 'radius',
      radius: [15, 95],
      center: ['50%', '38%'],
      data: [
        {value: 0, name: 'workshops'},
        {value: 0, name: 'presentations'},
      ],
      animationEasing: 'cubicInOut',
    },
  ],
})
</script>

<template>
  <v-chart :option="option" autoresize/>
</template>
