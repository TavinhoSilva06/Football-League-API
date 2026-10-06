import { Async, EmptyState, Badge } from '../components/ui'
import { useGet } from '../api/hooks'

export function CartõesPage({ temporadaId }) {
  const query = useGet(['estatisticas', temporadaId], `/estatisticas/temporadas/${temporadaId}`)

  return (
    <div className="space-y-4">
      <div className="mb-4 text-sm text-slate-600">
        Jogadores com cartões na temporada
      </div>
      <Async query={query} empty={`Nenhuma estatística disponível.`}>
        {(data) => {
          if (!data || data.length === 0) return <EmptyState>Nenhuma estatística de cartões registrada.</EmptyState>

          // Filtrar apenas jogadores com cartões
          const comCartoes = data.filter(row => row.cartoesAmarelos > 0 || row.cartoesVermelhos > 0)

          if (comCartoes.length === 0) return <EmptyState>Nenhum jogador com cartões registrados.</EmptyState>

          return (
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
                  {comCartoes.map((row, i) => (
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
          )
        }}
      </Async>
    </div>
  )
}
