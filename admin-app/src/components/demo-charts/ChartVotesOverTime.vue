<script setup lang="ts">
import type {ECOption} from '@/plugins/echarts'

const props = defineProps<{ source: unknown[] }>()

const option = computed<ECOption>(() => ({
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
  dataset: {source: props.source as any},
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
}))
</script>

<template>
  <v-chart :option="option" autoresize/>
</template>
