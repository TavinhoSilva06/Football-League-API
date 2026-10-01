const toOptions = (map) => Object.entries(map).map(([value, label]) => ({ value, label }))

export const TIPO_CAMPEONATO = {
  LIGA_NACIONAL: 'Liga nacional',
  CONTINENTAL: 'Continental',
  COPA: 'Copa',
}
export const FORMATO_CAMPEONATO = {
  PONTOS_CORRIDOS: 'Pontos corridos',
  MATA_MATA: 'Mata-mata',
}
export const STATUS_TEMPORADA = {
  PLANEJADA: 'Planejada',
  EM_ANDAMENTO: 'Em andamento',
  ENCERRADA: 'Encerrada',
}
export const STATUS_PARTIDA = {
  AGENDADA: 'Agendada',
  EM_ANDAMENTO: 'Em andamento',
  FINALIZADA: 'Finalizada',
  ADIADA: 'Adiada',
  CANCELADA: 'Cancelada',
}
export const POSICAO_JOGADOR = {
  GOLEIRO: 'Goleiro',
  DEFESA: 'Defesa',
  MEIO_CAMPO: 'Meio-campo',
  ATACANTE: 'Atacante',
}

export const tipoOptions = toOptions(TIPO_CAMPEONATO)
export const formatoOptions = toOptions(FORMATO_CAMPEONATO)
export const statusTemporadaOptions = toOptions(STATUS_TEMPORADA)
export const statusPartidaOptions = toOptions(STATUS_PARTIDA)
export const posicaoOptions = toOptions(POSICAO_JOGADOR)
