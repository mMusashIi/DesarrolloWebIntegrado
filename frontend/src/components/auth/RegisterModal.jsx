import { useState } from 'react'
import { useAuth } from '../../context/AuthContext'
import { useNavigate } from 'react-router-dom'
import { authAPI } from '../../services/api'

function RegisterModal({ show, onHide, onShowLogin }) {
  const { login } = useAuth()
  const navigate = useNavigate()
  const [formData, setFormData] = useState({
    nombres: '',
    apellidos: '',
    email: '',
    password: '',
    confirmPassword: '',
    telefono: '',
    nacionalidad: 'Perú',
    aceptaTerminos: false
  })
  const [loading, setLoading] = useState(false)
  const [errors, setErrors] = useState({})

  const handleInputChange = (e) => {
    const { name, value, type, checked } = e.target
    setFormData(prev => ({
      ...prev,
      [name]: type === 'checkbox' ? checked : value
    }))
    
    // Limpiar error del campo cuando el usuario empiece a escribir
    if (errors[name]) {
      setErrors(prev => ({
        ...prev,
        [name]: ''
      }))
    }
  }

  const validateForm = () => {
    const newErrors = {}

    if (!formData.nombres.trim()) {
      newErrors.nombres = 'Los nombres son requeridos'
    }

    if (!formData.apellidos.trim()) {
      newErrors.apellidos = 'Los apellidos son requeridos'
    }

    if (!formData.email.trim()) {
      newErrors.email = 'El email es requerido'
    } else if (!/\S+@\S+\.\S+/.test(formData.email)) {
      newErrors.email = 'El email no es válido'
    }

    if (!formData.password) {
      newErrors.password = 'La contraseña es requerida'
    } else if (formData.password.length < 6) {
      newErrors.password = 'La contraseña debe tener al menos 6 caracteres'
    }

    if (formData.password !== formData.confirmPassword) {
      newErrors.confirmPassword = 'Las contraseñas no coinciden'
    }

    if (!formData.telefono.trim()) {
      newErrors.telefono = 'El teléfono es requerido'
    }

    if (!formData.aceptaTerminos) {
      newErrors.aceptaTerminos = 'Debes aceptar los términos y condiciones'
    }

    setErrors(newErrors)
    return Object.keys(newErrors).length === 0
  }

  const handleSubmit = async (e) => {
    e.preventDefault()
    
    if (!validateForm()) {
      return
    }

    setLoading(true)
    setErrors({})

    try {
      // Preparar datos para el backend
      const registerData = {
        nombre: formData.nombres,
        apellido: formData.apellidos,
        email: formData.email,
        password: formData.password,
        telefono: formData.telefono,
        nacionalidad: formData.nacionalidad
      }

      // Llamada a la API del backend
      const response = await authAPI.register(registerData)

      if (response.success && response.data) {
        const { token, usuario } = response.data
        
        // Mapear el usuario del backend al formato esperado por el frontend
        const userData = {
          id: usuario.idUsuario,
          nombre: usuario.nombre,
          apellido: usuario.apellido,
          email: usuario.email,
          telefono: usuario.telefono,
          nacionalidad: usuario.nacionalidad,
          rol: usuario.rol?.toLowerCase() || 'cliente'
        }

        // Iniciar sesión automáticamente después del registro
        login(token, userData)
        onHide()
        
        // Los clientes registrados se quedan en la página actual
      } else {
        setErrors({ submit: response.message || 'Error al crear la cuenta. Inténtalo de nuevo.' })
      }
    } catch (error) {
      console.error('Error en registro:', error)
      const errorMessage = error.response?.data?.message || 
                          error.message || 
                          'Error al crear la cuenta. Inténtalo de nuevo.'
      setErrors({ submit: errorMessage })
    } finally {
      setLoading(false)
    }
  }

  if (!show) return null

  return (
    <div 
      className={`modal fade ${show ? 'show' : ''}`} 
      style={{ display: show ? 'block' : 'none', backgroundColor: 'rgba(0,0,0,0.5)' }}
      tabIndex="-1"
    >
      <div className="modal-dialog modal-dialog-centered modal-lg">
        <div className="modal-content">
          <div className="modal-header">
            <h5 className="modal-title">
              <i className="fas fa-user-plus text-primary-custom me-2"></i>
              Crear Cuenta
            </h5>
            <button 
              type="button" 
              className="btn-close" 
              onClick={onHide}
              disabled={loading}
            ></button>
          </div>
          
          <div className="modal-body">
            {errors.submit && (
              <div className="alert alert-error-custom mb-3">
                <i className="fas fa-exclamation-triangle me-2"></i>
                {errors.submit}
              </div>
            )}

            <form onSubmit={handleSubmit}>
              <div className="row">
                <div className="col-md-6 mb-3">
                  <label htmlFor="register-nombres" className="form-label fw-bold">
                    Nombres *
                  </label>
                  <input 
                    type="text" 
                    className={`form-control form-control-custom ${errors.nombres ? 'is-invalid' : ''}`}
                    id="register-nombres"
                    name="nombres"
                    value={formData.nombres}
                    onChange={handleInputChange}
                    disabled={loading}
                  />
                  {errors.nombres && <div className="invalid-feedback">{errors.nombres}</div>}
                </div>

                <div className="col-md-6 mb-3">
                  <label htmlFor="register-apellidos" className="form-label fw-bold">
                    Apellidos *
                  </label>
                  <input 
                    type="text" 
                    className={`form-control form-control-custom ${errors.apellidos ? 'is-invalid' : ''}`}
                    id="register-apellidos"
                    name="apellidos"
                    value={formData.apellidos}
                    onChange={handleInputChange}
                    disabled={loading}
                  />
                  {errors.apellidos && <div className="invalid-feedback">{errors.apellidos}</div>}
                </div>
              </div>

              <div className="mb-3">
                <label htmlFor="register-email" className="form-label fw-bold">
                  Correo Electrónico *
                </label>
                <input 
                  type="email" 
                  className={`form-control form-control-custom ${errors.email ? 'is-invalid' : ''}`}
                  id="register-email"
                  name="email"
                  value={formData.email}
                  onChange={handleInputChange}
                  disabled={loading}
                />
                {errors.email && <div className="invalid-feedback">{errors.email}</div>}
              </div>

              <div className="row">
                <div className="col-md-6 mb-3">
                  <label htmlFor="register-password" className="form-label fw-bold">
                    Contraseña *
                  </label>
                  <input 
                    type="password" 
                    className={`form-control form-control-custom ${errors.password ? 'is-invalid' : ''}`}
                    id="register-password"
                    name="password"
                    value={formData.password}
                    onChange={handleInputChange}
                    disabled={loading}
                  />
                  {errors.password && <div className="invalid-feedback">{errors.password}</div>}
                </div>

                <div className="col-md-6 mb-3">
                  <label htmlFor="register-confirm-password" className="form-label fw-bold">
                    Confirmar Contraseña *
                  </label>
                  <input 
                    type="password" 
                    className={`form-control form-control-custom ${errors.confirmPassword ? 'is-invalid' : ''}`}
                    id="register-confirm-password"
                    name="confirmPassword"
                    value={formData.confirmPassword}
                    onChange={handleInputChange}
                    disabled={loading}
                  />
                  {errors.confirmPassword && <div className="invalid-feedback">{errors.confirmPassword}</div>}
                </div>
              </div>

              <div className="row">
                <div className="col-md-6 mb-3">
                  <label htmlFor="register-telefono" className="form-label fw-bold">
                    Teléfono *
                  </label>
                  <input 
                    type="tel" 
                    className={`form-control form-control-custom ${errors.telefono ? 'is-invalid' : ''}`}
                    id="register-telefono"
                    name="telefono"
                    value={formData.telefono}
                    onChange={handleInputChange}
                    disabled={loading}
                  />
                  {errors.telefono && <div className="invalid-feedback">{errors.telefono}</div>}
                </div>

                <div className="col-md-6 mb-3">
                  <label htmlFor="register-nacionalidad" className="form-label fw-bold">
                    Nacionalidad
                  </label>
                  <select 
                    className="form-select form-control-custom"
                    id="register-nacionalidad"
                    name="nacionalidad"
                    value={formData.nacionalidad}
                    onChange={handleInputChange}
                    disabled={loading}
                  >
                    <option value="Perú">Perú</option>
                    <option value="Argentina">Argentina</option>
                    <option value="Chile">Chile</option>
                    <option value="Colombia">Colombia</option>
                    <option value="Ecuador">Ecuador</option>
                    <option value="México">México</option>
                    <option value="España">España</option>
                    <option value="Estados Unidos">Estados Unidos</option>
                    <option value="Otro">Otro</option>
                  </select>
                </div>
              </div>

              <div className="mb-3 form-check">
                <input 
                  type="checkbox" 
                  className={`form-check-input ${errors.aceptaTerminos ? 'is-invalid' : ''}`}
                  id="acepta-terminos"
                  name="aceptaTerminos"
                  checked={formData.aceptaTerminos}
                  onChange={handleInputChange}
                  disabled={loading}
                />
                <label className="form-check-label" htmlFor="acepta-terminos">
                  Acepto los <a href="#" className="text-primary-custom">Términos y Condiciones</a> y las <a href="#" className="text-primary-custom">Políticas de Privacidad</a> *
                </label>
                {errors.aceptaTerminos && <div className="invalid-feedback d-block">{errors.aceptaTerminos}</div>}
              </div>

              <div className="d-grid gap-2">
                <button 
                  type="submit" 
                  className="btn btn-primary-custom"
                  disabled={loading}
                >
                  {loading ? (
                    <>
                      <div className="loading-spinner me-2"></div>
                      Creando Cuenta...
                    </>
                  ) : (
                    <>
                      <i className="fas fa-user-plus me-2"></i>
                      Crear Cuenta
                    </>
                  )}
                </button>
              </div>
            </form>

            <div className="text-center mt-3">
              <button 
                className="btn btn-link p-0 text-primary-custom"
                onClick={onShowLogin}
                disabled={loading}
              >
                ¿Ya tienes cuenta? Inicia sesión aquí
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  )
}

export default RegisterModal