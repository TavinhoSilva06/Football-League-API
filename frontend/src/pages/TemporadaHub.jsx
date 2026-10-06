import { useState } from 'react'
import { useParams, Link } from 'react-router-dom'
import { useQueryClient } from '@tanstack/react-query'
import { PageHeader, Async, Button, Tabs, Badge, ConfirmDialog, FormModal, ErrorBox } from '../components/ui'
import { useGet } from '../api/hooks'
import { api } from '../api/client'
import { STATUS_TEMPORADA, statusTemporadaOptions } from '../lib/enums'
import { ParticipacoesPage } from './ParticipacoesPage'
import { PartidasPage } from './PartidasPage'
import { ClassificacaoPage } from './ClassificacaoPage'
import { ArtilhariaPage } from './ArtilhariaPage'
import { AssistenciasPage } from './AssistenciasPage'
import { CartõesPage } from './CartõesPage'

export function TemporadaHub() {
  const { id } = useParams()
  const qc = useQueryClient()
  const query = useGet(['temporada', id], `/temporadas/${id}`)
  const [tab, setTab] = useState('classificacao')
  const [showEdit, setShowEdit] = useState(false)
  const [showEncerrar, setShowEncerrar] = useState(false)
  const [showDelete, setShowDelete] = useState(false)
  const [encerrarError, setEncerrarError] = useState(null)

  async function handleEdit(data) {
    await api.put(`/temporadas/${id}`, data)
    await qc.invalidateQueries({ queryKey: ['temporada', id] })
  }

  async function handleEncerrar() {
    setEncerrarError(null)
    try {
      await api.put(`/temporadas/${id}/encerrar`)
      await qc.invalidateQueries({ queryKey: ['temporada', id] })
      setShowEncerrar(false)
    } catch (err) {
      setEncerrarError(err.response?.data?.message || err.message)
    }
  }

  async function handleDelete() {
    await api.delete(`/temporadas/${id}`)
    window.location.href = '/campeonatos'
  }

  return (
    <div>
      <Link to="/campeonatos" className="mb-4 inline-block text-sm text-red-700 hover:underline">← Voltar</Link>
      <Async query={query}>
        {(temp) => (
          <>
            <PageHeader
              title={temp.nome}
              subtitle={`Rodadas: ${temp.numRodadas || '—'}`}
              actions={[
                <Button key="edit" size="sm" onClick={() => setShowEdit(true)}>Editar</Button>,
                temp.status !== 'ENCERRADA' && <Button key="enc" size="sm" variant="secondary" onClick={() => setShowEncerrar(true)}>Encerrar</Button>,
                <Button key="del" size="sm" variant="danger" onClick={() => setShowDelete(true)}>Excluir</Button>,
              ]}
            />
            <div className="mb-6 flex flex-col gap-2 sm:flex-row sm:items-center sm:justify-between">
              <div className="flex gap-2">
                <Badge color={temp.status === 'ENCERRADA' ? 'slate' : temp.status === 'EM_ANDAMENTO' ? 'green' : 'amber'}>
                  {STATUS_TEMPORADA[temp.status]}
                </Badge>
              </div>
              <div className="text-sm text-slate-500">
                {temp.dataInicio} até {temp.dataFim}
              </div>
            </div>

            <Tabs
              tabs={[
                { label: 'Classificação', value: 'classificacao' },
                { label: 'Partidas', value: 'partidas' },
                { label: 'Artilharia', value: 'artilharia' },
                { label: 'Assistências', value: 'assistencias' },
                { label: 'Cartões', value: 'cartoes' },
                { label: 'Times Inscritos', value: 'times' },
              ]}
              value={tab}
              onChange={setTab}
            />

            {tab === 'classificacao' && <ClassificacaoPage temporadaId={id} />}
            {tab === 'partidas' && <PartidasPage temporadaId={id} />}
            {tab === 'artilharia' && <ArtilhariaPage temporadaId={id} />}
            {tab === 'assistencias' && <AssistenciasPage temporadaId={id} />}
            {tab === 'cartoes' && <CartõesPage temporadaId={id} />}
            {tab === 'times' && <ParticipacoesPage temporadaId={id} />}

            {showEdit && (
              <FormModal
                title="Editar Temporada"
                fields={[
                  { name: 'nome', label: 'Nome', required: true },
                  { name: 'dataInicio', label: 'Data Início', type: 'date' },
                  { name: 'dataFim', label: 'Data Fim', type: 'date' },
                  { name: 'numRodadas', label: 'Número de Rodadas', type: 'number', min: '1' },
                  { name: 'status', label: 'Status', type: 'select', options: statusTemporadaOptions, required: true },
                ]}
                initial={temp}
                onSubmit={handleEdit}
                onClose={() => setShowEdit(false)}
              />
            )}

            {showEncerrar && (
              <ConfirmDialog
                title="Encerrar Temporada"
                message="Tem certeza? A temporada não poderá ser reeditada após encerrada."
                confirmLabel="Encerrar"
                onConfirm={handleEncerrar}
                onClose={() => setShowEncerrar(false)}
              />
            )}

            {encerrarError && (
              <div className="mt-4">
                <ErrorBox message={encerrarError} />
              </div>
            )}

            {showDelete && (
              <ConfirmDialog
                title="Excluir Temporada"
                message={`Tem certeza que deseja excluir "${temp.nome}"?`}
                confirmLabel="Excluir"
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
