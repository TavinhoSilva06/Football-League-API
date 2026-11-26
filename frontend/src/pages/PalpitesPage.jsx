import { useEffect, useRef, useState } from 'react'
import { Link } from 'react-router-dom'
import { useQueryClient } from '@tanstack/react-query'
import { Async, Badge, Button, Card, EmptyState, ErrorBox, Escudo } from '../components/ui'
import { useGet } from '../api/hooks'
import { api, parseApiError } from '../api/client'
import { useAuth } from '../AuthContext'
import { PATENTE, PATENTE_COR, PONTOS_POR_PATENTE } from '../lib/patentes'
import { PalpiteResultadoItem } from '../components/PalpiteResultadoItem'

function formatarTempo(ms) {
  const total = Math.max(0, Math.ceil(ms / 1000))
  const minutos = String(Math.floor(total / 60)).padStart(2, '0')
  const segundos = String(total % 60).padStart(2, '0')
  return `${minutos}:${segundos}`
}

export function PalpitesPage({ temporadaId }) {
  const { user, recarregarPerfil } = useAuth()
  const qc = useQueryClient()
  const query = useGet(
    ['palpites-rodada', temporadaId],
    `/temporadas/${temporadaId}/palpites/rodada-atual`,
    { enabled: Boolean(user), refetchInterval: 10000 },
  )
  const [alteracoes, setAlteracoes] = useState({})
  const [salvandoTodos, setSalvandoTodos] = useState(false)
  const [erro, setErro] = useState(null)
  const [agora, setAgora] = useState(() => Date.now())
  const rodadaAnterior = useRef(null)

  useEffect(() => {
    const intervalo = setInterval(() => setAgora(Date.now()), 1000)
    return () => clearInterval(intervalo)
  }, [])

  useEffect(() => {
    if (query.isSuccess && query.data?.rodada !== rodadaAnterior.current) {
      rodadaAnterior.current = query.data?.rodada ?? null
      recarregarPerfil()
    }
  }, [query.isSuccess, query.data?.rodada, recarregarPerfil])

  if (!user) {
    return (
      <Card className="p-6 text-center">
        <p className="text-sm text-slate-600">Entre na sua conta para enviar palpites, ganhar pontos e subir de patente.</p>
        <Link to="/login" className="mt-4 inline-block font-semibold text-primary-dark hover:underline">Ir para o login</Link>
      </Card>
    )
  }

  function valoresPalpite(jogo) {
    const alterado = alteracoes[jogo.partidaId]
    return {
      golsMandante: alterado?.golsMandante ?? jogo.palpiteMandante ?? '',
      golsVisitante: alterado?.golsVisitante ?? jogo.palpiteVisitante ?? '',
    }
  }

  function palpitesParaEnvio(jogos) {
    return jogos
      .filter((jogo) => !jogo.bloqueado && !prazoExpirado)
      .map((jogo) => {
        const atual = valoresPalpite(jogo)
        if (atual.golsMandante === '' || atual.golsVisitante === '') return null
        return {
          partidaId: jogo.partidaId,
          golsMandante: Number(atual.golsMandante),
          golsVisitante: Number(atual.golsVisitante),
        }
      })
      .filter(Boolean)
  }

  async function salvarTodos() {
    if (palpitesPreenchidos.length === 0) {
      setErro('Preencha pelo menos um placar para salvar.')
      return
    }
    setSalvandoTodos(true)
    setErro(null)
    try {
      await api.post('/palpites/lote', { palpites: palpitesPreenchidos })
      await Promise.all([
        qc.invalidateQueries({ queryKey: ['palpites-rodada', temporadaId] }),
        qc.invalidateQueries({ queryKey: ['palpites-resultados', temporadaId] }),
      ])
      setAlteracoes({})
    } catch (err) {
      setErro(parseApiError(err).message)
    } finally {
      setSalvandoTodos(false)
    }
  }

  function alterar(jogo, campo, valor) {
    const atual = valoresPalpite(jogo)
    setAlteracoes((anterior) => ({
      ...anterior,
      [jogo.partidaId]: { ...atual, [campo]: valor },
    }))
  }

  const jogosAvaliados = (query.data?.jogos ?? []).filter((jogo) => jogo.acerto != null)
  const acertos = jogosAvaliados.filter((jogo) => jogo.acerto)
  const erros = jogosAvaliados.filter((jogo) => !jogo.acerto)
  const pontosGanhos = acertos.reduce((total, jogo) => total + (jogo.pontosGanhos ?? 0), 0)

  const pontos = user.pontos ?? 0
  const progresso = user.proximaPatente ? ((pontos % 1000) / 1000) * 100 : 100
  const dataLimite = query.data?.dataLimitePalpite ? new Date(query.data.dataLimitePalpite).getTime() : null
  const restanteMs = dataLimite === null ? null : Math.max(0, dataLimite - agora)
  const prazoExpirado = restanteMs !== null && restanteMs === 0
  const palpitesPreenchidos = palpitesParaEnvio(query.data?.jogos ?? [])

  return (
    <div className="space-y-5">
      <Card className="p-5">
        <div className="flex flex-wrap items-center justify-between gap-4">
          <div>
            <p className="text-xs font-semibold uppercase tracking-wide text-slate-500">Sua patente</p>
            <div className="mt-1 flex items-center gap-2">
              <span className="text-xl font-bold text-slate-900">{PATENTE[user.patente] || user.patente}</span>
              <Badge color={PATENTE_COR[user.patente]}>{pontos} pontos</Badge>
            </div>
          </div>
          <div className="text-right text-sm text-slate-600">
            <div>{PONTOS_POR_PATENTE[user.patente] ?? 10} pontos por acerto</div>
            <div className="text-xs text-slate-500">
              {user.proximaPatente
                ? `Faltam ${user.pontosParaProxima} pts para ${PATENTE[user.proximaPatente]}`
                : 'Patente máxima alcançada'}
            </div>
          </div>
        </div>
        <div className="mt-4 h-2 overflow-hidden rounded-full bg-slate-100">
          <div className="h-full rounded-full bg-gradient-to-r from-primary to-secondary" style={{ width: `${progresso}%` }} />
        </div>
      </Card>

      {erro && <ErrorBox message={erro} />}

      {jogosAvaliados.length > 0 && (
        <Card className="border-blue-200 bg-blue-50 p-4">
          <div className="flex flex-wrap items-center justify-between gap-2">
            <h2 className="font-bold text-slate-900">Resultado dos seus palpites</h2>
            <p className="text-sm text-slate-600">
              {acertos.length} acerto(s) · {erros.length} erro(s) · {pontosGanhos} pontos nesta rodada
            </p>
          </div>
          <div className="mt-4 grid gap-4 md:grid-cols-2">
            <div>
              <h3 className="mb-2 text-sm font-semibold text-emerald-800">Acertos</h3>
              {acertos.length === 0 ? (
                <p className="text-sm text-slate-600">Nenhum acerto nesta rodada.</p>
              ) : (
                <ul className="space-y-2">
                  {acertos.map((jogo) => <PalpiteResultadoItem key={jogo.partidaId} jogo={jogo} />)}
                </ul>
              )}
            </div>
            <div>
              <h3 className="mb-2 text-sm font-semibold text-red-700">Erros</h3>
              {erros.length === 0 ? (
                <p className="text-sm text-slate-600">Nenhum erro nesta rodada.</p>
              ) : (
                <ul className="space-y-2">
                  {erros.map((jogo) => <PalpiteResultadoItem key={jogo.partidaId} jogo={jogo} />)}
                </ul>
              )}
            </div>
          </div>
        </Card>
      )}

      <Async query={query} empty="Nenhuma rodada agendada no momento.">
        {(data) => {
          if (!data?.rodada) {
            return <EmptyState>Não há rodada atual agendada para palpites.</EmptyState>
          }

          return (
            <div className="space-y-4">
              <div className="flex flex-wrap items-center justify-between gap-3">
                <div>
                  <h2 className="text-lg font-bold text-slate-900">Rodada {data.rodada}</h2>
                  <p className="text-sm text-slate-500">
                    {data.palpitesEnviados} palpites enviados · {data.acertos} acertos · {data.pontosGanhos} pontos nesta rodada
                  </p>
                  {dataLimite !== null && (
                    <p className={`mt-1 text-sm font-semibold ${prazoExpirado ? 'text-amber-700' : 'text-primary-dark'}`}>
                      {prazoExpirado
                        ? 'Prazo encerrado — resultado em instantes'
                        : `Prazo para palpites: ${formatarTempo(restanteMs)}`}
                    </p>
                  )}
                </div>
                <Button
                  size="sm"
                  onClick={salvarTodos}
                  disabled={salvandoTodos || prazoExpirado || palpitesPreenchidos.length === 0}
                >
                  {salvandoTodos ? 'Salvando...' : `Salvar todos (${palpitesPreenchidos.length})`}
                </Button>
              </div>

              <div className="space-y-3">
                {data.jogos.map((jogo) => {
                  const atual = valoresPalpite(jogo)
                  const bloqueado = jogo.bloqueado || prazoExpirado
                  return (
                    <Card key={jogo.partidaId} className="p-4">
                      <div className="flex flex-wrap items-center justify-between gap-2">
                        <div className="flex flex-wrap items-center gap-2 font-semibold text-slate-900">
                          <Escudo time={{ nome: jogo.mandanteNome, escudoUrl: jogo.mandanteEscudoUrl }} size="sm" />
                          <span>{jogo.mandanteNome}</span>
                          <span className="text-slate-400">x</span>
                          <span>{jogo.visitanteNome}</span>
                          <Escudo time={{ nome: jogo.visitanteNome, escudoUrl: jogo.visitanteEscudoUrl }} size="sm" />
                        </div>
                        <div className="flex items-center gap-2">
                          {jogo.palpiteId && !bloqueado && <Badge color="blue">Palpite enviado</Badge>}
                          {bloqueado && (
                            <Badge color={jogo.acerto ? 'green' : 'slate'}>
                              {jogo.status === 'FINALIZADA'
                                ? jogo.acerto
                                  ? `Acerto +${jogo.pontosGanhos}`
                                  : 'Não acertou'
                                : jogo.status}
                            </Badge>
                          )}
                        </div>
                      </div>

                      {bloqueado ? (
                        <p className="mt-3 text-sm text-slate-600">
                          Resultado: <strong>{jogo.golsMandante ?? '-'} x {jogo.golsVisitante ?? '-'}</strong>
                          {' · '}Seu palpite: <strong>{jogo.palpiteMandante ?? '-'} x {jogo.palpiteVisitante ?? '-'}</strong>
                        </p>
                      ) : (
                        <div className="mt-3 flex flex-wrap items-center gap-2">
                          <input
                            type="number"
                            min="0"
                            aria-label={`Gols de ${jogo.mandanteNome}`}
                            value={atual.golsMandante}
                            onChange={(e) => alterar(jogo, 'golsMandante', e.target.value)}
                            className="w-20 rounded-lg border border-slate-300 px-3 py-1.5 text-sm"
                          />
                          <span className="text-slate-400">x</span>
                          <input
                            type="number"
                            min="0"
                            aria-label={`Gols de ${jogo.visitanteNome}`}
                            value={atual.golsVisitante}
                            onChange={(e) => alterar(jogo, 'golsVisitante', e.target.value)}
                            className="w-20 rounded-lg border border-slate-300 px-3 py-1.5 text-sm"
                          />
                        </div>
                      )}
                    </Card>
                  )
                })}
              </div>
            </div>
          )
        }}
      </Async>
    </div>
  )
}
