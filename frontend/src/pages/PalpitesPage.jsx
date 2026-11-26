import { useState } from 'react'
import { Link } from 'react-router-dom'
import { useQueryClient } from '@tanstack/react-query'
import { Async, Badge, Button, Card, ConfirmDialog, EmptyState, ErrorBox, Escudo } from '../components/ui'
import { useGet } from '../api/hooks'
import { api, parseApiError } from '../api/client'
import { useAuth } from '../AuthContext'
import { PATENTE, PATENTE_COR, PONTOS_POR_PATENTE } from '../lib/patentes'
import { PalpiteResultadoItem } from '../components/PalpiteResultadoItem'

export function PalpitesPage({ temporadaId }) {
  const { user, recarregarPerfil } = useAuth()
  const qc = useQueryClient()
  const query = useGet(
    ['palpites-rodada', temporadaId],
    `/temporadas/${temporadaId}/palpites/rodada-atual`,
    { enabled: Boolean(user) },
  )
  const [alteracoes, setAlteracoes] = useState({})
  const [busyId, setBusyId] = useState(null)
  const [simulando, setSimulando] = useState(false)
  const [showSimular, setShowSimular] = useState(false)
  const [erro, setErro] = useState(null)

  if (!user) {
    return (
      <Card className="p-6 text-center">
        <p className="text-sm text-slate-600">Entre na sua conta para enviar palpites, ganhar pontos e subir de patente.</p>
        <Link to="/login" className="mt-4 inline-block font-semibold text-red-700 hover:underline">Ir para o login</Link>
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

  async function salvar(jogo) {
    const atual = valoresPalpite(jogo)
    if (atual.golsMandante === '' || atual.golsVisitante === '') {
      setErro('Informe os dois placares do seu palpite.')
      return
    }
    setBusyId(jogo.partidaId)
    setErro(null)
    try {
      await api.post('/palpites', {
        partidaId: jogo.partidaId,
        golsMandante: Number(atual.golsMandante),
        golsVisitante: Number(atual.golsVisitante),
      })
      await qc.invalidateQueries({ queryKey: ['palpites-rodada', temporadaId] })
      await qc.invalidateQueries({ queryKey: ['palpites-resultados', temporadaId] })
    } catch (err) {
      setErro(parseApiError(err).message)
    } finally {
      setBusyId(null)
    }
  }

  async function simular(rodada) {
    setSimulando(true)
    setErro(null)
    try {
      await api.post(`/temporadas/${temporadaId}/rodadas/${rodada}/simular`)
      await Promise.all([
        qc.invalidateQueries({ queryKey: ['palpites-rodada', temporadaId] }),
        qc.invalidateQueries({ queryKey: ['palpites-resultados', temporadaId] }),
        qc.invalidateQueries({ queryKey: ['partidas-temp', temporadaId] }),
        qc.invalidateQueries({ queryKey: ['partidas-time'] }),
        qc.invalidateQueries({ queryKey: ['classificacao', temporadaId] }),
        qc.invalidateQueries({ queryKey: ['temporada', temporadaId] }),
      ])
      await recarregarPerfil()
    } catch (err) {
      setErro(parseApiError(err).message)
    } finally {
      setSimulando(false)
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
          <div className="h-full rounded-full bg-gradient-to-r from-red-500 to-red-700" style={{ width: `${progresso}%` }} />
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
                </div>
                {data.jogos.some((jogo) => !jogo.bloqueado) && (
                  <Button size="sm" onClick={() => setShowSimular(true)} disabled={simulando}>
                    {simulando ? 'Simulando...' : 'Simular rodada'}
                  </Button>
                )}
              </div>

              <div className="space-y-3">
                {data.jogos.map((jogo) => {
                  const atual = valoresPalpite(jogo)
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
                          {jogo.palpiteId && !jogo.bloqueado && <Badge color="blue">Palpite enviado</Badge>}
                          {jogo.bloqueado && (
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

                      {jogo.bloqueado ? (
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
                          <Button size="sm" variant="secondary" onClick={() => salvar(jogo)} disabled={busyId === jogo.partidaId}>
                            {busyId === jogo.partidaId ? 'Salvando...' : jogo.palpiteId ? 'Atualizar' : 'Salvar'}
                          </Button>
                        </div>
                      )}
                    </Card>
                  )
                })}
              </div>

              {showSimular && (
                <ConfirmDialog
                  title="Simular rodada"
                  message="Os resultados desta rodada serão gerados e os palpites serão avaliados. Após isso, não será possível alterar os palpites."
                  confirmLabel="Simular"
                  onConfirm={() => simular(data.rodada)}
                  onClose={() => setShowSimular(false)}
                />
              )}
            </div>
          )
        }}
      </Async>
    </div>
  )
}
