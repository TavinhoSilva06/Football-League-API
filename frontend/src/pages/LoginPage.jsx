import { useState } from 'react'
import { Navigate, useLocation, useNavigate } from 'react-router-dom'
import { PageHeader, Card, Button, ErrorBox } from '../components/ui'
import { useAuth } from '../AuthContext'
import { parseApiError } from '../api/client'

const inputClass =
  'w-full rounded-lg border border-slate-300 bg-white px-4 py-2.5 text-sm transition-all focus:border-red-600 focus:outline-none focus:ring-2 focus:ring-red-200/50'

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
    <div className="mx-auto max-w-md">
      <PageHeader
        title={modo === 'login' ? 'Entrar' : 'Criar conta'}
        subtitle="Faça seus palpites e suba de patente"
      />
      <Card className="p-6">
        <form onSubmit={handleSubmit} className="space-y-4">
          {modo === 'register' && (
            <label className="block">
              <span className="mb-1 block text-sm font-medium text-slate-700">Nome</span>
              <input className={inputClass} value={nome} onChange={(e) => setNome(e.target.value)} required />
            </label>
          )}
          <label className="block">
            <span className="mb-1 block text-sm font-medium text-slate-700">E-mail</span>
            <input
              className={inputClass}
              type="email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              required
            />
          </label>
          <label className="block">
            <span className="mb-1 block text-sm font-medium text-slate-700">Senha</span>
            <input
              className={inputClass}
              type="password"
              value={senha}
              onChange={(e) => setSenha(e.target.value)}
              minLength={modo === 'register' ? 6 : undefined}
              required
            />
          </label>
          {erro && <ErrorBox message={erro} />}
          <Button type="submit" className="w-full" disabled={busy}>
            {busy ? 'Aguarde...' : modo === 'login' ? 'Entrar' : 'Cadastrar'}
          </Button>
        </form>
        <button
          type="button"
          onClick={trocarModo}
          className="mt-4 w-full text-center text-sm text-red-700 hover:underline"
        >
          {modo === 'login' ? 'Ainda não tem conta? Cadastre-se' : 'Já tem conta? Entre'}
        </button>
      </Card>
    </div>
  )
}
