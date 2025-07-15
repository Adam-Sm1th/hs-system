<script setup lang="ts">
import { ref, watch, onMounted, onBeforeUnmount } from 'vue';
import * as echarts from 'echarts'; // ✅ 用于运行时
import type { ECharts, EChartsOption } from 'echarts'; // ✅ 类型用 type-only 导入

// 定义 props
interface Props {
  width?: string;
  height?: string;
  option: EChartsOption;
}

const props = withDefaults(defineProps<Props>(), {
  width: '100%',
  height: '100%',
  option: () => ({})
});

const myChartsRef = ref<HTMLDivElement>();
let myChart: ECharts | undefined;
let timer: number | undefined;

// 初始化 ECharts
const initChart = (): void => {
  if (myChart) {
    myChart.dispose();
  }
  if (myChartsRef.value) {
    myChart = echarts.init(myChartsRef.value);
    myChart.setOption(props.option, true);
  }
};

// resize 图表
const resizeChart = (): void => {
  timer = window.setTimeout(() => {
    myChart?.resize();
  }, 500);
};

onMounted(() => {
  initChart();
  window.addEventListener('resize', resizeChart);
});

onBeforeUnmount(() => {
  window.removeEventListener('resize', resizeChart);
  if (timer) {
    clearTimeout(timer);
    timer = undefined;
  }
});

// 监听 option 改变重新渲染图表
watch(
  () => props.option,
  () => {
    initChart();
  },
  {
    deep: true
  }
);
</script>

<template>
  <div ref="myChartsRef" :style="{ height: props.height, width: props.width }" />
</template>
