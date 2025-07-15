<template>
  <!-- 顶部统计概览 -->
  <el-row style="margin-bottom: 20px;">
    <el-card
      style="width: 100%; border-radius: 16px; box-shadow: 0 10px 30px rgba(0, 0, 0, 0.08); overflow: hidden; padding: 0;">
      <div style="display: flex; height: 100%;">
        <!-- 左边色块和图标 -->
        <div
          style="width: 120px; background: linear-gradient(135deg, #409EFF, #66b1ff); display: flex; align-items: center; justify-content: center;">
          <el-icon size="48" color="#fff"><i class="el-icon-office-building" /></el-icon>
        </div>

        <!-- 右边信息部分 -->
        <div style="flex: 1; padding: 20px;">
          <!-- 标题 -->
          <div style="font-size: 32px; font-weight: 600; color: #2c3e50; margin-bottom: 10px;">
            {{ departmentStore.departmentData.deptName }}
          </div>

          <!-- 信息项 -->
          <div style="font-size: 14px; color: #606266; line-height: 1.8;">
            <div><span style="font-weight: 500;">ID：</span><span style="color: #333;">{{
              departmentStore.departmentData.deptId }}</span></div>
            <div><span style="font-weight: 500;">创建时间：</span><span style="color: #333;">{{
              formattedCreateTime }}</span></div>

            <div style="margin-top: 10px;">
              <el-descriptions title="简介：" :column="1" size="small">
                <el-descriptions-item>
                  <div class="userinfo-description">
                    {{ departmentStore.departmentData.deptInfo }}
                  </div>
                </el-descriptions-item>
              </el-descriptions>
            </div>
          </div>
        </div>
      </div>
    </el-card>


    <el-col :span="24">
      <!-- Card for Statistics -->
      <el-card shadow="hover" body-style="padding: 10px; border-radius: 16px;" v-loading="loading">
        <div
          style="background: #ffffff; padding: 10px; border-radius: 12px; margin-bottom: 10px; box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1), 0 -2px 4px rgba(0, 0, 0, 0.1); rgba(0, 0, 0, 0.1);">
          <div style="font-size: 20px; font-weight: bold; color: #333;">仪表盘</div>
          <el-row>
            <el-col :span="6" style="text-align: center;">
              <el-statistic title="近一月排班总数" :value="outputsumSche" style="font-weight: bold;" />
            </el-col>
            <el-col :span="6" style="text-align: center;">
              <el-statistic title="平均达成率" :value="outputavgComplt.toFixed(2)" :suffix="'%'" style="font-weight: bold;" />
            </el-col>
            <el-col :span="6" style="text-align: center;">
              <el-statistic :value="outputavgSumPeople" style="font-weight: bold;">
                <template #title>
                  <div style="display: inline-flex; align-items: center">
                    科室人员总数
                    <el-tooltip effect="dark" content="点击查看详情" placement="top">
                      <el-icon style="margin-left: 4px; cursor: pointer;" :size="12" @click="openDraw">
                        <QuestionFilled />
                      </el-icon>

                    </el-tooltip>
                  </div>
                </template>
              </el-statistic>
            </el-col>
            <el-col :span="6" style="text-align: center;">
              <el-statistic title="当前日期" :value="currentDate" style="font-weight: bold;" />
            </el-col>
          </el-row>
        </div>
      </el-card>


      <!-- Card for Echarts -->
      <el-card shadow="hover" body-style="padding: 10px; border-radius: 16px;" v-loading="loading">

        <Echarts :option="scheCompltOption"
          style="width: 100%; height: 400px; border-radius: 8px; box-shadow: 0 8px 16px rgba(0, 0, 0, 0.1);" />
      </el-card>
    </el-col>

  </el-row>

  <!-- 第一排图表 -->
  <el-row :gutter="20">
    <el-col :span="12">
      <div style="margin-bottom: 6px;">📊 调度总览</div>
      <el-card shadow="hover" body-style="padding: 10px;" v-loading="loading">
        <Echarts :option="scheSumOption" style="width: 100%; height: 400px; border-radius: 8px;" />
      </el-card>
    </el-col>
    <el-col :span="12">
      <div style="margin-bottom: 6px;">📌 状态占比</div>
      <el-card shadow="hover" body-style="padding: 10px;" v-loading="loading">
        <Echarts :option="schePieOption" style="width: 100%; height: 400px; border-radius: 8px;" />
      </el-card>
    </el-col>
  </el-row>

  <!-- 第二排图表 -->
  <el-row :gutter="20" style="margin-top: 20px;">
    <el-col :span="12">
      <div style="margin-bottom: 6px;">📦 用户任务分布</div>
      <el-card shadow="hover" body-style="padding: 10px;" v-loading="loading">
        <Echarts :option="scheUserOption" style="width: 100%; height: 400px; border-radius: 8px;" />
      </el-card>
    </el-col>
    <el-col :span="12">
      <div style="margin-bottom: 6px;">⏱️ 用户工时趋势</div>
      <el-card shadow="hover" body-style="padding: 10px;" v-loading="loading">
        <Echarts :option="scheHoursOption" style="width: 100%; height: 400px; border-radius: 8px;" />
      </el-card>
    </el-col>
  </el-row>

  <el-drawer v-model="drawer" :with-header="false">
    <h3 style="margin-bottom: 10px; font-weight: bold;">医生列表</h3>
    <el-scrollbar style="height: 40%; margin-bottom: 10px;">
      <el-table :data="allDoctors" stripe style="width: 100%">
        <el-table-column prop="userId" label="医生编号" align="center" />
        <el-table-column prop="userName" label="医生名称" align="center" />
        <el-table-column label="医生头像" align="center">
          <template #default="scope">
            <el-avatar :size="50" :src="scope.row.userAvatar" />
          </template>
        </el-table-column>
      </el-table>
    </el-scrollbar>
    <h3 style="margin-bottom: 10px; font-weight: bold;">护士列表</h3>
    <el-scrollbar style="height: 40%;">
      <el-table :data="allNurses" stripe style="width: 100%">
        <el-table-column prop="userId" label="护士编号" align="center" />
        <el-table-column prop="userName" label="护士名称" align="center" />
        <el-table-column label="护士头像" align="center">
          <template #default="scope">
            <el-avatar :size="50" :src="scope.row.userAvatar" />
          </template>
        </el-table-column>
      </el-table>
    </el-scrollbar>
  </el-drawer>
