import { Link } from 'react-router-dom'
import { Async, Badge, Card } from '../components/ui'
import { useGet } from '../api/hooks'
import { useAuth } from '../AuthContext'
import { PalpiteResultadoItem } from '../components/PalpiteResultadoItem'

export function ResultadosPalpitesPage({ temporadaId }) {
  const { user } = useAuth()
  const query = useGet(
    ['palpites-resultados', temporadaId],
    `/temporadas/${temporadaId}/palpites/resultados`,
    { enabled: Boolean(user) },
  )

  if (!user) {
    return (
      <Card className="p-6 text-center">
        <p className="text-sm text-slate-600">Entre na sua conta para ver o histórico dos seus palpites.</p>
        <Link to="/login" className="mt-4 inline-block font-semibold text-primary-dark hover:underline">Ir para o login</Link>
      </Card>
    )
  }

  return (
    <Async query={query} empty="Nenhuma rodada simulada com palpites avaliados ainda.">
      {(rodadas) => (
        <div className="space-y-4">
          {rodadas.map((rodada) => {
            const acertos = rodada.jogos.filter((jogo) => jogo.acerto)
            const erros = rodada.jogos.filter((jogo) => !jogo.acerto)

            return (
              <Card key={rodada.rodada} className="p-4">
                <div className="flex flex-wrap items-center justify-between gap-2">
                  <h2 className="text-lg font-bold text-slate-900">Rodada {rodada.rodada}</h2>
                  <div className="flex flex-wrap items-center gap-2">
                    <Badge color="green">{acertos.length} acerto(s)</Badge>
                    <Badge color="slate">{erros.length} erro(s)</Badge>
                    <Badge color="blue">{rodada.pontosGanhos} pontos</Badge>
                  </div>
                </div>
                <div className="mt-4 grid gap-4 md:grid-cols-2">
                  <div>
                    <h3 className="mb-2 text-sm font-semibold text-emerald-800">Acertos</h3>
                    {acertos.length === 0 ? (
                      <p className="text-sm text-slate-600">Nenhum acerto nesta rodada.</p>
                    ) : (
                      <ul className="space-y-2">
                        {acertos.map((jogo) => <PalpiteResultadoItem key={jogo.partidaId} jogo={jogo} />)}
                      </ul>
                    )}
                  </div>
                  <div>
                    <h3 className="mb-2 text-sm font-semibold text-red-700">Erros</h3>
                    {erros.length === 0 ? (
                      <p className="text-sm text-slate-600">Nenhum erro nesta rodada.</p>
                    ) : (
                      <ul className="space-y-2">
                        {erros.map((jogo) => <PalpiteResultadoItem key={jogo.partidaId} jogo={jogo} />)}
                      </ul>
                    )}
                  </div>
                </div>
              </Card>
            )
          })}
        </div>
      )}
    </Async>
  )
}
