import { useState } from 'react'
import { Link } from 'react-router-dom'
import { useQueryClient } from '@tanstack/react-query'
import { PageHeader, Card, Button, Async, FormModal, ConfirmDialog, Escudo } from '../components/ui'
import { useGet } from '../api/hooks'
import { api } from '../api/client'

export function TimesPage() {
  const qc = useQueryClient()
  const query = useGet(['times'], '/times')
  const [showForm, setShowForm] = useState(false)
  const [editing, setEditing] = useState(null)
  const [showDelete, setShowDelete] = useState(null)

  async function submit(data) {
    if (editing) {
      await api.put(`/times/${editing.id}`, data)
      await qc.invalidateQueries({ queryKey: ['times'] })
      setEditing(null)
    } else {
      await api.post('/times', data)
      await qc.invalidateQueries({ queryKey: ['times'] })
      setShowForm(false)
    }
  }

  async function handleDelete() {
    if (showDelete) {
      await api.delete(`/times/${showDelete.id}`)
      await qc.invalidateQueries({ queryKey: ['times'] })
      setShowDelete(null)
    }
  }

  return (
    <div>
      <PageHeader
        title="Times"
        subtitle="Gerencie todos os times"
        actions={[<Button key="new" onClick={() => { setEditing(null); setShowForm(true) }}>Novo Time</Button>]}
      />
      <Async query={query} empty="Nenhum time criado ainda.">
        {(data) => (
          <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-3">
            {data.map((t) => (
              <Link key={t.id} to={`/times/${t.id}`}>
                <Card className="hover:border-red-300 hover:shadow-md transition">
                  <div className="space-y-3 p-4">
                    <div className="flex items-center gap-3">
                      <Escudo time={t} size="lg" />
                      <div className="flex-1">
                        <h3 className="font-semibold text-slate-900">{t.nome}</h3>
                        {t.sigla && <p className="text-xs text-slate-500">{t.sigla}</p>}
                      </div>
                    </div>
                    {t.cidade && <p className="text-sm text-slate-600">{t.cidade}</p>}
                    {t.estadio && <p className="text-xs text-slate-500">{t.estadio}</p>}
                    <div className="flex gap-2 pt-2">
                      <Button size="sm" variant="secondary" onClick={(e) => { e.preventDefault(); setEditing(t); setShowForm(true) }}>Editar</Button>
                      <Button size="sm" variant="danger" onClick={(e) => { e.preventDefault(); setShowDelete(t) }}>Excluir</Button>
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
          title={editing ? 'Editar Time' : 'Novo Time'}
          fields={[
            { name: 'nome', label: 'Nome', required: true },
            { name: 'sigla', label: 'Sigla' },
            { name: 'pais', label: 'País' },
            { name: 'cidade', label: 'Cidade' },
            { name: 'estadio', label: 'Estádio' },
            { name: 'escudoUrl', label: 'URL do Escudo' },
          ]}
          initial={editing}
          onSubmit={submit}
          onClose={() => { setShowForm(false); setEditing(null) }}
          submitLabel={editing ? 'Salvar' : 'Criar'}
        />
      )}

      {showDelete && (
        <ConfirmDialog
          title="Excluir Time"
          message={`Tem certeza que deseja excluir "${showDelete.nome}"?`}
          confirmLabel="Excluir"
          conflictHint="Este time tem partidas ou participações. Remova-as antes de excluir."
          onConfirm={handleDelete}
          onClose={() => setShowDelete(null)}
        />
      )}
    </div>
  )
}
