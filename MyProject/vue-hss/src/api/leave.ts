import request from '@/utils/request'
import * as LeaveInterface from '@/stores/interface/LeaveInterface'

export const leaveGetAllService = (pageNum: number, pageSize: number, deptId: string, searchForm:  LeaveInterface.LeaveSearchInterface) => {
    const queryParams = new URLSearchParams();
    for (const key in searchForm) {
        const typedKey = key as keyof LeaveInterface.LeaveSearchInterface; // 确保 key 是 SearchForm 的键
        if (searchForm[typedKey]) {
            queryParams.append(typedKey, String(searchForm[typedKey]));
        }
    }
    queryParams.set("deptId", deptId.toString())
    queryParams.set("pageNum", pageNum.toString())
    queryParams.set("pageSize", pageSize.toString())
    return request.get(`/leave`, { params: queryParams });
}

export const leaveAuditOneService = (leaveId: number, auditOperator: boolean) => {
    return request.put(`/leave/${leaveId}`, auditOperator, {
        headers: {
            'Content-Type': 'application/json'
        }
    });
};

export const leaveAuditListService = (leaves: LeaveInterface.LeaveFormInterface[], auditOperator: boolean) => {
    const leaveIds = leaves.map(leave => leave.leaveId);
    return request.put('/leave/batch',
        { leaveIds, auditOperator },  // 直接传 JSON 对象
        { headers: { 'Content-Type': 'application/json' } }  // 明确指定 JSON 头
    );
};


export const leaveAddService = (leaveAdd:LeaveInterface.LeaveAddInterface) => {
  return request.post(`/leave`,leaveAdd);
}

export const leaveDeleteService = (leaveId: number) => {
  return request.delete(`/leave/${leaveId}`);
}

