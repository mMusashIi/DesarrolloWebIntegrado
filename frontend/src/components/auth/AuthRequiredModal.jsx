import { useAuth } from '../../context/AuthContext'

function AuthRequiredModal({ show, onHide }) {
  const { isAuthenticated } = useAuth()

  if (!show) return null

  const handleLoginClick = () => {
    onHide()
    // El modal de login se abrirá desde el Header a través de App.jsx
    // Simplemente cerramos este modal
  }

  const handleRegisterClick = () => {
    onHide()
    // El modal de registro se abrirá desde el Header a través de App.jsx
    // Simplemente cerramos este modal
  }

  return (
    <div 
      className={`modal fade ${show ? 'show' : ''}`} 
      style={{ display: show ? 'block' : 'none', backgroundColor: 'rgba(0,0,0,0.5)' }}
      tabIndex="-1"
    >
      <div className="modal-dialog modal-dialog-centered">
        <div className="modal-content">
          <div className="modal-header">
            <h5 className="modal-title">
              <i className="fas fa-lock text-primary-custom me-2"></i>
              Acceso Requerido
            </h5>
            <button 
              type="button" 
              className="btn-close" 
              onClick={onHide}
            ></button>
          </div>
          <div className="modal-body text-center">
            <div className="mb-4">
              <i className="fas fa-user-lock fa-3x text-primary-custom mb-3"></i>
              <h4>Necesitas una cuenta</h4>
              <p className="text-muted">
                Para realizar reservas y acceder a todas las funciones, 
                necesitas tener una cuenta en Buganvilla Tours.
              </p>
            </div>
            
            <div className="row">
              <div className="col-6">
                <button 
                  className="btn btn-outline-primary w-100"
                  onClick={handleLoginClick}
                >
                  <i className="fas fa-sign-in-alt me-2"></i>
                  Iniciar Sesión
                </button>
              </div>
              <div className="col-6">
                <button 
                  className="btn btn-primary-custom w-100"
                  onClick={handleRegisterClick}
                >
                  <i className="fas fa-user-plus me-2"></i>
                  Crear Cuenta
                </button>
              </div>
            </div>
            
            <div className="mt-3">
              <small className="text-muted">
                Al crear una cuenta aceptas nuestros{' '}
                <a href="#" className="text-primary-custom">Términos y Condiciones</a>
              </small>
            </div>
          </div>
        </div>
      </div>
    </div>
  )
}

export default AuthRequiredModal