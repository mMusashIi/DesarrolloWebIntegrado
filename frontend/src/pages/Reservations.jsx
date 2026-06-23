import { useState } from 'react'
import { useAuth } from '../context/AuthContext'
import { useNavigate } from 'react-router-dom'
import ReservationForm from '../components/reservations/ReservationForm'
import ReservationSummary from '../components/reservations/ReservationSummary'
import ConfirmationModal from '../components/reservations/ConfirmationModal'
import LoginModal from '../components/auth/LoginModal'
import { reservationsAPI, inventoryAPI, mercadoPagoAPI } from '../services/api'

function Reservations() {
  const { isAuthenticated, user } = useAuth()
  const navigate = useNavigate()
  const [selectedPackage, setSelectedPackage] = useState(null)
  const [showConfirmation, setShowConfirmation] = useState(false)
  const [showLoginModal, setShowLoginModal] = useState(false)
  const [reservationData, setReservationData] = useState(null)
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState('')

  const handleReservationSubmit = async (data) => {
    // Verificar autenticación
    if (!isAuthenticated) {
      setShowLoginModal(true)
      return
    }

    setLoading(true)
    setError('')

    try {
      // Buscar inventario disponible para el paquete y fecha seleccionada
      const inventoryResponse = await inventoryAPI.getAvailableByPackage(data.package.id)
      
      if (!inventoryResponse.success || !inventoryResponse.data || inventoryResponse.data.length === 0) {
        throw new Error('No hay fechas disponibles para este paquete')
      }

      // Bypaseo de validación de fecha: simplemente tomamos el primer inventario disponible para este paquete
      // (para asegurar que siempre se pueda crear la reserva y probar MercadoPago)
      const availableInventory = inventoryResponse.data.find(inv => inv.cupoDisponible >= data.passengers)

      if (!availableInventory) {
        throw new Error('No hay cupo disponible para la fecha seleccionada')
      }

      // Crear la reserva en el formato que espera el backend
      const reservationRequest = {
        idInventario: availableInventory.idInventario,
        cantidadPersonas: parseInt(data.passengers)
      }

      // Enviar reserva al backend
      const reservationResponse = await reservationsAPI.create(reservationRequest)

      if (reservationResponse.success) {
        const idReserva = reservationResponse.data.idReserva
        
        // Combinar datos del formulario con la respuesta del backend
        setReservationData({
          ...data,
          idReserva: idReserva,
          codigoReserva: 'BT' + idReserva.toString().padStart(6, '0')
        })
        
        // Volvemos a mostrar el modal para que el usuario pueda ver su reserva/voucher primero
        setShowConfirmation(true)
      } else {
        throw new Error(reservationResponse.message || 'Error al crear la reserva')
      }
    } catch (err) {
      console.error('Error al crear reserva:', err)
      setError(err.response?.data?.message || err.message || 'Error al crear la reserva. Por favor intenta de nuevo.')
    } finally {
      setLoading(false)
    }
  }

  const handleCloseConfirmation = () => {
    setShowConfirmation(false)
    setReservationData(null)
    // Limpiar el formulario
    setSelectedPackage(null)
    window.location.reload()
  }

  const handleLoginSuccess = () => {
    setShowLoginModal(false)
    // El usuario ya está autenticado, puede intentar la reserva de nuevo
  }

  return (
    <>
      {/* Page Header */}
      <section 
        className="py-5 mt-5" 
        style={{ 
          background: 'linear-gradient(135deg, var(--primary-color), var(--secondary-color))', 
          color: 'white' 
        }}
      >
        <div className="container">
          <div className="text-center">
            <h1 className="display-4 mb-3">Reservar Tu Experiencia</h1>
            <p className="lead">Completa el formulario y confirma tu aventura con nosotros</p>
          </div>
        </div>
      </section>

      {/* Reservation Form */}
      <section className="section-padding">
        <div className="container">
          <div className="row justify-content-center">
            <div className="col-lg-10">
              <div className="reservation-form">
                <div className="row">
                  {/* Form Section */}
                  <div className="col-lg-8">
                    <ReservationForm 
                      onReservationSubmit={handleReservationSubmit}
                      selectedPackage={selectedPackage}
                      onPackageChange={setSelectedPackage}
                    />
                  </div>

                  {/* Summary Section */}
                  <div className="col-lg-4">
                    <ReservationSummary selectedPackage={selectedPackage} />
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </section>

      {/* Error Message */}
      {error && (
        <div className="container mt-3">
          <div className="alert alert-danger" role="alert">
            <i className="fas fa-exclamation-triangle me-2"></i>
            {error}
          </div>
        </div>
      )}

      {/* Loading Overlay */}
      {loading && (
        <div className="position-fixed top-0 start-0 w-100 h-100 d-flex align-items-center justify-content-center" 
             style={{ backgroundColor: 'rgba(0,0,0,0.5)', zIndex: 9999 }}>
          <div className="spinner-border text-light" role="status">
            <span className="visually-hidden">Creando reserva...</span>
          </div>
        </div>
      )}

      {/* Confirmation Modal */}
      <ConfirmationModal 
        show={showConfirmation}
        onHide={handleCloseConfirmation}
        reservationData={reservationData}
      />

      {/* Login Modal */}
      <LoginModal
        show={showLoginModal}
        onHide={() => setShowLoginModal(false)}
        onShowRegister={() => {
          setShowLoginModal(false)
          navigate('/register')
        }}
      />
    </>
  )
}

export default Reservations