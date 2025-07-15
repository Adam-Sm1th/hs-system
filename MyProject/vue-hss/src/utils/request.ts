import axios from "axios";
import { ElMessage } from 'element-plus'
import {useTokenStore} from '@/stores/store.ts'

const baseURL = "http://localhost:5173/api";
const instance = axios.create({ baseURL })
import router from "@/router";

instance.interceptors.request.use(
  (config) => {
    const tokenStore = useTokenStore();
    if (tokenStore.tokenData.token) {
      config.headers.Authorization = tokenStore.tokenData.token;
    }
    return config;
  },
  (err) => {
    if(err.response.status === 401){
      router.push("/login")
      ElMessage.error("请先登录")
    }
    return Promise.reject(err);
  }
);


instance.interceptors.response.use(
    response => {
        if (response.data.code === 101) {
            ElMessage({
                message: response.data.code ? response.data.msg : "操作失败",
                type: 'error',
            })
        } else {
            // ElMessage({
            //     message: response.data.code ? response.data.msg : "操作成功",
            //     type: 'success',
            // })
            return response.data;
        }
    },
    error => {
        ElMessage({
            message: "服务异常",
            type: 'error',
        })
        return Promise.reject(error);
    }
);



export default instance;
