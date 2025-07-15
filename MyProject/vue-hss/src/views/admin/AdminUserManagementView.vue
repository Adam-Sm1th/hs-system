<template>
  <el-card style="box-sizing: border-box; min-height: 95%; margin: 10px;">
    <template #header>
      <div class="card-header">
        <span>用户管理</span>
        <el-button type="primary" @click="addDialogOpen">新增用户</el-button>
      </div>
    </template>

    <div style="display: flex; align-items: center; gap: 20px; margin-bottom: 10px;">
      <span style="font-size: 12px; color: #333;">用户编号:</span>
      <el-input v-model="searchForm.userId" style="width: 150px" clearable size="small" />
      <span style="font-size: 12px; color: #333;">用户名称:</span>
      <el-input v-model="searchForm.userName" style="width: 150px" clearable size="small" />
      <span style="font-size: 12px; color: #333;">医护类型:</span>
      <el-select placeholder="全部" style="width: 100px" size="small" v-model="searchForm.userType">
        <el-option v-for="item in userTypeMap" :value="item.userType">
          {{ item.typeName }}
        </el-option>
        <template #label>
          {{ userTypeMap.find(item => item.userType === searchForm.userType)?.typeName }}
        </template>
      </el-select>
      <span style="font-size: 12px; color: #333;">所属科室:</span>
      <el-select placeholder="全部" style="width: 100px" size="small" v-model="searchForm.deptId">
        <el-option v-for="item in allDepts" :value="item.deptId">
          {{ item.deptName }}
        </el-option>
        <template #label>
          {{ allDepts.find(item => item.deptId === searchForm.deptId)?.deptName }}
        </template>
      </el-select>
      <div>
        <el-button size="small" @click="handleSearchReset">重置</el-button>
        <el-button type="primary" size="small" @click="handleSearchConfirm">搜索</el-button>
        <el-button type="danger" size="small" @click="handleDeleteByList">批量删除</el-button>
      </div>
    </div>

    <el-table :data="tableData" style="width: 100%" @selection-change="handleSelectionChange" v-loading="loading"
      element-loading-text="加载中..." stripe>

      <el-table-column fixed="left" type="selection" :selectable="selectable" align="center" />
      <el-table-column prop="userId" label="用户ID" width="120" align="center" />
      <el-table-column label="头像" width="80" align="center">
        <template #default="scope">
          <el-avatar :src="scope.row.userAvatar" size="small" />
        </template>
      </el-table-column>
      <el-table-column prop="userName" label="用户名" width="120" align="center" />
      <el-table-column prop="userGender" label="性别" width="80" align="center" />
      <el-table-column label="用户类型" width="100" align="center">
        <template #default="scope">
          {{ scope.row.userType == 1 ? "医生" : "护士" }}
        </template>
      </el-table-column>
      <el-table-column prop="userPhone" label="电话" width="200" align="center" />
      <el-table-column prop="userEmail" label="邮箱" width="250" show-overflow-tooltip align="center" />

      <el-table-column label="权限" width="100" align="center">
        <template #default="scope">
          <el-tag size="small" type="primary" v-if="scope.row.userPermission == 1">
            {{ permissionMap[scope.row.userPermission] }}
          </el-tag>
          <el-tag size="small" type="warning" v-if="scope.row.userPermission == 2">
            {{ permissionMap[scope.row.userPermission] }}
          </el-tag>
          <el-tag size="small" type="danger" v-if="scope.row.userPermission == 3">
            {{ permissionMap[scope.row.userPermission] }}
          </el-tag>
        </template>
      </el-table-column>

      <el-table-column fixed="right" label="操作" min-width="120" align="center">
        <template #default="scope">
          <el-button size="small" type="primary" @click="editDialogOpen(scope.row)" :icon="Edit" circle />
          <el-button size="small" type="danger" @click="handleDeleteOne(scope.row)" :icon="Delete" circle />
          <el-button size="small" type="success" @click="handleEdPwdOpen(scope.row)" :icon="Key" circle />
        </template>
      </el-table-column>

    </el-table>

    <template #footer>
      <el-pagination :current-page="pageData.current" :page-size="pageData.size" :background="true"
        layout="total, sizes, prev, pager, next, jumper" :total="pageData.total" @size-change="handleSizeChange"
        @current-change="handleCurrentChange" />
    </template>

    <!-- 新增用户 -->
    <el-drawer v-model="addDialog" title="添加用户" direction="rtl" size="30%">
      <el-form :model="addForm" :rules="rules" ref="addFormRef" label-width="80px">
        <el-form-item label="用户ID" prop="userId">
          <el-input v-model="addForm.userId" />
        </el-form-item>

        <el-form-item label="用户名" prop="userName">
          <el-input v-model="addForm.userName" />
        </el-form-item>

        <el-form-item label="密码" prop="userPwd">
          <el-input v-model="addForm.userPwd" show-password />
        </el-form-item>

        <el-form-item label="确认密码" prop="userRePwd">
          <el-input v-model="addForm.userRePwd" show-password />
        </el-form-item>

        <el-form-item label="性别" prop="userGender">
          <el-select v-model="addForm.userGender" placeholder="请选择性别">
            <el-option label="男" value="男" />
            <el-option label="女" value="女" />
          </el-select>
        </el-form-item>

        <el-form-item label="电话" prop="userPhone">
          <el-input v-model="addForm.userPhone" />
        </el-form-item>

        <el-form-item label="邮箱" prop="userEmail">
          <el-input v-model="addForm.userEmail" />
        </el-form-item>

        <el-form-item label="简介" prop="userInfo">
          <el-input v-model="addForm.userInfo" type="textarea" rows="3" />
        </el-form-item>

        <el-form-item label="权限" prop="userPermission">
          <el-select v-model="addForm.userPermission" placeholder="请选择权限">
            <el-option label="医护人员" :value="1" />
            <el-option label="科室管理员" :value="2" />
          </el-select>
        </el-form-item>

        <el-form-item label="出生日期" prop="userBirthday">
          <el-date-picker v-model="addForm.userBirthday" type="date" placeholder="选择日期" format="YYYY-MM-DD"
            value-format="YYYY-MM-DD" />
        </el-form-item>

        <el-form-item label="用户类型" prop="userType">
          <el-select v-model="addForm.userType" placeholder="请选择用户类型">
            <el-option label="医生" :value="1" />
            <el-option label="护士" :value="2" />
          </el-select>
        </el-form-item>

        <el-form-item label="积极性" prop="userProactivity">
          <el-slider v-model="addForm.userProactivity" :min="0" :max="100" show-input />
        </el-form-item>
      </el-form>

      <div style="margin-top: 10px; text-align: right;">
        <el-button @click="editDialog = false">取消</el-button>
        <el-button type="primary" @click="handleAddOne">提交新增</el-button>
      </div>
    </el-drawer>

    <!-- 编辑用户 -->
    <el-drawer v-model="editDialog" title="编辑用户" direction="rtl" size="30%">
      <el-form :model="editForm" ref="editFormRef" label-width="80px" :rules="rules">
        <div style="display: flex; justify-content: center; margin-bottom: 20px;">
          <el-upload :action="uploadUrl" :show-file-list="false" :on-success="handleAvatarSuccess"
            :before-upload="beforeAvatarUpload">
            <el-avatar :size="120" :src="editForm.userAvatar" style="cursor: pointer" />
          </el-upload>
        </div>
        <el-form-item label="用户ID">
          <el-input v-model="editForm.userId" disabled />
        </el-form-item>

        <el-form-item label="用户名" prop="userName">
          <el-input v-model="editForm.userName" />
        </el-form-item>

        <el-form-item label="性别" prop="userGender">
          <el-select v-model="editForm.userGender" placeholder="请选择性别">
            <el-option label="男" value="男" />
            <el-option label="女" value="女" />
          </el-select>
        </el-form-item>

        <el-form-item label="电话" prop="userPhone">
          <el-input v-model="editForm.userPhone" />
        </el-form-item>

        <el-form-item label="邮箱" prop="userEmail">
          <el-input v-model="editForm.userEmail" />
        </el-form-item>

        <el-form-item label="简介" prop="userInfo">
          <el-input v-model="editForm.userInfo" type="textarea" rows="3" />
        </el-form-item>

        <el-form-item label="权限" prop="userPermission">
          <el-select v-model="editForm.userPermission" placeholder="请选择权限">
            <el-option label="医护人员" :value="1" />
            <el-option label="科室管理员" :value="2" />
          </el-select>
        </el-form-item>

        <el-form-item label="出生日期" prop="userBirthday">
          <el-date-picker v-model="editForm.userBirthday" type="date" placeholder="选择日期" format="YYYY-MM-DD"
            value-format="YYYY-MM-DD" />
        </el-form-item>

        <el-form-item label="注册时间">
          <el-date-picker v-model="editForm.userRegtime" type="date" placeholder="选择时间" format="YYYY-MM-DD HH:mm:ss"
            value-format="YYYY-MM-DDTHH:mm:ss" disabled />
        </el-form-item>

        <el-form-item label="用户类型" prop="userType">
          <el-select v-model="editForm.userType" placeholder="请选择用户类型">
            <el-option label="医生" :value="1" />
            <el-option label="护士" :value="2" />
          </el-select>
        </el-form-item>

        <el-form-item label="积极性" prop="userProactivity">
          <el-slider v-model="editForm.userProactivity" :min="0" :max="100" show-input />
        </el-form-item>
      </el-form>

      <div style="margin-top: 10px; text-align: right;">
        <el-button @click="addDialog = false">取消</el-button>
        <el-button type="primary" @click="handleEditOne">提交修改</el-button>
      </div>
    </el-drawer>

  </el-card>
  <el-dialog v-model="dialogVisible" title="修改密码" width="400px">
    <el-form :model="passwordForm" ref="passwordFormRef" label-width="80px">
      <el-form-item label="密码" prop="newPassword" :rules="[
        { required: true, message: '请输入密码', trigger: 'blur' },
        { validator: passwordValidator, trigger: 'blur' }
      ]">
        <el-input v-model="passwordForm.newPassword" show-password />
      </el-form-item>

      <el-form-item label="确认密码" prop="confirmPassword" :rules="[
        { required: true, message: '请确认密码', trigger: 'blur' },
        { validator: validateConfirmPassword, trigger: 'blur' }
      ]">
        <el-input v-model="passwordForm.confirmPassword" show-password />
      </el-form-item>
    </el-form>

    <div slot="footer" class="dialog-footer" style="text-align: right;">
      <el-button @click="dialogVisible = false">取消</el-button>
      <el-button type="primary" @click="submitPasswordForm">确认</el-button>
    </div>
  </el-dialog>
