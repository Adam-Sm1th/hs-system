<template>
  <el-container class="layout-container">
    <el-aside width="auto">
      <el-scrollbar>
        <el-menu :default-active="$route.path" :router="true" :collapse="isCollapse" :default-openeds="['audit']">
          <div v-if="!isCollapse"
            style="display: flex; align-items: center; justify-content: center; padding-top: 30px; padding-right: 20px; padding-left: 20px;">
            <img style="width: 35px; height: 35px; margin-right: 10px; border-radius: 20%;" src="../../pic/myLogo.png"
              alt="Logo" />
            <span style="font-weight: bold; font-family: Arial, sans-serif; font-size: 18px;">HS-System</span>

          </div>

          <el-menu-item v-if="isCollapse"
            style="display: flex; justify-content: center; align-items: center; background-color: transparent; border: none;">
            <img style="width: 30px; height: 30px; border-radius: 20%; margin-top: 25px;" src="../../pic/myLogo.png"
              @click="toggleCollapse" />
          </el-menu-item>

          <el-divider />

          <el-menu-item index="/admin/admininfo">
            <el-icon>
              <TrendCharts />
            </el-icon>
            <template #title>
              医院简介
            </template>
          </el-menu-item>

          <el-menu-item index="/admin/admindept">
            <el-icon>
              <EditPen />
            </el-icon>
            <template #title>
              科室管理
            </template>
          </el-menu-item>

          <el-menu-item index="/admin/adminuser">
            <el-icon>
              <SetUp />
            </el-icon>
            <template #title>
              人员管理
            </template>
          </el-menu-item>

          <el-menu-item @click="toggleCollapse">
            <el-icon>
              <Fold v-if="!isCollapse" />
              <Expand v-else />
            </el-icon>
            <template #title>
              <span v-if="!isCollapse">界面收纳</span>
              <span v-else>界面展开</span>
            </template>
          </el-menu-item>

        </el-menu>
      </el-scrollbar>
    </el-aside>

    <el-container>
      <el-header
        style="text-align: right; font-size: 12px; display: flex; justify-content: space-between; align-items: center;">
        <!-- 第一个 div 左对齐 -->
        <div style="text-align: left;" class="hospital-header">医院排班系统-超级管理</div>

        <!-- 第二个 div 右对齐 -->
        <div class="toolbar" style="text-align: right;">
          <span style="margin-right: 15px; font-size: 16px;">{{ userStore.userData.userName }}</span>
          <el-dropdown>
            <el-avatar :src="userStore.userData.userAvatar" style="cursor: pointer;" />
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item @click="logOut">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>
      <el-main>
        <RouterView />
      </el-main>
    </el-container>

  </el-container>
</template>

<script lang="ts" setup>
import { ref, onMounted } from 'vue'
import { Message, Connection, Setting, Fold, Expand, Finished, TrendCharts, UserFilled, SetUp, EditPen } from '@element-plus/icons-vue'
import type { Department } from '@/stores/interface/DepartmentInterface.ts';
import { useUserStore, useTokenStore, useDepartmentStore } from '@/stores/store.ts'
import router from "@/router";

const isCollapse = ref(false)
const userStore = useUserStore()
const tokenStore = useTokenStore()
const departmentStore = useDepartmentStore()
const logOut = () => {
  userStore.reset();
  tokenStore.reset();
  departmentStore.reset();
  router.push("/login")
}

const toggleCollapse = () => {
  isCollapse.value = !isCollapse.value
}

onMounted(() => {

})
</script>

<style scoped>
.layout-container .el-header {
  position: relative;
  color: var(--el-text-color-primary);
  border-bottom: 1px solid #e0e0e0;
  /* 灰色下边框 */
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.08);
  /* 向下的阴影效果 */
}


.layout-container .el-aside {
  color: var(--el-text-color-primary);
  background: #f7f7f7;
  box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);
  /* 向下的阴影效果 */
  /* 浅灰色 */
}


.layout-container .el-menu {
  border-right: none;
}

.layout-container .el-main {
  padding: 0;
}

.layout-container .toolbar {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  height: 100%;
  position: absolute;
  right: 2%
}

.layout-container {
  width: 100vw;
  height: 100vh;
}

.hospital-header {
  text-align: left;
  /* 左对齐 */
  font-family: '微软雅黑', 'Arial', sans-serif;
  /* 使用适合的中文字体和英文字体 */
  font-size: 24px;
  /* 字体大小适中 */
  font-weight: bold;
  /* 字体加粗 */
  color: #2a4d66;
  /* 深蓝色，体现专业性 */
  margin: 20px 0;
  /* 上下间距，确保与其他元素不重叠 */
  padding-left: 15px;
  /* 左边距，避免紧贴页面左边 */
}
</style>
