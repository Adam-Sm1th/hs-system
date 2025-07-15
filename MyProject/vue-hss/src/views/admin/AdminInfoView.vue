<template>
  <el-container class="admin-dashboard">
    <el-main>
      <!-- 医院概况卡片 -->
      <el-card class="hospital-overview-card">
        <template #header>
          <div class="card-header">
            <el-icon>
              <Platform />
            </el-icon>
            <span>医院概况</span>
          </div>
        </template>

        <el-descriptions :column="1" size="large" border>
          <el-descriptions-item label="医院名称">
            <el-tag type="info">温大医院</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="医院地址">
            <el-text type="info">浙江省温州市瓯海区中心西路9号</el-text>
          </el-descriptions-item>
          <el-descriptions-item label="联系电话">
            <el-text type="primary">0577 8659 8000</el-text>
          </el-descriptions-item>
          <el-descriptions-item label="医院简介">
            <el-text>致力于提供优质的医疗服务，拥有先进的医疗设备和技术。</el-text>
          </el-descriptions-item>
        </el-descriptions>
      </el-card>

      <!-- 系统统计数据 -->
      <el-card class="system-stats-card">
        <template #header>
          <div class="card-header">
            <el-icon>
              <DataAnalysis />
            </el-icon>
            <span>系统统计数据</span>
          </div>
        </template>

        <el-row :gutter="20">
          <el-col :span="6">
            <el-card shadow="hover" class="stat-item">
              <div class="stat-content">
                <div class="stat-icon">🏥</div>
                <el-statistic title="科室总数" :value=dashboard.deptSum />
              </div>
            </el-card>
          </el-col>
          <el-col :span="6">
            <el-card shadow="hover" class="stat-item">
              <div class="stat-content">
                <div class="stat-icon">👥</div>
                <el-statistic title="人员总数" :value=dashboard.userSum />
              </div>
            </el-card>
          </el-col>
          <el-col :span="6">
            <el-card shadow="hover" class="stat-item">
              <div class="stat-content">
                <div class="stat-icon">👨‍⚕️</div>
                <el-statistic title="医生总数" :value=dashboard.doctorSum />
              </div>
            </el-card>
          </el-col>
          <el-col :span="6">
            <el-card shadow="hover" class="stat-item">
              <div class="stat-content">
                <div class="stat-icon">👩‍⚕️</div>
                <el-statistic title="护士总数" :value=dashboard.nurseSum />
              </div>
            </el-card>
          </el-col>
        </el-row>
      </el-card>
    </el-main>
  </el-container>
</template>

<script setup lang="ts">
import * as DepartmentInterface from '@/stores/interface/DepartmentInterface.ts'
import { Avatar, Edit, Delete, UserFilled } from '@element-plus/icons-vue'
import { ref, onMounted, reactive } from 'vue'
import * as ScheInterface from '@/stores/interface/ScheInterface.ts';
import { shiftStyleMap } from '@/stores/store';
import * as ScheRequest from '@/api/sche.ts'
import * as ShiftRequest from '@/api/shift.ts'
import * as UserRequest from '@/api/user.ts'
import * as DeptRequest from '@/api/dept.ts'
import type { ShiftVO } from '@/stores/interface/ShiftInterface';
import type { UserVO } from '@/stores/interface/UserInterface';
import { userTypeMap, scheStatusMap } from '@/stores/store.ts';
import { useDepartmentStore } from '@/stores/store.ts'
import { ElDrawer, ElMessageBox } from 'element-plus'
import type { FormRules, FormInstance } from 'element-plus'
import { ElMessage } from 'element-plus'

const dashboard = ref({
  deptSum: 0,
  userSum: 0,
  doctorSum: 0,
  nurseSum: 0
})

onMounted(async () => {
  const req = await UserRequest.userDashBoardService()
  dashboard.value.deptSum = req.data[0]
  dashboard.value.doctorSum = req.data[2]
  dashboard.value.nurseSum = req.data[3]
  dashboard.value.userSum = req.data[1]

})


</script>

<style scoped>  .admin-dashboard {
    background-color: #f5f7fa;
    padding: 20px;
  }

  .hospital-overview-card,
  .system-stats-card {
    margin-bottom: 20px;
    border-radius: 12px;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
  }

  .card-header {
    display: flex;
    align-items: center;
    gap: 10px;
    font-weight: bold;
    color: #409EFF;
  }

  .card-header .el-icon {
    font-size: 20px;
  }

  .stat-item {
    text-align: center;
    transition: all 0.3s ease;
  }

  .stat-item:hover {
    transform: translateY(-10px);
  }

  .stat-content {
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 10px;
  }

  .stat-icon {
    font-size: 36px;
    margin-bottom: 10px;
  }

  .el-statistic {
    width: 100%;
  }
</style>
