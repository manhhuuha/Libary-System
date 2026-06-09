import api from './index'

export const dashboardApi = {
  getSummary: () => api.get('/dashboard/summary')
}
