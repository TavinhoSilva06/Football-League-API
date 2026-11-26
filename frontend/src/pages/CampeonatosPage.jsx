import { useState } from 'react'
import { Link } from 'react-router-dom'
import { useQueryClient } from '@tanstack/react-query'
import { Card, Button, Async, Badge, FormModal, ConfirmDialog, HeroPanel } from '../components/ui'
import { IconArrowRight } from '../components/icons'
import { useGet } from '../api/hooks'
import { api } from '../api/client'
import { TIPO_CAMPEONATO, FORMATO_CAMPEONATO, tipoOptions, formatoOptions } from '../lib/enums'
import { useAuth } from '../AuthContext'

export function CampeonatosPage() {
  const qc = useQueryClient()
  const { user } = useAuth()
  const isAdmin = user?.role === 'ADMIN'
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
      <Async query={query} empty="Nenhum campeonato criado ainda.">
        {(data) => (
          <>
            <HeroPanel
              eyebrow="Painel principal"
              title="Tudo começa por aqui"
              description="Abra um campeonato, entre na temporada e transforme cada rodada em uma chance de subir de patente."
              stats={[
                { label: 'campeonatos', value: data.length },
                { label: 'missão', value: 'acerto exato' },
              ]}
              action={isAdmin ? <Button onClick={() => { setEditing(null); setShowForm(true) }}>Criar campeonato</Button> : undefined}
            />
            <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-3">
              {data.map((c, i) => (
                <Link key={c.id} to={`/campeonatos/${c.id}`} className="animate-fade-in" style={{ animationDelay: `${i * 60}ms` }}>
                  <Card className="group h-full transition">
                    <div className="flex h-full flex-col gap-3 p-5">
                      <div className="flex items-start justify-between gap-3">
                        <div>
                          <h3 className="text-lg font-bold text-slate-900 group-hover:text-primary-dark transition-colors">{c.nome}</h3>
                          {c.pais && <p className="mt-1 text-sm text-slate-500">{c.pais}</p>}
                        </div>
                        <IconArrowRight className="h-5 w-5 shrink-0 text-primary opacity-0 transition-all duration-300 group-hover:translate-x-1 group-hover:opacity-100" />
                      </div>
                      <div className="flex flex-wrap gap-2">
                        {c.tipo && <Badge color="primary">{TIPO_CAMPEONATO[c.tipo]}</Badge>}
                        {c.formato && <Badge color="slate">{FORMATO_CAMPEONATO[c.formato]}</Badge>}
                      </div>
                      {c.descricao && <p className="text-xs text-slate-500">{c.descricao}</p>}
                      {isAdmin && (
                        <div className="flex gap-2 pt-2">
                          <Button size="sm" variant="secondary" onClick={(e) => { e.preventDefault(); setEditing(c); setShowForm(true) }}>Editar</Button>
                          <Button size="sm" variant="danger" onClick={(e) => { e.preventDefault(); setShowDelete(c) }}>Excluir</Button>
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
