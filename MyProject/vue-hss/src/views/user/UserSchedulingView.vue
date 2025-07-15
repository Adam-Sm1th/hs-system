<template>
  <el-card style="box-sizing: border-box;min-height: 95%; margin: 10px; min-height: 95%;">
    <template #header>
      <div class="card-header">
        <span>个人排班查看</span>
      </div>
    </template>
    <el-row :gutter="5">
      <el-col :span="17">
        <el-calendar ref="calendar" v-loading="calendarLoading">
          <template #header="{ date }">
            <span>{{ date }}</span>
            <el-button-group>
              <el-button size="small" @click="selectDate('prev-month')">
                上一个月
              </el-button>
              <el-button size="small" @click="selectDate('today')">回到当天</el-button>
              <el-button size="small" @click="selectDate('next-month')">
                下一个月
              </el-button>
            </el-button-group>
          </template>

          <template #date-cell="{ data }">
            <div @click="handleDateClick(data)"
              style="display: flex; width: 100%; height: 100%; justify-content: center; align-items: center;">
              <p :class="data.isSelected ? 'is-selected' : ''">
                {{ data.day.split('-').slice(1).join(' -') }}
                {{ data.isSelected ? '✔️' : '' }}
                <span v-if="getScheduleForDate(data.day) && getScheduleForDate(data.day).length">
                  <span v-for="(item, index) in getScheduleForDate(data.day).slice(0, 2)" :key="index">
                    <div style="display: flex; justify-content: center; align-items: center;">
                      <el-tag size="middle" :type="styleFunction(item.shiftVO.shiftStyleId)">
                        {{ item.shiftVO.shiftName.substring(0, 4) }}
                      </el-tag>
                      <span v-if="index === 1 && getScheduleForDate(data.day).length > 2">...</span>
                    </div>
                  </span>
                </span>
              </p>
            </div>
          </template>

        </el-calendar>
      </el-col>
      <el-col :span="7">
        <el-card style="max-width: 480px">
          <span>排班信息</span>
          <el-scrollbar height="450px">
            <el-table :data="rightListData" style="width: 100%" v-loading="alreadyLoading" stripe>
              <el-table-column align="center" label="排班名称" show-overflow-tooltip>
                <template #default="scope">
                  <span style="
                  cursor: pointer;
                  text-decoration: underline;
                  font-size: 16px;
                  font-style: italic;
                " @click="showScheTable(scope.row.scheId)">
                    {{ scope.row.scheName }}
                  </span>
                </template>
              </el-table-column>
              <el-table-column label="班次" align="center" show-overflow-tooltip>
                <template #default="scope">
                  <el-tag size="large"
                    :type="shiftStyleMap.find(item => item.shiftStyleId === scope.row.shiftVO.shiftStyleId)?.shiftStyleType">
                    {{ scope.row.shiftVO.shiftName.substring(0, 3) }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column prop="shiftVO.shiftBegintime" label="开始时间" align="center" />
              <el-table-column prop="shiftVO.shiftEndtime" label="结束时间" align="center" />


              <el-table-column fixed="right" label="操作" :width="80" align="center" v-if="operatorShow">
                <template #default="scope">
                  <div style="white-space: nowrap;">
                    <el-button size="small" type="warning" @click="handleLeaveOpen(scope.row)" :icon="CloseBold" circle
                      :disabled="!(scope.row.scheStatus === 1 || scope.row.scheStatus === 2)">
                    </el-button>
                    <el-button size="small" type="success" @click="handleCshiftOpen(scope.row)" :icon="Switch" circle
                      :disabled="!(scope.row.scheStatus === 1 || scope.row.scheStatus === 2)">
                    </el-button>
                  </div>
                </template>
              </el-table-column>
            </el-table>

          </el-scrollbar>

        </el-card>


      </el-col>
    </el-row>
  </el-card>

  <el-dialog v-model="scheCenterDialogVisible" title="排班信息" width="500" center style="padding: 30px;">
    <template #footer>
      <div class="dialog-footer">
        <el-form :model="scheShowForm" ref="scheAddFormRef">
          <el-form-item label="排班科室">
            <el-input v-model="deptName" autocomplete="off" placeholder="请输入班次名称" disabled />
          </el-form-item>
          <el-form-item label="班次ID">
            <el-input v-model="scheShowForm.scheId" autocomplete="off" placeholder="请输入班次名称" disabled />
          </el-form-item>
          <el-form-item label="排班需求名称" prop="scheName">
            <el-input v-model="scheShowForm.scheName" autocomplete="off" placeholder="请输入排班需求名称" disabled />
          </el-form-item>
          <el-form-item label="排班需求简介" prop="scheInfo">
            <el-input v-model="scheShowForm.scheInfo" autocomplete="off" placeholder="请输入排班需求简介" maxlength="300"
              show-word-limit type="textarea" :autosize="{ minRows: 5 }" disabled />
          </el-form-item>
          <el-form-item prop="shiftId" label="排班所需班次">
            <el-select placeholder="请选择你需要的班次" v-model="scheShowForm.shiftId" disabled>
              <el-option v-for="item in allShift" :value="item.shiftId">
                {{ item.shiftName }}
              </el-option>
              <template #label>
                {{ allShift.find(item => item.shiftId === scheShowForm.shiftId)?.shiftName }}
              </template>
            </el-select>
          </el-form-item>


          <el-form-item prop="userType" label="所需医护类型">
            <el-select placeholder="请选择你需要的医护类型" v-model="scheShowForm.userType" disabled>
              <el-option v-for="item in userTypeMap" :value="item.userType">
                {{ item.typeName }}
              </el-option>
              <template #label>
                {{ userTypeMap.find(item => item.userType === scheShowForm.userType)?.typeName }}
              </template>
            </el-select>
          </el-form-item>

          <el-form-item label="所需排班日期" prop="scheDate">
            <el-date-picker type="date" placeholder="选择排班时间" style="width: 200px" v-model="scheShowForm.scheDate"
              disabled />
          </el-form-item>
        </el-form>
      </div>
    </template>
  </el-dialog>

  <el-dialog v-model="leaveDialogVisible" title="请假理由" width="500px" center style="border-radius: 12px; padding: 20px;">
    <div style="background-color: #f9f9f9; border-radius: 8px; padding: 16px;">
      <el-input v-model="leaveAdd.leaveInfo" type="textarea" placeholder="暂无请假理由" show-word-limit maxlength="300"
        :autosize="{ minRows: 6, maxRows: 10 }" style="width: 100%;" />
    </div>

    <template #footer>
      <div style="display: flex; justify-content: flex-end; gap: 12px; margin-top: 10px;">
        <el-button @click="leaveDialogVisible = false" type="default" style="border-radius: 6px;">
          关闭
        </el-button>
        <el-button @click="handleLeave" type="primary" style="border-radius: 6px;">
          确认
        </el-button>
      </div>
    </template>
  </el-dialog>

  <el-dialog v-model="shiftDialogVisible" title="申请调班" width="500px" center class="custom-dialog"
    style="border-radius: 12px; padding: 20px;">
    <div class="dialog-content">
      <el-form :model="cshiftForm" ref="scheAddFormRef" :rules="cshiftFormRules" label-position="top">
        <el-form-item label="调班排班日期" prop="cshiftTargetDay" required>
          <el-date-picker v-model="cshiftForm.cshiftTargetDay" type="date" :disabled-date="disabledBeforeToday"
            placeholder="选择排班时间" style="width: 100%;" />
        </el-form-item>

        <el-form-item label="调班所需班次" prop="shiftTargetId" required>
          <el-select v-model="cshiftForm.shiftTargetId" placeholder="请选择你需要的班次" style="width: 100%;">
            <el-option v-for="item in allShift" :key="item.shiftId" :value="item.shiftId" :label="item.shiftName" />
          </el-select>
        </el-form-item>

        <el-form-item label="调班理由" prop="cshiftInfo" required>
          <el-input v-model="cshiftForm.cshiftInfo" type="textarea" :autosize="{ minRows: 5 }" placeholder="请输入调班理由"
            maxlength="300" show-word-limit />
        </el-form-item>
      </el-form>

      <div class="dialog-footer">
        <el-button type="primary" size="large" @click="handleSwitch" style="width: 100%; border-radius: 8px;">
          提交调班
        </el-button>
      </div>
    </div>
  </el-dialog>
</template>

<script lang="ts" setup>
import { Avatar, Edit, Delete, Select, CloseBold, Plus, Minus, Switch } from '@element-plus/icons-vue'
import { ref, onMounted, reactive } from 'vue'
import * as ScheInterface from '@/stores/interface/ScheInterface.ts';
import { shiftStyleMap } from '@/stores/store';
import * as ScheRequest from '@/api/sche.ts'
import * as ShiftRequest from '@/api/shift.ts'
import * as UserRequest from '@/api/user.ts'
import * as LeaveRequest from '@/api/leave.ts'
import * as CshiftRequest from '@/api/cshift.ts'
import type { ShiftVO } from '@/stores/interface/ShiftInterface';
import type { UserVO } from '@/stores/interface/UserInterface';
import { userTypeMap, scheStatusMap, LeaveStatusMap } from '@/stores/store.ts';
import { useDepartmentStore } from '@/stores/store.ts'
import { ElDrawer, ElMessageBox, checkTagEmits } from 'element-plus'
import type { FormRules, FormInstance } from 'element-plus'
import { ElMessage } from 'element-plus'
import * as LeaveInterface from '@/stores/interface/LeaveInterface'
import type { CalendarDateType, CalendarInstance } from 'element-plus'
import { useUserStore } from '@/stores/store.ts'
import * as CshiftInterface from '@/stores/interface/CshiftInterface'
import * as DeptRequest from '@/api/dept.ts'

const deptName = ref();
const scheAddFormRef = ref<FormInstance | null>(null);
const shiftDialogVisible = ref(false)
const leaveDialogVisible = ref(false)
const userStore = useUserStore()
const calendarLoading = ref(false)
const operatorShow = ref(false)
const alreadyLoading = ref(false)
const weiLoading = ref(false)
const cshiftFormRules = ref<FormRules<CshiftInterface.CshiftAddDTO>>({
  cshiftInfo: [
    { required: true, message: '请输入调班理由', trigger: ['blur', 'change'] },
    { min: 1, max: 500, message: '调班理由长度在1到300之间', trigger: ['blur', 'change'] }
  ],
  shiftTargetId: [
    { required: true, message: '请选择班次', trigger: ['blur', 'change'] }
  ],
  cshiftTargetDay: [
    { required: true, message: '请选择日期', trigger: ['blur', 'change'] }
  ]
})
const disabledBeforeToday = (date: Date) => {
  const today = new Date()
  today.setHours(0, 0, 0, 0) // 清除时间部分
  return date <= today // 禁用今天之前的日期
}

const cshiftForm = ref<CshiftInterface.CshiftAddDTO>({
  scheId: 0,
  cshiftTargetDay: null,
  shiftTargetId: 0,
  cshiftInfo: ''
});


const leaveAdd = ref<LeaveInterface.LeaveAddInterface>({
  scheId: 0,
  leaveInfo: "",
})

const currentDate = ref<ScheInterface.DateCellData>({
  day: new Date().toISOString().split('T')[0],  // 获取今天的日期，格式为 'YYYY-MM-DD'
  isSelected: false,  // 默认未选中
});
const scheCenterDialogVisible = ref(false)
const centerDialogVisible = ref(false)
const loading = ref(false);
const selectUserShow = ref(false);
const auditShow = ref(true);
const departmentStore = useDepartmentStore()
const scheUser = ref<ScheInterface.SchedulingUser>({
  scheId: 0,
  userId: ""
});
const calendar = ref<CalendarInstance>()
const selectDate = (val: CalendarDateType) => {
  if (!calendar.value) return
  calendar.value.selectDate(val)
}
const tableData = ref<ScheInterface.SchedulingVO[]>([]);
const searchForm = ref<ScheInterface.ScheUserSearchForm>({
  userId: null,
  selectDate: null,
});
const formatDate = (row: LeaveInterface.LeaveFormInterface) => {
  if (!row.leaveDate) return '';
  const dateObj = new Date(row.leaveDate);
  const year = dateObj.getFullYear();
  const month = (dateObj.getMonth() + 1).toString().padStart(2, '0');
  const day = dateObj.getDate().toString().padStart(2, '0');
  return `${year}-${month}-${day}`;
};
const allShift = ref<ShiftVO[]>([])
const allUser = ref<UserVO[]>([])
const allDoctors = ref<UserVO[]>([])
const allNurses = ref<UserVO[]>([])
const rightListData = ref<ScheInterface.SchedulingVO[]>([])
const enableListData = ref<ScheInterface.SchedulingVO[]>([])

const scheShowForm = ref<ScheInterface.SchedulingEditForm>({
  scheId: null,
  scheName: "",
  shiftId: null,
  userType: null,
  scheInfo: "",
  scheDate: null,
  userNum: null,
  scheStatus: 1,
  deptId: departmentStore.departmentData.deptId,
});

onMounted(async () => {
  loading.value = true
  calendarLoading.value = true;
  const shiftlist = await ShiftRequest.shiftGetAllService(departmentStore.departmentData.deptId);
  const userlist = await UserRequest.userGetAllService(departmentStore.departmentData.deptId);
  const doctorlist = await UserRequest.userGetAllDoctorService(departmentStore.departmentData.deptId);
  const nurselist = await UserRequest.userGetAllNurseService(departmentStore.departmentData.deptId);

  allUser.value = userlist.data;
  allShift.value = shiftlist.data;
  allDoctors.value = doctorlist.data;
  allNurses.value = nurselist.data;
  calendarLoading.value = false;
  //上述是数据预导入

  searchForm.value.userId = userStore.userData.userId;
  reloadAllTable();
  ElMessage({
    message: "数据获取成功",
    type: 'success',
  })
});

const reloadtableData = async () => {
  const response = await ScheRequest.scheGetAllByUserIdService(departmentStore.departmentData.deptId, searchForm.value.userId as string);
  tableData.value = response.data;
  ElMessage({
    message: "数据获取成功",
    type: 'success',
  })
}
const showScheTable = async (scheId: number) => {
  const response = await ScheRequest.scheGetOneService(scheId);
  scheShowForm.value = response.data
  try {
    const resp = await DeptRequest.deptGetOneService(scheShowForm.value.deptId as string);
    deptName.value = resp.data.deptName
  } catch {
    deptName.value = "该科室已经不存在"
  } finally {
    scheCenterDialogVisible.value = true;
  }
}
const styleFunction = (styleId: number): string | undefined => {
  const style = shiftStyleMap.find(item => item.shiftStyleId === styleId);
  return style ? style.shiftStyleType : undefined;
};

// 获取某个日期的排班信息
const getScheduleForDate = (date: string) => {
  const targetDate = new Date(date);
  return tableData.value.filter(item => {
    const scheDate = new Date(item.scheDate);
    return scheDate.getFullYear() === targetDate.getFullYear() &&
      scheDate.getMonth() === targetDate.getMonth() &&
      scheDate.getDate() === targetDate.getDate();
  });
};

const getEnableSche = async (targetDate: Date) => {
  const response = await ScheRequest.scheGetEnableScheService(searchForm.value.userId as string, targetDate)
  enableListData.value = response.data
}

const handleSelectChange = () => {
  currentDate.value.day = new Date().toISOString().split('T')[0]
  reloadAllTable();
}

const handleDateClick = async (data: ScheInterface.DateCellData) => {
  alreadyLoading.value = true;
  weiLoading.value = true;
  rightListData.value = await getScheduleForDate(data.day);
  const targetDate = new Date(data.day);
  currentDate.value = data;
  await getEnableSche(targetDate);

  // 获取今天的日期
  const tomorrow = new Date();
  tomorrow.setHours(0, 0, 0, 0);
  tomorrow.setDate(tomorrow.getDate() + 1);



  // 如果 data.day 的日期大于今天，设置 operatorShow 为 true，否则为 false
  operatorShow.value = targetDate > tomorrow;

  alreadyLoading.value = false;
  weiLoading.value = false;
}


const reloadAllTable = async () => {
  calendarLoading.value = true;
  alreadyLoading.value = true;
  weiLoading.value = true;
  const response = await ScheRequest.scheGetAllByUserIdService(null, searchForm.value.userId as string);
  tableData.value = response.data;
  rightListData.value = getScheduleForDate(currentDate.value?.day as string);
  const targetDate = new Date(currentDate.value?.day as string);
  await getEnableSche(targetDate);
  calendarLoading.value = false;
  alreadyLoading.value = false;
  weiLoading.value = false;
}

const handleLeaveOpen = async (row: ScheInterface.SchedulingVO) => {
  leaveAdd.value.leaveInfo = "";
  leaveDialogVisible.value = true;
  leaveAdd.value.scheId = row.scheId;
}

const handleCshiftOpen = async (row: ScheInterface.SchedulingVO) => {
  cshiftForm.value.cshiftInfo = "";
  cshiftForm.value.cshiftTargetDay = null;
  cshiftForm.value.scheId = row.scheId;
  cshiftForm.value.shiftTargetId = null;
  shiftDialogVisible.value = true;
}
const handleLeave = async () => {
  await LeaveRequest.leaveAddService(leaveAdd.value);
  reloadAllTable();
  leaveDialogVisible.value = false;
}

const handleSwitch = async () => {
  if (!scheAddFormRef.value) return;

  scheAddFormRef.value.validate((valid) => {
    if (!valid) {
      console.log('表单校验失败，阻止提交');
      return;
    }

    loading.value = true;
    try {
      setTimeout(async () => {
        await CshiftRequest.cshiftAddService(cshiftForm.value);
        shiftDialogVisible.value = false;
        reloadAllTable();
      }, 300);
    } catch {
      loading.value = false
    }

  });
}

</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.custom-dialog ::v-deep .el-dialog__header {
  font-size: 20px;
  font-weight: bold;
  text-align: center;
  padding-bottom: 10px;
}

.custom-dialog ::v-deep .el-dialog__body {
  padding: 20px;
  background-color: #f9f9f9;
  border-radius: 12px;
}

.dialog-content {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.dialog-footer {
  margin-top: 20px;
}
</style>
