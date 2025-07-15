import { createRouter, createWebHistory } from 'vue-router'
import {useTokenStore} from '@/stores/store.ts'
import {useUserStore} from '@/stores/store.ts'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/departadmin',
      name: 'home',
      redirect: '/departadmin/schedulingAnalyze',
      component: () => import('../views/departadmin/DepartmentManagementView.vue'),
      children: [
        {
          path: 'shift/manage',
          component: () => import('../views/departadmin/ShiftManagementView.vue')
        },
        {
          path: 'scheduling/manage',
          component: () => import('../views/departadmin/SchedulingManagementView.vue')
        },
        {
          path: 'shift/audit/leave',
          component: () => import('../views/departadmin/AuditLeaveManagementView.vue')
        },
         {
          path: 'shift/audit/shifts',
          component: () => import('../views/departadmin/AuditShiftsManagementView.vue')
        },
        {
          path: 'schedulingUserManage',
          component: () => import('../views/departadmin/SchedulingUserManagementView.vue')
        },
        {
          path: 'schedulingAnalyze',
          component: () => import('../views/departadmin/ScheDataAnalyzeView.vue')
        }
      ]
    },
    {
      path: '/user',
      name: 'userHome',
      redirect: '/user/userinfo',
      component: () => import('../views/user/UserManagementView.vue'),
      children: [
        {
          path: 'userinfo',
          component: () => import('../views/user/UserInfoView.vue')
        },
        {
          path: 'usersche',
          component: () => import('../views/user/UserSchedulingView.vue')
        },
        {
          path: 'userleave',
          component: () => import('../views/user/UserLeaveView.vue')
        },
        {
          path: 'usershift',
          component: () => import('../views/user/UserShiftsView.vue')
        },
         {
          path: 'userstatus',
          component: () => import('../views/user/UserStatusView.vue')
        },
      ]
    },
    {
      path: '/admin',
      name: 'adminHome',
      redirect: '/admin/admininfo',
      component: () => import('../views/admin/AdminManagementView.vue'),
      children: [
        {
          path: 'admindept',
          component: () => import('../views/admin/AdminDepartmentManagementView.vue')
        },
        {
          path: 'adminuser',
          component: () => import('../views/admin/AdminUserManagementView.vue')
        },
        {
          path: 'admininfo',
          component: () => import('../views/admin/AdminInfoView.vue')
        }
      ]
    },
    {
      path: '/login',
      name: 'loginhome',
      component: () => import('../views/tour/loginView.vue'),
    },{
      path: '/403',
      name: '403',
      component: () => import('../views/tour/403View.vue'),
    }

  ],
})

// 全局路由守卫
import { ElMessage } from 'element-plus'

router.beforeEach((to, from, next) => {
  const userStore = useUserStore();
  const tokenStore = useTokenStore();

  const token = tokenStore.tokenData.token;
  const permission = userStore.userData.userPermission;

  const path = to.path;

  // 如果访问的是 /login 或 /403，登录状态也允许访问
  if (path === '/login' || path === '/403') {
    next();
    return;
  }

  // 未登录，跳转到登录页
  if (!token) {
    next({ path: '/login' });
    return;
  }

  // 登录状态，根据权限判断是否允许访问
  const isUserPath = path.startsWith('/user');
  const isDepartAdminPath = path.startsWith('/departadmin');
  const isAdminPath = path.startsWith('/admin');

  let allowed = false;

  if (permission === 1) {
    allowed = isUserPath;
  } else if (permission === 2) {
    allowed = isUserPath || isDepartAdminPath;
  } else if (permission === 3) {
    allowed = true;
  }

  if (!allowed) {
    ElMessage.error('权限不足，无法访问该页面');
    next({path:'/403'}); // 阻止跳转
  } else {
    next();
  }
});



export default router
