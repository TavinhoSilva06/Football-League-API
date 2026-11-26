import { useState } from 'react'
import { Link } from 'react-router-dom'
import { useQueryClient } from '@tanstack/react-query'
import { Card, Button, Async, FormModal, ConfirmDialog, Escudo, HeroPanel } from '../components/ui'
import { IconArrowRight } from '../components/icons'
import { useGet } from '../api/hooks'
import { api } from '../api/client'
import { useAuth } from '../AuthContext'

export function TimesPage() {
  const qc = useQueryClient()
  const { user } = useAuth()
  const isAdmin = user?.role === 'ADMIN'
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
      <Async query={query} empty="Nenhum time criado ainda.">
        {(data) => (
          <>
            <HeroPanel
              eyebrow="Elencos"
              title="Cada escudo conta uma história"
              description="Cadastre times, acompanhe elencos e veja quem aparece na artilharia, assistências e cartões."
              stats={[
                { label: 'times', value: data.length },
                { label: 'foco', value: 'elenco' },
              ]}
              action={isAdmin ? <Button onClick={() => { setEditing(null); setShowForm(true) }}>Criar time</Button> : undefined}
            />
            <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-3">
              {data.map((t, i) => (
                <Link key={t.id} to={`/times/${t.id}`} className="animate-fade-in" style={{ animationDelay: `${i * 60}ms` }}>
                  <Card className="group h-full transition">
                    <div className="flex h-full flex-col gap-3 p-5">
                      <div className="flex items-center gap-4">
                        <Escudo time={t} size="lg" />
                        <div className="min-w-0 flex-1">
                          <h3 className="truncate text-lg font-bold text-slate-900 group-hover:text-primary-dark transition-colors">{t.nome}</h3>
                          {t.sigla && <p className="text-xs font-semibold uppercase tracking-wide text-slate-400">{t.sigla}</p>}
                        </div>
                        <IconArrowRight className="h-5 w-5 shrink-0 text-primary opacity-0 transition-all duration-300 group-hover:translate-x-1 group-hover:opacity-100" />
                      </div>
                      {t.cidade && <p className="text-sm text-slate-600">{t.cidade}</p>}
                      {t.estadio && <p className="text-xs text-slate-500">{t.estadio}</p>}
                      {isAdmin && (
                        <div className="flex gap-2 pt-2">
                          <Button size="sm" variant="secondary" onClick={(e) => { e.preventDefault(); setEditing(t); setShowForm(true) }}>Editar</Button>
                          <Button size="sm" variant="danger" onClick={(e) => { e.preventDefault(); setShowDelete(t) }}>Excluir</Button>
                        </div>
                      )}
                    </div>
                  </Card>
                </Link>
              ))}
            </div>
          </>
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
