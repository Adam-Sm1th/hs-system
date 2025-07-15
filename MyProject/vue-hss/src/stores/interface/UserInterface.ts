export interface UserVO {
    userId: string;
    userName: string;
    userGender: string;
    userPhone: string;
    userEmail: string;
    userInfo: string;
    userAvatar: string;
    userPermission: number;
    userBirthday: Date;
    userRegtime: Date;
    userType: number;
    userProactivity: number;
}

export interface UserSearchForm {
    userId: string;
    userName: string;
    userType: number;
    deptId:string;
}

export interface UserDTO {
    userId: string;
    userName: string;
    userPwd:string;
    userRePwd:string;
    userGender: string;
    userPhone: string;
    userEmail: string;
    userInfo: string;
    userAvatar: string;
    userPermission: number;
    userBirthday: Date;
    userRegtime: Date;
    userType: number;
    userProactivity: number;
}

export interface UserLogin{
  identity: number|null,
  userId: string,
  userPwd: string,
  verifyCode: string
}

export interface UserEmailLogin{
  userId:string,
  verifyCode:string
}


