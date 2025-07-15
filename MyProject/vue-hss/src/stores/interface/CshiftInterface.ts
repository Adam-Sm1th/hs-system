export interface CshiftVO {
    cshiftId: number;
    userId: string;
    scheOriginalId: number;
    cshiftInfo: string;
    scheTargetId: number;
    cshiftTargetDay: Date;
    shiftTargetId: number;
    cshiftStatus: number;
    cshiftOriginalDay: Date;
    deptId: string;
}

export interface CshiftSearchInterface {
  cshiftId:number|null
  userId:string|null
  cshiftStatus:number|null
  isPast:Boolean|null
}

export interface CshiftAddDTO {
  scheId: number|null;
  cshiftTargetDay: Date|null;
  shiftTargetId: number|null;
  cshiftInfo: string;
}
