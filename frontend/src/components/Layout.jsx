import { Link, useNavigate } from 'react-router-dom'
import { useAuth } from '../AuthContext'
import { Badge, Button } from './ui'
import { PATENTE, PATENTE_COR } from '../lib/patentes'

export function Layout({ children }) {
  const { user, sair } = useAuth()
  const navigate = useNavigate()

  function handleSair() {
    sair()
    navigate('/login')
  }

  return (
    <div className="min-h-screen bg-gradient-to-br from-slate-50 via-slate-50 to-red-50">
      <nav className="border-b-4 border-red-700 bg-gradient-to-r from-red-700 to-red-800 shadow-2xl">
        <div className="mx-auto flex flex-wrap items-center justify-between gap-3 px-6 py-4">
          <Link to="/" className="flex items-center gap-3 transition-transform hover:scale-105">
            <div className="flex h-10 w-10 items-center justify-center rounded-lg bg-white/20 backdrop-blur-sm">
              <span className="text-xl font-bold text-white">⚽</span>
            </div>
            <span className="text-2xl font-bold text-white drop-shadow-lg">Football League</span>
          </Link>
          <div className="flex flex-wrap items-center gap-4 sm:gap-8">
            <Link
              to="/campeonatos"
              className="relative text-white font-medium transition-all duration-300 hover:text-red-100 after:absolute after:bottom-0 after:left-0 after:h-0.5 after:w-0 after:bg-red-100 after:transition-all after:duration-300 hover:after:w-full"
            >
              Campeonatos
            </Link>
            <Link
              to="/times"
              className="relative text-white font-medium transition-all duration-300 hover:text-red-100 after:absolute after:bottom-0 after:left-0 after:h-0.5 after:w-0 after:bg-red-100 after:transition-all after:duration-300 hover:after:w-full"
            >
              Times
            </Link>
            {user ? (
              <div className="flex items-center gap-2 rounded-lg bg-white/10 px-3 py-1.5">
                <div className="text-right text-xs text-white">
                  <div className="font-semibold">{user.nome}</div>
                  <div>{user.pontos} pts</div>
                </div>
                <Badge color={PATENTE_COR[user.patente]}>{PATENTE[user.patente] || user.patente}</Badge>
                {user.role === 'ADMIN' && <Badge color="red">Admin</Badge>}
                <Button size="sm" variant="ghost" className="text-white hover:bg-white/20 hover:text-white" onClick={handleSair}>
                  Sair
                </Button>
              </div>
            ) : (
              <Link to="/login" className="rounded-lg bg-white px-4 py-2 text-sm font-semibold text-red-700 hover:bg-red-50">
                Entrar
              </Link>
            )}
          </div>
        </div>
      </nav>
      <main className="mx-auto max-w-6xl px-6 py-8 animate-fade-in">{children}</main>
      <footer className="border-t border-slate-200 bg-white/80 backdrop-blur py-6 text-center text-xs text-slate-500 mt-16">
        <p>⚽ Football League API © 2026</p>
      </footer>
    </div>
  )
}
