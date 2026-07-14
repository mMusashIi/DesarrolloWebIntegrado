import { createContext, useContext, useState, useEffect } from 'react'
import { authAPI } from '../services/api'

const AuthContext = createContext()

export function AuthProvider({ children }) {
  const [user, setUser] = useState(null)
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    const savedUser = localStorage.getItem('buganvilla_user')
    const savedToken = localStorage.getItem('buganvilla_token')
    
    if (savedUser && savedToken) {
      try {
        setUser(JSON.parse(savedUser))
      } catch (error) {
        localStorage.removeItem('buganvilla_user')
        localStorage.removeItem('buganvilla_token')
      }
    }
    setLoading(false)
  }, [])

  const saveSession = (token, userData) => {
    localStorage.setItem('buganvilla_token', token)
    localStorage.setItem('buganvilla_user', JSON.stringify(userData))
    setUser(userData)
  }

  // --- MODIFICACIÓN AQUÍ ---
  const login = async (email, password) => {
    try {
      const response = await authAPI.login(email, password)
      const { token, usuario } = response.result || response.data || response;
      
      if (token) {
        saveSession(token, usuario)
        return usuario // Retornamos el objeto usuario para poder verificar el rol
      }
    } catch (error) {
      throw error
    }
  }

  const logout = () => {
    setUser(null)
    localStorage.removeItem('buganvilla_user')
    localStorage.removeItem('buganvilla_token')
    window.location.href = '/'
  }

  const register = async (userData) => {
    try {
      const response = await authAPI.register(userData)
      const { token, usuario } = response.result || response.data || response;

      if (token) {
        saveSession(token, usuario)
      }
      return response
    } catch (error) {
      throw error
    }
  }

  const value = {
    user,
    login,
    logout,
    register,
    isAuthenticated: !!user,
    isAdmin: user && (user.rol === 'admin' || user.rol === 'ADMIN'),
    isLoading: loading
  }

  return (
    <AuthContext.Provider value={value}>
      {children}
    </AuthContext.Provider>
  )
}

export function useAuth() {
  const context = useContext(AuthContext)
  if (!context) throw new Error('useAuth debe ser usado dentro de un AuthProvider')
  return context
}