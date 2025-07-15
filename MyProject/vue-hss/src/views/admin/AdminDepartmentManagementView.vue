<template>
  <el-card style="box-sizing: border-box; min-height: 95%; margin: 10px;">
    <template #header>
      <div class="card-header">
        <span>部门管理</span>
        <el-button type="primary" @click="addDialogOpen">新增部门</el-button>
      </div>
    </template>

    <div style="display: flex; align-items: center; gap: 20px; margin-bottom: 10px;">
      <span style="font-size: 12px; color: #333;">部门编号:</span>
      <el-input v-model="searchForm.deptId" style="width: 150px" clearable size="small" />
      <span style="font-size: 12px; color: #333;">部门名称:</span>
      <el-input v-model="searchForm.deptName" style="width: 150px" clearable size="small" />
      <div>
        <el-button size="small" @click="handleSearchReset">重置</el-button>
        <el-button type="primary" size="small" @click="handleSearchConfirm">搜索</el-button>
        <el-button type="danger" size="small" @click="handleDeleteByList">批量删除</el-button>
      </div>
    </div>

    <el-table :data="tableData" style="width: 100%" @selection-change="handleSelectionChange" v-loading="loading"
      element-loading-text="加载中..." stripe>
      <el-table-column type="selection" min-width="5%" align="center" />

      <el-table-column label="部门ID" prop="deptId" min-width="15%" align="center" />

      <el-table-column label="部门名称" min-width="20%" align="center">
        <template #default="scope">
          <el-tag size="large">{{ scope.row.deptName }}</el-tag>
        </template>
      </el-table-column>

      <el-table-column label="部门描述" prop="deptInfo" min-width="25%" />

      <el-table-column prop="deptCreatetime" label="创建时间" width="200" :formatter="formatDate" align="center" />
      <el-table-column label="操作" align="center" min-width="15%">
        <template #default="scope">
          <div style="white-space: nowrap;">
            <el-button size="small" type="primary" @click="editDialogOpen(scope.row)" :icon="Edit" circle />
            <el-button size="small" type="danger" @click="handleDeleteOne(scope.row)" :icon="Delete" circle />
            <el-button size="small" type="success" @click="openDraw(scope.row)" :icon="UserFilled" circle />
          </div>
        </template>
      </el-table-column>
    </el-table>
    <template #footer>
      <el-pagination :current-page="pageData.current" :page-size="pageData.size" :background="true"
        layout="total, sizes, prev, pager, next, jumper" :total="pageData.total" @size-change="handleSizeChange"
        @current-change="handleCurrentChange" />
    </template>

    <!-- Add Department Dialog -->
    <el-drawer v-model="addDialog" title="添加部门" direction="rtl" size="30%">
      <div class="drawer__content">
        <el-form :model="addForm" :rules="departmentFormRules" ref="departmentAddFormRef">
          <el-form-item label="部门编号" prop="deptId" required>
            <el-input v-model="addForm.deptId" placeholder="请输入部门编号" />
          </el-form-item>

          <el-form-item label="部门名称" prop="deptName" required>
            <el-input v-model="addForm.deptName" placeholder="请输入部门名称" />
          </el-form-item>

          <el-form-item label="部门描述" prop="deptInfo">
            <el-input v-model="addForm.deptInfo" type="textarea" placeholder="请输入部门描述" :rows="3" />
          </el-form-item>
        </el-form>
        <div class="drawer__footer">
          <el-button @click="handleDialogReset">重置</el-button>
          <el-button type="primary" :loading="loading" @click="handleAddOne">
            {{ loading ? '添加 ...' : '添加' }}
          </el-button>
        </div>
      </div>
    </el-drawer>

    <!-- Edit Department Dialog -->
    <el-drawer v-model="editDialog" title="编辑部门" direction="rtl" size="30%">
      <div class="drawer__content">
        <el-form :model="editForm" :rules="departmentFormRules" ref="departmentEditFormRef">
          <el-form-item label="部门编号">
            <el-input v-model="editForm.deptId" disabled />
          </el-form-item>

          <el-form-item label="部门名称" prop="deptName" required>
            <el-input v-model="editForm.deptName" placeholder="请输入部门名称" />
          </el-form-item>

          <el-form-item label="部门描述" prop="deptInfo">
            <el-input v-model="editForm.deptInfo" type="textarea" placeholder="请输入部门描述" :rows="3" />
          </el-form-item>
        </el-form>
        <div class="drawer__footer">
          <el-button type="primary" :loading="loading" @click="handleEditOne">
            {{ loading ? '提交修改 ...' : '提交修改' }}
          </el-button>
        </div>
      </div>
    </el-drawer>
  </el-card>

  <el-drawer v-model="drawer" :with-header="false" size="30%">
    <div style="display: flex; align-items: center; margin-bottom: 10px;">
      <h3 style="margin: 0; font-weight: bold; flex: 1;">医生列表</h3>
      <el-icon style="font-size: 28px; color: #67C23A; cursor: pointer; margin-right: 10px;">
        <CirclePlusFilled @click="showAddDialog(1)" />
      </el-icon>
      <el-icon style="font-size: 28px; color: #F56C6C; cursor: pointer; margin-right: 30px;"
        @click="handleDelBathDocDept">
        <RemoveFilled />
      </el-icon>
    </div>
    <el-scrollbar style="height: 40%; margin-bottom: 10px;">
      <el-table :data="deptDoctors" stripe style="width: 100%" @selection-change="handleDelDocSelectionChange">
        <el-table-column type="selection" min-width="20%" align="center" :selectable="selectable" />
        <el-table-column prop="userId" label="医生编号" align="center" />
        <el-table-column prop="userName" label="医生名称" align="center" />
        <el-table-column label="医生头像" align="center">
          <template #default="scope">
            <el-avatar :size="50" :src="scope.row.userAvatar" />
          </template>
        </el-table-column>
      </el-table>
    </el-scrollbar>
    <div style="display: flex; align-items: center; margin-bottom: 10px;">
      <h3 style="margin: 0; font-weight: bold; flex: 1;">护士列表</h3>
      <el-icon style="font-size: 28px; color: #67C23A; cursor: pointer; margin-right: 10px;">
        <CirclePlusFilled @click="showAddDialog(2)" />
      </el-icon>
      <el-icon style="font-size: 28px; color: #F56C6C; cursor: pointer; margin-right: 30px;"
        @click="handleDelBathNurDept">
        <RemoveFilled />
      </el-icon>
    </div>
    <el-scrollbar style="height: 40%;">
      <el-table :data="deptNurses" stripe style="width: 100%" @selection-change="handleDelNurSelectionChange">
        <el-table-column type="selection" min-width="20%" align="center" :selectable="selectable" />
        <el-table-column prop="userId" label="护士编号" align="center" />
        <el-table-column prop="userName" label="护士名称" align="center" />
        <el-table-column label="护士头像" align="center">
          <template #default="scope">
            <el-avatar :size="50" :src="scope.row.userAvatar" />
          </template>
        </el-table-column>
      </el-table>
    </el-scrollbar>
  </el-drawer>

  <el-dialog v-model="addUserDeptVisible" title="添加医护" width="600px">
    <!-- 🔍 搜索栏 -->
    <div style="margin-bottom: 10px;">
      <span style="margin-right: 10px;">医护姓名:</span>
      <el-input style="width: 150px" clearable size="small" v-model="searchUserName" />
      <el-button type="primary" size="small" @click="handleSearch" style="margin-left: 10px;">搜索</el-button>

    </div>

    <el-scrollbar style="height: 400px; margin-bottom: 10px;">
      <el-table :data="addList" stripe style="width: 100%" @selection-change="handleAddSelectionChange">
        <el-table-column type="selection" min-width="20%" align="center" :selectable="selectable" />
        <el-table-column prop="userId" label="医护编号" align="center" />
        <el-table-column prop="userName" label="医护名称" align="center" />
        <el-table-column label="医护头像" align="center">
          <template #default="scope">
            <el-avatar :size="50" :src="scope.row.userAvatar" />
          </template>
        </el-table-column>
      </el-table>
    </el-scrollbar>

    <!-- 底部按钮插槽 -->
    <template #footer>
      <div style="text-align: right;">
        <el-button @click="addUserDeptVisible = false">取消</el-button>
        <el-button type="primary" @click="handleAddUserDeptConfirm">确认</el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import * as DepartmentInterface from '@/stores/interface/DepartmentInterface.ts'
