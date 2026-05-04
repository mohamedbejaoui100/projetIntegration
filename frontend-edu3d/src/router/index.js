import { createRouter, createWebHistory } from 'vue-router'
import Auth from '../views/Auth.vue'

const routes = [
  { path: '/', redirect: '/login' },
  { path: '/login', component: Auth },
  {
    path: '/admin',
    component: () => import('../views/AdminDashboard.vue'),
    meta: { requiresAuth: true, role: 'ADMIN' }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from) => {
  const token = localStorage.getItem('token')
  const role  = localStorage.getItem('role')

  if (to.meta.requiresAuth && !token) return '/login'
  if (to.meta.role && to.meta.role !== role) return '/login'
  return true   // ← return true au lieu de next()
})

export default router