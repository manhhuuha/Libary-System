import api from './index'

export const bookApi = {
  getAll: () => api.get('/books'),
  getById: id => api.get(`/books/${id}`),
  search: params => api.get('/books/search', { params }),
  create: data => api.post('/books', data),
  update: (id, data) => api.put(`/books/${id}`, data),
  delete: id => api.delete(`/books/${id}`)
}
