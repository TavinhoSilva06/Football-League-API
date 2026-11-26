import { useState } from 'react'
import { useQueryClient } from '@tanstack/react-query'
import { Link } from 'react-router-dom'
import { Button, Async, Card, ConfirmDialog, Escudo, FormModal } from '../components/ui'
import { useGet } from '../api/hooks'
import { api } from '../api/client'

export function ParticipacoesPage({ temporadaId }) {
  const qc = useQueryClient()
  const participacoesQuery = useGet(['participacoes', temporadaId], `/temporadas/${temporadaId}/participacoes`)
  const timesQuery = useGet(['times'], '/times')
  const escudosPorTime = Object.fromEntries((timesQuery.data ?? []).map((time) => [time.id, time.escudoUrl]))
  const [showAdd, setShowAdd] = useState(false)
  const [showDelete, setShowDelete] = useState(null)

  async function handleAdd(data) {
    await api.post(`/temporadas/${temporadaId}/participacoes`, data)
    await qc.invalidateQueries({ queryKey: ['participacoes', temporadaId] })
  }

  async function handleDelete() {
    if (showDelete) {
      await api.delete(`/participacoes/${showDelete.id}`)
      await qc.invalidateQueries({ queryKey: ['participacoes', temporadaId] })
      setShowDelete(null)
    }
  }

  return (
    <div className="space-y-4">
      <div className="flex justify-end">
        <Button size="sm" onClick={() => setShowAdd(true)}>Inscrever Time</Button>
      </div>

      <Async query={participacoesQuery} empty="Nenhum time inscrito.">
        {(data) => (
          <div className="space-y-2">
            {data.map((p) => (
              <Card key={p.id} className="flex items-center justify-between p-4">
                <div className="flex items-center gap-3">
                  <Escudo time={{ nome: p.timeNome, escudoUrl: p.timeEscudoUrl ?? escudosPorTime[p.timeId] }} size="sm" />
                  <Link to={`/times/${p.timeId}?temporada=${temporadaId}`} className="font-medium text-slate-900 hover:text-primary-dark hover:underline">
                    {p.timeNome}
                  </Link>
                </div>
                <Button
                  size="sm"
                  variant="danger"
                  onClick={() => setShowDelete(p)}
                >
                  Remover
                </Button>
              </Card>
            ))}
          </div>
        )}
      </Async>

      {showAdd && (
        <Async query={timesQuery}>
          {(times) => {
            const inscritos = participacoesQuery.data?.map(p => p.timeId) || []
            const disponiveis = times.filter(t => !inscritos.includes(t.id))
            return (
              <FormModal
                title="Inscrever Time"
                fields={[
                  {
                    name: 'timeId',
                    label: 'Time',
                    type: 'select',
                    required: true,
                    options: disponiveis.map(t => ({ value: String(t.id), label: t.nome })),
                  },
                ]}
                onSubmit={handleAdd}
                onClose={() => setShowAdd(false)}
              />
            )
          }}
        </Async>
      )}

      {showDelete && (
        <ConfirmDialog
          title="Remover Time"
          message={`Tem certeza que deseja remover "${showDelete.timeNome}" desta temporada?`}
          confirmLabel="Remover"
          onConfirm={handleDelete}
          onClose={() => setShowDelete(null)}
        />
      )}
    </div>
  )
}
