import * as UserDeptInterface from '@/stores/interface/UserDeptInterface'
import request from '@/utils/request'

export const userDeptDeleteListService = (userdepts: UserDeptInterface.UserDeptVO[]) => {
    return request.delete('/userdept/batch', {
        data: userdepts
    });
};


export const userDeptDocGetListService = (userName:string)=> {
   const queryParams = new URLSearchParams();
   queryParams.set("userName", userName)
  return request.get(`/userdept/able/doctor`,{ params: queryParams });
}

export const userDeptNurGetListService = (userName:string)=> {
   const queryParams = new URLSearchParams();
   queryParams.set("userName", userName)
  return request.get(`/userdept/able/nurse`,{ params: queryParams });
}

export const userDeptDocAddListService = (userDepts:UserDeptInterface.UserDeptVO[])=> {
  return request.post(`/userdept/batch`, userDepts);
}


export const getUserDeptService = (userid:string) => {
  return request.get(`/userdept/dept/${userid}`)
}
