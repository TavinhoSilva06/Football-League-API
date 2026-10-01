import { useState } from 'react'
import { Link } from 'react-router-dom'
import { useQueryClient } from '@tanstack/react-query'
import { PageHeader, Card, Button, Async, Badge, FormModal, ConfirmDialog } from '../components/ui'
import { useGet } from '../api/hooks'
import { api } from '../api/client'
import { TIPO_CAMPEONATO, FORMATO_CAMPEONATO, tipoOptions, formatoOptions } from '../lib/enums'

export function CampeonatosPage() {
  const qc = useQueryClient()
  const query = useGet(['campeonatos'], '/campeonatos')
  const [showForm, setShowForm] = useState(false)
  const [showDelete, setShowDelete] = useState(null)
  const [editing, setEditing] = useState(null)

  async function submit(data) {
    if (editing) {
      await api.put(`/campeonatos/${editing.id}`, data)
      await qc.invalidateQueries({ queryKey: ['campeonatos'] })
      setEditing(null)
    } else {
      await api.post('/campeonatos', data)
      await qc.invalidateQueries({ queryKey: ['campeonatos'] })
      setShowForm(false)
    }
  }

  async function handleDelete() {
    if (showDelete) {
      await api.delete(`/campeonatos/${showDelete.id}`)
      await qc.invalidateQueries({ queryKey: ['campeonatos'] })
      setShowDelete(null)
    }
  }

  return (
    <div>
      <PageHeader
        title="Campeonatos"
        subtitle="Gerencie as competições"
        actions={[<Button key="new" onClick={() => { setEditing(null); setShowForm(true) }}>Novo Campeonato</Button>]}
      />
      <Async query={query} empty="Nenhum campeonato criado ainda.">
        {(data) => (
          <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-3">
            {data.map((c) => (
              <Link key={c.id} to={`/campeonatos/${c.id}`}>
                <Card className="hover:border-red-300 hover:shadow-md transition">
                  <div className="space-y-2 p-4">
                    <h3 className="font-semibold text-slate-900">{c.nome}</h3>
                    {c.pais && <p className="text-sm text-slate-600">{c.pais}</p>}
                    <div className="flex gap-2">
                      {c.tipo && <Badge color="slate">{TIPO_CAMPEONATO[c.tipo]}</Badge>}
                      {c.formato && <Badge color="slate">{FORMATO_CAMPEONATO[c.formato]}</Badge>}
                    </div>
                    {c.descricao && <p className="text-xs text-slate-500">{c.descricao}</p>}
                    <div className="flex gap-2 pt-2">
                      <Button size="sm" variant="secondary" onClick={(e) => { e.preventDefault(); setEditing(c); setShowForm(true) }}>Editar</Button>
                      <Button size="sm" variant="danger" onClick={(e) => { e.preventDefault(); setShowDelete(c) }}>Excluir</Button>
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
          title={editing ? 'Editar Campeonato' : 'Novo Campeonato'}
          fields={[
            { name: 'nome', label: 'Nome', required: true },
            { name: 'pais', label: 'País' },
            { name: 'tipo', label: 'Tipo', type: 'select', options: tipoOptions },
            { name: 'formato', label: 'Formato', type: 'select', options: formatoOptions },
            { name: 'descricao', label: 'Descrição', type: 'textarea' },
          ]}
          initial={editing}
          onSubmit={submit}
          onClose={() => { setShowForm(false); setEditing(null) }}
          submitLabel={editing ? 'Salvar' : 'Criar'}
        />
      )}

      {showDelete && (
        <ConfirmDialog
          title="Excluir Campeonato"
          message={`Tem certeza que deseja excluir "${showDelete.nome}"? Remova as temporadas primeiro se houver.`}
          confirmLabel="Excluir"
          conflictHint="Este campeonato tem temporadas. Remova-as antes de excluir."
          onConfirm={handleDelete}
          onClose={() => setShowDelete(null)}
        />
      )}
    </div>
  )
}