import { Avatar, Edit, Delete, UserFilled, CirclePlusFilled, RemoveFilled } from '@element-plus/icons-vue'
import { ref, onMounted, reactive } from 'vue'
import * as ScheInterface from '@/stores/interface/ScheInterface.ts';
import { shiftStyleMap } from '@/stores/store';
import * as ScheRequest from '@/api/sche.ts'
import * as ShiftRequest from '@/api/shift.ts'
import * as UserRequest from '@/api/user.ts'
import * as DeptRequest from '@/api/dept.ts'
import * as UserDeptRequest from '@/api/userdept.ts'
import type { ShiftVO } from '@/stores/interface/ShiftInterface';
import type { UserVO } from '@/stores/interface/UserInterface';
import { userTypeMap, scheStatusMap } from '@/stores/store.ts';
import { useDepartmentStore } from '@/stores/store.ts'
import { ElDrawer, ElMessageBox } from 'element-plus'
import type { FormRules, FormInstance } from 'element-plus'
import { ElMessage } from 'element-plus'
import { all } from 'axios';

const searchUserName = ref("")
const addUserDeptVisible = ref(false)
const departmentStore = useDepartmentStore();
const drawer = ref(false)
const selectedRows = ref<DepartmentInterface.Department[]>([]);
const selectAddRows = ref<UserVO[]>([])
const selectedRowsDelDoc = ref<UserVO[]>([]);
const selectedRowsDelNur = ref<UserVO[]>([]);

