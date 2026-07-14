import { useState } from 'react'
import { useNavigate } from 'react-router-dom' // Importamos el hook de navegación
import { useAuth } from '../../context/AuthContext'

function LoginModal({ isOpen, onClose, onRegisterClick }) {
  const { login } = useAuth()
  const navigate = useNavigate() // Inicializamos el hook
  
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [error, setError] = useState('')
  
  if (!isOpen) return null 

  const handleSubmit = async (e) => {
    e.preventDefault()
    setError('')
    try {
      // 1. Esperamos a que el login termine y nos de los datos del usuario
      const userData = await login(email, password)
      
      // 2. Cerramos el modal inmediatamente
      onClose()

      // 3. VERIFICACIÓN DE ROL PARA REDIRECCIÓN
      if (userData && (userData.rol === 'admin' || userData.rol === 'ADMIN')) {
        navigate('/admin') // Si es jefe, a la oficina
      } else {
        // Si es cliente, se queda donde estaba (o podrías mandarlo a /reservas si quisieras)
        // navigate('/') 
      }

    } catch (error) {
      console.error(error)
      setError('Credenciales incorrectas o error de conexión')
    }
  }

  return (
    <div className="modal fade show" style={{ display: 'block', backgroundColor: 'rgba(0,0,0,0.5)', zIndex: 1055 }}>
      <div className="modal-dialog modal-dialog-centered">
        <div className="modal-content border-0 shadow">
          <div className="modal-header border-bottom-0">
            <h5 className="modal-title fw-bold">Iniciar Sesión</h5>
            <button type="button" className="btn-close" onClick={onClose}></button>
          </div>
          <div className="modal-body p-4">
            {error && <div className="alert alert-danger">{error}</div>}
            
            <form onSubmit={handleSubmit}>
              <div className="mb-3">
                <label className="form-label">Correo Electrónico</label>
                <input type="email" className="form-control" value={email} onChange={e=>setEmail(e.target.value)} required />
              </div>
              <div className="mb-4">
                <label className="form-label">Contraseña</label>
                <input type="password" className="form-control" value={password} onChange={e=>setPassword(e.target.value)} required />
              </div>
              <button type="submit" className="btn btn-primary w-100 py-2">Ingresar</button>
            </form>

            <div className="text-center mt-4">
              <p className="text-muted">¿No tienes cuenta? 
                <span onClick={onRegisterClick} style={{color:'var(--primary-color)', cursor:'pointer', fontWeight:'bold', marginLeft:'5px'}}>
                  Regístrate
                </span>
              </p>
            </div>
          </div>
        </div>
      </div>
    </div>
  )
}
export default LoginModal