import { useQuery } from '@tanstack/react-query'
import { api } from './client'

export function useGet(key, url, options = {}) {
  return useQuery({ queryKey: key, queryFn: () => api.get(url).then((r) => r.data), ...options })
}

export function refreshPartidas(qc) {
  qc.invalidateQueries({ queryKey: ['partidas'] })
  qc.invalidateQueries({ queryKey: ['classificacao'] })
}