const handleSearch = async () => {
  const resp = await UserDeptRequest.userDeptDocGetListService(searchUserName.value)
  console.log(resp.data)
  addList.value = resp.data
  addUserDeptVisible.value = true;
}

const handleAddUserDeptConfirm = async () => {
  const uds = selectAddRows.value.map(item => {
    return {
      userId: item.userId,
      deptId: nowDept.value
    }
  });
  await UserDeptRequest.userDeptDocAddListService(uds);
  addUserDeptVisible.value = false;
  await reloadDocList();

}

const showAddDialog = async (mod: number) => {
  if (mod === 1) {
    const resp = await UserDeptRequest.userDeptDocGetListService("")
    console.log(resp.data)
    addList.value = resp.data
    addUserDeptVisible.value = true;
  } else {
    const resp = await UserDeptRequest.userDeptNurGetListService("")
    console.log(resp.data)
    addList.value = resp.data
    addUserDeptVisible.value = true;
  }

}
const handleDelBathDocDept = async () => {
  const userDepts = selectedRowsDelDoc.value.map(item => {
    return {
      userId: item.userId,
      deptId: nowDept.value
    }
  })
  await UserDeptRequest.userDeptDeleteListService(userDepts)
  await reloadDocList()
}

const handleDelBathNurDept = async () => {
  const userDepts = selectedRowsDelNur.value.map(item => {
    return {
      userId: item.userId,
      deptId: nowDept.value
    }
  })
  await UserDeptRequest.userDeptDeleteListService(userDepts)
  await reloadDocList()
}

const selectable = (row: any, index: number) => {
  // auditShow = false 时全部禁选，或者只有 leaveStatus === 1 才能勾选
  return true
}
const formatDate = (row: DepartmentInterface.Department) => {
  if (!row.deptCreatetime) return '';
  const dateObj = new Date(row.deptCreatetime);
  const year = dateObj.getFullYear();
  const month = (dateObj.getMonth() + 1).toString().padStart(2, '0');
  const day = dateObj.getDate().toString().padStart(2, '0');
  return `${year}-${month}-${day}`;
};

// Define reactive variables
const searchForm = ref<DepartmentInterface.SearcheForm>({
  deptId: '',
  deptName: ''
})

const addForm = ref<DepartmentInterface.Department>({
  deptId: '',
  deptName: '',
  deptInfo: '',
  deptCreatetime: new Date() // 或者 new Date('') 表示空时间
})

