import { useState, useEffect } from 'react'
import { usePackages } from '../../hooks/usePackages'
import { useAuth } from '../../context/AuthContext'
import { apisNetAPI, authAPI } from '../../services/api'

function ReservationForm({ onReservationSubmit, selectedPackage, onPackageChange }) {
  const [formData, setFormData] = useState({
    fullName: '',
    email: '',
    phone: '',
    document: '',
    travelDate: '',
    passengers: 1,
    services: {
      hotelPickup: false,
      lunchIncluded: false,
      guideEnglish: false,
      insurance: false
    },
    specialRequests: ''
  })

  const { packages } = usePackages()
  const { user } = useAuth()
  const [isFetchingDni, setIsFetchingDni] = useState(false)

  // Fetch DNI data if user has DNI or when document changes
  useEffect(() => {
    const fetchDniData = async (dni) => {
      if (!dni || dni.length !== 8) return
      
      setIsFetchingDni(true)
      try {
        const data = await apisNetAPI.getPersonaByDni(dni)
        if (data && data.nombres) {
          setFormData(prev => ({
            ...prev,
            fullName: `${data.nombres} ${data.apellidoPaterno} ${data.apellidoMaterno}`
          }))
        }
      } catch (error) {
        console.error('Error fetching DNI:', error)
      } finally {
        setIsFetchingDni(false)
      }
    }

    // Auto-fill from user context if available
    if (user) {
      setFormData(prev => ({
        ...prev,
        email: user.email || '',
        phone: user.telefono || '',
        document: user.dni || ''
      }))
      
      if (user.dni) {
        // DNI is available in session - use it directly
        fetchDniData(user.dni)
      } else {
        // DNI missing from local session - fetch fresh profile from backend
        authAPI.getProfile().then(profileResp => {
          const freshUser = profileResp?.result || profileResp?.data || profileResp
          if (freshUser?.dni) {
            setFormData(prev => ({ ...prev, document: freshUser.dni }))
            fetchDniData(freshUser.dni)
            // Also update localStorage so future loads have the dni
            const stored = localStorage.getItem('buganvilla_user')
            if (stored) {
              try {
                const parsed = JSON.parse(stored)
                localStorage.setItem('buganvilla_user', JSON.stringify({ ...parsed, dni: freshUser.dni }))
              } catch (_) {}
            }
          }
        }).catch(() => {})
      }
    }
  }, [user])

  const handleDocumentBlur = (e) => {
    const dni = e.target.value
    if (dni && dni.length === 8 && (!user || dni !== user.dni)) {
      // Re-fetch only if user types a different DNI
      const fetchDniData = async () => {
        setIsFetchingDni(true)
        try {
          const data = await apisNetAPI.getPersonaByDni(dni)
          if (data && data.nombres) {
            setFormData(prev => ({
              ...prev,
              fullName: `${data.nombres} ${data.apellidoPaterno} ${data.apellidoMaterno}`
            }))
          }
        } catch (error) {
          console.error('Error fetching DNI:', error)
        } finally {
          setIsFetchingDni(false)
        }
      }
      fetchDniData()
    }
  }

  useEffect(() => {
    // Set minimum date to tomorrow
    const tomorrow = new Date()
    tomorrow.setDate(tomorrow.getDate() + 1)
    const minDate = tomorrow.toISOString().split('T')[0]
    
    const dateInput = document.getElementById('travel-date')
    if (dateInput) {
      dateInput.min = minDate
    }
  }, [])

  const handleInputChange = (e) => {
    const { name, value, type, checked } = e.target
    setFormData(prev => ({
      ...prev,
      [name]: type === 'checkbox' ? checked : value
    }))
  }

  const handleServiceChange = (service) => {
    setFormData(prev => ({
      ...prev,
      services: {
        ...prev.services,
        [service]: !prev.services[service]
      }
    }))
  }

  const handleSubmit = (e) => {
    e.preventDefault()
    
    if (!selectedPackage) {
      alert('Por favor selecciona un paquete')
      return
    }

    const reservationData = {
      ...formData,
      package: selectedPackage,
      total: calculateTotal()
    }

    onReservationSubmit(reservationData)
  }

  const calculateTotal = () => {
    if (!selectedPackage) return 0

    const basePrice = selectedPackage.precio * formData.passengers
    const servicesPrice = calculateServicesPrice()
    
    return basePrice + servicesPrice
  }

  const calculateServicesPrice = () => {
    const prices = {
      hotelPickup: 20,
      lunchIncluded: 35,
      guideEnglish: 50,
      insurance: 15
    }

    return Object.entries(formData.services).reduce((total, [service, isSelected]) => {
      return total + (isSelected ? prices[service] * formData.passengers : 0)
    }, 0)
  }

  return (
    <>
      <h3 className="mb-4">
        <i className="fas fa-clipboard-list text-primary-custom me-2"></i>
        Datos de Reserva
      </h3>
      
      <form onSubmit={handleSubmit}>
        {/* Package Selection */}
        <div className="mb-4">
          <label htmlFor="package-select" className="form-label fw-bold">
            Seleccionar Paquete *
          </label>
          <select 
            className="form-select form-control-custom" 
            id="package-select"
            value={selectedPackage?.id || ''}
            onChange={(e) => {
              const pkg = packages.find(p => p.id === parseInt(e.target.value))
              onPackageChange(pkg)
            }}
            required
          >
            <option value="">Selecciona un paquete...</option>
            {packages
              .filter(pkg => pkg.cupoDisponible > 0)
              .map(pkg => (
                <option key={pkg.id} value={pkg.id}>
                  {pkg.nombrePaquete} - S/ {pkg.precio}
                </option>
              ))
            }
          </select>
        </div>

        {/* Personal Information */}
        <div className="row">
          <div className="col-md-6 mb-3">
            <label htmlFor="full-name" className="form-label fw-bold">
              Nombre Completo *
            </label>
            <input 
              type="text" 
              className="form-control form-control-custom" 
              id="full-name"
              name="fullName"
              value={formData.fullName}
              onChange={handleInputChange}
              readOnly={!!formData.fullName}
              required 
            />
          </div>
          <div className="col-md-6 mb-3">
            <label htmlFor="email" className="form-label fw-bold">
              Email *
            </label>
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
        </div>

        <div className="row">
          <div className="col-md-6 mb-3">
            <label htmlFor="phone" className="form-label fw-bold">
              Teléfono *
            </label>
            <input 
              type="tel" 
              className="form-control form-control-custom" 
              id="phone"
              name="phone"
              value={formData.phone}
              onChange={handleInputChange}
              required 
            />
          </div>
          <div className="col-md-6 mb-3">
            <label htmlFor="document" className="form-label fw-bold">
              DNI/Documento
            </label>
            <input 
              type="text" 
              className="form-control form-control-custom" 
              id="document"
              name="document"
              value={formData.document}
              onChange={handleInputChange}
              onBlur={handleDocumentBlur}
              disabled={isFetchingDni}
            />
            {isFetchingDni && <small className="text-muted"><i className="fas fa-spinner fa-spin me-1"></i> Consultando RENIEC...</small>}
          </div>
        </div>

        {/* Trip Details */}
        <div className="row">
          <div className="col-md-6 mb-3">
            <label htmlFor="travel-date" className="form-label fw-bold">
              Fecha de Viaje *
            </label>
            <input 
              type="date" 
              className="form-control form-control-custom" 
              id="travel-date"
              name="travelDate"
              value={formData.travelDate}
              onChange={handleInputChange}
              required 
            />
          </div>
          <div className="col-md-6 mb-3">
            <label htmlFor="passengers" className="form-label fw-bold">
              Número de Personas *
            </label>
            <select 
              className="form-select form-control-custom" 
              id="passengers"
              name="passengers"
              value={formData.passengers}
              onChange={handleInputChange}
              required
            >
              <option value="1">1 persona</option>
              <option value="2">2 personas</option>
              <option value="3">3 personas</option>
              <option value="4">4 personas</option>
              <option value="5">5 personas</option>
              <option value="6">6 personas</option>
              <option value="7">7 personas</option>
              <option value="8">8 personas</option>
            </select>
          </div>
        </div>

        {/* Additional Services */}
        <div className="mb-4">
          <label className="form-label fw-bold">Servicios Adicionales</label>
          <div className="row">
            <div className="col-md-6">
              <div className="form-check">
                <input 
                  className="form-check-input" 
                  type="checkbox" 
                  id="hotel-pickup"
                  checked={formData.services.hotelPickup}
                  onChange={() => handleServiceChange('hotelPickup')}
                />
                <label className="form-check-label" htmlFor="hotel-pickup">
                  Recojo en hotel (+S/20)
                </label>
              </div>
              <div className="form-check">
                <input 
                  className="form-check-input" 
                  type="checkbox" 
                  id="lunch-included"
                  checked={formData.services.lunchIncluded}
                  onChange={() => handleServiceChange('lunchIncluded')}
                />
                <label className="form-check-label" htmlFor="lunch-included">
                  Almuerzo incluido (+S/35)
                </label>
              </div>
            </div>
            <div className="col-md-6">
              <div className="form-check">
                <input 
                  className="form-check-input" 
                  type="checkbox" 
                  id="guide-english"
                  checked={formData.services.guideEnglish}
                  onChange={() => handleServiceChange('guideEnglish')}
                />
                <label className="form-check-label" htmlFor="guide-english">
                  Guía en inglés (+S/50)
                </label>
              </div>
              <div className="form-check">
                <input 
                  className="form-check-input" 
                  type="checkbox" 
                  id="insurance"
                  checked={formData.services.insurance}
                  onChange={() => handleServiceChange('insurance')}
                />
                <label className="form-check-label" htmlFor="insurance">
                  Seguro de viaje (+S/15)
                </label>
              </div>
            </div>
          </div>
        </div>

        {/* Special Requests */}
        <div className="mb-4">
          <label htmlFor="special-requests" className="form-label fw-bold">
            Solicitudes Especiales
          </label>
          <textarea 
            className="form-control form-control-custom" 
            id="special-requests" 
            rows="3"
            name="specialRequests"
            value={formData.specialRequests}
            onChange={handleInputChange}
            placeholder="Alergias alimentarias, necesidades especiales, etc."
          ></textarea>
        </div>

        {/* Submit Button */}
        <div className="text-center">
          <button type="submit" className="btn btn-primary-custom btn-lg px-5">
            <i className="fas fa-check-circle me-2"></i>Confirmar Reserva
          </button>
        </div>
      </form>
    </>
  )
}

export default ReservationForm