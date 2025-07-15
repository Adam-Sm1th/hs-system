<template>
  <div class="userinfo-wrapper">
    <el-card class="userinfo-card" shadow="always" v-loading="loading">
      <div class="userinfo-header">
        <el-upload :action=uploadUrl :show-file-list="false" :on-success="handleAvatarSuccess"
          :before-upload="beforeAvatarUpload">
          <el-avatar :size="120" :src="user.userAvatar" style="cursor: pointer" />
        </el-upload>
        <div class="userinfo-name">{{ user.userName }}</div>
        <div class="userinfo-type">{{ getUserType(user.userType) }}</div>
        <el-button size="big" type="primary" @click="dialogVisible = true" style="margin-top: 10px;">
          编辑资料
        </el-button>
      </div>

      <el-divider />

      <!-- 基本信息 -->
      <el-descriptions title="基本信息" :column="2" border>
        <el-descriptions-item label="用户名">{{ user.userId }}</el-descriptions-item>
        <el-descriptions-item label="性别">{{ user.userGender }}</el-descriptions-item>
        <el-descriptions-item label="手机号">{{ user.userPhone }}</el-descriptions-item>
        <el-descriptions-item label="邮箱">{{ user.userEmail }}</el-descriptions-item>
        <el-descriptions-item label="权限等级">{{ user.userPermission }}</el-descriptions-item>
        <el-descriptions-item label="出生日期">{{ formatTime(user.userBirthday) }}</el-descriptions-item>
        <el-descriptions-item label="注册时间">{{ formatTime(user.userRegtime) }}</el-descriptions-item>
        <el-descriptions-item label="排班积极性">{{ user.userProactivity }}/100</el-descriptions-item>
      </el-descriptions>

      <el-divider />

      <!-- 简介 -->
      <el-descriptions title="简介">
        <el-descriptions-item>
          <div class="userinfo-description">{{ user.userInfo }}</div>
        </el-descriptions-item>
      </el-descriptions>
    </el-card>

    <!-- 编辑弹窗 -->
    <el-dialog v-model="dialogVisible" title="编辑个人信息" width="500px" destroy-on-close>
      <el-form :model="editUser" label-position="top">

        <el-form-item label="姓名">
          <el-input v-model="editUser.userName" />
        </el-form-item>
        <el-form-item label="性别">
          <el-select v-model="editUser.userGender" placeholder="请选择性别">
            <el-option label="男" value="男" />
            <el-option label="女" value="女" />
          </el-select>
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="editUser.userPhone" />
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="editUser.userEmail" />
        </el-form-item>
        <el-form-item label="排班积极性">
          <el-input-number v-model="editUser.userProactivity" :min="0" :max="100" :step="1" controls-position="right" />
        </el-form-item>

        <el-form-item label="简介">
          <el-input type="textarea" :rows="4" v-model="editUser.userInfo" />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSave">保存</el-button>
      </template>
    </el-dialog>
  </div>
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
import * as UserInterface from '@/stores/interface/UserInterface.ts';
import { useUserStore } from '@/stores/store.ts'
import type { UploadProps } from 'element-plus'
import { ElMessage } from 'element-plus'

const loading = ref(true)
const userStore = useUserStore()
const formatTime = (date: Date | string | null): string => {
  if (!date) return ''
  const dateObj = new Date(date)
  if (isNaN(dateObj.getTime())) return ''
  const year = dateObj.getFullYear()
  const month = (dateObj.getMonth() + 1).toString().padStart(2, '0')
  const day = dateObj.getDate().toString().padStart(2, '0')

  return `${year}-${month}-${day}`
}
const user = ref<UserInterface.UserVO>({
  userId: "12345",
  userName: "TETE",
  userGender: "女",
  userPhone: "15394280152",
  userEmail: "w971759869@example.com",
  userInfo: "软件开发工程师，热爱编程，喜欢挑战技术难题。",
  userAvatar: "https://hs-system.oss-cn-hangzhou.aliyuncs.com/5e2777d449887dbee4e498bcde37036.png",  // 这里可以替换为实际的头像链接
  userPermission: 1,  // 1代表普通用户，其他数字可根据实际权限设定
  userBirthday: new Date("2000-05-10"),
  userRegtime: new Date("2023-08-15"),
  userType: 2,  // 例如，2代表员工，1代表管理员，具体根据实际需求设定
  userProactivity: 5,  // 假设这个值为活动度，1为低，5为高
});