const editForm = ref<DepartmentInterface.Department>({
  deptId: '',
  deptName: '',
  deptInfo: '',
  deptCreatetime: new Date() // 或者 new Date('') 表示空时间
})

const tableData = ref<DepartmentInterface.Department[]>([])
const pageData = ref({
  current: 1,
  size: 10,
  total: 0
})


const addList = ref<UserVO[]>([])
const nurLoading = ref(false)
const nowDept = ref();
const addDialog = ref(false)
const editDialog = ref(false)
const loading = ref(false)
const allShift = ref<ShiftVO[]>([])
const allUser = ref<UserVO[]>([])
const allDoctors = ref<UserVO[]>([])
const allNurses = ref<UserVO[]>([])
const selectUsers = ref<UserVO[]>([])
const deptDoctors = ref<UserVO[]>([])
const deptNurses = ref<UserVO[]>([])

// Form validation rules
const departmentFormRules = {
  deptId: [{ required: true, message: '请输入部门编号', trigger: 'blur' }],
  deptName: [{ required: true, message: '请输入部门名称', trigger: 'blur' }]
}

// Methods (to be implemented)
const handleSearchReset = () => {
  searchForm.value = { deptId: '', deptName: '' }
}

const handleSearchConfirm = async () => {
  await reloadtableData()
  // Implement search logic
}

const handleDeleteByList = async () => {
  await DeptRequest.deptDeleteListService(selectedRows.value);
  await reloadtableData()
  // Implement bulk delete logic
}

const addDialogOpen = () => {
  addDialog.value = true
}

const editDialogOpen = (row: DepartmentInterface.Department) => {
  editForm.value = { ...row }
  editDialog.value = true
}

const handleAddOne = async () => {
  await DeptRequest.deptAddService(addForm.value);
  await reloadtableData();
  addDialog.value = false;
  addForm.value = {
    deptId: '',
    deptName: '',
    deptInfo: '',
    deptCreatetime: new Date() // 或者 new Date('') 表示空时间
  }
}

const handleEditOne = async () => {
  await DeptRequest.deptUpdateService(editForm.value);
  await reloadtableData();
  editDialog.value = false;
  // Implement edit department logic
}

const handleDeleteOne = async (row: DepartmentInterface.Department) => {
  await DeptRequest.deptDeleteService(row.deptId)
  reloadtableData();
  // Implement single item delete logic
}

const handleSizeChange = async (val: number) => {
  pageData.value.size = val
  await reloadtableData();
}

const handleCurrentChange = async (val: number) => {
  pageData.value.current = val
  await reloadtableData();
  // Refresh data
}

const handleDialogReset = () => {
  addForm.value = {
    deptId: '',
    deptName: '',
    deptInfo: '',
    deptCreatetime: new Date() // 或者 new Date('') 表示空时间
  }
}

const handleSelectionChange = (selection: DepartmentInterface.Department[]) => {
  // Handle row selection
  selectedRows.value = selection;
}

const handleDelDocSelectionChange = (selection: UserVO[]) => {
  selectedRowsDelDoc.value = selection
}

const handleDelNurSelectionChange = (selection: UserVO[]) => {
  selectedRowsDelNur.value = selection
}

const handleAddSelectionChange = (selection: UserVO[]) => {
  selectAddRows.value = selection
}
const reloadtableData = async () => {
  loading.value = true;
  const response = await DeptRequest.deptGetAllPagesService(pageData.value.current, pageData.value.size, searchForm.value)
  console.log(response.data.items)
  tableData.value = response.data.items
  loading.value = false;
}

const openDraw = async (row: DepartmentInterface.Department) => {
  nowDept.value = row.deptId;
  await reloadDocList();
  drawer.value = !drawer.value;
}

const reloadDocList = async () => {
  const response1 = await UserRequest.userGetAllDoctorService(nowDept.value)
  deptDoctors.value = response1.data
  const response2 = await UserRequest.userGetAllNurseService(nowDept.value)
  deptNurses.value = response2.data
}

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

  await reloadtableData()
  ElMessage({
    message: "数据获取成功",
    type: 'success',
  })
});
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
