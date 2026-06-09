import axios from 'axios'

const api = axios.create({
  baseURL: '/api',
  headers: { 'Content-Type': 'application/json' }
})

api.interceptors.request.use(config => {
  const auth = localStorage.getItem('auth')
  if (auth) {
    const { username, password } = JSON.parse(auth)
    config.headers.Authorization = 'Basic ' + btoa(username + ':' + password)
  }
  return config
})

api.interceptors.response.use(
  response => response,
  error => {
    if (error.response && error.response.status === 401) {
      localStorage.removeItem('auth')
      window.location.href = '/login'
    }
    return Promise.reject(error)
  }
)

export default api
