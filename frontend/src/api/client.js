import axios from 'axios'

const TOKEN_KEY = 'football-league-token'

export function setToken(token) {
  if (token) {
    localStorage.setItem(TOKEN_KEY, token)
  } else {
    localStorage.removeItem(TOKEN_KEY)
  }
}

export function getToken() {
  return localStorage.getItem(TOKEN_KEY)
}

export const api = axios.create({ baseURL: '/api' })

api.interceptors.request.use((config) => {
  const token = getToken()
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401 && !error.config?.url?.startsWith('/auth/login')) {
      setToken(null)
      window.dispatchEvent(new Event('auth:unauthorized'))
    }
    return Promise.reject(error)
  },
)

export function parseApiError(error) {
  const data = error?.response?.data
  return {
    status: data?.status ?? error?.response?.status ?? 0,
    message: data?.message ?? error?.message ?? 'Erro desconhecido',
    fieldErrors: data?.errors ?? {},
  }
}
