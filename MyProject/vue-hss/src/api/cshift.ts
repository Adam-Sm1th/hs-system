import request from '@/utils/request'
import * as CshiftInterface from '@/stores/interface/CshiftInterface'


export const cshiftGetAllService = (pageNum: number, pageSize: number, deptId: string, searchForm:  CshiftInterface.CshiftSearchInterface) => {
    const queryParams = new URLSearchParams();
    for (const key in searchForm) {
        const typedKey = key as keyof CshiftInterface.CshiftSearchInterface; // 确保 key 是 SearchForm 的键
        if (searchForm[typedKey]) {
            queryParams.append(typedKey, String(searchForm[typedKey]));
        }
    }
    queryParams.set("deptId", deptId.toString())
    queryParams.set("pageNum", pageNum.toString())
    queryParams.set("pageSize", pageSize.toString())
    return request.get(`/cshift`, { params: queryParams });
}

export const cshiftAuditDelayOneService = (cshiftId: number) => {
    return request.put(`/cshift/delay/${cshiftId}`);
};

export const cshiftAuditAcceptOneService = (cshift:CshiftInterface.CshiftVO) => {
    return request.put(`/cshift/accept/${cshift.cshiftId}`,cshift);
};


export const cshiftSuitDataService = (cshiftId: number) => {
    return request.get(`/cshift/suitdata/${cshiftId}`);
};

export const cshiftDelayListService = (cshifts: CshiftInterface.CshiftVO[]) => {
    const cshiftIds = cshifts.map(cshift => cshift.cshiftId);
    return request.put('/cshift/batch',
        { cshiftIds},  // 直接传 JSON 对象
        { headers: { 'Content-Type': 'application/json' } }  // 明确指定 JSON 头
    );
};

export const cshiftAddService = (cshiftAdd:CshiftInterface.CshiftAddDTO) => {
  return request.post(`/cshift`,cshiftAdd);
}

export const cshiftDeleteService = (cshiftId: number) => {
  return request.delete(`/cshift/${cshiftId}`);
}
