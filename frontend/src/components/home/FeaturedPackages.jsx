import { useState } from 'react'
import { Link } from 'react-router-dom'
import { useAuth } from '../../context/AuthContext'
import { usePackages } from '../../hooks/usePackages'
import { formatCurrency } from '../../utils/formatters'
import PackageModal from '../packages/PackageModal'
import AuthRequiredModal from '../auth/AuthRequiredModal'
import LoginModal from '../auth/LoginModal'
import RegisterModal from '../auth/RegisterModal'

function FeaturedPackages() {
  const { packages, loading } = usePackages()
  const { isAuthenticated } = useAuth()
  const [selectedPackage, setSelectedPackage] = useState(null)
  const [showPackageModal, setShowPackageModal] = useState(false)
  const [showAuthModal, setShowAuthModal] = useState(false)
  const [showLoginModal, setShowLoginModal] = useState(false)
  const [showRegisterModal, setShowRegisterModal] = useState(false)

  const featuredPackages = packages.slice(0, 3)

  const handlePackageSelect = (pkg) => {
    setSelectedPackage(pkg)
    setShowPackageModal(true)
  }

  const handleClosePackageModal = () => {
    setShowPackageModal(false)
    setSelectedPackage(null)
  }

  const handleReserveClick = (e, pkgId) => {
    if (!isAuthenticated) {
      e.preventDefault()
      setShowAuthModal(true)
    }
  }

  const handleAuthRequired = () => {
    setShowAuthModal(true)
  }

  const handleShowLogin = () => {
    setShowAuthModal(false)
    setShowLoginModal(true)
  }

  const handleShowRegister = () => {
    setShowAuthModal(false)
    setShowRegisterModal(true)
  }

  const handleSwitchToRegister = () => {
    setShowLoginModal(false)
    setShowRegisterModal(true)
  }

  const handleSwitchToLogin = () => {
    setShowRegisterModal(false)
    setShowLoginModal(true)
  }

  if (loading) {
    return (
      <section id="packages" className="section-padding">
        <div className="container">
          <div className="text-center">
            <div className="loading-spinner"></div>
            <p className="mt-3">Cargando paquetes...</p>
          </div>
        </div>
      </section>
    )
  }

  return (
    <>
      <section id="packages" className="section-padding">
        <div className="container">
          <div className="text-center mb-5">
            <h2 className="section-title">Paquetes Destacados</h2>
            <p className="section-subtitle">Descubre nuestras experiencias más populares</p>
          </div>
          <div className="row">
            {featuredPackages.map(pkg => (
              <div key={pkg.id} className="col-lg-4 col-md-6 mb-4">
                <div className="card card-custom package-card h-100">
                  {pkg.cupoDisponible === 0 ? (
                    <div className="package-badge bg-danger">Agotado</div>
                  ) : pkg.cupoDisponible <= 3 ? (
                    <div className="package-badge">Últimos cupos</div>
                  ) : null}
                  
                  <img 
                    src={pkg.imagen} 
                    className="card-img-top" 
                    alt={pkg.nombrePaquete}
                    onError={(e) => {
                      e.target.src = '/images/placeholder.jpg'
                    }}
                  />
                  <div className="card-body d-flex flex-column">
                    <h5 className="card-title">{pkg.nombrePaquete}</h5>
                    <p className="card-text">
                      {pkg.descripcion.substring(0, 100)}...
                    </p>
                    <div className="mt-auto">
                      <div className="d-flex justify-content-between align-items-center mb-3">
                        <span className="price-tag">
                          {formatCurrency(pkg.precio)}
                        </span>
                        <small className="text-muted">
                          <i className="fas fa-clock me-1"></i>
                          {pkg.duracion}
                        </small>
                      </div>
                      <div className="d-grid gap-2">
                        <button 
                          className="btn btn-primary-custom"
                          onClick={() => handlePackageSelect(pkg)}
                        >
                          <i className="fas fa-eye me-2"></i>Ver Detalles
                        </button>
                        <Link 
                          to={isAuthenticated ? `/reservas?package=${pkg.id}` : '#'}
                          className={`btn btn-secondary-custom ${!isAuthenticated ? 'disabled' : ''}`}
                          onClick={(e) => handleReserveClick(e, pkg.id)}
                        >
                          <i className="fas fa-calendar-plus me-2"></i>
                          {pkg.cupoDisponible === 0 ? 'Agotado' : 'Reservar Ahora'}
                        </Link>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            ))}
          </div>
          <div className="text-center mt-5">
            <Link to="/paquetes" className="btn btn-primary-custom btn-lg">
              <i className="fas fa-eye me-2"></i>Ver Todos los Paquetes
            </Link>
          </div>
        </div>
      </section>

      {/* Package Modal */}
      {selectedPackage && (
        <PackageModal 
          package={selectedPackage}
          show={showPackageModal}
          onHide={handleClosePackageModal}
          onAuthRequired={handleAuthRequired}
        />
      )}

      {/* Auth Required Modal */}
      <AuthRequiredModal 
        show={showAuthModal}
        onHide={() => setShowAuthModal(false)}
        onShowLogin={handleShowLogin}
        onShowRegister={handleShowRegister}
      />

      {/* Login Modal */}
      <LoginModal 
        show={showLoginModal}
        onHide={() => setShowLoginModal(false)}
        onShowRegister={handleSwitchToRegister}
      />

      {/* Register Modal */}
      <RegisterModal 
        show={showRegisterModal}
        onHide={() => setShowRegisterModal(false)}
        onShowLogin={handleSwitchToLogin}
      />
    </>
  )
}

export default FeaturedPackages