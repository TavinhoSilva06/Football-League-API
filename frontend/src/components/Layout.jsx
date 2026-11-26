import { useState } from 'react'
import { Link, useLocation, useNavigate } from 'react-router-dom'
import { useAuth } from '../AuthContext'
import { Badge, Button } from './ui'
import { IconBall, IconBolt, IconClose, IconGrid, IconLogout, IconMenu, IconTarget, IconUsers } from './icons'
import { BRAND } from '../lib/brand'
import { PATENTE, PATENTE_COR } from '../lib/patentes'

function BrandMark() {
  return (
    <div className="brand-gradient animate-gradient flex h-11 w-11 items-center justify-center rounded-2xl shadow-lg shadow-primary/25">
      <IconBall className="h-7 w-7 text-white" />
    </div>
  )
}

function SidebarItem({ to, label, hint, icon: Icon, active, onClick }) {
  return (
    <Link
      to={to}
      onClick={onClick}
      className={`group relative flex items-center gap-3 rounded-2xl px-3 py-3 transition-all duration-300 ${
        active ? 'nav-active font-bold' : 'font-semibold text-slate-500 hover:bg-slate-50 hover:text-primary-dark'
      }`}
    >
      <Icon className={`h-5 w-5 transition-transform duration-300 group-hover:scale-110 ${active ? 'text-primary' : ''}`} />
      <span className="flex-1">
        <span className="block text-sm">{label}</span>
        {hint && <span className="block text-[10px] font-medium text-slate-400">{hint}</span>}
      </span>
      {active && <span className="h-2 w-2 animate-pulse-slow rounded-full bg-primary" />}
    </Link>
  )
}

function rotaTitulo(path) {
  if (path === '/palpites') return 'Seus Palpites'
  if (path.startsWith('/temporadas')) return 'Central da Temporada'
  if (path.startsWith('/campeonatos/')) return 'Campeonato'
  if (path.startsWith('/campeonatos')) return 'Campeonatos'
  if (path.startsWith('/times/')) return 'Detalhe do Time'
  if (path.startsWith('/times')) return 'Times'
  if (path === '/register') return 'Criar conta'
  if (path === '/login') return 'Entrar'
  return BRAND.name
}

