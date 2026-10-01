import { useState } from 'react'
import { useParams, Link } from 'react-router-dom'
import { useQueryClient } from '@tanstack/react-query'
import { PageHeader, Async, Button, Tabs, Card, FormModal, ConfirmDialog, Escudo } from '../components/ui'
import { useGet } from '../api/hooks'
import { api } from '../api/client'
import { POSICAO_JOGADOR, posicaoOptions } from '../lib/enums'
import { calcAge } from '../lib/format'
import { PartidaItem } from '../components/PartidaItem'

export function TimeDetalhe() {
  const { id } = useParams()
  const qc = useQueryClient()
  const query = useGet(['time', id], `/times/${id}`)
  const jogadoresQuery = useGet(['jogadores-time', id], `/jogadores/time/${id}`)
  const partidasQuery = useGet(['partidas-time', id], `/times/${id}/partidas`)

  const [tab, setTab] = useState('elenco')
  const [showEditTime, setShowEditTime] = useState(false)
  const [showAddJogador, setShowAddJogador] = useState(false)
  const [editingJogador, setEditingJogador] = useState(null)
  const [showDeleteJogador, setShowDeleteJogador] = useState(null)
  const [showDeleteTime, setShowDeleteTime] = useState(false)
  const [temporadaFilter, setTemporadaFilter] = useState('')

  async function handleEditTime(data) {
    await api.put(`/times/${id}`, data)
    await qc.invalidateQueries({ queryKey: ['time', id] })
  }

  async function handleAddJogador(data) {
    await api.post('/jogadores', data)
    await qc.invalidateQueries({ queryKey: ['jogadores-time', id] })
  }

  async function handleEditJogador(data) {
    await api.put(`/jogadores/${editingJogador.id}`, data)
    await qc.invalidateQueries({ queryKey: ['jogadores-time', id] })
    setEditingJogador(null)
  }

  async function handleDeleteJogador() {
    if (showDeleteJogador) {
      await api.delete(`/jogadores/${showDeleteJogador.id}`)
      await qc.invalidateQueries({ queryKey: ['jogadores-time', id] })
      setShowDeleteJogador(null)
    }
  }

  async function handleDeleteTime() {
    await api.delete(`/times/${id}`)
    window.location.href = '/times'
  }

  return (
    <div>
      <Link to="/times" className="mb-4 inline-block text-sm text-red-700 hover:underline">← Voltar</Link>
      <Async query={query}>
        {(time) => (
          <>
            <PageHeader
              title={time.nome}
              subtitle={time.cidade || time.pais}
              actions={[
                <Button key="edit" size="sm" onClick={() => setShowEditTime(true)}>Editar</Button>,
                <Button key="del" size="sm" variant="danger" onClick={() => setShowDeleteTime(true)}>Excluir</Button>,
              ]}
            />

            <div className="mb-6 flex flex-col items-start gap-4 sm:flex-row sm:items-center">
              <Escudo time={time} size="lg" />
              <div>
                {time.sigla && <p className="text-sm font-medium text-slate-700">Sigla: {time.sigla}</p>}
                {time.pais && <p className="text-sm text-slate-600">País: {time.pais}</p>}
                {time.estadio && <p className="text-sm text-slate-600">Estádio: {time.estadio}</p>}
              </div>
            </div>

            <Tabs
              tabs={[
                { label: 'Elenco', value: 'elenco' },
                { label: 'Jogos', value: 'jogos' },
              ]}
              value={tab}
              onChange={setTab}
            />

            {tab === 'elenco' && (
              <div className="space-y-4">
                <div className="flex justify-end">
                  <Button size="sm" onClick={() => setShowAddJogador(true)}>Adicionar Jogador</Button>
                </div>
                <Async query={jogadoresQuery} empty="Nenhum jogador neste time.">
                  {(jogadores) => {
                    const byPosicao = {}
                    jogadores.forEach((j) => {
                      if (!byPosicao[j.posicao]) byPosicao[j.posicao] = []
                      byPosicao[j.posicao].push(j)
                    })

                    const posicoes = ['GOLEIRO', 'DEFESA', 'MEIO_CAMPO', 'ATACANTE']
                    return (
                      <div className="space-y-6">
                        {posicoes.map((pos) => {
                          const jogadores = byPosicao[pos] || []
                          if (jogadores.length === 0) return null
                          return (
                            <div key={pos}>
                              <h3 className="mb-3 border-b-2 border-red-700 pb-2 font-semibold text-slate-900">{POSICAO_JOGADOR[pos]}</h3>
                              <div className="space-y-2">
                                {jogadores.map((j) => (
                                  <Card key={j.id} className="flex items-center justify-between p-4">
                                    <div className="flex-1">
                                      <p className="font-medium text-slate-900">
                                        {j.numeroCamisa && <span className="mr-2 inline-block h-6 w-6 rounded-full bg-red-700 text-center text-xs font-bold text-white">{j.numeroCamisa}</span>}
                                        {j.nome}
                                      </p>
                                      <div className="flex gap-3 text-xs text-slate-500">
                                        {j.nacionalidade && <span>{j.nacionalidade}</span>}
                                        {j.dataNascimento && <span>{calcAge(j.dataNascimento)} anos</span>}
                                      </div>
                                    </div>
                                    <div className="flex gap-2">
                                      <Button size="sm" variant="secondary" onClick={() => setEditingJogador(j)}>Editar</Button>
                                      <Button size="sm" variant="danger" onClick={() => setShowDeleteJogador(j)}>Excluir</Button>
                                    </div>
                                  </Card>
                                ))}
                              </div>
                            </div>
                          )
                        })}
                      </div>
                    )
                  }}
                </Async>
              </div>
            )}

            {tab === 'jogos' && (
              <div className="space-y-4">
                <div className="flex justify-end">
                  <input
                    type="number"
                    placeholder="Filtrar por temporada"
                    value={temporadaFilter}
                    onChange={(e) => setTemporadaFilter(e.target.value)}
                    className="rounded-md border border-slate-300 px-3 py-1.5 text-sm"
                  />
                </div>
                <Async query={partidasQuery} empty="Nenhum jogo.">
                  {(partidas) => {
                    const filtered = temporadaFilter ? partidas.filter((p) => p.temporadaId === Number(temporadaFilter)) : partidas
                    return (
                      <div className="space-y-2">
                        {filtered.map((p) => (
                          <PartidaItem key={p.id} partida={p} compact />
                        ))}
                      </div>
                    )
                  }}
                </Async>
              </div>
            )}

            {showEditTime && (
              <FormModal
                title="Editar Time"
                fields={[
                  { name: 'nome', label: 'Nome', required: true },
                  { name: 'sigla', label: 'Sigla' },
                  { name: 'pais', label: 'País' },
                  { name: 'cidade', label: 'Cidade' },
                  { name: 'estadio', label: 'Estádio' },
                  { name: 'escudoUrl', label: 'URL do Escudo' },
                ]}
                initial={time}
                onSubmit={handleEditTime}
                onClose={() => setShowEditTime(false)}
              />
            )}

            {showAddJogador && (
              <FormModal
                title="Adicionar Jogador"
                fields={[
                  { name: 'nome', label: 'Nome', required: true },
                  { name: 'numeroCamisa', label: 'Número da Camisa', type: 'number' },
                  { name: 'posicao', label: 'Posição', type: 'select', options: posicaoOptions },
                  { name: 'nacionalidade', label: 'Nacionalidade' },
                  { name: 'dataNascimento', label: 'Data de Nascimento', type: 'date' },
                  { name: 'timeId', label: 'Time', type: 'hidden', defaultValue: id },
                ]}
                initial={{ timeId: id }}
                onSubmit={handleAddJogador}
                onClose={() => setShowAddJogador(false)}
                submitLabel="Adicionar"
              />
            )}

            {editingJogador && (
              <FormModal
                title="Editar Jogador"
                fields={[
                  { name: 'nome', label: 'Nome', required: true },
                  { name: 'numeroCamisa', label: 'Número da Camisa', type: 'number' },
                  { name: 'posicao', label: 'Posição', type: 'select', options: posicaoOptions },
                  { name: 'nacionalidade', label: 'Nacionalidade' },
                  { name: 'dataNascimento', label: 'Data de Nascimento', type: 'date' },
                  { name: 'timeId', label: 'Time', type: 'hidden', defaultValue: id },
                ]}
                initial={editingJogador}
                onSubmit={handleEditJogador}
                onClose={() => setEditingJogador(null)}
              />
            )}

            {showDeleteJogador && (
              <ConfirmDialog
                title="Excluir Jogador"
                message={`Tem certeza que deseja excluir "${showDeleteJogador.nome}"?`}
                confirmLabel="Excluir"
                onConfirm={handleDeleteJogador}
                onClose={() => setShowDeleteJogador(null)}
              />
            )}

            {showDeleteTime && (
              <ConfirmDialog
                title="Excluir Time"
                message={`Tem certeza que deseja excluir "${time.nome}"?`}
                confirmLabel="Excluir"
                onConfirm={handleDeleteTime}
                onClose={() => setShowDeleteTime(false)}
              />
            )}
          </>
        )}
      </Async>
    </div>
  )
}
