<template>
  <el-card style="box-sizing: border-box;min-height: 95%; margin: 10px; min-height: 95%;">
    <template #header>
      <div class="card-header">
        <span>请假审核</span>
      </div>
    </template>

    <div style="display: flex; align-items: center; gap: 20px; margin-bottom: 10px;">
      <span style="font-size: 12px; color: #333;">请假编号:</span>
      <el-input style="width: 150px" clearable size="small" v-model="searchForm.leaveId" />
      <span style="font-size: 12px; color: #333;">医护人员:</span>
      <el-select placeholder="选择医护人员" style="width: 150px" size="small" v-model="searchForm.userId"
        @change="handleSearch">
        <el-option v-for="item in allUser" :value="item.userId">
          {{ item.userName }}
        </el-option>
        <template #label>
          {{ allUser.find(item => item.userId === searchForm.userId)?.userName }}
        </template>
      </el-select>

      <span style="font-size: 12px; color: #333;">审核状态:</span>
      <el-select placeholder="全部" style="width: 100px" size="small" v-model="searchForm.leaveStatus"
        @change="handleSearch">
        <el-option v-for="item in LeaveStatusMap" :value="item.status">
          {{ item.statusName }}
        </el-option>
        <template #label>
          {{ LeaveStatusMap.find(item => item.status === searchForm.leaveStatus)?.statusName }}
        </template>
      </el-select>
      <el-switch v-model="searchForm.isPast" size="small" active-text="查看已过期审核" @click="reloadtableData" />

      <div>
        <el-button size="small" @Click="handleReset">重置</el-button>
        <el-button type="primary" size="small" @click="handleSearch">搜索</el-button>
        <el-button type="danger" size="small" @click="handleRefuseByList">批量拒绝</el-button>
        <el-button type="success" size="small" @click="handleAgreeByList">批量同意</el-button>
      </div>
    </div>



    <el-table :data="tableData" style="width: 100%" @selection-change="handleSelectionChange" v-loading="loading"
      element-loading-text="加载中..." stripe>

      <el-table-column v-if="auditShow" fixed="left" type="selection" :selectable="selectable" align="center" />

      <el-table-column prop="leaveId" label="请假ID" width="150" align="center" />
      <el-table-column prop="userId" label="医护姓名" width="200" align="center">
        <template #default="scope">
          <span @click="showUserTable(scope.row.userId)" style="
            cursor: pointer;
            text-decoration: underline;
            font-size: 16px;
            font-style: italic;
          ">
            {{ allUser.find(user => user.userId === scope.row.userId)?.userName }}
          </span>
        </template>
      </el-table-column>

      <el-table-column prop="scheId" label="排班ID" width="150" align="center">
        <template #default="scope">
          <span @click="showScheTable(scope.row.scheId)" style="
            cursor: pointer;
            text-decoration: underline;
            font-size: 16px;
            font-style: italic;
          ">
            {{ scope.row.scheId }}
          </span>
        </template>
      </el-table-column>
      <el-table-column prop="leaveDate" label="请假目标日期" width="200" :formatter="formatDate" align="center" />
      <el-table-column prop="leaveStatus" label="审批状态" width="150" align="center">
        <template #default="scope">
          <el-text type="info" v-if="scope.row.leaveStatus == 1">未审核</el-text>
          <el-text type="success" v-if="scope.row.leaveStatus == 2">审核通过</el-text>
          <el-text type="danger" v-if="scope.row.leaveStatus == 3">审核不通过</el-text>
        </template>
      </el-table-column>
      <el-table-column prop="leaveInfo" label="请假理由" width="400" show-overflow-tooltip align="center" />

      <el-table-column fixed="right" label="审核操作" min-width="120" align="center">
        <template #default="scope">
          <div style="white-space: nowrap;">
            <el-button size="small" type="danger" @click="handleLeaveAudit(scope.row, false)" :icon="CloseBold" circle
              style="margin-left: 15px;" :disabled="!(auditShow && scope.row.leaveStatus == 1)">
            </el-button>
            <el-button size="small" type="success" @click="handleLeaveAudit(scope.row, true)" :icon="Select" circle
              style="margin-left: 15px;" :disabled="!(auditShow && scope.row.leaveStatus == 1)">
            </el-button>
          </div>
        </template>
      </el-table-column>
    </el-table>
    <template #footer>
      <el-pagination :current-page="pageData.current" :page-size="pageData.size" :background="true"
        layout="total, sizes, prev, pager, next, jumper" :total="pageData.total" @size-change="handleSizeChange"
        @current-change="handleCurrentChange" />
    </template>
  </el-card>

  <el-dialog v-model="scheCenterDialogVisible" title="排班信息" width="500" center style="padding: 30px;">
    <template #footer>
      <div class="dialog-footer">
        <el-form :model="scheShowForm" ref="scheAddFormRef">
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

  <el-dialog v-model="userCenterDialogVisible" title="用户信息" width="500" center style="padding: 30px;">
    <template #footer>
      <div class="dialog-footer">
        <el-form :model="userShowForm" ref="userFormRef">
          <el-form-item label="用户ID">
            <el-input v-model="userShowForm.userId" autocomplete="off" placeholder="请输入用户ID" disabled />
          </el-form-item>
          <el-form-item label="用户名" prop="userName">
            <el-input v-model="userShowForm.userName" autocomplete="off" placeholder="请输入用户名" disabled />
          </el-form-item>
          <el-form-item label="性别" prop="userGender">
            <el-input v-model="userShowForm.userGender" autocomplete="off" placeholder="请输入性别" disabled />
          </el-form-item>
          <el-form-item label="手机号" prop="userPhone">
            <el-input v-model="userShowForm.userPhone" autocomplete="off" placeholder="请输入手机号" disabled />
          </el-form-item>
          <el-form-item label="邮箱" prop="userEmail">
            <el-input v-model="userShowForm.userEmail" autocomplete="off" placeholder="请输入邮箱" disabled />
          </el-form-item>
          <el-form-item label="用户信息" prop="userInfo">
            <el-input v-model="userShowForm.userInfo" autocomplete="off" placeholder="请输入用户信息" maxlength="300"
              show-word-limit type="textarea" :autosize="{ minRows: 5 }" disabled />
          </el-form-item>
          <el-form-item label="用户头像" prop="userAvatar">
            <el-input v-model="userShowForm.userAvatar" autocomplete="off" placeholder="请输入头像URL" disabled />
          </el-form-item>
          <el-form-item label="权限级别" prop="userPermission">
            <el-input v-model="userShowForm.userPermission" autocomplete="off" placeholder="请输入权限级别" disabled />
          </el-form-item>
          <el-form-item label="出生日期" prop="userBirthday">
            <el-date-picker type="date" placeholder="选择出生日期" style="width: 200px" v-model="userShowForm.userBirthday"
              disabled />
          </el-form-item>
          <el-form-item label="注册时间" prop="userRegtime">
            <el-date-picker type="date" placeholder="选择注册时间" style="width: 200px" v-model="userShowForm.userRegtime"
              disabled />
          </el-form-item>
          <el-form-item label="用户类型" prop="userType">
            <el-select placeholder="请选择用户类型" v-model="userShowForm.userType" disabled>
              <el-option v-for="item in userTypeMap" :value="item.userType">
                {{ item.typeName }}
              </el-option>
              <template #label>
                {{ userTypeMap.find(item => item.userType === userShowForm.userType)?.typeName }}
              </template>
            </el-select>
          </el-form-item>
          <el-form-item label="用户主动性" prop="userProactivity">
            <el-input v-model="userShowForm.userProactivity" autocomplete="off" placeholder="请输入主动性" disabled />
          </el-form-item>
        </el-form>
      </div>
    </template>
  </el-dialog>
