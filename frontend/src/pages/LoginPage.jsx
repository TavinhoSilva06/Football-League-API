import { useState } from 'react'
import { Navigate, useLocation, useNavigate } from 'react-router-dom'
import { Card, Button, ErrorBox, Badge } from '../components/ui'
import { useAuth } from '../AuthContext'
import { parseApiError } from '../api/client'
import { BRAND } from '../lib/brand'

const inputClass =
  'w-full rounded-xl border border-slate-300 bg-white px-4 py-3 text-sm transition-all duration-200 hover:border-primary/40 focus:border-primary focus:outline-none focus:ring-4 focus:ring-primary/10'

export function LoginPage() {
  const { user, entrar, cadastrar } = useAuth()
  const navigate = useNavigate()
  const location = useLocation()
  const [modo, setModo] = useState(location.pathname === '/register' ? 'register' : 'login')
  const [nome, setNome] = useState('')
  const [email, setEmail] = useState('')
  const [senha, setSenha] = useState('')
  const [erro, setErro] = useState(null)
  const [busy, setBusy] = useState(false)

  if (user) {
    return <Navigate to="/campeonatos" replace />
  }

  async function handleSubmit(event) {
    event.preventDefault()
    setErro(null)
    setBusy(true)
    try {
      const dados = modo === 'register' ? { nome, email, senha } : { email, senha }
      if (modo === 'register') {
        await cadastrar(dados)
      } else {
        await entrar(dados)
      }
      navigate('/campeonatos')
    } catch (err) {
      setErro(parseApiError(err).message)
    } finally {
      setBusy(false)
    }
  }

  function trocarModo() {
    const proximo = modo === 'login' ? 'register' : 'login'
    setModo(proximo)
    setErro(null)
    navigate(proximo === 'login' ? '/login' : '/register')
  }

  return (
    <div className="mx-auto grid max-w-5xl items-center gap-8 py-4 lg:grid-cols-[1.05fr_0.95fr]">
      <div className="animate-slide-in hidden lg:block">
        <Badge color="primary">{BRAND.name} · Palpite Manager</Badge>
        <h1 className="mt-5 text-5xl font-extrabold leading-tight tracking-tight text-slate-900">
          O placar exato <span className="brand-text">é o seu lugar.</span>
        </h1>
        <p className="mt-5 max-w-md text-base leading-relaxed text-slate-600">
          {BRAND.description}. Acerte, pontue e acompanhe cada rodada como num painel de estádio.
        </p>
        <div className="mt-8 flex flex-wrap gap-3">
          <div className="animate-pop delay-1 rounded-2xl border border-primary/10 bg-white px-5 py-4 shadow-lg shadow-primary/5">
            <div className="text-2xl font-extrabold text-primary">6</div>
            <div className="text-xs font-semibold uppercase tracking-wide text-slate-500">patentes</div>
          </div>
          <div className="animate-pop delay-2 rounded-2xl border border-primary/10 bg-white px-5 py-4 shadow-lg shadow-primary/5">
            <div className="text-2xl font-extrabold text-primary">100%</div>
            <div className="text-xs font-semibold uppercase tracking-wide text-slate-500">placar exato</div>
          </div>
          <div className="animate-pop delay-3 rounded-2xl border border-primary/10 bg-white px-5 py-4 shadow-lg shadow-primary/5">
            <div className="text-2xl font-extrabold text-primary">∞</div>
            <div className="text-xs font-semibold uppercase tracking-wide text-slate-500">rodadas</div>
          </div>
        </div>
      </div>

      <Card className="animate-slide-in-right p-7 sm:p-8">
        <div className="mb-6">
          <div className="mb-4 h-1.5 w-16 animate-gradient rounded-full brand-gradient" />
          <h2 className="text-2xl font-extrabold tracking-tight text-slate-900">
            {modo === 'login' ? `Bem-vindo ao ${BRAND.name}` : `Entre no ${BRAND.name}`}
          </h2>
          <p className="mt-1.5 text-sm text-slate-500">
            {modo === 'login' ? 'Entre para enviar seus palpites.' : 'É rápido: só nome, e-mail e senha.'}
          </p>
        </div>

        <form onSubmit={handleSubmit} className="space-y-4">
          {modo === 'register' && (
            <label className="block">
              <span className="mb-1.5 block text-sm font-semibold text-slate-700">Nome</span>
              <input className={inputClass} value={nome} onChange={(e) => setNome(e.target.value)} placeholder="Seu nome" required />
            </label>
          )}
          <label className="block">
            <span className="mb-1.5 block text-sm font-semibold text-slate-700">E-mail</span>
            <input className={inputClass} type="email" value={email} onChange={(e) => setEmail(e.target.value)} placeholder="voce@email.com" required />
          </label>
          <label className="block">
            <span className="mb-1.5 block text-sm font-semibold text-slate-700">Senha</span>
            <input
              className={inputClass}
              type="password"
              value={senha}
              onChange={(e) => setSenha(e.target.value)}
              minLength={modo === 'register' ? 6 : undefined}
              placeholder={modo === 'register' ? 'Mínimo de 6 caracteres' : 'Sua senha'}
              required
            />
          </label>
          {erro && <ErrorBox message={erro} />}
          <Button type="submit" className="w-full py-3" disabled={busy}>
            {busy ? 'Aguarde...' : modo === 'login' ? 'Entrar' : 'Cadastrar'}
          </Button>
        </form>

        <button
          type="button"
          onClick={trocarModo}
          className="mt-5 w-full rounded-xl bg-primary-light/60 py-3 text-center text-sm font-semibold text-primary-dark transition-all duration-200 hover:bg-primary-light"
        >
          {modo === 'login' ? 'Ainda não tem conta? Cadastre-se' : 'Já tem conta? Entre'}
        </button>
      </Card>
    </div>
  )
}
