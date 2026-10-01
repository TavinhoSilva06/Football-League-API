import { useState } from 'react'
import { useQueryClient } from '@tanstack/react-query'
import { Button, Async, FormModal, ConfirmDialog } from '../components/ui'
import { PartidaItem } from '../components/PartidaItem'
import { useGet, refreshPartidas } from '../api/hooks'
import { api } from '../api/client'
import { statusPartidaOptions } from '../lib/enums'

export function PartidasPage({ temporadaId }) {
  const qc = useQueryClient()
  const query = useGet(['partidas-temp', temporadaId], `/temporadas/${temporadaId}/partidas`)
  const tempQuery = useGet(['temporada', temporadaId], `/temporadas/${temporadaId}`)
  const participacoesQuery = useGet(['participacoes', temporadaId], `/temporadas/${temporadaId}/participacoes`)

  const [rodadaFilter, setRodadaFilter] = useState('')
  const [statusFilter, setStatusFilter] = useState('')
  const [showCreate, setShowCreate] = useState(false)
  const [showEdit, setShowEdit] = useState(null)
  const [showResultado, setShowResultado] = useState(null)
  const [showDelete, setShowDelete] = useState(null)

  async function handleCreate(data) {
    await api.post(`/temporadas/${temporadaId}/partidas`, data)
    await qc.invalidateQueries({ queryKey: ['partidas-temp', temporadaId] })
    setShowCreate(false)
  }

  async function handleEdit(data) {
    await api.put(`/partidas/${showEdit.id}`, data)
    await qc.invalidateQueries({ queryKey: ['partidas-temp', temporadaId] })
    setShowEdit(null)
  }

  async function handleResultado(data) {
    await api.put(`/partidas/${showResultado.id}/resultado`, data)
    refreshPartidas(qc)
    setShowResultado(null)
  }

  async function handleDelete() {
    if (showDelete) {
      await api.delete(`/partidas/${showDelete.id}`)
      await qc.invalidateQueries({ queryKey: ['partidas-temp', temporadaId] })
      setShowDelete(null)
    }
  }

  return (
    <div className="space-y-4">
      <div className="flex flex-wrap gap-2">
        <Button size="sm" onClick={() => setShowCreate(true)}>Nova Partida</Button>
        <input
          type="number"
          min="1"
          placeholder="Filtrar por rodada"
          value={rodadaFilter}
          onChange={(e) => setRodadaFilter(e.target.value)}
          className="rounded-md border border-slate-300 px-3 py-1.5 text-sm"
        />
        <select
          value={statusFilter}
          onChange={(e) => setStatusFilter(e.target.value)}
          className="rounded-md border border-slate-300 px-3 py-1.5 text-sm"
        >
          <option value="">Todos os status</option>
          {statusPartidaOptions.map((o) => (
            <option key={o.value} value={o.value}>{o.label}</option>
          ))}
        </select>
      </div>

      <Async query={query} empty="Nenhuma partida criada.">
        {(data) => {
          const filtered = data.filter((p) => {
            if (rodadaFilter && p.rodada !== Number(rodadaFilter)) return false
            if (statusFilter && p.status !== statusFilter) return false
            return true
          })

          const byRodada = {}
          filtered.forEach((p) => {
            if (!byRodada[p.rodada]) byRodada[p.rodada] = []
            byRodada[p.rodada].push(p)
          })

          return (
            <div className="space-y-6">
              {Object.entries(byRodada).map(([rodada, partidas]) => (
                <div key={rodada}>
                  <h3 className="mb-3 border-b-2 border-red-700 pb-2 font-semibold text-slate-900">Rodada {rodada}</h3>
                  <div className="space-y-2">
                    {partidas.map((p) => (
                      <PartidaItem
                        key={p.id}
                        partida={p}
                        onResultado={() => setShowResultado(p)}
                        onEdit={() => setShowEdit(p)}
                        onDelete={() => setShowDelete(p)}
                      />
                    ))}
                  </div>
                </div>
              ))}
            </div>
          )
        }}
      </Async>

      {showCreate && (
        <Async query={tempQuery}>
          {(temp) => (
            <Async query={participacoesQuery}>
              {(participacoes) => {
                const times = participacoes || []
                return (
                  <FormModal
                    title="Nova Partida"
                    fields={[
                      { name: 'rodada', label: 'Rodada', type: 'number', required: true, min: '1' },
                      {
                        name: 'timeMandanteId',
                        label: 'Time Mandante',
                        type: 'select',
                        required: true,
                        options: times.map((p) => ({ value: String(p.timeId), label: p.timeNome })),
                      },
                      {
                        name: 'timeVisitanteId',
                        label: 'Time Visitante',
                        type: 'select',
                        required: true,
                        options: times.map((p) => ({ value: String(p.timeId), label: p.timeNome })),
                      },
                      { name: 'dataHora', label: 'Data e Hora', type: 'datetime-local' },
                      { name: 'local', label: 'Local' },
                    ]}
                    onSubmit={handleCreate}
                    onClose={() => setShowCreate(false)}
                    validate={(data) => {
                      if (data.timeMandanteId === data.timeVisitanteId) {
                        return 'Time mandante não pode ser igual ao time visitante'
                      }
                      if (temp.numRodadas && data.rodada > temp.numRodadas) {
                        return `Rodada deve ser <= ${temp.numRodadas}`
                      }
                    }}
                  />
                )
              }}
            </Async>
          )}
        </Async>
      )}

      {showEdit && (
        <Async query={participacoesQuery}>
          {(participacoes) => {
            const times = participacoes || []
            return (
              <FormModal
                title="Editar Partida"
                fields={[
                  { name: 'rodada', label: 'Rodada', type: 'number', required: true },
                  {
                    name: 'timeMandanteId',
                    label: 'Time Mandante',
                    type: 'select',
                    required: true,
                    disabled: true,
                    options: times.map((p) => ({ value: String(p.timeId), label: p.timeNome })),
                  },
                  {
                    name: 'timeVisitanteId',
                    label: 'Time Visitante',
                    type: 'select',
                    required: true,
                    disabled: true,
                    options: times.map((p) => ({ value: String(p.timeId), label: p.timeNome })),
                  },
                  { name: 'dataHora', label: 'Data e Hora', type: 'datetime-local' },
                  { name: 'local', label: 'Local' },
                ]}
                initial={showEdit}
                onSubmit={handleEdit}
                onClose={() => setShowEdit(null)}
              />
            )
          }}
        </Async>
      )}

      {showResultado && (
        <FormModal
          title={showResultado.status === 'FINALIZADA' ? 'Corrigir Resultado' : 'Registrar Resultado'}
          fields={[
            { name: 'golsMandante', label: `Gols ${showResultado.timeMandanteNome}`, type: 'number', required: true, min: '0' },
            { name: 'golsVisitante', label: `Gols ${showResultado.timeVisitanteNome}`, type: 'number', required: true, min: '0' },
          ]}
          initial={showResultado}
          onSubmit={handleResultado}
          onClose={() => setShowResultado(null)}
          submitLabel={showResultado.status === 'FINALIZADA' ? 'Corrigir' : 'Registrar'}
        />
      )}

      {showDelete && (
        <ConfirmDialog
          title="Excluir Partida"
          message={`Tem certeza que deseja excluir a partida ${showDelete.timeMandanteNome} x ${showDelete.timeVisitanteNome}?`}
          confirmLabel="Excluir"
          onConfirm={handleDelete}
          onClose={() => setShowDelete(null)}
        />
      )}
    </div>
  )
}
