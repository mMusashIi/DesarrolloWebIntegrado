import { useState } from 'react'
import { useAuth } from '../../context/AuthContext'

function RegisterModal({ show, onHide, onSuccess }) {
  const [formData, setFormData] = useState({
    nombre: '',
    apellido: '',
    email: '',
    telefono: '',
    password: '',
    confirmPassword: ''
  })
  const { register, isLoading } = useAuth()

  const handleInputChange = (e) => {
    const { name, value } = e.target
    setFormData(prev => ({
      ...prev,
      [name]: value
    }))
  }

  const handleSubmit = async (e) => {
    e.preventDefault()
    
    if (formData.password !== formData.confirmPassword) {
      alert('Las contraseñas no coinciden')
      return
    }

    try {
      await register({
        id: Date.now(),
        nombre: formData.nombre,
        apellido: formData.apellido,
        email: formData.email,
        telefono: formData.telefono
      })
      onSuccess()
      onHide()
    } catch (error) {
      alert('Error en el registro. Inténtalo de nuevo.')
    }
  }

  if (!show) return null

  return (
    <div 
      className={`modal fade ${show ? 'show' : ''}`} 
      style={{ display: show ? 'block' : 'none', backgroundColor: 'rgba(0,0,0,0.5)' }}
      tabIndex="-1"
    >
      <div className="modal-dialog modal-lg">
        <div className="modal-content">
          <div className="modal-header bg-primary-custom text-white">
            <h5 className="modal-title">
              <i className="fas fa-user-plus me-2"></i>Regístrate para Reservar
            </h5>
            <button 
              type="button" 
              className="btn-close btn-close-white" 
              onClick={onHide}
            ></button>
          </div>
          <div className="modal-body">
            <div className="text-center mb-4">
              <i className="fas fa-lock fa-3x text-primary-custom mb-3"></i>
              <h4>Registro Requerido</h4>
              <p className="text-muted">
                Para realizar reservas necesitas tener una cuenta. Es rápido y fácil.
              </p>
            </div>

            <form onSubmit={handleSubmit}>
              <div className="row">
                <div className="col-md-6 mb-3">
                  <label htmlFor="modal-nombre" className="form-label fw-bold">
                    Nombre *
                  </label>
                  <input 
                    type="text" 
                    className="form-control form-control-custom" 
                    id="modal-nombre"
                    name="nombre"
                    value={formData.nombre}
                    onChange={handleInputChange}
                    required 
                  />
                </div>
                <div className="col-md-6 mb-3">
                  <label htmlFor="modal-apellido" className="form-label fw-bold">
                    Apellido *
                  </label>
                  <input 
                    type="text" 
                    className="form-control form-control-custom" 
                    id="modal-apellido"
                    name="apellido"
                    value={formData.apellido}
                    onChange={handleInputChange}
                    required 
                  />
                </div>
              </div>

              <div className="row">
                <div className="col-md-6 mb-3">
                  <label htmlFor="modal-email" className="form-label fw-bold">
                    Email *
                  </label>
                  <input 
                    type="email" 
                    className="form-control form-control-custom" 
                    id="modal-email"
                    name="email"
                    value={formData.email}
                    onChange={handleInputChange}
                    required 
                  />
                </div>
                <div className="col-md-6 mb-3">
                  <label htmlFor="modal-telefono" className="form-label fw-bold">
                    Teléfono
                  </label>
                  <input 
                    type="tel" 
                    className="form-control form-control-custom" 
                    id="modal-telefono"
                    name="telefono"
                    value={formData.telefono}
                    onChange={handleInputChange}
                  />
                </div>
              </div>

              <div className="row">
                <div className="col-md-6 mb-3">
                  <label htmlFor="modal-password" className="form-label fw-bold">
                    Contraseña *
                  </label>
                  <input 
                    type="password" 
                    className="form-control form-control-custom" 
                    id="modal-password"
                    name="password"
                    value={formData.password}
                    onChange={handleInputChange}
                    required 
                  />
                </div>
                <div className="col-md-6 mb-3">
                  <label htmlFor="modal-confirmPassword" className="form-label fw-bold">
                    Confirmar Contraseña *
                  </label>
                  <input 
                    type="password" 
                    className="form-control form-control-custom" 
                    id="modal-confirmPassword"
                    name="confirmPassword"
                    value={formData.confirmPassword}
                    onChange={handleInputChange}
                    required 
                  />
                </div>
              </div>

              <div className="text-center">
                <button 
                  type="submit" 
                  className="btn btn-primary-custom btn-lg w-100"
                  disabled={isLoading}
                >
                  {isLoading ? (
                    <>
                      <div className="loading-spinner me-2"></div>
                      Creando Cuenta...
                    </>
                  ) : (
                    <>
                      <i className="fas fa-user-plus me-2"></i>
                      Crear Cuenta y Continuar
                    </>
                  )}
                </button>
              </div>
            </form>
          </div>
        </div>
      </div>
    </div>
  )
}

export default RegisterModal