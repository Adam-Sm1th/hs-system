export interface ShiftVO {
    shiftId: number
    shiftName: string
    shiftBegintime: string
    shiftEndtime: string
    deptId: String
    shiftStyleId: number
}

export interface Response {
    code: string;
    message?: string;
    data?: any
}

export interface AddShiftForm {
    shiftName: string
    shiftBegintime: string
    shiftEndtime: string
    deptId: String
    shiftStyleId: number
}

export interface RuleForm {
    shiftName: string
    shiftBegintime: string
    shiftEndtime: string
    shiftStyleId: number
}

export interface SearchForm {
    shiftId: number | null
    shiftName: string
}