</template>

<script setup lang="ts">
import * as DepartmentInterface from '@/stores/interface/DepartmentInterface.ts'
import { Avatar, Edit, Delete, UserFilled, Key, Menu } from '@element-plus/icons-vue'
import { ref, onMounted, reactive, computed } from 'vue'
import * as ScheInterface from '@/stores/interface/ScheInterface.ts';
import * as UserInterface from '@/stores/interface/UserInterface.ts';
import { shiftStyleMap } from '@/stores/store';
import * as ScheRequest from '@/api/sche.ts'
import * as ShiftRequest from '@/api/shift.ts'
import * as UserRequest from '@/api/user.ts'
import * as DeptRequest from '@/api/dept.ts'
import type { ShiftVO } from '@/stores/interface/ShiftInterface';
import type { UserVO } from '@/stores/interface/UserInterface';
import { userTypeMap, scheStatusMap } from '@/stores/store.ts';
import { useDepartmentStore } from '@/stores/store.ts'
import { ElDrawer, ElMessageBox } from 'element-plus'
import type { FormRules, FormInstance } from 'element-plus'
import { ElMessage } from 'element-plus'
import type { UploadProps } from 'element-plus'
import { ElForm } from 'element-plus'

const passwordFormRef = ref()
const addDialog = ref(false)
const editDialog = ref(false)
const loading = ref(false)
const selectedRows = ref<UserVO[]>([])
const allDepts = ref<DepartmentInterface.Department[]>([])
const dialogVisible = ref(false); // 控制弹窗显示
const passwordForm = ref({
  userId: '',
  newPassword: '',
  confirmPassword: ''
});

