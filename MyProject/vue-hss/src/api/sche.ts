import request from '@/utils/request'
import type { ScheSearchForm,SchedulingVO,SchedulingAddForm, SchedulingEditForm, SchedulingUser } from '@/stores/interface/ScheInterface.ts'

export const scheGetAllService = (pageNum: number, pageSize: number, deptId: string, scheVO: ScheSearchForm) => {
    const queryParams = new URLSearchParams();
    for (const key in scheVO) {
        const typedKey = key as keyof ScheSearchForm; // 确保 key 是 SearchForm 的键
        if (scheVO[typedKey]) {
            queryParams.append(typedKey, String(scheVO[typedKey]));
        }
    }
    queryParams.set("deptId", deptId.toString())
    queryParams.set("pageNum", pageNum.toString())
    queryParams.set("pageSize", pageSize.toString())
    return request.get(`/sche`, { params: queryParams });
}

export const scheDeleteListService = (sches: SchedulingVO[]) => {
    // 提取出每个 Shift 对象的 shiftId，作为批量删除的 ID 列表
    const scheIds = sches.map(sche => sche.scheId);
    // 将 shiftIds 数组作为请求体传递
    return request.delete('/sche/batch', {
        data: scheIds
    });
};

export const scheAddOneService = (sche: SchedulingAddForm) => {
    return request.post(`/sche`, sche)
}

export const scheDeleteOneService = (scheId: number) => {
    return request.delete(`/sche/${scheId}`)
}

export const scheEditOneService = (sche: SchedulingEditForm) => {
    return request.put(`/sche/${sche.scheId}`, sche)
}

export const scheUserOneService = (scheUser: SchedulingUser) => {
  return request.put(`/sche/${scheUser.scheId}`, scheUser);
};

export const scheGetOneService = (scheId:number) => {
  return request.get(`/sche/${scheId}`);
}

export const scheGetAllByUserIdService = (deptId: string|null, userId: string) => {
    const queryParams = new URLSearchParams();
    if(deptId != null) {
      queryParams.set("deptId", deptId.toString())
    }
    queryParams.set("userId", userId.toString())
    return request.get(`/sche`, { params: queryParams });
}

export const scheGetEnableScheService = (userId: string, scheDate: Date) => {
    const queryParams = new URLSearchParams();
    queryParams.set("userId", userId);
    queryParams.set("scheDate", scheDate.toISOString());
    return request.get(`/sche/enableSche`, { params: queryParams });
}

export const scheUserDelService = (scheUser: SchedulingUser) => {
    return request.put(`/sche/delscheuser`, scheUser);
}

export const scheUserAddService = (scheUser: SchedulingUser) => {
    return request.put(`/sche/addscheuser`, scheUser);
}

export const delscheUserService = (scheId:number) => {
    return request.put(`/sche/deluser/${scheId}`);
}

export const scheGetStatusService = (deptId:String) => {
    const queryParams = new URLSearchParams();
    queryParams.set("deptId", deptId.toString());
    return request.get(`/sche/scheStatus`,{ params: queryParams });
}

export const scheGetPieStatusService = (deptId:String) => {
    const queryParams = new URLSearchParams();
    queryParams.set("deptId", deptId.toString());
    return request.get(`/sche/schePieStatus`,{ params: queryParams });
}

export const scheGetUserStatusService = (deptId:String) => {
    const queryParams = new URLSearchParams();
    queryParams.set("deptId", deptId.toString());
    return request.get(`/sche/scheUserStatus`,{ params: queryParams });
}

export const scheGetCompltStatusService = (deptId:String) => {
    const queryParams = new URLSearchParams();
    queryParams.set("deptId", deptId.toString());
    return request.get(`/sche/scheCompltStatus`,{ params: queryParams });
}

export const scheGetHoursStatusService = (deptId:String) => {
    const queryParams = new URLSearchParams();
    queryParams.set("deptId", deptId.toString());
    return request.get(`/sche/scheHoursStatus`,{ params: queryParams });
}

export const scheGetDashboardStatusService = (deptId:String) => {
    const queryParams = new URLSearchParams();
    queryParams.set("deptId", deptId.toString());
    return request.get(`/sche/dashboard`,{ params: queryParams });
}


export const scheGetUserDashboardStatusService = (userId:String) => {
    const queryParams = new URLSearchParams();
    queryParams.set("userId", userId.toString());
    return request.get(`/sche/user/dashboard`,{ params: queryParams });
}

export const scheGetUserHoursStatusService = (userId:String) => {
    const queryParams = new URLSearchParams();
    queryParams.set("userId", userId.toString());
    return request.get(`/sche/user/scheHoursStatus`,{ params: queryParams });
}

export const scheGetOneUserStatusService = (userId:String) => {
    const queryParams = new URLSearchParams();
    queryParams.set("userId", userId.toString());
    return request.get(`/sche/user/scheStatus`,{ params: queryParams });
}

export const scheGetUserPieStatusService = (userId:String) => {
    const queryParams = new URLSearchParams();
    queryParams.set("userId", userId.toString());
    return request.get(`/sche/user/schePieStatus`,{ params: queryParams });
}


