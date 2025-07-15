<template>
  <el-card style="box-sizing: border-box;min-height: 95%; margin: 10px; min-height: 95%;">
    <template #header>
      <div class="card-header">
        <span>排班需求管理</span>
        <el-button type="primary" @click="showAddForm">新增排班需求</el-button>
      </div>
    </template>

    <div style="display: flex; align-items: center; gap: 20px; margin-bottom: 10px;">
      <span style="font-size: 12px; color: #333;">排班编号:</span>
      <el-input style="width: 150px" clearable size="small" v-model="searchForm.scheId" />
      <span style="font-size: 12px; color: #333;">排班名称:</span>
      <el-input style="width: 150px" clearable size="small" v-model="searchForm.scheName" />
      <span style="font-size: 12px; color: #333;">排班简介:</span>
      <el-input style="width: 150px" clearable size="small" v-model="searchForm.scheInfo" />
      <span style="font-size: 12px; color: #333;">开始时间:</span>
      <el-date-picker type="date" placeholder="选择开始时间" size="small" style="width: 190px" v-model="searchForm.beginDate" />
      <span style="font-size: 12px; color: #333;">结束时间:</span>
      <el-date-picker type="date" placeholder="选择结束时间" size="small" style="width: 190px" v-model="searchForm.endDate" />
    </div>

    <div style="display: flex; align-items: center; gap: 20px; margin-bottom: 10px; ">
      <span style="font-size: 12px; color: #333;">所需班次:</span>
      <div>
        <el-select placeholder="选择班次" style="width: 150px" size="small" v-model="searchForm.shiftId">
          <el-option v-for="item in allShift" :value="item.shiftId">
            {{ item.shiftName }}
          </el-option>
          <template #label>
            {{ allShift.find(item => item.shiftId === searchForm.shiftId)?.shiftName }}
          </template>
        </el-select>
      </div>

      <span style="font-size: 12px; color: #333;">医护人员:</span>
      <el-select placeholder="选择医护人员" style="width: 150px" size="small" v-model="searchForm.userId">
        <el-option v-for="item in allUser" :value="item.userId">
          {{ item.userName }}
        </el-option>
        <template #label>
          {{ allUser.find(item => item.userId === searchForm.userId)?.userName }}
        </template>
      </el-select>
      <span style="font-size: 12px; color: #333;">医护类型:</span>
      <el-select placeholder="选择类型" style="width: 100px" size="small" v-model="searchForm.userType">
        <el-option v-for="item in userTypeMap" :value="item.userType">
          {{ item.typeName }}
        </el-option>
        <template #label>
          {{ userTypeMap.find(item => item.userType === searchForm.userType)?.typeName }}
        </template>
      </el-select>
      <span style="font-size: 12px; color: #333;">当前状态:</span>
      <el-select placeholder="全部" style="width: 100px" size="small" v-model="searchForm.scheStatus"
        @change="handleSearch">
        <el-option v-for="item in scheStatusMap" :value="item.status">
          {{ item.statusName }}
        </el-option>
        <template #label>
          {{ scheStatusMap.find(item => item.status === searchForm.scheStatus)?.statusName }}
        </template>
      </el-select>
      <div class="mt-2">
        <el-switch v-model="searchForm.isPast" size="small" active-text="显示过期排班" @click="handleSearch" />
      </div>
      <div>
        <el-button size="small" @Click="handleReset">重置</el-button>
        <el-button type="primary" size="small" @click="handleSearch">搜索</el-button>
        <el-button type="danger" size="small" @click="handleDeleteByList">批量删除</el-button>
      </div>
    </div>

    <el-table :data="tableData" style="width: 100%" @selection-change="handleSelectionChange" v-loading="loading"
      element-loading-text="加载中..." stripe>

      <el-table-column fixed="left" type="selection" :selectable="selectable" align="center" />
      <el-table-column prop="scheName" fixed label="排班名称" width="120" align="center" />
      <el-table-column prop="scheId" label="排班ID" width="120" align="center" />
      <el-table-column label="所需班次" width="120" align="center">
        <template #default="scope">
          <el-tag size="large"
            :type="shiftStyleMap.find(item => item.shiftStyleId === scope.row.shiftVO.shiftStyleId)?.shiftStyleType">
            {{ scope.row.shiftVO.shiftName }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="医护类型" width="120" align="center">
        <template #default="scope">
          <div>{{ scope.row.userType == 1 ? "医生" : "护士" }}</div>

        </template>
      </el-table-column>
      <el-table-column prop="scheInfo" label="排班简介" width="400" show-overflow-tooltip align="center" />
      <el-table-column prop="scheDate" label="排班日期" width="200" :formatter="formatDate" align="center" />
      <el-table-column label="当前状态" width="120" align="center">
        <template #default="scope">
          <el-text class="mx-1" type="info" v-if="scope.row.scheStatus == 1">● 待排班</el-text>
          <el-text class="mx-1" type="success" v-if="scope.row.scheStatus == 2">√ 已排班</el-text>
          <el-text class="mx-1" type="warning" v-if="scope.row.scheStatus == 3">- 请假中</el-text>
        </template>
      </el-table-column>
      <el-table-column prop="userVO.userName" label="排班人员" width="170" align="center" />
      <el-table-column fixed="right" label="排班操作" min-width="120" align="center">
        <template #default="scope">
          <div style="white-space: nowrap;">
            <el-button size="small" type="primary" @click="handleEditForm(scope.row)" :icon="Edit" circle>
            </el-button>
            <el-button size="small" type="danger" @click="handleDeleteOne(scope.row)" :icon="Delete" circle>
            </el-button>
            <el-button size="small" type="info" @click="handleScheUserShow(scope.row)" :icon="Avatar" circle>
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

  <el-drawer v-model="addFormShow" title="新增排班需求" :with-header="true">
    <div class="drawer__content">
      <el-form :model="addForm" ref="scheAddFormRef" :rules="scheFormRules">
        <el-form-item label="排班需求名称" prop="scheName" required>
          <el-input v-model="addForm.scheName" autocomplete="off" placeholder="请输入排班需求名称" />
        </el-form-item>
        <el-form-item label="排班需求简介" prop="scheInfo" required>
          <el-input v-model="addForm.scheInfo" autocomplete="off" placeholder="请输入排班需求简介" maxlength="300" show-word-limit
            type="textarea" :autosize="{ minRows: 5 }" />
        </el-form-item>
        <el-form-item prop="shiftId" label="排班所需班次" required>
          <el-select placeholder="请选择你需要的班次" v-model="addForm.shiftId">
            <el-option v-for="item in allShift" :value="item.shiftId">
              {{ item.shiftName }}
            </el-option>
            <template #label>
              {{ allShift.find(item => item.shiftId === addForm.shiftId)?.shiftName }}
            </template>
          </el-select>
        </el-form-item>

        <el-form-item prop="userType" label="所需医护类型" required>
          <el-select placeholder="请选择你需要的医护类型" v-model="addForm.userType">
            <el-option v-for="item in userTypeMap" :value="item.userType">
              {{ item.typeName }}
            </el-option>
            <template #label>
              {{ userTypeMap.find(item => item.userType === addForm.userType)?.typeName }}
            </template>
          </el-select>
        </el-form-item>

        <el-form-item label="所需医护人数" prop="userNum" required>
          <el-input v-model="addForm.userNum" autocomplete="off" placeholder="请输入所需人数" />
        </el-form-item>

        <el-form-item label="所需排班日期" prop="scheDate" required>
          <el-date-picker type="date" placeholder="选择排班时间" style="width: 200px" v-model="addForm.scheDate" />
        </el-form-item>
      </el-form>
      <div class="drawer__footer">
        <el-button type="primary" @click="handeladdOne" style="display: flex;width: 100%;">
          {{ loading ? '提交新增 ...' : '提交新增' }}
        </el-button>
      </div>
    </div>
  </el-drawer>

  <el-drawer v-model="editFormShow" title="修改排班需求" :with-header="true">
    <div class="drawer__content">
      <el-form :model="editForm" ref="scheAddFormRef" :rules="scheFormRules">
        <el-form-item label="班次ID">
          <el-input v-model="editForm.scheId" autocomplete="off" placeholder="请输入班次名称" disabled />
        </el-form-item>
        <el-form-item label="排班需求名称" prop="scheName" required>
          <el-input v-model="editForm.scheName" autocomplete="off" placeholder="请输入排班需求名称" />
        </el-form-item>
        <el-form-item label="排班需求简介" prop="scheInfo" required>
          <el-input v-model="editForm.scheInfo" autocomplete="off" placeholder="请输入排班需求简介" maxlength="300" show-word-limit
            type="textarea" :autosize="{ minRows: 5 }" />
        </el-form-item>
        <el-form-item prop="shiftId" label="排班所需班次" required>
          <el-select placeholder="请选择你需要的班次" v-model="editForm.shiftId">
            <el-option v-for="item in allShift" :value="item.shiftId">
              {{ item.shiftName }}
            </el-option>
            <template #label>
              {{ allShift.find(item => item.shiftId === editForm.shiftId)?.shiftName }}
            </template>
          </el-select>
        </el-form-item>

        <el-form-item prop="userType" label="所需医护类型" required>
          <el-select placeholder="请选择你需要的医护类型" v-model="editForm.userType">
            <el-option v-for="item in userTypeMap" :value="item.userType">
              {{ item.typeName }}
            </el-option>
            <template #label>
              {{ userTypeMap.find(item => item.userType === editForm.userType)?.typeName }}
            </template>
          </el-select>
        </el-form-item>

        <el-form-item label="所需排班日期" prop="scheDate" required>
          <el-date-picker type="date" placeholder="选择排班时间" style="width: 200px" v-model="editForm.scheDate" />
        </el-form-item>
      </el-form>
      <div class="drawer__footer">
        <el-button type="primary" @click="handleEditOne" style="display: flex;width: 100%;">
          {{ loading ? '提交修改 ...' : '提交修改' }}
        </el-button>
      </div>
    </div>
  </el-drawer>

  <el-dialog v-model="selectUserShow" title="选择医护人员" width="500">

    <el-select placeholder="选择医护人员" v-model="scheUser.userId">
      <el-option v-for="item in selectUsers" :value="item.userId">
        {{ item.userName }}
      </el-option>
      <template #label>
        {{ allUser.find(item => item.userId === scheUser.userId)?.userName }}
      </template>
    </el-select>

    <template #footer>
      <div class="dialog-footer">
        <el-button @click="handleDeleteUser">取消排班</el-button>
        <el-button type="primary" @click="submitScheUser">确认提交</el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script lang="ts" setup>
import { Avatar, Edit, Delete } from '@element-plus/icons-vue'
import { ref, onMounted, reactive } from 'vue'
import * as ScheInterface from '@/stores/interface/ScheInterface.ts';
import { shiftStyleMap } from '@/stores/store';
import * as ScheRequest from '@/api/sche.ts'
import * as ShiftRequest from '@/api/shift.ts'
import * as UserRequest from '@/api/user.ts'
import type { ShiftVO } from '@/stores/interface/ShiftInterface';
import type { UserVO } from '@/stores/interface/UserInterface';
import { userTypeMap, scheStatusMap } from '@/stores/store.ts';
import { useDepartmentStore } from '@/stores/store.ts'
import { ElDrawer, ElMessageBox } from 'element-plus'
import type { FormRules, FormInstance } from 'element-plus'
import { ElMessage } from 'element-plus'

const scheAddFormRef = ref<FormInstance | null>(null);
const loading = ref(false);
const selectUserShow = ref(false);
const addFormShow = ref(false);
const editFormShow = ref(false);
const departmentStore = useDepartmentStore()
const scheUser = ref<ScheInterface.SchedulingUser>({
  scheId: 0,
  userId: ""
});

const addForm = ref<ScheInterface.SchedulingAddForm>({
  scheName: "",
  shiftId: null,
  userType: null,
  scheInfo: "",
  scheDate: null,
  userNum: null,
  scheStatus: 1,
  deptId: departmentStore.departmentData.deptId
});
const editForm = ref<ScheInterface.SchedulingEditForm>({
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
const tableData = ref<ScheInterface.SchedulingVO[]>([]);
const selectedRows = ref<ScheInterface.SchedulingVO[]>([]);
const searchForm = reactive<ScheInterface.ScheSearchForm>({
  scheId: null,
  scheName: null,
  scheInfo: null,
  beginDate: null,
  endDate: null,
  shiftId: null,
  userId: null,
  userType: null,
  scheStatus: null,
  isPast: false
});

const pageData = ref({
  pages: 0,
  current: 1,
  size: 10,
  total: 0,
})//分页条

const scheFormRules = ref<FormRules<ScheInterface.SchedulingAddForm>>({
  scheName: [
    { required: true, message: '请输入排班名称', trigger: ['blur', 'change'] },
    { min: 1, max: 40, message: '班次名称长度在1到40之间', trigger: ['blur', 'change'] }
  ],
  scheInfo: [
    { required: true, message: '请输入排班简介', trigger: ['blur', 'change'] },
    { min: 1, max: 500, message: '班次名称长度在1到300之间', trigger: ['blur', 'change'] }
  ],
  shiftId: [
    { required: true, message: '请输入排班类型', trigger: ['blur', 'change'] }
  ],
  userType: [
    { required: true, message: '请输入医护类型', trigger: ['blur', 'change'] }
  ],
  userNum: [
    { required: true, message: '请输入所需人数', trigger: ['blur', 'change'] },
    { min: 1, message: '不能少于一人', trigger: ['blur', 'change'] }
  ],
  scheDate: [
    { required: true, message: '请选择日期', trigger: ['blur', 'change'] }
  ]
})

const formatDate = (row: ScheInterface.SchedulingVO) => {
  if (!row.scheDate) return '';
  const dateObj = new Date(row.scheDate);
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

const selectable = () => true//处理能不能选中的函数



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

const reloadtableData = async () => {
  const response = await ScheRequest.scheGetAllService(pageData.value.current, pageData.value.size, departmentStore.departmentData.deptId, searchForm);
  tableData.value = response.data.items;
  pageData.value.current = response.data.current;
  pageData.value.pages = response.data.pages;
  pageData.value.size = response.data.size;
  pageData.value.total = response.data.total;
  loading.value = false;
}

const reloadtableData2 = async () => {
  const response = await ScheRequest.scheGetAllService(pageData.value.current, pageData.value.size, departmentStore.departmentData.deptId, searchForm);
  tableData.value = response.data.items;
  pageData.value.current = response.data.current;
  pageData.value.pages = response.data.pages;
  pageData.value.size = response.data.size;
  pageData.value.total = response.data.total;
  loading.value = false;
}

const handleSelectionChange = (selection: ScheInterface.SchedulingVO[]) => {
  selectedRows.value = selection;
}

const handleSearch = () => {
  loading.value = true
  reloadtableData()
}

const handleDeleteByList = () => {
  ElMessageBox.confirm('你确定要批量删除这些内容吗？',
    '警告',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    }).then(() => {
      ScheRequest.scheDeleteListService(selectedRows.value)
      loading.value = true
      try {
        setTimeout(reloadtableData, 300)
      } catch {
        loading.value = false
      }
    })
}

const showAddForm = () => {
  addFormShow.value = true;
}

const handleEditForm = (row: ScheInterface.SchedulingEditForm) => {
  editForm.value = JSON.parse(JSON.stringify(row));
  editFormShow.value = true;
}

const handleDialogReset = () => {
  addForm.value.scheDate = null;
  addForm.value.scheInfo = "";
  addForm.value.scheName = "";
  addForm.value.shiftId = null;
  addForm.value.userType = null;
}

const handeladdOne = () => {
  if (!scheAddFormRef.value) return;

  scheAddFormRef.value.validate((valid) => {
    if (!valid) {
      console.log('表单校验失败，阻止提交');
      return;
    }

    loading.value = true;
    try {
      setTimeout(async () => {
        await ScheRequest.scheAddOneService(addForm.value)
        reloadtableData()
        addFormShow.value = false;
        handleDialogReset()
      }, 300);
    } catch {
      loading.value = false
    }

  });
};

const handleSizeChange = (newSize: number) => {
  pageData.value.size = newSize;
  loading.value = true;
  try {
    setTimeout(reloadtableData, 300)
  } catch (err) {
    loading.value = false
  }

};

const handleCurrentChange = (newPage: number) => {
  pageData.value.current = newPage;
  loading.value = true;
  try {
    setTimeout(reloadtableData, 300)
  } catch (err) {
    loading.value = false
  }
}

const handleReset = () => {
  searchForm.scheId = null;
  searchForm.scheName = null;
  searchForm.scheInfo = null;
  searchForm.beginDate = null;
  searchForm.endDate = null;
  searchForm.shiftId = null;
  searchForm.userId = null;
  searchForm.userType = null;
  searchForm.scheStatus = null;
  searchForm.isPast = false;
  handleSearch();
};

const handleDeleteOne = (row: ScheInterface.SchedulingVO) => {
  ElMessageBox.confirm(
    '你确定要删除这项内容吗？',
    '警告',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    }).then(() => {
      setTimeout(async () => {
        if (row.scheId !== null) {
          await ScheRequest.scheDeleteOneService(row.scheId);
        } else {
          console.error("scheId 不能为空");
        }

        loading.value = true;
        try {
          setTimeout(reloadtableData, 300)
        } catch {
          loading.value = false
        }
      }, 200)
    })
}

const handleEditOne = () => {
  if (!scheAddFormRef.value) return;

  scheAddFormRef.value.validate((valid) => {
    if (!valid) {
      console.log('表单校验失败，阻止提交');
      return;
    }

    ElMessageBox.confirm('你确定要修改这项内容吗？',
      '警告',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      }).then(() => {
        loading.value = true;
        setTimeout(async () => {
          try {
            await ScheRequest.scheEditOneService(editForm.value)
          } finally {
            reloadtableData();
            editFormShow.value = false;
          }
        }, 300)
      })
  })
}

const handleScheUserShow = async (row: ScheInterface.SchedulingVO) => {
  selectUserShow.value = !selectUserShow.value;
  if (row.userType === 1) selectUsers.value = allDoctors.value
  else selectUsers.value = allNurses.value
  scheUser.value.scheId = row.scheId ?? 0;
  scheUser.value.userId = row.userVO?.userId ?? ""
}

const handleDeleteUser = async () => {
  await ScheRequest.delscheUserService(scheUser.value.scheId)
  loading.value = true;
  reloadtableData();
  loading.value = false;
  selectUserShow.value = !selectUserShow.value;
}

const submitScheUser = async () => {
  try {
    ElMessage({
      message: "操作成功提交",
      type: 'success',
    })
    const a = await ScheRequest.scheUserOneService(scheUser.value);
  } finally {
    selectUserShow.value = false;
    scheUser.value.scheId = 0;
    scheUser.value.userId = "";
    reloadtableData2();
  }

}

</script>


<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>

