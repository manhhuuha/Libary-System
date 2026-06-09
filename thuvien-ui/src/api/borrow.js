import api from './index'

export const borrowApi = {
  borrow: (userId, bookCopyId, dueDate) => {
    const params = { userId, bookCopyId }
    if (dueDate) params.dueDate = dueDate
    return api.post('/borrow', null, { params })
  },
  returnBook: bookCopyId => api.put('/borrow/return', null, { params: { bookCopyId } }),
  dueSoon: () => api.get('/borrow/due-soon'),
  overdue: () => api.get('/borrow/overdue'),
  countBookNotReturn: () => api.get('/borrow/count-book-not-return'),
  current: () => api.get('/borrow/current'),
  history: userId => api.get('/borrow/history', { params: { userId } }),
  historyAll: () => api.get('/borrow/history/all'),
  sendReminder: id => api.post(`/borrow/send-reminder/${id}`)
}
