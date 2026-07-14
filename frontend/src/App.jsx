import { useState } from 'react'
import { Routes, Route, useLocation, useNavigate } from 'react-router-dom'
import { AuthProvider } from './context/AuthContext'
import Header from './components/common/Header'
import Footer from './components/common/Footer'
import Home from './pages/Home'
import Packages from './pages/Packages'
import Reservations from './pages/Reservations'
import Admin from './pages/Admin'
import About from './pages/About'
import Contact from './pages/Contact'
import Register from './pages/Register'
import LoginModal from './components/auth/LoginModal'

function App() {
  const [showLoginModal, setShowLoginModal] = useState(false)
  const location = useLocation()
  const navigate = useNavigate()
  const isAdminRoute = location.pathname.startsWith('/admin')

  const handleShowLogin = () => setShowLoginModal(true)
  const handleCloseLogin = () => setShowLoginModal(false)

  // Ir a la página de registro
  const handleSwitchToRegister = () => {
    setShowLoginModal(false)
    navigate('/register')
  }

  // Abrir login (se pasa a la página de registro)
  const handleSwitchToLogin = () => {
    setShowLoginModal(true)
  }

  return (
    <AuthProvider>
      <div className="App">
        {!isAdminRoute && (
          <Header 
            onShowLogin={handleShowLogin} 
            onShowRegister={handleSwitchToRegister} 
          />
        )}
        
        <main>
          <Routes>
            <Route path="/" element={<Home />} />
            <Route path="/paquetes" element={<Packages />} />
            <Route path="/reservas" element={<Reservations />} />
            <Route path="/admin" element={<Admin />} />
            <Route path="/nosotros" element={<About />} />
            <Route path="/contacto" element={<Contact />} />
            
            {/* RUTA DE REGISTRO */}
            <Route 
              path="/register" 
              element={<Register onOpenLogin={handleSwitchToLogin} />} 
            />
          </Routes>
        </main>

        {!isAdminRoute && <Footer />}

        <LoginModal 
          isOpen={showLoginModal} 
          onClose={handleCloseLogin}
          onRegisterClick={handleSwitchToRegister}
        />
      </div>
    </AuthProvider>
  )
}

export default App