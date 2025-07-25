import './assets/main.css'

import { createApp } from 'vue'
import { createPinia } from 'pinia'

import App from './App.vue'
import router from './router'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
//引入echarts
import * as echarts from 'echarts';
import piniaPluginPersistedstate from 'pinia-plugin-persistedstate'
const pinia = createPinia()

pinia.use(piniaPluginPersistedstate)

const app = createApp(App)
app.config.globalProperties.$echarts = echarts; //配置echarts
app.use(pinia)
app.use(router)
app.use(ElementPlus)

app.mount('#app')
