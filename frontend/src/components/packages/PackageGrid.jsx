import { Link } from 'react-router-dom'
import { useAuth } from '../../context/AuthContext'
import { formatCurrency } from '../../utils/formatters'
import LoadingSpinner from '../common/LoadingSpinner'

function PackageGrid({ packages, loading, error, onPackageSelect, onAuthRequired }) {
  const { isAuthenticated } = useAuth()

  if (loading) {
    return <LoadingSpinner />
  }

  if (error) {
    return (
      <div className="col-12">
        <div className="alert alert-error-custom">
          <i className="fas fa-exclamation-triangle me-2"></i>
          {error}
        </div>
      </div>
    )
  }

  if (packages.length === 0) {
    return (
      <div className="col-12 text-center py-5">
        <i className="fas fa-search fa-3x text-muted mb-3"></i>
        <h4>No se encontraron paquetes</h4>
        <p>Intenta con otros términos de búsqueda o filtros</p>
      </div>
    )
  }

  const getPackageBadge = (pkg) => {
    if (pkg.cupoDisponible === 0) {
      return <div className="package-badge bg-danger">Agotado</div>
    } else if (pkg.cupoDisponible <= 3) {
      return <div className="package-badge bg-warning">Últimos cupos</div>
    } else if (pkg.categoria === 'aventura') {
      return <div className="package-badge bg-success">Aventura</div>
    }
    return null
  }

  const getCategoryLabel = (category) => {
    const labels = {
      'aventura': 'Aventura',
      'cultura': 'Cultura',
      'gastronomia': 'Gastronomía',
      'naturaleza': 'Naturaleza'
    }
    return labels[category] || 'General'
  }

  const getProgressBarClass = (pkg) => {
    const percentage = (pkg.cupoDisponible / pkg.cupoTotal) * 100
    if (percentage === 0) return 'bg-danger'
    if (percentage <= 25) return 'bg-warning'
    return 'bg-success'
  }

  const handleReserveClick = (e, pkg) => {
    if (!isAuthenticated) {
      e.preventDefault()
      if (onAuthRequired) {
        onAuthRequired()
      }
    }
  }

  return (
    <div className="row">
      {packages.map(pkg => (
        <div key={pkg.id} className="col-lg-4 col-md-6 mb-4">
          <div className="card card-custom package-card h-100">
            {getPackageBadge(pkg)}
            <img 
              src={pkg.imagen || '/images/placeholder.jpg'} 
              className="card-img-top" 
              alt={pkg.nombrePaquete}
              onError={(e) => {
                if (e.target.src !== window.location.origin + '/images/placeholder.jpg') {
                  e.target.src = '/images/placeholder.jpg'
                }
              }}
            />
            <div className="card-body d-flex flex-column">
              <div className="d-flex justify-content-between align-items-start mb-2">
                <h5 className="card-title">{pkg.nombrePaquete}</h5>
                <span className="badge bg-secondary">
                  {getCategoryLabel(pkg.categoria)}
                </span>
              </div>
              <p className="card-text">{pkg.descripcion}</p>
              
              <div className="mb-3">
                <small className="text-muted">
                  <i className="fas fa-clock me-1"></i>Duración: {pkg.duracion}
                </small>
              </div>
              
              <div className="mb-3">
                <small className="text-muted">
                  <i className="fas fa-users me-1"></i>
                  Disponible: {pkg.cupoDisponible}/{pkg.cupoTotal} cupos
                </small>
                <div className="progress mt-1" style={{ height: '4px' }}>
                  <div 
                    className={`progress-bar ${getProgressBarClass(pkg)}`}
                    style={{ width: `${(pkg.cupoDisponible / pkg.cupoTotal) * 100}%` }}
                  ></div>
                </div>
              </div>
              
              <div className="mt-auto">
                <div className="d-flex justify-content-between align-items-center mb-3">
                  <div>
                    <span className="price-tag">
                      {formatCurrency(pkg.precio)}
                    </span>
                    <small className="text-muted d-block">por persona</small>
                  </div>
                </div>
                
                <div className="d-grid gap-2">
                  <button 
                    className="btn btn-outline-primary"
                    onClick={() => onPackageSelect(pkg)}
                  >
                    <i className="fas fa-eye me-2"></i>Ver Detalles
                  </button>
                  <Link 
                    to={isAuthenticated ? `/reservas?package=${pkg.id}` : '#'}
                    className={`btn btn-primary-custom ${pkg.cupoDisponible === 0 || !isAuthenticated ? 'disabled' : ''}`}
                    onClick={(e) => handleReserveClick(e, pkg)}
                  >
                    <i className="fas fa-calendar-plus me-2"></i>
                    {pkg.cupoDisponible === 0 ? 'Agotado' : 'Reservar Ahora'}
                  </Link>
                </div>

                {/* Mensaje para usuarios no autenticados */}
                {!isAuthenticated && pkg.cupoDisponible > 0 && (
                  <div className="mt-2 text-center">
                    <small className="text-muted">
                      <i className="fas fa-info-circle me-1"></i>
                      Inicia sesión para reservar
                    </small>
                  </div>
                )}
              </div>
            </div>
          </div>
        </div>
      ))}
    </div>
  )
}

export default PackageGrid