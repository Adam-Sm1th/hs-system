<template>
  <el-card style="box-sizing: border-box;min-height: 95%; margin: 10px; min-height: 95%;">
    <template #header>
      <div class="card-header">
        <span>班次管理</span>
        <el-button type="primary" @click="addDialogOpen">新增班次</el-button>
      </div>
    </template>

    <div style="display: flex; align-items: center; gap: 20px; margin-bottom: 10px;">
      <span style="font-size: 12px; color: #333;">班次编号:</span>
      <el-input v-model="searchForm.shiftId" style="width: 150px" clearable size="small" />
      <span style="font-size: 12px; color: #333;">班次名称:</span>
      <el-input v-model="searchForm.shiftName" style="width: 150px" clearable size="small" />
      <div>
        <el-button size="small" @Click="handleSearchReset">重置</el-button>
        <el-button type="primary" size="small" @click="handleSearchConfirm">搜索</el-button>
        <el-button type="danger" size="small" @click="handleDeleteByList">批量删除</el-button>
      </div>
    </div>

    <el-table :data="tableData" style="width: 100%" @selection-change="handleSelectionChange" v-loading="loading"
      element-loading-text="加载中..." stripe>
      <el-table-column type="selection" :selectable="selectable" min-width="5%" align="center" />

      <el-table-column label="班次ID" min-width="10%" align="center">
        <template #default="scope">
          <div>{{ scope.row.shiftId }}</div>
        </template>
      </el-table-column>

      <el-table-column label="班次开始时间" min-width="25%">
        <template #default="scope">
          <div style="display: flex; align-items: center">
            <el-icon>
              <timer />
            </el-icon>
            <span style="margin-left: 10px">{{ scope.row.shiftBegintime }}</span>
          </div>
        </template>
      </el-table-column>

      <el-table-column label="班次结束时间" min-width="25%">
        <template #default="scope">
          <div style="display: flex; align-items: center">
            <el-icon>
              <timer />
            </el-icon>
            <span style="margin-left: 10px">{{ scope.row.shiftEndtime }}</span>
          </div>
        </template>
      </el-table-column>

      <el-table-column label="班次名称" min-width="20%" align="center">
        <template #default="scope">
          <el-tag size="large" :type="styleFunction(scope.row.shiftStyleId)">
            {{ scope.row.shiftName }}
          </el-tag>
        </template>
      </el-table-column>

      <el-table-column label="操作" align=center min-width="15%">
        <template #default="scope">
          <div style="white-space: nowrap;">
            <el-button size="small" type="primary" @click="eidtDialogOpen(scope.$index, scope.row)" :icon="Edit" circle>
            </el-button>
            <el-button size="small" type="danger" @click="handleDeleteOne(scope.row)" :icon="Delete" circle>
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

  <el-drawer v-model="addDialog" title="添加班次" direction="rtl" class="demo-drawer" size="30%">
    <div class="drawer__content">
      <el-form :model="addForm" :rules="shiftFormRules" ref="shiftAddFormRef">
        <el-form-item label="班次名称" prop="shiftName" required>
          <el-input v-model="addForm.shiftName" autocomplete="off" placeholder="请输入班次名称" />
        </el-form-item>

        <el-form-item label="班次时间" required>

          <el-col :span="10">
            <el-form-item prop="shiftBegintime">
              <el-time-select v-model="addForm.shiftBegintime" style="width: 100%" placeholder="开始时间"
                :max-time="addForm.shiftEndtime" start="00:00" step="00:5" end="24:00" />
            </el-form-item>
          </el-col>

          <el-col :span="1" style="display: flex; align-items: center; justify-content: center; margin: 0 0.5rem;">
            -
          </el-col>

          <el-col :span="10">
            <el-form-item prop="shiftEndtime">
              <el-time-select v-model="addForm.shiftEndtime" style="width: 100%" placeholder="结束时间"
                :min-time="addForm.shiftBegintime" start="00:00" step="00:5" end="24:00" />
            </el-form-item>
          </el-col>

        </el-form-item>

        <el-form-item label="图标样式" required prop="shiftStyleId">
          <el-select v-model="addForm.shiftStyleId" placeholder="请选择样式">
            <el-option v-for="item in shiftStyleMap" :value="item.shiftStyleId">
              <el-tag size="large" :type="styleFunction(item.shiftStyleId)"
                style="display: flex;justify-content: center;align-items: center;width: 100%;">
                {{ "样式" + item.shiftStyleId }}
              </el-tag>
            </el-option>
            <template #label>
              <el-tag size="small" :type="styleFunction(addForm.shiftStyleId)"
                style="display: flex;justify-content: center;align-items: center;width: 100%;">
                {{ "样式" + addForm.shiftStyleId }}
              </el-tag>
            </template>
          </el-select>
        </el-form-item>
      </el-form>
      <div class="drawer__footer">
        <el-button @click="handleDialogReset">重置</el-button>
        <el-button type="primary" :loading="loading" @click="handeladdOne">
          {{ loading ? '添加 ...' : '添加' }}
        </el-button>
      </div>
    </div>
  </el-drawer>

  <el-drawer v-model="editDialog" title="编辑班次" direction="rtl" class="demo-drawer" size="30%">
    <div class="drawer__content">
      <el-form :model="editForm" :rules="shiftFormRules" ref="shiftEidtFormRef">
        <el-form-item label="班次ID">
          <el-input v-model="editForm.shiftId" autocomplete="off" placeholder="请输入班次名称" disabled />
        </el-form-item>
        <el-form-item label="班次名称" prop="shiftName" required>
          <el-input v-model="editForm.shiftName" autocomplete="off" placeholder="请输入班次名称" />
        </el-form-item>

        <el-form-item label="班次时间" required>
          <el-col :span="10">
            <el-form-item prop="shiftBegintime">
              <el-time-select v-model="editForm.shiftBegintime" style="width: 100%" placeholder="开始时间"
                :max-time="editForm.shiftEndtime" start="00:00" step="00:5" end="24:00" />
            </el-form-item>
          </el-col>

          <el-col :span="1" style="display: flex; align-items: center; justify-content: center; margin: 0 0.5rem;">
            -
          </el-col>

          <el-col :span="10">
            <el-form-item prop="shiftEndtime">
              <el-time-select v-model="editForm.shiftEndtime" style="width: 100%" placeholder="结束时间"
                :min-time="editForm.shiftBegintime" start="00:00" step="00:5" end="24:00" />
            </el-form-item>
          </el-col>

        </el-form-item>

        <el-form-item label="图标样式" required>
          <el-select v-model="editForm.shiftStyleId" placeholder="请选择样式">
            <el-option v-for="item in shiftStyleMap" :value="item.shiftStyleId">
              <el-tag size="large" :type="styleFunction(item.shiftStyleId)"
                style="display: flex;justify-content: center;align-items: center;width: 100%;">
                {{ "样式" + item.shiftStyleId }}
              </el-tag>
            </el-option>
            <template #label>
              <el-tag size="small" :type="styleFunction(editForm.shiftStyleId)"
                style="display: flex;justify-content: center;align-items: center;width: 100%;">
                {{ "样式" + editForm.shiftStyleId }}
              </el-tag>
            </template>
          </el-select>
        </el-form-item>
      </el-form>
      <div class="drawer__footer">
        <el-button type="primary" :loading="loading" @click="handleEditOne" style="display: flex;width: 100%;">
          {{ loading ? '提交修改 ...' : '提交修改' }}
        </el-button>
      </div>
    </div>
  </el-drawer>
