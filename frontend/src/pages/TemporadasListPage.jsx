import { useState } from 'react'
import { useParams, Link } from 'react-router-dom'
import { useQueryClient } from '@tanstack/react-query'
import { PageHeader, Card, Button, Async, Badge, FormModal, ConfirmDialog } from '../components/ui'
import { useGet } from '../api/hooks'
import { api } from '../api/client'
import { STATUS_TEMPORADA, statusTemporadaOptions } from '../lib/enums'
import { formatDate } from '../lib/format'

export function TemporadasListPage() {
  const { campeonatoId } = useParams()
  const qc = useQueryClient()
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
      <Link to="/campeonatos" className="mb-4 inline-block text-sm text-red-700 hover:underline">← Voltar para Campeonatos</Link>
      <Async query={campQuery}>
        {(camp) => (
          <>
            <PageHeader
              title={`Temporadas de ${camp.nome}`}
              subtitle={camp.pais}
              actions={[<Button key="new" onClick={() => { setEditing(null); setShowForm(true) }}>Nova Temporada</Button>]}
            />

            <Async query={query} empty="Nenhuma temporada neste campeonato. Crie uma para começar!">
              {(data) => (
                <div className="space-y-3">
                  {data.map((t) => (
                    <Link key={t.id} to={`/temporadas/${t.id}`}>
                      <Card className="hover:border-red-300 hover:shadow-lg transition-all duration-300 cursor-pointer group">
                        <div className="flex flex-col gap-3 p-5 sm:flex-row sm:items-center sm:justify-between">
                          <div className="flex-1">
                            <h3 className="text-lg font-semibold text-slate-900 group-hover:text-red-700 transition-colors">{t.nome}</h3>
                            <div className="mt-2 flex flex-wrap gap-2 text-sm text-slate-600">
                              {t.dataInicio && <span>📅 {formatDate(t.dataInicio)}</span>}
                              {t.dataFim && <span>até {formatDate(t.dataFim)}</span>}
                              {t.numRodadas && <span>🎯 {t.numRodadas} rodadas</span>}
                            </div>
                          </div>
                          <div className="flex flex-col items-end gap-3 sm:items-center">
                            <Badge color={t.status === 'ENCERRADA' ? 'slate' : t.status === 'EM_ANDAMENTO' ? 'green' : 'amber'}>
                              {STATUS_TEMPORADA[t.status]}
                            </Badge>
                            <div className="flex gap-2">
                              <Button size="sm" variant="secondary" onClick={(e) => { e.preventDefault(); setEditing(t); setShowForm(true) }}>Editar</Button>
                              <Button size="sm" variant="danger" onClick={(e) => { e.preventDefault(); setShowDelete(t) }}>Excluir</Button>
                            </div>
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
