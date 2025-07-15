<template>
  <div class="login-container">
    <div class="login-card">
      <h2 class="title">医院排班系统登录</h2>

      <el-form :model="form" :rules="rules" ref="loginFormRef" label-width="0">
        <el-form-item prop="identity">
          <el-select v-model="form.identity" placeholder="请选择身份" class="identity-select">
            <el-option label="个人主页" value="1" />
            <el-option label="科室管理员" value="2" />
            <el-option label="超级管理员" value="3" />
          </el-select>
        </el-form-item>

        <template v-if="!useEmailLogin">
          <el-form-item prop="username">
            <el-input v-model="form.userId" placeholder="账号" :prefix-icon="Avatar" />
          </el-form-item>
          <el-form-item prop="password">
            <el-input v-model="form.userPwd" placeholder="密码" show-password :prefix-icon="Key" />
          </el-form-item>
        </template>

        <template v-else>
          <el-form-item prop="email">
            <el-input v-model="form.userId" placeholder="账号" :prefix-icon="Avatar" />
          </el-form-item>
          <el-form-item prop="code">
            <div style="display: flex; width: 100%; gap: 10px">
              <el-input v-model="form.verifyCode" placeholder="验证码" :prefix-icon="Key" style="flex: 1" />
              <el-button type="primary" :disabled="countdown > 0" @click="handleGetCode">
                {{ countdown > 0 ? countdown + 's' : '获取验证码' }}
              </el-button>
            </div>
          </el-form-item>
        </template>

        <el-form-item>
          <el-button type="primary" class="w-full login-btn" @click="handleLogin">登录</el-button>
        </el-form-item>
        <el-form-item>
          <el-button link type="primary" @click="toggleLoginMode" class="toggle-btn">
            {{ useEmailLogin ? '使用账号密码登录' : '使用邮箱登录' }}
          </el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'
import { Avatar, Key, Message } from '@element-plus/icons-vue'
import { useDepartmentStore } from '@/stores/store.ts'
import { useUserStore } from '@/stores/store.ts'
import { useTokenStore } from '@/stores/store.ts'
import * as UserRequest from '@/api/user.ts'
import * as UserDepartmentRequset from '@/api/userdept.ts'
import { ElMessage } from 'element-plus'
import router from '@/router'
import type * as UserInterface from '@/stores/interface/UserInterface.ts'

const useEmailLogin = ref(false)
const tokenStore = useTokenStore()
const userStore = useUserStore()
const deptStore = useDepartmentStore()

const form = ref<UserInterface.UserLogin>({
  identity: null,
  userId: '',
  userPwd: '',
  verifyCode: ''
})

const loginFormRef = ref()

const rules = {
  identity: [{ required: true, message: '请选择身份', trigger: 'change' }],
  userId: [{ required: true, message: '请输入账号', trigger: 'blur' }],
  userPwd: [{ required: true, message: '请输入密码', trigger: 'blur' }],
  userEmail: [{ required: true, message: '请输入邮箱', trigger: 'blur' }],
  verifyCode: [{ required: true, message: '请输入验证码', trigger: 'blur' }]
}

const toggleLoginMode = () => {
  useEmailLogin.value = !useEmailLogin.value
}

const handleLogin = async () => {
  loginFormRef.value?.validate(async (valid: boolean) => {
    if (valid) {
      let user: any
      let resp: any

      try {
        if (!useEmailLogin.value) {
          user = {
            userId: form.value.userId,
            userPwd: form.value.userPwd
          }
          resp = await UserRequest.userLoginService(user as UserInterface.UserDTO)
        } else {
          user = {
            userId: form.value.userId,
            verifyCode: form.value.verifyCode
          }
          resp = await UserRequest.userEmailLoginService(user as UserInterface.UserEmailLogin)
        }

        tokenStore.setTokenData(resp.data.token)
        const resp2 = await UserRequest.userGetOneService(user.userId)
        userStore.setUserData(resp2.data)

        if (form.value.identity == 1 && resp2.data.userPermission >= form.value.identity) {
          router.push("/user")
        }

        if (form.value.identity == 2) {
          if (resp2.data.userPermission >= form.value.identity) {
            const resp3 = await UserDepartmentRequset.getUserDeptService(form.value.userId);
            deptStore.setDepartmentData(resp3.data)
            router.push("/departadmin")
          } else {
            ElMessage.error("权限不足")
            router.push("/login")
          }
        }

        if (form.value.identity == 3) {
          if (resp2.data.userPermission >= form.value.identity) {
            router.push("/admin")
          } else {
            ElMessage.error("权限不足")
            router.push("/login")
          }
        }
      } catch {

      } finally {
        console.log('Finally block executed');
      }
    }
  })
}


// ========== 获取验证码逻辑 ==========
const countdown = ref(0)
let timer: number | null = null

const handleGetCode = async () => {
  if (!form.value.userId) {
    ElMessage.warning("请输入账号")
    return
  }

  try {
    await UserRequest.sendVerifyCodeService(form.value.userId)
    ElMessage.success("验证码已发送")
    startCountdown()
  } catch (error) {
    ElMessage.error("验证码发送失败")
  }
}

const startCountdown = () => {
  countdown.value = 60
  timer = window.setInterval(() => {
    countdown.value--
    if (countdown.value <= 0 && timer) {
      clearInterval(timer)
      timer = null
    }
  }, 1000)
}

onUnmounted(() => {
  if (timer) clearInterval(timer)
})
</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  width: 100vw;
  height: 100vh;
  background: linear-gradient(to right, #bce0fd, #ffffff);
  padding: 20px;
  box-sizing: border-box;
}

.login-card {
  background: #ffffff;
  padding: 48px 64px;
  width: 450px;
  border-radius: 12px;
  box-shadow: 0 12px 32px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
}

.title {
  text-align: center;
  margin-bottom: 36px;
  font-size: 30px;
  color: #0288d1;
  font-weight: 600;
}

.identity-select .el-select-dropdown__item {
  background-color: #e3f2fd;
}

.w-full {
  width: 100%;
}

.login-btn {
  background-color: #0288d1;
  border-color: #0288d1;
  font-size: 16px;
}

.login-btn:hover {
  background-color: #0277bd;
}

.toggle-btn {
  text-align: center;
  color: #0288d1;
  font-weight: bold;
}

.toggle-btn:hover {
  color: #01579b;
}
</style>
