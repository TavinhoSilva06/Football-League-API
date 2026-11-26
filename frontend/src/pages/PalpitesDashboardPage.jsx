import { useMemo, useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { useQueries } from '@tanstack/react-query'
import { Async, Badge, Button, Card, EmptyState, HeroPanel, Spinner } from '../components/ui'
import { useGet } from '../api/hooks'
import { api } from '../api/client'
import { useAuth } from '../AuthContext'
import { IconArrowRight, IconTarget } from '../components/icons'

const selectClass =
  'w-full rounded-xl border border-slate-300 bg-white px-4 py-2.5 text-sm font-semibold text-slate-800 transition focus:border-primary focus:outline-none focus:ring-4 focus:ring-primary/10'

function resumir(rodadas = []) {
  const jogos = rodadas.flatMap((rodada) => rodada.jogos ?? [])
  const acertos = jogos.filter((jogo) => jogo.acerto === true).length
  const erros = jogos.filter((jogo) => jogo.acerto === false).length
  const pontos = rodadas.reduce((total, rodada) => total + (rodada.pontosGanhos ?? 0), 0)
  return { acertos, erros, pontos, total: acertos + erros }
}

function porcentagem(parte, total) {
  return total ? Math.round((parte / total) * 100) : 0
}

export function PalpitesDashboardPage() {
  const { user } = useAuth()
  const navigate = useNavigate()
  const campeonatosQuery = useGet(['campeonatos'], '/campeonatos')
  const [campeonatoId, setCampeonatoId] = useState('')
  const [temporadaId, setTemporadaId] = useState('')

  const campeonatos = useMemo(() => campeonatosQuery.data ?? [], [campeonatosQuery.data])

  const temporadasQueries = useQueries({
    queries: campeonatos.map((campeonato) => ({
      queryKey: ['campeonato-temporadas', campeonato.id],
      queryFn: () => api.get(`/campeonatos/${campeonato.id}/temporadas`).then((r) => r.data),
      enabled: Boolean(user) && Boolean(campeonato.id),
      staleTime: 5 * 60 * 1000,
    })),
  })

  const temporadas = useMemo(
    () => temporadasQueries.flatMap((query, i) =>
      (query.data ?? []).map((temporada) => ({
        ...temporada,
        campeonatoId: campeonatos[i]?.id,
        campeonatoNome: campeonatos[i]?.nome,
      })),
    ),
    [temporadasQueries, campeonatos],
  )

  const resultadosQueries = useQueries({
    queries: temporadas.map((temporada) => ({
      queryKey: ['palpites-resultados', temporada.id],
      queryFn: () => api.get(`/temporadas/${temporada.id}/palpites/resultados`).then((r) => r.data),
      enabled: Boolean(user),
      staleTime: 5 * 60 * 1000,
    })),
  })

  const resumos = useMemo(() => {
    const mapa = new Map()
    temporadas.forEach((temporada, i) => mapa.set(temporada.id, resumir(resultadosQueries[i]?.data)))
    return mapa
  }, [temporadas, resultadosQueries])

  const totais = useMemo(
    () => Array.from(resumos.values()).reduce(
      (acc, r) => ({ acertos: acc.acertos + r.acertos, erros: acc.erros + r.erros, pontos: acc.pontos + r.pontos }),
      { acertos: 0, erros: 0, pontos: 0 },
    ),
    [resumos],
  )

  const totalAvaliados = totais.acertos + totais.erros
  const taxaAcertos = porcentagem(totais.acertos, totalAvaliados)
  const taxaErros = totalAvaliados ? 100 - taxaAcertos : 0

  const temporadasDoCampeonato = temporadas.filter((temporada) => String(temporada.campeonatoId) === String(campeonatoId))
  const carregando = temporadasQueries.some((query) => query.isLoading) || resultadosQueries.some((query) => query.isLoading)

  if (!user) {
    return (
      <Card className="p-8 text-center">
        <p className="text-sm text-slate-600">Entre na sua conta para ver seus acertos, erros e enviar palpites.</p>
        <Link to="/login" className="brand-gradient mt-5 inline-block rounded-xl px-4 py-2 text-sm font-bold text-white shadow-lg shadow-primary/20">Entrar</Link>
      </Card>
    )
  }

  return (
    <div className="space-y-6">
      <HeroPanel
        eyebrow="Seus palpites"
        title="Cada rodada é uma nova chance"
        description="Veja onde você acertou, onde errou e escolha rapidamente a próxima temporada para palpitar."
        stats={[
          { label: 'aproveitamento', value: `${taxaAcertos}%` },
          { label: 'acertos', value: totais.acertos },
          { label: 'erros', value: totais.erros },
          { label: 'pontos', value: totais.pontos },
        ]}
      />

      <Card className="p-5 sm:p-6">
        <div className="flex flex-wrap items-center justify-between gap-3">
          <div>
            <h2 className="text-lg font-bold text-slate-900">Aproveitamento geral</h2>
            <p className="mt-1 text-sm text-slate-500">Percentual de acertos e erros em todos os palpites já avaliados.</p>
          </div>
          <Badge color="primary">{totalAvaliados} palpite(s) avaliado(s)</Badge>
        </div>

        <div className="mt-5 grid gap-4 sm:grid-cols-2">
          <div className="animate-pop rounded-2xl border border-emerald-200 bg-emerald-50 p-4">
            <p className="text-xs font-bold uppercase tracking-wide text-emerald-800">Acertos</p>
            <p className="mt-1 text-3xl font-extrabold text-emerald-700">{taxaAcertos}%</p>
            <p className="mt-1 text-xs font-medium text-emerald-800">{totais.acertos} de {totalAvaliados}</p>
          </div>
          <div className="animate-pop delay-1 rounded-2xl border border-red-200 bg-red-50 p-4">
            <p className="text-xs font-bold uppercase tracking-wide text-red-800">Erros</p>
            <p className="mt-1 text-3xl font-extrabold text-red-700">{taxaErros}%</p>
            <p className="mt-1 text-xs font-medium text-red-800">{totais.erros} de {totalAvaliados}</p>
          </div>
        </div>

        <div className="mt-4 flex h-3 overflow-hidden rounded-full bg-slate-100">
          <div className="bg-emerald-500 transition-all duration-700" style={{ width: `${taxaAcertos}%` }} />
          <div className="bg-red-400 transition-all duration-700" style={{ width: `${taxaErros}%` }} />
        </div>
      </Card>

      <Card className="p-5 sm:p-6">
        <div className="flex flex-wrap items-center justify-between gap-3">
          <div>
            <h2 className="text-lg font-bold text-slate-900">Fazer um palpite</h2>
            <p className="mt-1 text-sm text-slate-500">Escolha o campeonato e a temporada, como na visão geral.</p>
          </div>
          <Link to="/campeonatos" className="inline-flex items-center gap-1.5 text-sm font-semibold text-primary-dark hover:underline">
            Ver visão geral
            <IconArrowRight className="h-4 w-4" />
          </Link>
        </div>

        <div className="mt-5 grid gap-4 sm:grid-cols-2 lg:grid-cols-[1fr_1fr_auto] lg:items-end">
          <div>
            <label className="mb-1.5 block text-xs font-bold uppercase tracking-wide text-slate-500" htmlFor="palpite-campeonato">Campeonato</label>
            <select
              id="palpite-campeonato"
              className={selectClass}
              value={campeonatoId}
              onChange={(e) => { setCampeonatoId(e.target.value); setTemporadaId('') }}
            >
              <option value="">Selecione...</option>
              {campeonatos.map((campeonato) => <option key={campeonato.id} value={campeonato.id}>{campeonato.nome}</option>)}
            </select>
          </div>
          <div>
            <label className="mb-1.5 block text-xs font-bold uppercase tracking-wide text-slate-500" htmlFor="palpite-temporada">Temporada</label>
            <select
              id="palpite-temporada"
              className={selectClass}
              value={temporadaId}
              disabled={!campeonatoId}
              onChange={(e) => setTemporadaId(e.target.value)}
            >
              <option value="">Selecione...</option>
              {temporadasDoCampeonato.map((temporada) => <option key={temporada.id} value={temporada.id}>{temporada.nome}</option>)}
            </select>
          </div>
          <Button
            className="h-[42px]"
            disabled={!temporadaId}
            onClick={() => navigate(`/temporadas/${temporadaId}?aba=palpites`)}
          >
            <IconTarget className="h-4 w-4" />
            Palpitar
          </Button>
        </div>
      </Card>

      <div>
        <div className="mb-4 flex items-end justify-between gap-3">
          <div>
            <h2 className="text-xl font-extrabold text-slate-900">Seu histórico por temporada</h2>
            <p className="text-sm text-slate-500">Somente palpites já avaliados entram na contagem.</p>
          </div>
          {carregando && <Spinner />}
        </div>

        <Async query={campeonatosQuery} empty="Nenhum campeonato cadastrado ainda.">
          {(lista) => (
            <div className="space-y-4">
              {lista.map((campeonato) => {
                const temporadasDoGrupo = temporadas.filter((temporada) => temporada.campeonatoId === campeonato.id)
                if (temporadasDoGrupo.length === 0) return null

                return (
                  <Card key={campeonato.id} className="p-5 sm:p-6">
                    <div className="mb-4 flex flex-wrap items-center justify-between gap-2">
                      <h3 className="text-lg font-bold text-slate-900">{campeonato.nome}</h3>
                      <Badge color="primary">{temporadasDoGrupo.length} temporada(s)</Badge>
                    </div>
                    <div className="grid gap-3 sm:grid-cols-2 xl:grid-cols-3">
                      {temporadasDoGrupo.map((temporada, i) => {
                        const resumo = resumos.get(temporada.id) ?? { acertos: 0, erros: 0, pontos: 0, total: 0 }
                        const taxaAcertoTemp = porcentagem(resumo.acertos, resumo.total)
                        const taxaErroTemp = resumo.total ? 100 - taxaAcertoTemp : 0

                        return (
                          <button
                            key={temporada.id}
                            type="button"
                            onClick={() => navigate(`/temporadas/${temporada.id}?aba=palpites`)}
                            className="animate-fade-in group rounded-2xl border border-slate-200 bg-white p-4 text-left transition-all duration-300 hover:-translate-y-0.5 hover:border-primary/30 hover:shadow-lg"
                            style={{ animationDelay: `${i * 40}ms` }}
                          >
                            <div className="flex items-start justify-between gap-3">
                              <div>
                                <div className="font-bold text-slate-900 group-hover:text-primary-dark">{temporada.nome}</div>
                                <div className="mt-0.5 text-xs text-slate-400">{campeonato.nome}</div>
                              </div>
                              <IconArrowRight className="h-4 w-4 shrink-0 text-primary opacity-0 transition-all group-hover:translate-x-1 group-hover:opacity-100" />
                            </div>
                            <div className="mt-4 flex flex-wrap gap-2">
                              <Badge color="green">{taxaAcertoTemp}% acerto(s) · {resumo.acertos}</Badge>
                              <Badge color="slate">{taxaErroTemp}% erro(s) · {resumo.erros}</Badge>
                              <Badge color="blue">{resumo.pontos} pts</Badge>
                            </div>
                            <div className="mt-3 flex h-1.5 overflow-hidden rounded-full bg-slate-100">
                              <div className="h-full bg-emerald-500 transition-all duration-700" style={{ width: `${taxaAcertoTemp}%` }} />
                              <div className="h-full bg-red-400 transition-all duration-700" style={{ width: `${taxaErroTemp}%` }} />
                            </div>
                          </button>
                        )
                      })}
                    </div>
                  </Card>
                )
              })}
            </div>
          )}
        </Async>

        {!carregando && temporadas.length === 0 && <EmptyState>Nenhuma temporada disponível para palpitar.</EmptyState>}
      </div>
    </div>
  )
}