export function Layout({ children }) {
  const { user, sair } = useAuth()
  const navigate = useNavigate()
  const location = useLocation()
  const [menuAberto, setMenuAberto] = useState(false)

  function handleSair() {
    sair()
    setMenuAberto(false)
    navigate('/login')
  }

  const visaoAtiva = location.pathname === '/' || location.pathname === '/campeonatos'
  const palpitesAtivos = location.pathname === '/palpites' || location.pathname.startsWith('/temporadas')
  const timesAtivos = location.pathname.startsWith('/times')
  const pontos = user?.pontos ?? 0
  const progresso = user?.proximaPatente ? ((pontos % 1000) / 1000) * 100 : 100

  return (
    <div className="app-glow relative min-h-screen text-slate-900">
      <div className="pointer-events-none fixed inset-0 -z-10 overflow-hidden">
        <div className="animate-float-slow absolute -left-32 top-10 h-96 w-96 rounded-full bg-primary/10 blur-3xl" />
        <div className="animate-float-slow delay-2 absolute -right-24 top-1/2 h-[28rem] w-[28rem] rounded-full bg-secondary/10 blur-3xl" />
      </div>

      {menuAberto && (
        <button
          type="button"
          aria-label="Fechar menu"
          className="animate-fade-in fixed inset-0 z-40 bg-slate-950/40 backdrop-blur-sm lg:hidden"
          onClick={() => setMenuAberto(false)}
        />
      )}

      <aside className={`fixed inset-y-0 left-0 z-50 w-72 transform border-r border-primary/10 bg-white/95 shadow-2xl shadow-slate-900/5 backdrop-blur-xl transition-transform duration-300 ease-out lg:translate-x-0 ${menuAberto ? 'translate-x-0' : '-translate-x-full'}`}>
        <div className="flex h-full flex-col">
          <div className="flex items-center gap-3 border-b border-slate-100 px-6 py-6">
            <BrandMark />
            <div className="min-w-0 flex-1">
              <span className="brand-text block truncate text-lg font-extrabold tracking-tight">{BRAND.name}</span>
              <span className="block text-[10px] font-bold uppercase tracking-[0.2em] text-slate-400">palpite manager</span>
            </div>
            <button type="button" aria-label="Fechar menu" className="rounded-lg p-1 text-slate-400 hover:bg-slate-100 lg:hidden" onClick={() => setMenuAberto(false)}>
              <IconClose className="h-5 w-5" />
            </button>
          </div>

          <nav className="flex-1 space-y-1 overflow-y-auto px-3 py-6">
            <p className="px-3 pb-2 text-[10px] font-bold uppercase tracking-[0.2em] text-slate-400">Navegação</p>
            <SidebarItem to="/campeonatos" label="Visão geral" hint="campeonatos e temporadas" icon={IconGrid} active={visaoAtiva} onClick={() => setMenuAberto(false)} />
            <SidebarItem to="/palpites" label="Palpites" hint="acertos e erros" icon={IconTarget} active={palpitesAtivos} onClick={() => setMenuAberto(false)} />
            <SidebarItem to="/times" label="Times" hint="elencos e escudos" icon={IconUsers} active={timesAtivos} onClick={() => setMenuAberto(false)} />

            <p className="px-3 pb-2 pt-6 text-[10px] font-bold uppercase tracking-[0.2em] text-slate-400">Sua jornada</p>
            <div className="mx-1 rounded-2xl border border-primary/10 bg-primary-light/60 p-4">
              {user ? (
                <>
                  <div className="flex items-center justify-between gap-2">
                    <Badge color={PATENTE_COR[user.patente]}>{PATENTE[user.patente] || user.patente}</Badge>
                    <span className="text-xs font-bold text-primary-dark">{pontos} pts</span>
                  </div>
                  <div className="mt-3 h-2 overflow-hidden rounded-full bg-white/80">
                    <div className="brand-gradient animate-gradient h-full rounded-full transition-all duration-700" style={{ width: `${progresso}%` }} />
                  </div>
                  <p className="mt-2 text-[11px] text-slate-500">
                    {user.proximaPatente ? `Faltam ${user.pontosParaProxima} pts para a próxima patente` : 'Patente máxima alcançada'}
                  </p>
                  <Button size="sm" variant="ghost" className="mt-3 w-full justify-start" onClick={handleSair}>
                    <IconLogout className="h-4 w-4" />
                    Sair da conta
                  </Button>
                </>
              ) : (
                <>
                  <div className="flex items-center gap-2 text-sm font-bold text-slate-900">
                    <IconBolt className="h-4 w-4 text-primary" />
                    Entre na sua jornada
                  </div>
                  <p className="mt-1 text-xs text-slate-500">Palpite, pontue e suba de patente.</p>
                  <Link to="/login" onClick={() => setMenuAberto(false)} className="brand-gradient mt-3 block rounded-xl px-3 py-2 text-center text-xs font-bold text-white shadow-md shadow-primary/20">
                    Entrar
                  </Link>
                </>
              )}
            </div>
          </nav>

          <div className="border-t border-slate-100 px-6 py-4">
            <p className="text-[10px] font-medium text-slate-400">{BRAND.tagline}</p>
          </div>
        </div>
      </aside>

      <div className="lg:ml-72">
        <header className="glass-panel sticky top-0 z-30 flex h-16 items-center justify-between gap-3 border-b border-slate-200/80 px-4 sm:px-6">
          <div className="flex items-center gap-3">
            <button type="button" aria-label="Abrir menu" className="rounded-xl border border-slate-200 bg-white p-2 text-slate-600 shadow-sm hover:border-primary/30 hover:text-primary lg:hidden" onClick={() => setMenuAberto(true)}>
              <IconMenu className="h-5 w-5" />
            </button>
            <div>
              <p className="text-[10px] font-bold uppercase tracking-[0.18em] text-slate-400">{BRAND.name}</p>
              <h1 className="text-sm font-bold text-slate-900">{rotaTitulo(location.pathname)}</h1>
            </div>
          </div>

          <div className="flex items-center gap-3">
            {user ? (
              <div className="flex items-center gap-3 rounded-2xl border border-primary/10 bg-white/80 px-3 py-1.5 shadow-sm">
                <div className="brand-gradient flex h-8 w-8 items-center justify-center rounded-full text-xs font-bold text-white">
                  {(user.nome || 'U').charAt(0).toUpperCase()}
                </div>
                <div className="hidden text-right text-xs leading-tight sm:block">
                  <div className="font-bold text-slate-900">{user.nome}</div>
                  <div className="text-slate-500">{user.pontos} pts</div>
                </div>
                <Badge color={PATENTE_COR[user.patente]}>{PATENTE[user.patente] || user.patente}</Badge>
                {user.role === 'ADMIN' && <Badge color="primary">Admin</Badge>}
              </div>
            ) : (
              <Link to="/login" className="brand-gradient animate-gradient rounded-xl px-4 py-2 text-sm font-bold text-white shadow-lg shadow-primary/20 transition-all duration-300 hover:-translate-y-0.5">
                Entrar
              </Link>
            )}
          </div>
        </header>

        <main key={location.pathname} className="relative z-10 mx-auto max-w-7xl px-4 py-8 animate-fade-in sm:px-6 lg:py-10">
          <div className="app-grid pointer-events-none fixed inset-0 -z-10 opacity-60" />
          {children}
        </main>

        <footer className="relative z-10 mt-16 border-t border-primary/10 bg-white/70 py-6 text-center text-xs font-medium text-slate-500 backdrop-blur">
          <p>{BRAND.name} © 2026 · {BRAND.tagline}</p>
        </footer>
      </div>
    </div>
  )
}
