import api from './index'

export const bookApi = {
  getAll: () => api.get('/books'),
  getPaged: (page, size, params) => api.get('/books', { params: { page, size, ...params } }),
  getById: id => api.get(`/books/${id}`),
  getCopies: id => api.get(`/books/${id}/copies`),
  addCopies: (id, isbns) => api.post(`/books/${id}/copies`, { isbns }),
  create: data => api.post('/books', data),
  update: (id, data) => api.put(`/books/${id}`, data),
  delete: id => api.delete(`/books/${id}`)
}
