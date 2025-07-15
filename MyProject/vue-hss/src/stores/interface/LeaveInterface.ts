export interface LeaveSearchInterface {
  leaveId:number|null
  userId:string|null
  leaveStatus:number|null
  isPast:Boolean|null
}

export interface LeaveFormInterface {
  leaveId:number
  userId:number
  scheId:number
  leaveInfo:string
  leaveStatus:number
  leaveDate:Date
}

export interface LeaveAddInterface {
  scheId:number|null
  leaveInfo:String|null
}
