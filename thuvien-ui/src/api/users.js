import api from './index'

export const userApi = {
  getAll: () => api.get('/users'),
  getById: id => api.get(`/users/${id}`),
  update: (id, data) => api.put(`/users/${id}`, data),
  delete: id => api.delete(`/users/${id}`),
  register: data => api.post('/users/register', data),
  getMe: () => api.get('/users/me'),
  getBorrowHistory: () => api.get('/users/me/borrow-history'),
  getCurrentBorrows: () => api.get('/users/me/current-borrows'),
  search: keyword => api.get('/users/search', { params: { keyword } })
}
