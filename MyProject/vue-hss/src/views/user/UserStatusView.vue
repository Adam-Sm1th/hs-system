<template>
  <!-- 顶部统计概览 -->
  <el-row style="margin-bottom: 20px;">
    <el-col :span="24">
      <!-- Card for Statistics -->
      <el-card shadow="hover" body-style="padding: 10px; border-radius: 16px;" v-loading="loading">
        <div
          style="background: #ffffff; padding: 10px; border-radius: 12px; margin-bottom: 10px; box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1), 0 -2px 4px rgba(0, 0, 0, 0.1); rgba(0, 0, 0, 0.1);">
          <div style="font-size: 20px; font-weight: bold; color: #333;">仪表盘</div>
          <el-row>
            <el-col :span="8" style="text-align: center;">
              <el-statistic title="近一月个人排班总数" :value="outputsumSche" style="font-weight: bold;" />
            </el-col>
            <el-col :span="8" style="text-align: center;">
              <el-statistic title="近一月个人总工时" :value="outputavgComplt.toFixed(2)" :suffix="'时'"
                style="font-weight: bold;" />
            </el-col>

            <el-col :span="8" style="text-align: center;">
              <el-statistic title="当前日期" :value="currentDate" style="font-weight: bold;" />
            </el-col>
          </el-row>
        </div>
      </el-card>

      <!-- Card for Echarts -->
      <el-card shadow="hover" body-style="padding: 10px;" v-loading="loading">
        <Echarts :option="scheHoursOption" style="width: 100%; height: 400px; border-radius: 8px;" />
      </el-card>
    </el-col>

  </el-row>

  <!-- 第一排图表 -->
  <el-row :gutter="20">
    <el-col :span="12">
      <el-card shadow="hover" body-style="padding: 10px;" v-loading="loading">
        <Echarts :option="scheSumOption" style="width: 100%; height: 400px; border-radius: 8px;" />
      </el-card>
    </el-col>
    <el-col :span="12">
      <el-card shadow="hover" body-style="padding: 10px;" v-loading="loading">
        <Echarts :option="schePieOption" style="width: 100%; height: 400px; border-radius: 8px;" />
      </el-card>
    </el-col>
  </el-row>
</template>





<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import Echarts from '@/components/ReEcharts/index.vue'
import * as ScheRequest from '@/api/sche'
import { useDepartmentStore, useUserStore } from '@/stores/store'
import type { EChartsOption } from 'echarts'
import type { EchartVO } from '@/stores/interface/EchartInterface'
import { useTransition } from '@vueuse/core'
import { Message, Connection, Setting, Fold, Expand, Finished, Warning, QuestionFilled } from '@element-plus/icons-vue'
import type { UserVO } from '@/stores/interface/UserInterface';
import * as UserRequest from '@/api/user.ts'
import * as ScheInterface from '@/stores/interface/ScheInterface.ts';

const loading = ref(true)
const drawer = ref(false)
const userStore = useUserStore()
const departmentStore = useDepartmentStore()
const scheSumOption = ref<EChartsOption>();
const schePieOption = ref<EChartsOption>();
const scheUserOption = ref<EChartsOption>();
const scheCompltOption = ref<EChartsOption>();
const scheHoursOption = ref<EChartsOption>();
const currentDate = ref('')
const sumSche = ref(0);
const avgComplt = ref(0)
const avgSumPeople = ref(0)
const allDoctors = ref<UserVO[]>([])
const allNurses = ref<UserVO[]>([])

const outputsumSche = useTransition(sumSche, {
  duration: 1500,
})
const outputavgComplt = useTransition(avgComplt, {
  duration: 1500,
})

const openDraw = () => {
  drawer.value = !drawer.value;
}
// 格式化当前日期为 'YYYY-MM-DD'
const formatCurrentDate = () => {
  const now = new Date()
  const year = now.getFullYear()
  const month = (now.getMonth() + 1).toString().padStart(2, '0') // 月份从0开始，所以加1
  const day = now.getDate().toString().padStart(2, '0')

  return `${year}-${month}-${day}`
}
const formattedCreateTime = computed(() => {
  const raw = departmentStore.departmentData.deptCreatetime
  if (!raw) return ''
  const dateObj = new Date(raw)
  const year = dateObj.getFullYear()
  const month = (dateObj.getMonth() + 1).toString().padStart(2, '0')
  const day = dateObj.getDate().toString().padStart(2, '0')
  return `${year}-${month}-${day}`
})

