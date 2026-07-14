import { Link, useLocation, useNavigate } from 'react-router-dom'
import { useAuth } from '../../context/AuthContext'
import '../../styles/main.css'; // Asegúrate de que aquí estén tus estilos custom

function Header({ onShowLogin, onShowRegister }) {
  const location = useLocation()
  const navigate = useNavigate()
  const { user, logout, isAdmin } = useAuth()

  // Función para marcar el link activo
  const isActive = (path) => location.pathname === path

  const handleLogout = () => {
    if (confirm('¿Estás seguro de que quieres cerrar sesión?')) {
      logout()
      navigate('/')
    }
  }

  // Manejadores para el menú dropdown
  const handleLoginClick = (e) => {
    e.preventDefault()
    if (onShowLogin) onShowLogin()
  }

  const handleRegisterClick = (e) => {
    e.preventDefault()
    if (onShowRegister) {
        onShowRegister()
    } else {
        navigate('/register')
    }
  }

  return (
    <nav className="navbar navbar-expand-lg navbar-light fixed-top navbar-custom">
      <div className="container">
        {/* LOGO CON TUS ESTILOS */}
        <Link className="navbar-brand navbar-brand-custom" to="/">
          <i className="fas fa-mountain text-primary-custom me-2"></i>
          Buganvilla Tours
        </Link>
        
        <button className="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav">
          <span className="navbar-toggler-icon"></span>
        </button>
        
        <div className="collapse navbar-collapse" id="navbarNav">
          <ul className="navbar-nav ms-auto align-items-center">
            
            {/* ENLACES PRINCIPALES (Usando nav-link-custom) */}
            <li className="nav-item">
              <Link className={`nav-link nav-link-custom ${isActive('/') ? 'active' : ''}`} to="/">
                Inicio
              </Link>
            </li>
            <li className="nav-item">
              <Link className={`nav-link nav-link-custom ${isActive('/paquetes') ? 'active' : ''}`} to="/paquetes">
                Paquetes
              </Link>
            </li>
            <li className="nav-item">
              <Link className={`nav-link nav-link-custom ${isActive('/nosotros') ? 'active' : ''}`} to="/nosotros">
                Nosotros
              </Link>
            </li>
            <li className="nav-item">
              <Link className={`nav-link nav-link-custom ${isActive('/contacto') ? 'active' : ''}`} to="/contacto">
                Contacto
              </Link>
            </li>

            {/* Link Admin (Solo si es admin) */}
            {isAdmin && (
              <li className="nav-item">
                <Link className={`nav-link nav-link-custom fw-bold text-danger ${isActive('/admin') ? 'active' : ''}`} to="/admin">
                  <i className="fas fa-user-cog me-1"></i>Admin
                </Link>
              </li>
            )}

            {/* SECCIÓN DE USUARIO / CUENTA */}
            {user ? (
              // --- ESTADO LOGUEADO ---
              <li className="nav-item dropdown ms-lg-3">
                <a 
                  className="nav-link nav-link-custom dropdown-toggle btn btn-outline-primary-custom rounded-pill px-3" 
                  href="#" 
                  role="button" 
                  data-bs-toggle="dropdown"
                >
                  <i className="fas fa-user-check me-2"></i>
                  Hola, {user.nombre ? user.nombre.split(' ')[0] : 'Usuario'}
                </a>
                <ul className="dropdown-menu dropdown-menu-end shadow-sm border-0">
                  <li>
                    <span className="dropdown-item-text small text-muted">
                      <i className="fas fa-envelope me-2"></i>{user.email}
                    </span>
                  </li>
                  <li><hr className="dropdown-divider" /></li>
                  <li>
                    <Link className="dropdown-item" to="/reservas">
                      <i className="fas fa-suitcase me-2 text-primary-custom"></i>Mis Viajes
                    </Link>
                  </li>
                  <li><hr className="dropdown-divider" /></li>
                  <li>
                    <button className="dropdown-item text-danger" onClick={handleLogout}>
                      <i className="fas fa-sign-out-alt me-2"></i>Cerrar Sesión
                    </button>
                  </li>
                </ul>
              </li>
            ) : (
              // --- ESTADO NO LOGUEADO (Botón "Cuenta") ---
              <li className="nav-item dropdown ms-lg-3">
                <a 
                  className="nav-link nav-link-custom dropdown-toggle btn btn-primary-custom text-white rounded-pill px-4" 
                  href="#" 
                  role="button" 
                  data-bs-toggle="dropdown"
                >
                  <i className="fas fa-user-circle me-2"></i>
                  Cuenta
                </a>
                <ul className="dropdown-menu dropdown-menu-end shadow-sm border-0">
                  {/* Opción 1: Iniciar Sesión (Abre Modal) */}
                  <li>
                    <button className="dropdown-item py-2 w-100 text-start" onClick={handleLoginClick}>
                      <i className="fas fa-sign-in-alt me-2 text-primary-custom"></i>
                      Iniciar Sesión
                    </button>
                  </li>
                  
                  {/* Opción 2: Registrarse (Va a página nueva) */}
                  <li>
                    <button className="dropdown-item py-2 w-100 text-start" onClick={handleRegisterClick}>
                      <i className="fas fa-user-plus me-2 text-primary-custom"></i>
                      Registrarse
                    </button>
                  </li>
                </ul>
              </li>
            )}
          </ul>
        </div>
      </div>
    </nav>
  )
}

export default Header