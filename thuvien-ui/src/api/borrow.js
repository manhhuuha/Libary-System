import api from './index'

export const borrowApi = {
  borrow: (userId, bookId) => api.post('/borrow', null, { params: { userId, bookId } }),
  returnBook: bookId => api.put('/borrow/return', null, { params: { bookId } }),
  dueSoon: () => api.get('/borrow/due-soon'),
  overdue: () => api.get('/borrow/overdue'),
  countBookNotReturn: () => api.get('/borrow/count-book-not-return'),
  current: () => api.get('/borrow/current')
}
