import { Escudo, Badge } from './ui'
import { STATUS_PARTIDA } from '../lib/enums'
import { formatDateTime } from '../lib/format'

const statusColors = {
  AGENDADA: 'slate',
  EM_ANDAMENTO: 'orange',
  FINALIZADA: 'green',
  ADIADA: 'amber',
  CANCELADA: 'red',
}

export function PartidaItem({ partida, onResultado, onEdit, onDelete }) {
  return (
    <div className="group flex flex-col gap-3 rounded-xl border border-slate-200 bg-gradient-to-br from-white to-slate-50/50 p-4 shadow-sm hover:shadow-lg hover:border-red-300 transition-all duration-300 sm:flex-row sm:items-center sm:justify-between">
      <div className="flex flex-1 items-center justify-between gap-2 sm:gap-4">
        <div className="flex min-w-0 flex-1 items-center gap-3">
          <div className="transform transition-transform duration-300 group-hover:scale-110">
            <Escudo time={{ nome: partida.timeMandanteNome, sigla: partida.timeMandanteNome.slice(0, 3) }} size="sm" />
          </div>
          <div className="min-w-0 flex-1 truncate text-sm font-semibold text-slate-900">{partida.timeMandanteNome}</div>
        </div>
        <div className="shrink-0 text-center">
          {partida.status === 'FINALIZADA' ? (
            <div className="text-xl font-bold text-red-700 drop-shadow-sm">
              {partida.golsMandante} <span className="text-slate-400">×</span> {partida.golsVisitante}
            </div>
          ) : (
            <div className="whitespace-nowrap text-xs font-medium text-slate-600 bg-slate-100/50 px-3 py-1 rounded-lg">{formatDateTime(partida.dataHora)}</div>
          )}
        </div>
        <div className="flex min-w-0 flex-1 items-center justify-end gap-3">
          <div className="min-w-0 flex-1 truncate text-right text-sm font-semibold text-slate-900">{partida.timeVisitanteNome}</div>
          <div className="transform transition-transform duration-300 group-hover:scale-110">
            <Escudo time={{ nome: partida.timeVisitanteNome, sigla: partida.timeVisitanteNome.slice(0, 3) }} size="sm" />
          </div>
        </div>
      </div>
      <div className="flex flex-wrap items-center justify-between gap-2">
        <Badge color={statusColors[partida.status]}>{STATUS_PARTIDA[partida.status]}</Badge>
        {onResultado && (
          <button
            type="button"
            onClick={() => onResultado(partida)}
            className="text-xs font-semibold text-red-700 hover:text-white hover:bg-red-700 px-3 py-1.5 rounded-lg transition-all duration-200"
          >
            {partida.status === 'FINALIZADA' ? '✏️ Corrigir' : '⚡ Resultado'}
          </button>
        )}
        {onEdit && (
          <button
            type="button"
            onClick={() => onEdit(partida)}
            className="text-xs font-semibold text-blue-700 hover:text-white hover:bg-blue-700 px-3 py-1.5 rounded-lg transition-all duration-200"
          >
            📝 Editar
          </button>
        )}
        {onDelete && (
          <button
            type="button"
            onClick={() => onDelete(partida)}
            className="text-xs font-semibold text-slate-500 hover:text-white hover:bg-red-600 px-3 py-1.5 rounded-lg transition-all duration-200"
          >
            🗑️ Excluir
          </button>
        )}
      </div>
    </div>
  )
}
