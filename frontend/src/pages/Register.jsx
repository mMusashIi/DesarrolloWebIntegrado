import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'

function Register({ onOpenLogin }) {
  // 1. LÓGICA CORREGIDA: Estado inicial vacío
  const [formData, setFormData] = useState({
    nombre: '',
    apellido: '',
    email: '',
    telefono: '',
    password: '',
    confirmPassword: '',
    nacionalidad: 'Perú' // Valor por defecto
  })
  
  const { register, isLoading } = useAuth()
  const navigate = useNavigate()

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
      return alert('Las contraseñas no coinciden')
    }

    try {
      // 2. LÓGICA CORREGIDA: Envío de datos completo
      await register({
        nombre: formData.nombre,
        apellido: formData.apellido,
        email: formData.email,
        telefono: formData.telefono,
        password: formData.password,
        nacionalidad: formData.nacionalidad
      })
      
      // 3. LÓGICA CORREGIDA: Redirección automática sin alerta (opcional)
      // alert('¡Cuenta creada con éxito!') 
      navigate('/') 
      
    } catch (error) {
      console.error(error)
      const msg = error.response?.data?.message || 'Error en registro. Intenta nuevamente.'
      alert(msg)
    }
  }

  return (
    <>
      {/* HEADER CON TUS ESTILOS DE GRADIENTE */}
      <section 
        className="py-5 mt-5 d-flex align-items-center justify-content-center" 
        style={{ 
          minHeight: '220px',
          backgroundColor: '#4c1d95', /* Color sólido elegante */
          backgroundImage: 'radial-gradient(circle at top right, #db2777 0%, transparent 40%)', /* Un toque sutil de luz rosada */
          color: 'white' 
        }}
      >
        <div className="container text-center">
          <h1 className="display-5 fw-bold mb-2">Crear Cuenta</h1>
          <p className="lead text-white-50">Comienza tu viaje con nosotros</p>
        </div>
      </section>

      {/* FORMULARIO CON TUS CLASES "CUSTOM" */}
      <section className="section-padding">
        <div className="container">
          <div className="row justify-content-center">
            <div className="col-lg-8">
              <div className="reservation-form"> {/* Clase clave para tu estilo de tarjeta */}
                <form onSubmit={handleSubmit}>
                  <div className="row">
                    <div className="col-md-6 mb-3">
                      <label htmlFor="nombre" className="form-label fw-bold">Nombre *</label>
                      <input 
                        type="text" 
                        className="form-control form-control-custom" 
                        id="nombre"
                        name="nombre" 
                        value={formData.nombre} 
                        onChange={handleInputChange} 
                        required 
                      />
                    </div>
                    <div className="col-md-6 mb-3">
                      <label htmlFor="apellido" className="form-label fw-bold">Apellido *</label>
                      <input 
                        type="text" 
                        className="form-control form-control-custom" 
                        id="apellido"
                        name="apellido" 
                        value={formData.apellido} 
                        onChange={handleInputChange} 
                        required 
                      />
                    </div>
                  </div>

                  <div className="row">
                    <div className="col-md-6 mb-3">
                      <label htmlFor="email" className="form-label fw-bold">Email *</label>
                      <input 
                        type="email" 
                        className="form-control form-control-custom" 
                        id="email"
                        name="email" 
                        value={formData.email} 
                        onChange={handleInputChange} 
                        required 
                      />
                    </div>
                    <div className="col-md-6 mb-3">
                      <label htmlFor="telefono" className="form-label fw-bold">Teléfono</label>
                      <input 
                        type="tel" 
                        className="form-control form-control-custom" 
                        id="telefono"
                        name="telefono" 
                        value={formData.telefono} 
                        onChange={handleInputChange} 
                      />
                    </div>
                  </div>

                  <div className="row">
                    <div className="col-md-6 mb-3">
                      <label htmlFor="password" className="form-label fw-bold">Contraseña *</label>
                      <input 
                        type="password" 
                        className="form-control form-control-custom" 
                        id="password"
                        name="password" 
                        value={formData.password} 
                        onChange={handleInputChange} 
                        required 
                      />
                    </div>
                    <div className="col-md-6 mb-3">
                      <label htmlFor="confirmPassword" className="form-label fw-bold">Confirmar Contraseña *</label>
                      <input 
                        type="password" 
                        className="form-control form-control-custom" 
                        id="confirmPassword"
                        name="confirmPassword" 
                        value={formData.confirmPassword} 
                        onChange={handleInputChange} 
                        required 
                      />
                    </div>
                  </div>

                  <div className="mb-4">
                    <div className="form-check">
                      <input className="form-check-input" type="checkbox" id="terms" required />
                      <label className="form-check-label" htmlFor="terms">
                        Acepto los <a href="#" className="text-primary-custom">términos y condiciones</a> y las <a href="#" className="text-primary-custom">políticas de privacidad</a> *
                      </label>
                    </div>
                  </div>

                  <div className="text-center">
                    <button type="submit" className="btn btn-primary-custom btn-lg px-5 mb-3" disabled={isLoading}>
                      {isLoading ? (
                        <>
                          <div className="loading-spinner me-2"></div>
                          Creando Cuenta...
                        </>
                      ) : (
                        <>
                          <i className="fas fa-user-plus me-2"></i> Crear Cuenta
                        </>
                      )}
                    </button>
                    
                    <p className="text-muted">
                      ¿Ya tienes una cuenta?{' '}
                      <span 
                        onClick={onOpenLogin} 
                        className="text-primary-custom" 
                        style={{ cursor: 'pointer', textDecoration: 'underline', fontWeight: 'bold' }}
                      >
                        Inicia sesión aquí
                      </span>
                    </p>
                  </div>
                </form>
              </div>
            </div>
          </div>
        </div>
      </section>
    </>
  )
}

export default Register