const uploadUrl = computed(() => {
  return `http://localhost:5173/api/oss/upload/${user.value.userId}`;
});

const handleAvatarSuccess: UploadProps['onSuccess'] = async (
  response,
  uploadFile
) => {
  console.log(response.data.imageUrl)
  const newAvatarUrl = `${response.data.imageUrl}?timestamp=${new Date().getTime()}`;
  user.value.userAvatar = newAvatarUrl;
  await UserRequest.userEditOneService(user.value);
  userStore.setUserData(user.value)
}

const beforeAvatarUpload: UploadProps['beforeUpload'] = (rawFile) => {

  const allowedFormats = ['image/jpeg', 'image/png', 'image/gif'];
  if (!allowedFormats.includes(rawFile.type)) {
    ElMessage.error('头像必须是JPG,PNG,或GIF格式')
    return false;
  }
  if (rawFile.size / 1024 / 1024 > 10) {
    ElMessage.error('头像大小不能超过20MB!')
    return false;
  }
  return true;
}


const getUserType = (type: number) => {
  switch (type) {
    case 0: return '管理员'
    case 1: return '医生'
    case 2: return '护士'
    default: return '未知类型'
  }
}

const dialogVisible = ref(false)
const editUser = ref({ ...user.value })


const handleSave = async () => {
  try {
    await UserRequest.userEditOneService(editUser.value);
    Object.assign(user.value, editUser.value);
    userStore.setUserData(editUser.value)
    dialogVisible.value = false
  } catch { }
}

onMounted(async () => {
  loading.value = true;
  Object.assign(user.value, userStore.userData);
  Object.assign(editUser.value, userStore.userData);
  loading.value = false;
})


</script>

<style scoped>
.userinfo-wrapper {
  padding: 32px;
  background: #f5f7fa;
  min-height: 100vh;
  box-sizing: border-box;
}

.userinfo-card {
  border-radius: 20px;
  overflow: hidden;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
  background-color: white;
}

.userinfo-banner {
  position: relative;
  height: 200px;
  background-image: url("../../pic/in_banner.jpg");
  background-size: cover;
  /* 保证背景图覆盖整个容器 */
  background-position: center;
  /* 居中显示背景图片 */
  background-repeat: no-repeat;
  /* 防止背景图重复 */
}

.userinfo-avatar {
  position: absolute;
  bottom: -60px;
  left: 50%;
  transform: translateX(-50%);
  border: 6px solid white;
  background-color: #fff;
  z-index: 2;
}

.edit-btn {
  position: absolute;
  top: 16px;
  right: 20px;
  z-index: 3;
  border-radius: 16px;
}

.userinfo-header {
  height: 350px;
  text-align: center;
  padding-top: 80px;
  padding-bottom: 24px;
  background-image: url("../../pic/in_banner.jpg");
  /* 使用相同的背景图 */
  background-size: cover;
  /* 保证背景图覆盖整个容器 */
  background-position: center;
  /* 居中显示背景图片 */
  background-repeat: no-repeat;
  /* 防止背景图重复 */
  border-radius: 20px;
  /* 添加圆角效果 */
}


.userinfo-name {
  font-size: 26px;
  font-weight: 600;
  color: #ffffff;
}

.userinfo-type {
  font-size: 16px;
  color: #ffffff;
  margin-top: 6px;
}

.userinfo-description {
  padding: 16px;
  background-color: #f8f8f8;
  border-radius: 10px;
  color: #666;
  line-height: 1.7;
}

.el-divider {
  margin: 20px 0;
}

.el-card {
  border-radius: 16px;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.08);
}
</style>
