import { usePackages } from '../../hooks/usePackages'
import { formatCurrency } from '../../utils/formatters'

function PackagesManagement() {
  const { packages } = usePackages()

  const handleEditPackage = (id) => {
    alert(`Editar paquete: ${id}`)
  }

  const handleDeletePackage = (id) => {
    if (confirm('¿Estás seguro de eliminar este paquete?')) {
      alert(`Paquete ${id} eliminado`)
    }
  }

  return (
    <div id="packages-section">
      <div className="row" id="packages-grid-admin">
        {packages.map(pkg => (
          <div key={pkg.id} className="col-lg-4 col-md-6 mb-4">
            <div className="card h-100">
              <img 
                src={pkg.imagen} 
                className="card-img-top" 
                style={{ height: '200px', objectFit: 'cover' }} 
                alt={pkg.nombrePaquete}
                onError={(e) => {
                  e.target.src = '/images/placeholder.jpg'
                }}
              />
              <div className="card-body">
                <h6 className="card-title">{pkg.nombrePaquete}</h6>
                <p className="card-text small">
                  {pkg.descripcion.substring(0, 100)}...
                </p>
                <div className="row text-center">
                  <div className="col-4">
                    <small className="text-muted">Precio</small>
                    <div className="fw-bold">{formatCurrency(pkg.precio)}</div>
                  </div>
                  <div className="col-4">
                    <small className="text-muted">Disponible</small>
                    <div className={`fw-bold text-${pkg.cupoDisponible > 0 ? 'success' : 'danger'}`}>
                      {pkg.cupoDisponible}
                    </div>
                  </div>
                  <div className="col-4">
                    <small className="text-muted">Total</small>
                    <div className="fw-bold">{pkg.cupoTotal}</div>
                  </div>
                </div>
              </div>
              <div className="card-footer">
                <div className="btn-group w-100">
                  <button 
                    className="btn btn-outline-primary btn-sm"
                    onClick={() => handleEditPackage(pkg.id)}
                  >
                    <i className="fas fa-edit"></i> Editar
                  </button>
                  <button 
                    className="btn btn-outline-danger btn-sm"
                    onClick={() => handleDeletePackage(pkg.id)}
                  >
                    <i className="fas fa-trash"></i> Eliminar
                  </button>
                </div>
              </div>
            </div>
          </div>
        ))}
      </div>
    </div>
  )
}

export default PackagesManagement