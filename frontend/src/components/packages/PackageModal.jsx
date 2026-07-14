import { Link } from 'react-router-dom'
import { formatCurrency } from '../../utils/formatters'

function PackageModal({ package: pkg, show, onHide }) {
  if (!pkg || !show) return null

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

  return (
    <div 
      className={`modal fade ${show ? 'show' : ''}`} 
      style={{ display: show ? 'block' : 'none', backgroundColor: 'rgba(0,0,0,0.5)' }}
      tabIndex="-1"
    >
      <div className="modal-dialog modal-lg">
        <div className="modal-content">
          <div className="modal-header">
            <h5 className="modal-title">{pkg.nombrePaquete}</h5>
            <button 
              type="button" 
              className="btn-close" 
              onClick={onHide}
            ></button>
          </div>
          <div className="modal-body">
            <div className="row">
              <div className="col-md-6">
                <img 
                  src={pkg.imagen} 
                  className="img-fluid rounded mb-3" 
                  alt={pkg.nombrePaquete}
                  onError={(e) => {
                    e.target.src = '/images/placeholder.jpg'
                  }}
                />
                <div className="d-flex gap-2 mb-3">
                  <span className="badge bg-primary">
                    {getCategoryLabel(pkg.categoria)}
                  </span>
                  <span className={`badge ${
                    pkg.cupoDisponible === 0 ? 'bg-danger' : 
                    pkg.cupoDisponible <= 3 ? 'bg-warning' : 'bg-success'
                  }`}>
                    {pkg.cupoDisponible === 0 ? 'Agotado' : 
                     pkg.cupoDisponible <= 3 ? 'Últimos cupos' : 'Disponible'}
                  </span>
                </div>
              </div>
              <div className="col-md-6">
                <h6><i className="fas fa-tag text-primary-custom me-2"></i>Precio</h6>
                <p className="fs-4 text-primary-custom fw-bold">
                  {formatCurrency(pkg.precio)} por persona
                </p>
                
                <h6><i className="fas fa-clock text-primary-custom me-2"></i>Duración</h6>
                <p>{pkg.duracion}</p>
                
                <h6><i className="fas fa-users text-primary-custom me-2"></i>Disponibilidad</h6>
                <p>{pkg.cupoDisponible} de {pkg.cupoTotal} cupos disponibles</p>
                <div className="progress mb-3" style={{ height: '8px' }}>
                  <div 
                    className={`progress-bar ${getProgressBarClass(pkg)}`}
                    style={{ width: `${(pkg.cupoDisponible / pkg.cupoTotal) * 100}%` }}
                  ></div>
                </div>
                
                <h6><i className="fas fa-list text-primary-custom me-2"></i>Incluye</h6>
                <ul className="list-unstyled">
                  {pkg.incluye.map((item, index) => (
                    <li key={index}>
                      <i className="fas fa-check text-success me-2"></i>
                      {item}
                    </li>
                  ))}
                </ul>
              </div>
            </div>
            <div className="row mt-3">
              <div className="col-12">
                <h6><i className="fas fa-info-circle text-primary-custom me-2"></i>Descripción Completa</h6>
                <p>{pkg.descripcion}</p>
                
                <div className="alert alert-info">
                  <i className="fas fa-info-circle me-2"></i>
                  <strong>Importante:</strong> Los precios pueden variar según la temporada. 
                  Consulta por descuentos grupales.
                </div>
              </div>
            </div>
          </div>
          <div className="modal-footer">
            <button 
              type="button" 
              className="btn btn-secondary" 
              onClick={onHide}
            >
              Cerrar
            </button>
            <Link 
              to={`/reservas?package=${pkg.id}`}
              className={`btn ${
                pkg.cupoDisponible === 0 ? 'btn-secondary disabled' : 'btn-primary-custom'
              }`}
              onClick={onHide}
            >
              <i className="fas fa-calendar-plus me-2"></i>
              {pkg.cupoDisponible === 0 ? 'Agotado' : 'Reservar Ahora'}
            </Link>
          </div>
        </div>
      </div>
    </div>
  )
}

export default PackageModal