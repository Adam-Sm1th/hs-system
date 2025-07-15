<template>
  <div class="error-container">
    <div class="error-wrapper">
      <div class="error-content">
        <div class="error-code">403</div>
        <div class="error-message">
          权限受限
          <span>您没有访问此页面的权限</span>
        </div>
        <button @click="goHome" class="back-button">
          返回
        </button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/store.ts'

const userStore = useUserStore();
const router = useRouter()

const goHome = () => {
  const permission = userStore.userData.userPermission;

  if (permission === 1) {
    router.push('/user')
  } else if (permission === 2) {
    router.push('/departadmin')
  } else if (permission === 3) {
    router.push('/admin')
  } else {
    router.push('/login')
  }
}
</script>

<style scoped>  .error-container {
    display: flex;
    justify-content: center;
    align-items: center;
    height: 100vh;
    background-color: #f0f2f5;
  }

  .error-container {
    display: flex;
    justify-content: center;
    align-items: center;
    height: 100vh;
    width: 100vw;
    /* 添加这一行 */
    background-color: #f0f2f5;
    margin: 0;
    /* 确保没有边距 */
    padding: 0;
    /* 确保没有内边距 */
  }

  .error-wrapper {
    width: 100%;
    /* 修改这一行 */
    max-width: 400px;
    text-align: center;
    padding: 20px;
  }

  .error-code {
    font-size: 72px;
    font-weight: bold;
    color: #ff4d4f;
    margin-bottom: 20px;
  }

  .error-message {
    font-size: 16px;
    color: #666;
    margin-bottom: 30px;
    display: flex;
    flex-direction: column;
  }

  .error-message span {
    font-size: 14px;
    color: #999;
    margin-top: 10px;
  }

  .back-button {
    background-color: #1890ff;
    color: white;
    border: none;
    padding: 10px 20px;
    border-radius: 4px;
    cursor: pointer;
    transition: background-color 0.3s;
  }

  .back-button:hover {
    background-color: #40a9ff;
  }</style>