const currentTime = ref('')

// 格式化当前时间
const formatCurrentTime = () => {
  const now = new Date()
  const hours = now.getHours().toString().padStart(2, '0')
  const minutes = now.getMinutes().toString().padStart(2, '0')
  const seconds = now.getSeconds().toString().padStart(2, '0')

  return `${hours}:${minutes}:${seconds}`
}

const formatDate = (row: ScheInterface.SchedulingVO) => {
  if (!row.scheDate) return '';
  const dateObj = new Date(row.scheDate);
  const year = dateObj.getFullYear();
  const month = (dateObj.getMonth() + 1).toString().padStart(2, '0');
  const day = dateObj.getDate().toString().padStart(2, '0');
  return `${year}-${month}-${day}`;
};

onMounted(async () => {
  setInterval(() => {
    currentDate.value = formatCurrentDate()
  }, 1000)

  // 初始化时显示当前日期
  currentDate.value = formatCurrentDate()
  setInterval(() => {
    currentTime.value = formatCurrentTime()
  }, 1000)

  const doctorlist = await UserRequest.userGetAllDoctorService(departmentStore.departmentData.deptId);
  const nurselist = await UserRequest.userGetAllNurseService(departmentStore.departmentData.deptId);
  allDoctors.value = doctorlist.data;
  allNurses.value = nurselist.data;

  // 初始化时显示当前时间
  currentTime.value = formatCurrentTime()
  const response1 = await ScheRequest.scheGetOneUserStatusService(userStore.userData.userId)
  const echartData1: EchartVO = response1.data

  const response2 = await ScheRequest.scheGetUserPieStatusService(userStore.userData.userId)
  const echartData2: EchartVO = response2.data

  const response5 = await ScheRequest.scheGetUserHoursStatusService(userStore.userData.userId)
  const echartData5: EchartVO = response5.data

  const response6 = await ScheRequest.scheGetUserDashboardStatusService(userStore.userData.userId)
  loading.value = false;
  sumSche.value = response6.data[0];
  avgComplt.value = response6.data[1];
  avgSumPeople.value = response6.data[2];


  // 构造 EChartsOption
  scheSumOption.value = {
    title: {
      text: echartData1.title,
      left: 'center'
    },
    tooltip: {
      trigger: 'axis'
    },
    legend: {
      data: echartData1.seriesNames,
      orient: 'vertical',
      left: 'right'
    },
    xAxis: {
      type: 'category',
      data: echartData1.xAxis
    },
    yAxis: {
      type: 'value'
    },
    series: echartData1.seriesNames.map((name, index) => ({
      name,
      type: 'line',
      smooth: true,
      data: echartData1.yAxis[index]
    }))
  }

  schePieOption.value = {
    title: {
      text: echartData2.title,
      left: 'center'
    },
    tooltip: {
      trigger: 'item'
    },
    legend: {
      orient: 'vertical',
      left: 'right'
    },
    series: [
      {
        name: 'Access From',
        type: 'pie',
        radius: '50%',
        data: echartData2.seriesNames.map((name, index) => ({
          value: echartData2.yAxis[0][index],
          name: name
        })),
        emphasis: {
          itemStyle: {
            shadowBlur: 10,
            shadowOffsetX: 0,
            shadowColor: 'rgba(0, 0, 0, 0.5)'
          }
        }
      }
    ]
  };




  scheHoursOption.value = {
    title: {
      text: echartData5.title,
      left: 'center'
    },
    tooltip: {
      trigger: 'axis'
    },
    legend: {
      data: echartData5.seriesNames,
      orient: 'vertical',
      left: 'right'
    },
    xAxis: {
      type: 'category',
      data: echartData5.xAxis
    },
    yAxis: {
      type: 'value'
    },
    series: echartData5.seriesNames.map((name, index) => ({
      name,
      type: 'line',
      smooth: true,
      data: echartData5.yAxis[index]
    }))
  }

})
</script>

<style scoped>
.el-statistic {
  --el-statistic-content-font-size: 28px;
}

.userinfo-description {
  padding: 16px;
  background-color: #f8f8f8;
  border-radius: 10px;
  color: #666;
  font-size: 13px;
  line-height: 1.7;
}
</style>
