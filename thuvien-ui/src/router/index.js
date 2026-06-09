import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '../stores/auth'

const routes = [
  { path: '/login', name: 'Login', component: () => import('../views/LoginView.vue') },
  { path: '/register', name: 'Register', component: () => import('../views/RegisterView.vue') },
  { path: '/', name: 'Books', component: () => import('../views/BooksView.vue') },
  { path: '/books/:id', name: 'BookDetail', component: () => import('../views/BookDetailView.vue') },

  { path: '/profile', name: 'Profile', component: () => import('../views/ProfileView.vue'), meta: { auth: true } },
  { path: '/profile/borrow-history', name: 'BorrowHistory', component: () => import('../views/BorrowHistoryView.vue'), meta: { auth: true } },
  { path: '/profile/current-borrows', name: 'CurrentBorrows', component: () => import('../views/CurrentBorrowsView.vue'), meta: { auth: true } },

  { path: '/admin/books/new', name: 'BookCreate', component: () => import('../views/BookFormView.vue'), meta: { auth: true, admin: true } },
  { path: '/admin/books/:id/edit', name: 'BookEdit', component: () => import('../views/BookFormView.vue'), meta: { auth: true, admin: true } },
  { path: '/admin/users', name: 'Users', component: () => import('../views/UsersView.vue'), meta: { auth: true, admin: true } },
  { path: '/admin/users/:id', name: 'UserDetail', component: () => import('../views/UserDetailView.vue'), meta: { auth: true, admin: true } },
  { path: '/admin/categories', name: 'Categories', component: () => import('../views/CategoriesView.vue'), meta: { auth: true, admin: true } },
  { path: '/admin/borrow', name: 'BorrowManagement', component: () => import('../views/BorrowManagementView.vue'), meta: { auth: true, admin: true } },
  { path: '/admin/dashboard', name: 'Dashboard', component: () => import('../views/DashboardView.vue'), meta: { auth: true, admin: true } },
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const auth = useAuthStore()
  if (to.meta.auth && !auth.isAuthenticated) {
    next({ name: 'Login', query: { redirect: to.fullPath } })
  } else if (to.meta.admin && !auth.isAdmin) {
    next({ name: 'Books' })
  } else {
    next()
  }
})

export default router