</template>

<script lang="ts" setup>
import { Avatar, Edit, Delete, Select, CloseBold } from '@element-plus/icons-vue'
import { ref, onMounted, reactive } from 'vue'
import * as ScheInterface from '@/stores/interface/ScheInterface.ts';
import { shiftStyleMap } from '@/stores/store';
import * as ScheRequest from '@/api/sche.ts'
import * as ShiftRequest from '@/api/shift.ts'
import * as UserRequest from '@/api/user.ts'
import * as LeaveRequest from '@/api/leave.ts'
import type { ShiftVO } from '@/stores/interface/ShiftInterface';
import type { UserVO } from '@/stores/interface/UserInterface';
import { userTypeMap, scheStatusMap, LeaveStatusMap } from '@/stores/store.ts';
import { useDepartmentStore } from '@/stores/store.ts'
import { ElDrawer, ElMessageBox, checkTagEmits } from 'element-plus'
import type { FormRules, FormInstance } from 'element-plus'
import { ElMessage } from 'element-plus'
import * as LeaveInterface from '@/stores/interface/LeaveInterface'

const scheCenterDialogVisible = ref(false)
const userCenterDialogVisible = ref(false)
const loading = ref(false);
const selectUserShow = ref(false);
const departmentStore = useDepartmentStore()
const auditShow = ref(true) // 控制是否显示审核相关功能

const selectable = (row: any, index: number) => {
  // auditShow = false 时全部禁选，或者只有 leaveStatus === 1 才能勾选
  return auditShow.value && row.leaveStatus === 1
}

const scheUser = ref<ScheInterface.SchedulingUser>({
  scheId: 0,
  userId: ""
});

const pageData = ref({
  pages: 0,
  current: 1,
  size: 10,
  total: 0,
})//分页条

const tableData = ref<LeaveInterface.LeaveFormInterface[]>([]);
const selectedRows = ref<LeaveInterface.LeaveFormInterface[]>([]);
const searchForm = ref<LeaveInterface.LeaveSearchInterface>({
  leaveId: null,
  userId: null,
  leaveStatus: 1,
  isPast: false
});

