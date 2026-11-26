import { Link } from 'react-router-dom'
import { Async, EmptyState, Escudo } from '../components/ui'
import { useGet } from '../api/hooks'

export function ClassificacaoPage({ temporadaId }) {
  const query = useGet(['classificacao', temporadaId], `/temporadas/${temporadaId}/classificacao`)

  return (
    <div className="animate-slide-in space-y-4 overflow-x-auto">
      <Async query={query} empty="Nenhum time inscrito.">
        {(data) => {
          if (!data || data.length === 0) return <EmptyState>Nenhuma classificação disponível.</EmptyState>
          return (
            <table className="w-full border-collapse text-sm">
              <thead>
                <tr className="border-b-2 border-primary bg-primary-light">
                  <th className="px-3 py-2 text-left font-semibold text-slate-900">#</th>
                  <th className="px-3 py-2 text-left font-semibold text-slate-900">Time</th>
                  <th className="px-3 py-2 text-center font-semibold text-slate-900">Pts</th>
                  <th className="px-3 py-2 text-center font-semibold text-slate-900">J</th>
                  <th className="px-3 py-2 text-center font-semibold text-slate-900">V</th>
                  <th className="px-3 py-2 text-center font-semibold text-slate-900">E</th>
                  <th className="px-3 py-2 text-center font-semibold text-slate-900">D</th>
                  <th className="px-3 py-2 text-center font-semibold text-slate-900">GP</th>
                  <th className="px-3 py-2 text-center font-semibold text-slate-900">GC</th>
                  <th className="px-3 py-2 text-center font-semibold text-slate-900">SG</th>
                </tr>
              </thead>
              <tbody>
                {data.map((row, i) => (
                  <tr key={row.timeId} className={`transition-colors duration-200 hover:bg-primary-light/60 ${i % 2 === 0 ? 'bg-white' : 'bg-slate-50'}`}>
                    <td className="px-3 py-2 font-bold text-slate-900">{row.posicao}</td>
                    <td className="px-3 py-2">
                      <Link to={`/times/${row.timeId}`} className="flex items-center gap-2 text-primary-dark hover:underline">
                        <Escudo time={{ nome: row.timeNome, escudoUrl: row.timeEscudoUrl }} size="sm" />
                        {row.timeNome}
                      </Link>
                    </td>
                    <td className="px-3 py-2 text-center font-semibold text-primary-dark">{row.pontos}</td>
                    <td className="px-3 py-2 text-center">{row.jogos}</td>
                    <td className="px-3 py-2 text-center">{row.vitorias}</td>
                    <td className="px-3 py-2 text-center">{row.empates}</td>
                    <td className="px-3 py-2 text-center">{row.derrotas}</td>
                    <td className="px-3 py-2 text-center">{row.golsPro}</td>
                    <td className="px-3 py-2 text-center">{row.golsContra}</td>
                    <td className="px-3 py-2 text-center font-medium">{row.saldoGols}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          )
        }}
      </Async>
    </div>
  )
}