const handleEdPwdOpen = (row: UserInterface.UserVO) => {
  passwordForm.value.userId = row.userId
  dialogVisible.value = true;
}

const validateConfirmPassword = (_rule: any, value: string, callback: any) => {
  if (value !== passwordForm.value.newPassword) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

const submitPasswordForm = () => {
  passwordFormRef.value.validate((valid: boolean) => {
    if (valid) {
      UserRequest.userEditPwdService(passwordForm.value.userId, passwordForm.value.newPassword)
      ElMessage({
        message: "密码修改成功",
        type: 'success',
      })
      dialogVisible.value = false
      passwordForm.value.userId = '';
      passwordForm.value.confirmPassword = '';
      passwordForm.value.newPassword = '';
    }
  })
}
const uploadUrl = computed(() => {
  return `http://localhost:5173/api/oss/upload/${editForm.value.userId}`;
});
const handleAvatarSuccess: UploadProps['onSuccess'] = async (
  response,
  uploadFile
) => {
  console.log(response.data.imageUrl)
  const newAvatarUrl = `${response.data.imageUrl}?timestamp=${new Date().getTime()}`;
  editForm.value.userAvatar = newAvatarUrl;
  await UserRequest.userEditOneService(editForm.value);
  await reloadAllTable()
}
const selectable = () => true//处理能不能选中的函数
const formatDate = (date: string | Date): string => {
  if (!date) return "-"
  return new Date(date).toLocaleDateString()
}

const beforeAvatarUpload: UploadProps['beforeUpload'] = (rawFile) => {
  const allowedFormats = ['image/jpeg', 'image/png', 'image/gif'];
  if (!allowedFormats.includes(rawFile.type)) {
    ElMessage.error('头像必须是JPG,PNG,或GIF格式')
    return false;
  }
  if (rawFile.size / 1024 / 1024 > 10) {
    ElMessage.error('头像大小不能超过20MB!')
    return false;
  }
  return true;
}

const permissionMap: { [key: number]: string } = {
  1: '医护人员',
  2: '科室管理员',
  3: '超级管理员'
  // 其他权限类型...
}

const searchForm = ref<UserInterface.UserSearchForm>({
  userId: '',
  userName: '',
  userType: 0,
  deptId: '',
})

const addForm = ref<UserInterface.UserDTO>({
  userId: '',
  userName: '',
  userPwd: '',
  userRePwd: '',
  userGender: '',
  userPhone: '',
  userEmail: '',
  userInfo: '',
  userAvatar: '',
  userPermission: 1,
  userBirthday: new Date(),
  userRegtime: new Date(),
  userType: 1,
  userProactivity: 0
})

const editForm = ref<UserInterface.UserVO>({
  userId: '',
  userName: '',
  userGender: '',
  userPhone: '',
  userEmail: '',
  userInfo: '',
  userAvatar: '',
  userPermission: 0,
  userBirthday: new Date(),
  userRegtime: new Date(),
  userType: 0,
  userProactivity: 0
})

const tableData = ref<UserVO[]>([])
const pageData = ref({
  current: 1,
  size: 10,
  total: 0
})

const addDialogOpen = () => {
  addDialog.value = true
}

const editDialogOpen = (row: UserVO) => {
  editForm.value = { ...row, userPwd: '' } as UserInterface.UserDTO
  editDialog.value = true
}

// 引用表单
const addFormRef = ref<InstanceType<typeof ElForm> | null>(null)
const editFormRef = ref<InstanceType<typeof ElForm> | null>(null)

const passwordValidator = (rule: any, value: string, callback: any) => {
  if (!/^(?=.*[A-Za-z])(?=.*\d)[A-Za-z\d]{6,20}$/.test(value)) {
    callback(new Error('密码必须包含字母和数字，6-20个字符'))
  } else {
    callback()
  }
}

// 确认密码验证规则
const confirmPasswordValidator = (rule: any, value: string, callback: any) => {
  if (value !== addForm.value.userPwd) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

// 表单验证规则
const rules = reactive({
  userId: [
    { required: true, message: '请输入用户ID', trigger: 'blur' },
    { min: 4, max: 20, message: '用户ID长度为4-20个字符', trigger: 'blur' }
  ],
  userName: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 2, max: 10, message: '用户名长度为2-10个字符', trigger: 'blur' }
  ],
  userPwd: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { validator: passwordValidator, trigger: 'blur' }
  ],
  userRePwd: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    { validator: confirmPasswordValidator, trigger: 'blur' }
  ],
  userGender: [
    { required: true, message: '请选择性别', trigger: 'change' }
  ],
  userPhone: [
    { required: true, message: '请输入电话号码', trigger: 'blur' },
    {
      pattern: /^1[3-9]\d{9}$/,
      message: '请输入正确的手机号码',
      trigger: 'blur'
    }
  ],
  userEmail: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    {
      type: 'email',
      message: '请输入正确的邮箱地址',
      trigger: ['blur', 'change']
    }
  ],
  userInfo: [
    { required: true, message: '请输入个人简介', trigger: 'blur' },
    { max: 200, message: '简介不能超过200个字符', trigger: 'blur' }
  ],
  userPermission: [
    { required: true, message: '请选择用户权限', trigger: 'change' }
  ],
  userBirthday: [
    { required: true, message: '请选择出生日期', trigger: 'change' }
  ],
  userType: [
    { required: true, message: '请选择用户类型', trigger: 'change' }
  ],
  userProactivity: [
    { required: true, message: '请设置用户积极性', trigger: 'change' }
  ]
})