const handleSizeChange = (newSize: number) => {
  pageData.value.size = newSize;
  loading.value = true;
  try {
    setTimeout(reloadtableData, 300)
  } catch (err) {
    loading.value = false
  }

};

const scheShowForm = ref<ScheInterface.SchedulingEditForm>({
  scheId: null,
  scheName: "",
  shiftId: null,
  userType: null,
  scheInfo: "",
  scheDate: null,
  userNum: null,
  scheStatus: 1,
  deptId: departmentStore.departmentData.deptId
});

const userShowForm = ref<UserVO>({
  userId: "",
  userName: "",
  userGender: "",
  userPhone: "",
  userEmail: "",
  userInfo: "",
  userAvatar: "",
  userPermission: 0,
  userBirthday: new Date("2000-01-01"),
  userRegtime: new Date("2020-01-01"),
  userType: 0,
  userProactivity: 0
})

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
const selectUsers = ref<UserVO[]>([])

onMounted(async () => {
  loading.value = true
  const shiftlist = await ShiftRequest.shiftGetAllService(departmentStore.departmentData.deptId);
  const userlist = await UserRequest.userGetAllService(departmentStore.departmentData.deptId);
  const doctorlist = await UserRequest.userGetAllDoctorService(departmentStore.departmentData.deptId);
  const nurselist = await UserRequest.userGetAllNurseService(departmentStore.departmentData.deptId);

  allUser.value = userlist.data;
  allShift.value = shiftlist.data;
  allDoctors.value = doctorlist.data;
  allNurses.value = nurselist.data;

  reloadtableData()
  ElMessage({
    message: "数据获取成功",
    type: 'success',
  })
});

const handleCurrentChange = (newPage: number) => {
  pageData.value.current = newPage;
  loading.value = true;
  try {
    setTimeout(reloadtableData, 300)
  } catch (err) {
    loading.value = false
  }
}

const reloadtableData = async () => {
  loading.value = true;
  const response = await LeaveRequest.leaveGetAllService(pageData.value.current, pageData.value.size, departmentStore.departmentData.deptId, searchForm.value)
  auditShow.value = !searchForm.value.isPast
  tableData.value = response.data.items;
  pageData.value.current = response.data.current;
  pageData.value.pages = response.data.pages;
  pageData.value.size = response.data.size;
  pageData.value.total = response.data.total;
  loading.value = false;
}



const showScheTable = async (scheId: number) => {

  // const temp = ref<ScheInterface.ScheSearchForm>({
  //   scheId: scheId,
  //   scheName: null,
  //   scheInfo: null,
  //   beginDate: null,
  //   endDate: null,
  //   shiftId: null,
  //   userId: null,
  //   userType: null,
  //   scheStatus: null,
  //   isPast: false
  // })
  // const response = await ScheRequest.scheGetAllService(1, 100, departmentStore.departmentData.deptId, temp.value)
  // scheShowForm.value = response.data.items[0]
  // console.log(response.data.items);
  const response = await ScheRequest.scheGetOneService(scheId);
  scheShowForm.value = response.data
  scheCenterDialogVisible.value = true;

}

const showUserTable = async (userId: string) => {
  const response = await UserRequest.userGetOneService(userId);
  userShowForm.value = response.data
  userCenterDialogVisible.value = true;
}

const handleSelectionChange = (selection: LeaveInterface.LeaveFormInterface[]) => {
  selectedRows.value = selection;
}

const handleSearch = () => {
  loading.value = true
  reloadtableData()
}

const handleRefuseByList = async () => {

  ElMessageBox.confirm('你确定要批量拒绝这些内容吗？',
    '警告',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    }).then(async () => {
      //批量删除内容
      loading.value = true
      try {
        await LeaveRequest.leaveAuditListService(selectedRows.value, false)
        setTimeout(reloadtableData, 300)
      } catch {
        loading.value = false
      }
    })
}

const handleAgreeByList = async () => {
  ElMessageBox.confirm('你确定要批量同意这些内容吗？',
    '警告',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    }).then(async () => {
      //批量删除内容
      loading.value = true
      try {
        await LeaveRequest.leaveAuditListService(selectedRows.value, true)
        setTimeout(reloadtableData, 300)
      } catch {
        loading.value = false
      }
    })
}

const handleReset = () => {
  searchForm.value.isPast = false;
  searchForm.value.leaveId = null;
  searchForm.value.leaveStatus = 1;
  searchForm.value.userId = null;
  reloadtableData()
  //处理搜索框恢复
};


const handleLeaveAudit = async (row: LeaveInterface.LeaveFormInterface, operator: boolean) => {
  loading.value = true;
  const t = await LeaveRequest.leaveAuditOneService(row.leaveId, operator);
  await reloadtableData();

}

</script>


<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
