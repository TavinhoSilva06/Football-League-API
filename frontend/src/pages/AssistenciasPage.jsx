import { Async, EmptyState, Badge } from '../components/ui'
import { useGet } from '../api/hooks'

export function AssistenciasPage({ temporadaId, limit = 10 }) {
  const query = useGet(
    ['assistencias', temporadaId, limit],
    `/estatisticas/temporadas/${temporadaId}/assistencias?limit=${limit}`,
    { staleTime: 5 * 60 * 1000, refetchOnWindowFocus: false },
  )

  return (
    <div className="space-y-4">
      <div className="mb-4 text-sm text-slate-600">
        Top {limit} assistentes da temporada
      </div>
      <Async query={query} empty={`Nenhuma estatística disponível.`}>
        {(data) => {
          if (!data || data.length === 0) return <EmptyState>Nenhuma estatística de assistências registrada.</EmptyState>

          // Filtrar apenas jogadores com assistências
          const comAssistencias = data.filter(row => row.assistencias > 0)

          if (comAssistencias.length === 0) return <EmptyState>Nenhum jogador com assistências registradas.</EmptyState>

          return (
            <div className="animate-slide-in delay-1 overflow-x-auto">
              <table className="w-full border-collapse text-sm">
                <thead>
                  <tr className="border-b-2 border-primary bg-primary-light">
                    <th className="px-3 py-2 text-left font-semibold text-slate-900">#</th>
                    <th className="px-3 py-2 text-left font-semibold text-slate-900">Jogador</th>
                    <th className="px-3 py-2 text-center font-semibold text-slate-900">Assistências</th>
                    <th className="px-3 py-2 text-center font-semibold text-slate-900">Jogos</th>
                    <th className="px-3 py-2 text-center font-semibold text-slate-900">Média Asst/Jogo</th>
                  </tr>
                </thead>
                <tbody>
                  {comAssistencias.map((row, i) => (
                    <tr key={row.id} className={`transition-colors duration-200 hover:bg-primary-light/60 ${i % 2 === 0 ? 'bg-white' : 'bg-slate-50'}`}>
                      <td className="px-3 py-2 font-bold text-primary">{i + 1}</td>
                      <td className="px-3 py-2">
                        <span className="font-medium text-slate-900">{row.jogadorNome}</span>
                      </td>
                      <td className="px-3 py-2 text-center">
                        <Badge color="blue">{row.assistencias}</Badge>
                      </td>
                      <td className="px-3 py-2 text-center text-slate-600">{row.jogos}</td>
                      <td className="px-3 py-2 text-center text-slate-600 font-medium">
                        {(row.assistencias / (row.jogos || 1)).toFixed(2)}
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )
        }}
      </Async>
    </div>
  )
}
