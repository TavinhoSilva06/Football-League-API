import axios from 'axios'

export const api = axios.create({ baseURL: '/api' })

// Normaliza o erro do backend: { timestamp, status, message, path, errors? }
export function parseApiError(error) {
  const data = error?.response?.data
  return {
    status: data?.status ?? error?.response?.status ?? 0,
    message: data?.message ?? error?.message ?? 'Erro desconhecido',
    fieldErrors: data?.errors ?? {},
  }
}
