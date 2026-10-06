import { Async, EmptyState, Badge } from '../components/ui'
import { useGet } from '../api/hooks'

export function ArtilhariaPage({ temporadaId, limit = 10 }) {
  const query = useGet(['artilharia', temporadaId, limit], `/estatisticas/temporadas/${temporadaId}/artilharia?limit=${limit}`)

  return (
    <div className="space-y-4">
      <div className="mb-4 text-sm text-slate-600">
        Top {limit} goleadores da temporada
      </div>
      <Async query={query} empty={`Nenhuma estatística disponível.`}>
        {(data) => {
          if (!data || data.length === 0) return <EmptyState>Nenhuma estatística de gols registrada.</EmptyState>
          return (
            <div className="overflow-x-auto">
              <table className="w-full border-collapse text-sm">
                <thead>
                  <tr className="border-b-2 border-red-700 bg-red-50">
                    <th className="px-3 py-2 text-left font-semibold text-slate-900">#</th>
                    <th className="px-3 py-2 text-left font-semibold text-slate-900">Jogador</th>
                    <th className="px-3 py-2 text-center font-semibold text-slate-900">Gols</th>
                    <th className="px-3 py-2 text-center font-semibold text-slate-900">Jogos</th>
                    <th className="px-3 py-2 text-center font-semibold text-slate-900">Média Gols/Jogo</th>
                  </tr>
                </thead>
                <tbody>
                  {data.map((row, i) => (
                    <tr key={row.id} className={i % 2 === 0 ? 'bg-white' : 'bg-slate-50'}>
                      <td className="px-3 py-2 font-bold text-red-700">{i + 1}</td>
                      <td className="px-3 py-2">
                        <span className="font-medium text-slate-900">{row.jogadorNome}</span>
                      </td>
                      <td className="px-3 py-2 text-center">
                        <Badge color="red">{row.gols}</Badge>
                      </td>
                      <td className="px-3 py-2 text-center text-slate-600">{row.jogos}</td>
                      <td className="px-3 py-2 text-center text-slate-600 font-medium">
                        {(row.gols / (row.jogos || 1)).toFixed(2)}
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