</template>





<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import Echarts from '@/components/ReEcharts/index.vue'
import * as ScheRequest from '@/api/sche'
import { useDepartmentStore } from '@/stores/store'
import type { EChartsOption } from 'echarts'
import type { EchartVO } from '@/stores/interface/EchartInterface'
import { useTransition } from '@vueuse/core'
import { Message, Connection, Setting, Fold, Expand, Finished, Warning, QuestionFilled } from '@element-plus/icons-vue'
import type { UserVO } from '@/stores/interface/UserInterface';
import * as UserRequest from '@/api/user.ts'
import * as ScheInterface from '@/stores/interface/ScheInterface.ts';

const loading = ref(true)
const drawer = ref(false)
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
const outputavgSumPeople = useTransition(avgSumPeople, {
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
  const response1 = await ScheRequest.scheGetStatusService(departmentStore.departmentData.deptId)
  const echartData1: EchartVO = response1.data

  const response2 = await ScheRequest.scheGetPieStatusService(departmentStore.departmentData.deptId)
  const echartData2: EchartVO = response2.data

  const response3 = await ScheRequest.scheGetUserStatusService(departmentStore.departmentData.deptId)
  const echartData3: EchartVO = response3.data

  const response4 = await ScheRequest.scheGetCompltStatusService(departmentStore.departmentData.deptId)
  const echartData4: EchartVO = response4.data


  const response5 = await ScheRequest.scheGetHoursStatusService(departmentStore.departmentData.deptId)
  const echartData5: EchartVO = response5.data

  const response6 = await ScheRequest.scheGetDashboardStatusService(departmentStore.departmentData.deptId)
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


  scheUserOption.value = {
    title: {
      text: echartData3.title,
      left: 'center'
    },
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        // Use axis to trigger tooltip
        type: 'shadow' // 'shadow' as default; can also be 'line' or 'shadow'
      }
    },
    legend: {
      orient: 'vertical',
      left: 'right'
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'value'
    },
    yAxis: {
      type: 'category',
      data: echartData3.xAxis
    },
    series: echartData3.xAxis.map((name, index) => ({
      name: echartData3.seriesNames[index],
      type: 'bar',
      stack: 'total',
      label: {
        show: true
      },
      emphasis: {
        focus: 'series'
      },
      data: echartData3.yAxis[index]
    }))
  };

  scheCompltOption.value = {
    title: {
      text: echartData4.title,
      left: 'center'
    },
    tooltip: {
      trigger: 'axis'
    },
    legend: {
      data: echartData4.seriesNames,
      orient: 'vertical',
      left: 'right'
    },
    xAxis: {
      type: 'category',
      data: echartData4.xAxis
    },
    yAxis: {
      type: 'value'
    },
    series: echartData4.seriesNames.map((name, index) => ({
      name,
      type: 'line',
      smooth: true,
      data: echartData4.yAxis[index]
    }))
  }


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
