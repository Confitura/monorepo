<script setup lang="ts">
import type {ECOption} from '@/plugins/echarts'
import {dashboardApi} from '@/utils/api.ts'

const option = ref<ECOption>({
  backgroundColor: 'transparent',
  title: {
    text: 'Votes over time',
    left: 'center',
    textStyle: {fontSize: 14},
  },
  tooltip: {
    trigger: 'axis',
  },
  grid: {
    top: 40,
    left: '2%',
    right: '3%',
    bottom: '3%',
    containLabel: true,
  },
  xAxis: {
    type: 'time',
  },
  yAxis: {
    type: 'value',
  },
  dataset: {source: []},
  series: [
    {
      name: 'votes',
      type: 'line',
      showSymbol: false,
      encode: {x: 'date', y: 'total'},
      lineStyle: {width: 3},
      areaStyle: {},
    },
  ],
})

dashboardApi.votes()
  .then(res => {
    option.value.dataset = {source: res.data as any}
  })
  .catch(e => console.error(e))
</script>

<template>
  <v-chart :option="option" autoresize/>
</template>
