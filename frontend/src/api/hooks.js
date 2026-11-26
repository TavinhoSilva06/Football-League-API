import { useQuery } from '@tanstack/react-query'
import { api } from './client'

export function useGet(key, url, options = {}) {
  return useQuery({ queryKey: key, queryFn: () => api.get(url).then((r) => r.data), ...options })
}

export function refreshPartidas(qc) {
  qc.invalidateQueries({ queryKey: ['partidas-temp'] })
  qc.invalidateQueries({ queryKey: ['partidas-time'] })
  qc.invalidateQueries({ queryKey: ['palpites-rodada'] })
  qc.invalidateQueries({ queryKey: ['palpites-resultados'] })
  qc.invalidateQueries({ queryKey: ['classificacao'] })
  qc.invalidateQueries({ queryKey: ['artilharia'] })
  qc.invalidateQueries({ queryKey: ['assistencias'] })
  qc.invalidateQueries({ queryKey: ['cartoes'] })
  qc.invalidateQueries({ queryKey: ['estatisticas'] })
}
