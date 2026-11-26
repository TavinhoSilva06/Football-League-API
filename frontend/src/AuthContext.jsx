import { createContext, useCallback, useContext, useEffect, useState } from 'react'
import { api, getToken, setToken } from './api/client'

const AuthContext = createContext(null)

export function AuthProvider({ children }) {
  const [user, setUser] = useState(null)
  const [loading, setLoading] = useState(true)

  const recarregarPerfil = useCallback(async () => {
    if (!getToken()) {
      setUser(null)
      setLoading(false)
      return
    }
    try {
      const response = await api.get('/auth/me')
      setUser(response.data)
    } catch {
      setUser(null)
    } finally {
      setLoading(false)
    }
  }, [])

  useEffect(() => {
    recarregarPerfil()
    const aoPerderAutorizacao = () => setUser(null)
    window.addEventListener('auth:unauthorized', aoPerderAutorizacao)
    return () => window.removeEventListener('auth:unauthorized', aoPerderAutorizacao)
  }, [recarregarPerfil])

  async function entrar(dados) {
    const response = await api.post('/auth/login', dados)
    setToken(response.data.token)
    setUser(response.data.usuario)
    return response.data
  }

  async function cadastrar(dados) {
    const response = await api.post('/auth/register', dados)
    setToken(response.data.token)
    setUser(response.data.usuario)
    return response.data
  }

  function sair() {
    setToken(null)
    setUser(null)
  }

  return (
    <AuthContext.Provider value={{ user, loading, entrar, cadastrar, sair, recarregarPerfil }}>
      {children}
    </AuthContext.Provider>
  )
}

export function useAuth() {
  return useContext(AuthContext)
}