</template>

<script lang="ts" setup>
import { Timer, Edit, Delete } from '@element-plus/icons-vue'
import { shiftPagesGetAllService, shiftDeleteOneService, shiftAddOneService, shiftEditOneService, shiftDeleteListService } from '@/api/shift.ts'
import { ref, onMounted } from 'vue'
import { shiftStyleMap } from '@/stores/store.ts'
import { useDepartmentStore } from '@/stores/store.ts'
import type { ShiftVO, Response, AddShiftForm, RuleForm, SearchForm } from '@/stores/interface/ShiftInterface.ts'
import type { FormRules, FormInstance } from 'element-plus'
import { ElDrawer, ElMessageBox } from 'element-plus'
import { ElMessage } from 'element-plus'

const shiftAddFormRef = ref<FormInstance | null>(null);
const shiftEidtFormRef = ref<FormInstance | null>(null);
const editDialog = ref(false)
const addDialog = ref(false)
const loading = ref(false)
const deleteLoading = ref(false)
const tableData = ref<ShiftVO[]>([])
const selectedRows = ref<ShiftVO[]>([])
const selectDelectRow = ref<ShiftVO | null>(null)
const selectable = () => true
const departmentStore = useDepartmentStore()

const pageData = ref({
  pages: 0,
  current: 1,
  size: 10,
  total: 0,
})
const searchForm = ref<SearchForm>({
  shiftId: null,
  shiftName: "",
})
const addForm = ref<AddShiftForm>({
  shiftName: "",
  shiftBegintime: "",
  shiftEndtime: "",
  deptId: departmentStore.departmentData.deptId,
  shiftStyleId: 1
})
const editForm = ref<ShiftVO>({
  shiftId: 1,
  shiftName: "",
  shiftBegintime: "",
  shiftEndtime: "",
  deptId: departmentStore.departmentData.deptId,
  shiftStyleId: 1
})

