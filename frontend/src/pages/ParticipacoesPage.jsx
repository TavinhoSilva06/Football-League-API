import { useState } from 'react'
import { useQueryClient } from '@tanstack/react-query'
import { Button, Async, Card, ConfirmDialog, FormModal } from '../components/ui'
import { useGet } from '../api/hooks'
import { api } from '../api/client'

export function ParticipacoesPage({ temporadaId }) {
  const qc = useQueryClient()
  const participacoesQuery = useGet(['participacoes', temporadaId], `/temporadas/${temporadaId}/participacoes`)
  const timesQuery = useGet(['times'], '/times')
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
                <span className="font-medium text-slate-900">{p.timeNome}</span>
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