// 提交处理
const handleAddOne = async () => {
  // 确保表单引用存在
  if (!addFormRef.value) return

  // 使用 Promise 处理表单验证
  addFormRef.value.validate(async (valid) => {
    if (valid) {
      await UserRequest.userAddService(addForm.value)
      await reloadAllTable()
      addDialog.value = false;
      handleDialogReset()

    } else {
      // 验证失败，显示错误消息
      ElMessage.error('请检查表单信息')
    }
  }).catch((error) => {
    console.error('表单验证错误', error)
  })
}

const handleDialogReset = () => {
  addForm.value = {
    userId: '',
    userName: '',
    userPwd: '',
    userRePwd: '',
    userGender: '',
    userPhone: '',
    userEmail: '',
    userInfo: '',
    userAvatar: '',
    userPermission: 1,
    userBirthday: new Date(),
    userRegtime: new Date(),
    userType: 1,
    userProactivity: 0
  }
}

const handleSearchReset = () => {
  searchForm.value = {
    userId: '',
    userName: '',
    userType: 0,
    deptId: ''
  }
  // TODO: 重新加载用户列表
}

const reloadAllTable = async () => {
  loading.value = true;
  const resp = await UserRequest.userGetAllPagesService(pageData.value.current, pageData.value.size, searchForm.value);
  tableData.value = resp.data.items
  loading.value = false;
}