const shiftFormRules = ref<FormRules<RuleForm>>({
  shiftName: [
    { required: true, message: '请输入班次名称', trigger: ['blur', 'change'] },
    { min: 1, max: 40, message: '班次名称长度在1到40之间', trigger: ['blur', 'change'] }
  ],
  shiftBegintime: [
    {
      type: 'string',
      required: true,
      message: '请选择开始时间',
      trigger: 'change',
    },
  ],
  shiftEndtime: [
    {
      type: 'string',
      required: true,
      message: '请选择结束时间',
      trigger: 'change',
    }
  ]
})

onMounted(() => {
  loading.value = true;
  try {
    setTimeout(reloadtableData, 500)
  } catch {
    loading.value = false;
  }
  ElMessage({
    message: "数据获取成功",
    type: 'success',
  })
});

const handleDeleteByList = () => {
  ElMessageBox.confirm('你确定要批量删除这些内容吗？',
    '警告',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    }).then(() => {
      shiftDeleteListService(selectedRows.value)
      loading.value = true
      try {
        setTimeout(reloadtableData, 300)
      } catch {
        loading.value = false
      }
    })
}

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

};


const handleSearchConfirm = () => {
  loading.value = true;

  setTimeout(async () => {
    try {
      setTimeout(reloadtableData, 300)
    } catch (err) {
      loading.value = false
    }
  }, 300)

}

const handleSearchReset = () => {
  searchForm.value.shiftId = null;
  searchForm.value.shiftName = "";
  reloadtableData()
}

const addDialogOpen = () => {
  addDialog.value = true
}

const handleDialogReset = () => {
  addForm.value.shiftName = "";
  addForm.value.shiftBegintime = "";
  addForm.value.shiftEndtime = "";
  addForm.value.shiftStyleId = 1;
}


const handeladdOne = () => {
  if (!shiftAddFormRef.value) return;

  shiftAddFormRef.value.validate((valid) => {
    if (!valid) {
      console.log('表单校验失败，阻止提交');
      return;
    }

    loading.value = true;
    try {
      setTimeout(async () => {
        await shiftAddOneService(addForm.value)
        reloadtableData()
        addDialog.value = false;
        handleDialogReset()
      }, 300);
    } catch {
      loading.value = false
    }

  });
};


const reloadtableData = async () => {
  const response = await shiftPagesGetAllService(pageData.value.current, pageData.value.size, departmentStore.departmentData.deptId, searchForm.value);
  tableData.value = response.data.items;
  pageData.value.current = response.data.current;
  pageData.value.pages = response.data.pages;
  pageData.value.size = response.data.size;
  pageData.value.total = response.data.total;
  loading.value = false;
}

const eidtDialogOpen = (index: number, row: ShiftVO) => {
  editForm.value = JSON.parse(JSON.stringify(row));
  editDialog.value = true;
}


const handleDeleteOne = (row: ShiftVO) => {
  ElMessageBox.confirm(
    '你确定要删除这项内容吗？',
    '警告',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    }).then(() => {
      selectDelectRow.value = JSON.parse(JSON.stringify(row))
      deleteLoading.value = true;
      setTimeout(async () => {
        if (!selectDelectRow.value) return;
        await shiftDeleteOneService(selectDelectRow.value.shiftId);
        loading.value = true;
        try {
          setTimeout(reloadtableData, 300)
        } catch {
          loading.value = false
        }
        deleteLoading.value = false;
      }, 200)
    })
}

const handleEditOne = () => {
  if (!shiftEidtFormRef.value) return;

  shiftEidtFormRef.value.validate((valid) => {
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
            await shiftEditOneService(editForm.value)
            const index = tableData.value.findIndex(item => item.shiftId === editForm.value.shiftId);
            if (index !== -1) {
              tableData.value[index] = { ...editForm.value };
            }
          } finally {
            editDialog.value = false;
            loading.value = false;
          }
        }, 300)
      })
  })

}

const styleFunction = (styleId: number): string | undefined => {
  const style = shiftStyleMap.find(item => item.shiftStyleId === styleId);
  return style ? style.shiftStyleType : undefined;
};

const handleSelectionChange = (selection: ShiftVO[]) => {
  selectedRows.value = selection;
  // console.log('当前选中的行:', selectedRows.value);
}

</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
