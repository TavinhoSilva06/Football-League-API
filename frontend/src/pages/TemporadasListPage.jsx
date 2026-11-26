import { useState } from 'react'
import { useParams, Link } from 'react-router-dom'
import { useQueryClient } from '@tanstack/react-query'
import { Card, Button, Async, Badge, FormModal, ConfirmDialog, HeroPanel } from '../components/ui'
import { IconArrowRight, IconCalendar, IconTarget } from '../components/icons'
import { useGet } from '../api/hooks'
import { api } from '../api/client'
import { STATUS_TEMPORADA, statusTemporadaOptions } from '../lib/enums'
import { formatDate } from '../lib/format'
import { useAuth } from '../AuthContext'

export function TemporadasListPage() {
  const { campeonatoId } = useParams()
  const qc = useQueryClient()
  const { user } = useAuth()
  const isAdmin = user?.role === 'ADMIN'
  const campQuery = useGet(['campeonato', campeonatoId], `/campeonatos/${campeonatoId}`)
  const query = useGet(['campeonato-temporadas', campeonatoId], `/campeonatos/${campeonatoId}/temporadas`)
  const [showForm, setShowForm] = useState(false)
  const [editing, setEditing] = useState(null)
  const [showDelete, setShowDelete] = useState(null)

  async function submit(data) {
    if (editing) {
      await api.put(`/temporadas/${editing.id}`, data)
      await qc.invalidateQueries({ queryKey: ['campeonato-temporadas', campeonatoId] })
      setEditing(null)
    } else {
      await api.post(`/campeonatos/${campeonatoId}/temporadas`, data)
      await qc.invalidateQueries({ queryKey: ['campeonato-temporadas', campeonatoId] })
      setShowForm(false)
    }
  }

  async function handleDelete() {
    if (showDelete) {
      await api.delete(`/temporadas/${showDelete.id}`)
      await qc.invalidateQueries({ queryKey: ['campeonato-temporadas', campeonatoId] })
      setShowDelete(null)
    }
  }

  return (
    <div>
      <Link to="/campeonatos" className="mb-4 inline-flex items-center gap-2 text-sm font-semibold text-primary-dark hover:underline">
        <IconArrowRight className="h-4 w-4 rotate-180" />
        Voltar para Campeonatos
      </Link>
      <Async query={campQuery}>
        {(camp) => (
          <>
            <HeroPanel
              eyebrow="Temporadas"
              title={`Temporadas de ${camp.nome}`}
              description={camp.pais ? `${camp.pais} · escolha uma temporada para abrir a central de palpites, partidas e resultados.` : 'Escolha uma temporada para abrir a central de palpites, partidas e resultados.'}
              action={isAdmin ? <Button onClick={() => { setEditing(null); setShowForm(true) }}>Criar temporada</Button> : undefined}
            />

            <Async query={query} empty="Nenhuma temporada neste campeonato. Crie uma para começar!">
              {(data) => (
                <div className="space-y-3">
                  {data.map((t, i) => (
                    <Link key={t.id} to={`/temporadas/${t.id}`} className="animate-fade-in" style={{ animationDelay: `${i * 60}ms` }}>
                      <Card className="group cursor-pointer transition-all duration-300">
                        <div className="flex flex-col gap-3 p-5 sm:flex-row sm:items-center sm:justify-between">
                          <div className="flex min-w-0 flex-1 items-center gap-4">
                            <div className="flex h-11 w-11 shrink-0 items-center justify-center rounded-2xl bg-primary-light text-primary ring-1 ring-primary/10">
                              <IconTarget className="h-5 w-5" />
                            </div>
                            <div className="min-w-0">
                              <h3 className="truncate text-lg font-bold text-slate-900 group-hover:text-primary-dark transition-colors">{t.nome}</h3>
                              <div className="mt-2 flex flex-wrap items-center gap-x-4 gap-y-1 text-sm text-slate-600">
                                {t.dataInicio && <span className="inline-flex items-center gap-1.5"><IconCalendar className="h-3.5 w-3.5 text-primary" />{formatDate(t.dataInicio)}</span>}
                                {t.dataFim && <span>até {formatDate(t.dataFim)}</span>}
                                {t.numRodadas && <span className="inline-flex items-center gap-1.5"><IconTarget className="h-3.5 w-3.5 text-secondary" />{t.numRodadas} rodadas</span>}
                              </div>
                            </div>
                          </div>
                          <div className="flex flex-col items-end gap-3 sm:items-center">
                            <div className="flex items-center gap-3">
                              <Badge color={t.status === 'ENCERRADA' ? 'slate' : t.status === 'EM_ANDAMENTO' ? 'green' : 'amber'}>
                                {STATUS_TEMPORADA[t.status]}
                              </Badge>
                              <IconArrowRight className="h-5 w-5 text-primary opacity-0 transition-all duration-300 group-hover:translate-x-1 group-hover:opacity-100" />
                            </div>
                            {isAdmin && (
                              <div className="flex gap-2">
                                <Button size="sm" variant="secondary" onClick={(e) => { e.preventDefault(); setEditing(t); setShowForm(true) }}>Editar</Button>
                                <Button size="sm" variant="danger" onClick={(e) => { e.preventDefault(); setShowDelete(t) }}>Excluir</Button>
                              </div>
                            )}
                          </div>
                        </div>
                      </Card>
                    </Link>
                  ))}
                </div>
              )}
            </Async>

            {showForm && (
              <FormModal
                title={editing ? 'Editar Temporada' : 'Nova Temporada'}
                fields={[
                  { name: 'nome', label: 'Nome', required: true },
                  { name: 'dataInicio', label: 'Data Início', type: 'date' },
                  { name: 'dataFim', label: 'Data Fim', type: 'date' },
                  { name: 'numRodadas', label: 'Número de Rodadas', type: 'number', min: '1' },
                  { name: 'status', label: 'Status', type: 'select', options: statusTemporadaOptions, required: true },
                ]}
                initial={editing}
                onSubmit={submit}
                onClose={() => { setShowForm(false); setEditing(null) }}
                submitLabel={editing ? 'Salvar' : 'Criar'}
              />
            )}

            {showDelete && (
              <ConfirmDialog
                title="Excluir Temporada"
                message={`Tem certeza que deseja excluir "${showDelete.nome}"?`}
                confirmLabel="Excluir"
                onConfirm={handleDelete}
                onClose={() => setShowDelete(null)}
              />
            )}
          </>
        )}
      </Async>
    </div>
  )
}
