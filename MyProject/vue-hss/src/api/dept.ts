import request from '@/utils/request'
import * as DepartmentInterface from '@/stores/interface/DepartmentInterface.ts'

export const deptGetAllPagesService = (pageNum: number, pageSize: number,searchForm: DepartmentInterface.SearcheForm) => {
    const queryParams = new URLSearchParams();
    for (const key in searchForm) {
        const typedKey = key as keyof DepartmentInterface.SearcheForm; // 确保 key 是 SearchForm 的键
        if (searchForm[typedKey]) {
            queryParams.append(typedKey, String(searchForm[typedKey]));
        }
    }
    queryParams.set("pageNum", pageNum.toString())
    queryParams.set("pageSize", pageSize.toString())
    return request.get(`/dept/pages`, { params: queryParams });
}

export const deptAddService = (deptAdd:DepartmentInterface.Department) => {
  return request.post(`/dept`,deptAdd);
}

export const deptUpdateService = (deptEdit:DepartmentInterface.Department) => {
  return request.put(`/dept/${deptEdit.deptId}`,deptEdit);
}

export const deptDeleteService = (deptId:string) => {
  return request.delete(`/dept/${deptId}`);
}

export const deptDeleteListService = (depts: DepartmentInterface.Department[]) => {
    // 提取出每个 Shift 对象的 shiftId，作为批量删除的 ID 列表
    const shiftIds = depts.map(dept => dept.deptId);
    // 将 shiftIds 数组作为请求体传递
    return request.delete('/dept/batch', {
        data: shiftIds // 使用 data 来传递请求体数据
    });
};

export const deptGetAllService = () => {
    return request.get(`/dept`);
}


export const deptGetOneService = (deptId: string) => {
    return request.get(`/dept/${deptId}`);
}
