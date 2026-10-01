import { useState } from 'react'
import { useParams, Link } from 'react-router-dom'
import { useQueryClient } from '@tanstack/react-query'
import { PageHeader, Async, Button, Card, Badge, ConfirmDialog, FormModal } from '../components/ui'
import { useGet } from '../api/hooks'
import { api } from '../api/client'
import { TIPO_CAMPEONATO, FORMATO_CAMPEONATO, tipoOptions, formatoOptions } from '../lib/enums'

export function CampeonatoDetalhe() {
  const { id } = useParams()
  const qc = useQueryClient()
  const query = useGet(['campeonato', id], `/campeonatos/${id}`)
  const timesQuery = useGet(['campeonato-times', id], `/campeonatos/${id}/times`)

  const [showEdit, setShowEdit] = useState(false)
  const [showDelete, setShowDelete] = useState(false)

  async function handleEdit(data) {
    await api.put(`/campeonatos/${id}`, data)
    await qc.invalidateQueries({ queryKey: ['campeonato', id] })
  }

  async function handleDelete() {
    await api.delete(`/campeonatos/${id}`)
    window.location.href = '/campeonatos'
  }

  return (
    <div>
      <Link to="/campeonatos" className="mb-4 inline-block text-sm text-red-700 hover:underline">← Voltar</Link>
      <Async query={query}>
        {(camp) => (
          <>
            <PageHeader
              title={camp.nome}
              subtitle={camp.pais}
              actions={[
                <Button key="edit" size="sm" onClick={() => setShowEdit(true)}>Editar</Button>,
                <Button key="del" size="sm" variant="danger" onClick={() => setShowDelete(true)}>Excluir</Button>,
              ]}
            />
            <div className="mb-6 flex gap-2">
              {camp.tipo && <Badge color="slate">{TIPO_CAMPEONATO[camp.tipo]}</Badge>}
              {camp.formato && <Badge color="slate">{FORMATO_CAMPEONATO[camp.formato]}</Badge>}
            </div>
            {camp.descricao && <p className="mb-6 text-slate-600">{camp.descricao}</p>}

            <div className="space-y-6">
              <div>
                <h2 className="mb-4 text-xl font-bold text-slate-900">Temporadas</h2>
                <Link to={`/campeonatos/${camp.id}/temporadas`}>
                  <Button>📅 Gerenciar Temporadas</Button>
                </Link>
              </div>

              <div>
                <h2 className="mb-4 text-xl font-bold text-slate-900">Times Participantes</h2>
                <Async query={timesQuery} empty="Nenhum time neste campeonato.">
                  {(data) => (
                    <div className="grid gap-3 md:grid-cols-2">
                      {data.map((t) => (
                        <Link key={t.id} to={`/times/${t.id}`}>
                          <Card className="hover:border-red-300 hover:shadow-lg transition-all duration-300 cursor-pointer">
                            <div className="p-4">
                              <h3 className="font-semibold text-slate-900">{t.nome}</h3>
                              {t.cidade && <p className="text-sm text-slate-500 mt-1">{t.cidade}</p>}
                            </div>
                          </Card>
                        </Link>
                      ))}
                    </div>
                  )}
                </Async>
              </div>
            </div>

            {showEdit && (
              <FormModal
                title="Editar Campeonato"
                fields={[
                  { name: 'nome', label: 'Nome', required: true },
                  { name: 'pais', label: 'País' },
                  { name: 'tipo', label: 'Tipo', type: 'select', options: tipoOptions },
                  { name: 'formato', label: 'Formato', type: 'select', options: formatoOptions },
                  { name: 'descricao', label: 'Descrição', type: 'textarea' },
                ]}
                initial={camp}
                onSubmit={handleEdit}
                onClose={() => setShowEdit(false)}
              />
            )}

            {showDelete && (
              <ConfirmDialog
                title="Excluir Campeonato"
                message={`Tem certeza que deseja excluir "${camp.nome}"?`}
                confirmLabel="Excluir"
                conflictHint="Este campeonato tem temporadas. Remova-as antes de excluir."
                onConfirm={handleDelete}
                onClose={() => setShowDelete(false)}
              />
            )}
          </>
        )}
      </Async>
    </div>
  )
}