const handleSearchConfirm = async () => {
  await reloadAllTable();
  // TODO: 根据 searchForm 发起查询请求并更新 tableData
}

const handleDeleteByList = async () => {
  ElMessageBox.confirm('你确定要删除这些内容吗？',
    '警告',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    }).then(async () => {
      await UserRequest.userDeleteListService(selectedRows.value);
      reloadAllTable();
    })
}

const handleDeleteOne = async (row: UserVO) => {
  ElMessageBox.confirm('你确定要删除这个吗？',
    '警告',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    }).then(async () => {
      await UserRequest.userDeleteService(row.userId);
      await reloadAllTable();
    })
}

const handleEditOne = async () => {
  // 确保表单引用存在
  if (!editFormRef.value) return

  // 使用 Promise 处理表单验证
  editFormRef.value.validate(async (valid) => {
    if (valid) {
      await UserRequest.userEditOneService(editForm.value);
      editDialog.value = false;
      await reloadAllTable();
    } else {
      // 验证失败，显示错误消息
      ElMessage.error('请检查表单信息')
    }
  }).catch((error) => {
    console.error('表单验证错误', error)
  })

}

const handleSelectionChange = (rows: UserVO[]) => {
  selectedRows.value = rows
}

const handleSizeChange = (size: number) => {
  pageData.value.size = size
  // TODO: 重新请求分页数据
}

const handleCurrentChange = (page: number) => {
  pageData.value.current = page
  // TODO: 重新请求分页数据
}

onMounted(async () => {
  const resp = await DeptRequest.deptGetAllService();
  allDepts.value = [{ deptId: null, deptName: '全部' }, ...resp.data];

  await reloadAllTable();
  console.log(tableData.value)
})

</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
