import { defineStore } from "pinia";
import { ref } from 'vue'
import * as UserInterface from '@/stores/interface/UserInterface.ts';
import piniaPluginPersistedstate from 'pinia-plugin-persistedstate'

interface ShiftStyleMap {
    shiftStyleId: number
    shiftStyleType: string
}

export const shiftStyleMap: ShiftStyleMap[] = [
    { shiftStyleId: 1, shiftStyleType: "primary" },
    { shiftStyleId: 2, shiftStyleType: "success" },
    { shiftStyleId: 3, shiftStyleType: "info" },
    { shiftStyleId: 4, shiftStyleType: "warning" },
    { shiftStyleId: 5, shiftStyleType: "danger" }
];

export const userTypeMap = [
    { "userType": null, "typeName": "全部" },
    { "userType": 1, "typeName": "医生" },
    { "userType": 2, "typeName": "护士" },
]

export const scheStatusMap = [
    { "status": 1, "statusName": "待排班" },
    { "status": 2, "statusName": "已排班" },//3是请假中4是调班中
    { "status": 3, "statusName": "请假中" },
    { "status": 4, "statusName": "调班中" },
    { "status": null, "statusName": "全部情况" },
]

export const LeaveStatusMap = [
    { "status": 1, "statusName": "未审核" },
    { "status": 2, "statusName": "审核通过" },
    { "status": 3, "statusName": "审核不通过" },
    { "status": null, "statusName": "全部" },
]

export const useDepartmentStore = defineStore("department", () => {
    const departmentData = ref({
        deptId: "12345",
        deptName: "精神科",
        deptInfo: "精神科致力于提供专业的心理健康服务，包括心理评估、治疗和康复。我们拥有经验丰富的心理医生和治疗师团队，为患者提供个性化的治疗方案。科室配备先进的治疗设备，旨在帮助患者恢复心理健康，提高生活质量。",
        deptCreatetime: new Date(),
    });

    const setDepartmentData = (newData: any) => {
        departmentData.value = newData;
    };

    const reset = () => {
        departmentData.value = {
          deptId: "12345",
          deptName: "精神科",
          deptInfo: "精神科致力于提供专业的心理健康服务，包括心理评估、治疗和康复。我们拥有经验丰富的心理医生和治疗师团队，为患者提供个性化的治疗方案。科室配备先进的治疗设备，旨在帮助患者恢复心理健康，提高生活质量。",
          deptCreatetime: new Date(),
        };
    };
    return { departmentData, setDepartmentData,reset };
}, {
  persist: true  // ✅ 添加这个就开启持久化了
});


export const useUserStore = defineStore("user", () => {
    const userData = ref<UserInterface.UserVO>({
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

    const setUserData = (newData: any) => {
        userData.value = newData;
    };

    const reset = () => {
        userData.value = {
         userId: "12345",
         userName: "TETE",
         userGender: "女",
         userPhone: "15394280152",
         userEmail: "w971759869@example.com",
         userInfo: "软件开发工程师，热爱编程，喜欢挑战技术难题。",
         userAvatar: "https://hs-system.oss-cn-hangzhou.aliyuncs.com/5e2777d449887dbee4e498bcde37036.png",  // 这里可以替换为实际的头像链接
         userPermission: 0,  // 1代表普通用户，其他数字可根据实际权限设定
         userBirthday: new Date("2000-05-10"),
         userRegtime: new Date("2023-08-15"),
         userType: 2,  // 例如，2代表员工，1代表管理员，具体根据实际需求设定
         userProactivity: 5,  // 假设这个值为活动度，1为低，5为高
        };
    };

    return { userData, setUserData,reset};
}, {
  persist: true  // ✅ 添加这个就开启持久化了
});

interface tokenDataInterface  {
  token:string|null
}
export const useTokenStore = defineStore("token", () => {
    const tokenData = ref<tokenDataInterface>({
        token:null
    });

    const setTokenData = (token:string) => {
        tokenData.value.token = token
    };

    const reset = () => {
        tokenData.value = {
          token:null
        };
    };
    return { tokenData, setTokenData,reset };
}, {
  persist: true  // ✅ 添加这个就开启持久化了
});
