import request from '@/utils/request'
import type { AddShiftForm, ShiftVO, SearchForm } from '@/stores/interface/ShiftInterface';

export const shiftGetOneService = (shiftId: number) => {
    return request.get(`/shift/${shiftId}`);
}

export const shiftGetAllService = (deptId: string) => {
    const queryParams = new URLSearchParams();
    queryParams.set("deptId", deptId)
    return request.get(`/shift?${queryParams.toString()}`);
}

export const shiftPagesGetAllService = (pageNum: number, pageSize: number, deptId: string, shift: SearchForm) => {
    const queryParams = new URLSearchParams();
    for (const key in shift) {
        const typedKey = key as keyof SearchForm; // 确保 key 是 SearchForm 的键
        if (shift[typedKey]) {
            queryParams.append(typedKey, String(shift[typedKey]));
        }
    }
    queryParams.set("pageNum", pageNum.toString())
    queryParams.set("pageSize", pageSize.toString())
    queryParams.set("deptId", deptId.toString())
    return request.get(`/shift/pages`, { params: queryParams });
}

export const shiftDeleteOneService = (shiftId: number) => {
    return request.delete(`/shift/${shiftId}`)
}

export const shiftDeleteListService = (shifts: ShiftVO[]) => {
    // 提取出每个 Shift 对象的 shiftId，作为批量删除的 ID 列表
    const shiftIds = shifts.map(shift => shift.shiftId);
    // 将 shiftIds 数组作为请求体传递
    return request.delete('/shift/batch', {
        data: shiftIds // 使用 data 来传递请求体数据
    });
};


export const shiftAddOneService = (shift: AddShiftForm) => {
    return request.post(`/shift`, shift)
}

export const shiftEditOneService = (shift: ShiftVO) => {
    return request.put(`/shift/${shift.shiftId}`, shift)
}

export const shiftGetBySearchService = (shift: SearchForm) => {
    const queryParams = new URLSearchParams();

    for (const key in shift) {
        const typedKey = key as keyof SearchForm; // 确保 key 是 SearchForm 的键
        if (shift[typedKey]) {
            queryParams.append(typedKey, String(shift[typedKey]));
        }
    }

    return request.get(`/shift/search?${queryParams.toString()}`);
};
