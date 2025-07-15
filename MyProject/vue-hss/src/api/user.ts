import type { UserVO } from '@/stores/interface/UserInterface';
import request from '@/utils/request'
import * as UserInterface from '@/stores/interface/UserInterface.ts';

export const userGetAllService = (deptId: string) => {
    const queryParams = new URLSearchParams();
    queryParams.set("deptId", deptId)
    return request.get(`/user?${queryParams.toString()}`)
}

export const userGetAllNurseService = (deptId: string) => {
    const queryParams = new URLSearchParams();
    queryParams.set("deptId", deptId)
    return request.get(`/user/nurse?${queryParams.toString()}`)
}

export const userGetAllDoctorService = (deptId: string) => {
    const queryParams = new URLSearchParams();
    queryParams.set("deptId", deptId)
    return request.get(`/user/doctor?${queryParams.toString()}`)
}

export const userGetOneService = (userId: string) => {
  return request.get(`/user/${userId}`);
}

export const userEditOneService = (user: UserVO) => {
    return request.put(`/user/${user.userId}`, user)
}

export const userDashBoardService = ()=> {
  return request.get(`/user/admin/dashboard`);
}

export const userGetAllPagesService = (pageNum: number, pageSize: number,searchForm: UserInterface.UserSearchForm) => {
    const queryParams = new URLSearchParams();
    for (const key in searchForm) {
        const typedKey = key as keyof UserInterface.UserSearchForm; // 确保 key 是 SearchForm 的键
        if (searchForm[typedKey]) {
            queryParams.append(typedKey, String(searchForm[typedKey]));
        }
    }
    queryParams.set("pageNum", pageNum.toString())
    queryParams.set("pageSize", pageSize.toString())
    return request.get(`/user/pages`, { params: queryParams });
}

export const userDeleteService = (userId:string) => {
  return request.delete(`/user/${userId}`);
}

export const userDeleteListService = (users: UserInterface.UserVO[]) => {
    const userIds = users.map(user => user.userId);
    return request.delete('/user/batch', {
        data: userIds
    });
};


export const userAddService = (user:UserInterface.UserDTO) => {
  return request.post(`/user`, user)
}

export const userEditPwdService = (userId:string, userPwd:String) => {
      return request.put(`/user/changepwd/${userId}`,
        { userPwd },  // 直接传 JSON 对象
        { headers: { 'Content-Type': 'application/json' } }  // 明确指定 JSON 头
    );
}

export const userLoginService = (user:UserInterface.UserDTO)=> {
  return request.post(`/user/login`,user);
}

export const sendVerifyCodeService = (userId:String) => {
  return request.get(`/user/verifycode/${userId}`);
}

export const userEmailLoginService = (user:UserInterface.UserEmailLogin)=> {
  return request.post(`/user/emaillogin`,user);
}


