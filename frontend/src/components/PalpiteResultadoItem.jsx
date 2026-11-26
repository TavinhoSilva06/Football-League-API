import { Escudo } from './ui'

export function PalpiteResultadoItem({ jogo }) {
  const acertou = Boolean(jogo.acerto)

  return (
    <li className={`rounded-lg border px-3 py-2 text-sm ${acertou ? 'border-emerald-200 bg-emerald-50' : 'border-slate-200 bg-white'}`}>
      <div className="flex flex-wrap items-center gap-2 font-medium text-slate-900">
        <Escudo time={{ nome: jogo.mandanteNome, escudoUrl: jogo.mandanteEscudoUrl }} size="sm" />
        <span>{jogo.mandanteNome} x {jogo.visitanteNome}</span>
        <Escudo time={{ nome: jogo.visitanteNome, escudoUrl: jogo.visitanteEscudoUrl }} size="sm" />
      </div>
      <div className="mt-1 text-xs text-slate-600">
        Seu palpite: <strong>{jogo.palpiteMandante ?? '-'} x {jogo.palpiteVisitante ?? '-'}</strong>
        {' · '}Resultado: <strong>{jogo.golsMandante ?? '-'} x {jogo.golsVisitante ?? '-'}</strong>
        {acertou && <span className="ml-1 font-semibold text-emerald-700">+{jogo.pontosGanhos ?? 0} pts</span>}
      </div>
    </li>
  )
}
