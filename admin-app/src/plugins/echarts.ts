import * as echarts from 'echarts/core'
import VChart from 'vue-echarts'
import type { Plugin } from 'vue'

import {
  LineChart,
  type LineSeriesOption,
  EffectScatterChart,
  type EffectScatterSeriesOption,
  ScatterChart,
  type ScatterSeriesOption,
  PieChart,
  type PieSeriesOption,
} from 'echarts/charts'
import { CanvasRenderer } from 'echarts/renderers'
import {
  DataZoomComponent,
  type DataZoomComponentOption,
  LegendComponent,
  type LegendComponentOption,
  TooltipComponent,
  type TooltipComponentOption,
  ToolboxComponent,
  type ToolboxComponentOption,
  GridComponent,
  type GridComponentOption,
  TitleComponent,
  type TitleComponentOption,
  MarkPointComponent,
  type MarkPointComponentOption,
  DatasetComponent,
  type DatasetComponentOption,
} from 'echarts/components'

echarts.use([
  LineChart,
  PieChart,
  EffectScatterChart,
  ScatterChart,
  CanvasRenderer,
  DataZoomComponent,
  LegendComponent,
  TooltipComponent,
  ToolboxComponent,
  GridComponent,
  TitleComponent,
  MarkPointComponent,
  DatasetComponent,
])

export type ECOption = echarts.ComposeOption<
  | LineSeriesOption
  | PieSeriesOption
  | EffectScatterSeriesOption
  | ScatterSeriesOption
  | DataZoomComponentOption
  | LegendComponentOption
  | TooltipComponentOption
  | ToolboxComponentOption
  | GridComponentOption
  | TitleComponentOption
  | MarkPointComponentOption
  | DatasetComponentOption
>

export default {
  install(app) {
    app.component('VChart', VChart)
  },
} satisfies Plugin
