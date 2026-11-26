import { Async, Badge } from '../components/ui'
import { useGet } from '../api/hooks'

export function CartõesPage({ temporadaId, limit = 10 }) {
  const query = useGet(
    ['cartoes', temporadaId, limit],
    `/estatisticas/temporadas/${temporadaId}/cartoes?limit=${limit}`,
    { staleTime: 5 * 60 * 1000, refetchOnWindowFocus: false },
  )

  return (
    <div className="space-y-4">
      <div className="mb-4 text-sm text-slate-600">
        Top {limit} jogadores com cartões na temporada
      </div>
      <Async query={query} empty="Nenhum jogador com cartões registrados.">
        {(data) => (
            <div className="overflow-x-auto">
              <table className="w-full border-collapse text-sm">
                <thead>
                  <tr className="border-b-2 border-red-700 bg-red-50">
                    <th className="px-3 py-2 text-left font-semibold text-slate-900">Jogador</th>
                    <th className="px-3 py-2 text-center font-semibold text-slate-900">Cartões Amarelos</th>
                    <th className="px-3 py-2 text-center font-semibold text-slate-900">Cartões Vermelhos</th>
                    <th className="px-3 py-2 text-center font-semibold text-slate-900">Total</th>
                  </tr>
                </thead>
                <tbody>
                  {data.map((row, i) => (
                    <tr key={row.id} className={i % 2 === 0 ? 'bg-white' : 'bg-slate-50'}>
                      <td className="px-3 py-2">
                        <span className="font-medium text-slate-900">{row.jogadorNome}</span>
                      </td>
                      <td className="px-3 py-2 text-center">
                        {row.cartoesAmarelos > 0 ? (
                          <Badge color="yellow">{row.cartoesAmarelos}</Badge>
                        ) : (
                          <span className="text-slate-400">—</span>
                        )}
                      </td>
                      <td className="px-3 py-2 text-center">
                        {row.cartoesVermelhos > 0 ? (
                          <Badge color="red">{row.cartoesVermelhos}</Badge>
                        ) : (
                          <span className="text-slate-400">—</span>
                        )}
                      </td>
                      <td className="px-3 py-2 text-center text-slate-600 font-medium">
                        {row.cartoesAmarelos + row.cartoesVermelhos}
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
        )}
      </Async>
    </div>
  )
}
