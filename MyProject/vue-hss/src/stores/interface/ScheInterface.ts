import * as Shift from "@/stores/interface/ShiftInterface.ts"
import * as User from "@/stores/interface/UserInterface.ts"

export interface SchedulingVO {
    scheId: number | null;
    scheName: string;
    scheInfo: string;
    scheDate: Date;
    userType: number | null;
    userId: string | null;
    shiftId: number | null;
    scheStatus: number | null;
    deptId: number | null;
    shiftVO: Shift.ShiftVO;
    userVO: User.UserVO | null;
}

export interface ScheSearchForm {
    scheId: number | null;
    scheName: string | null;
    scheInfo: string | null;
    beginDate: string | null;
    endDate: string | null;
    shiftId: number | null;
    userId: string | null;
    userType: number | null;
    scheStatus: number | null;
    isPast: boolean | null;
}

export interface SchedulingAddForm {
    scheName: string;
    shiftId: number | null;
    userType: number | null;
    scheInfo: string;
    scheDate: Date|null;
    userNum:number |null;
    scheStatus:number|null;
    deptId:string|null;
}

export interface SchedulingEditForm {
    scheId:number|null;
    scheName: string;
    shiftId: number | null;
    userType: number | null;
    scheInfo: string;
    scheDate: Date|null;
    userNum:number |null;
    scheStatus:number|null;
    deptId:string|null;
}

export interface SchedulingUser {
  scheId: number,
  userId: String
}

export interface ScheUserSearchForm {
    userId: string|null;
    selectDate:Date|null;
}

export interface DateCellData {
  day: string;          // 日期字符串
  isSelected: boolean;  // 是否被选中的布尔值
}

