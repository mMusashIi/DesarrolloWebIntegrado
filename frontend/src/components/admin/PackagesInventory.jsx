import { useState, useEffect } from 'react'
import { packagesAPI } from '../../services/api'
import { formatCurrency } from '../../utils/formatters'
import PackageFormModal from './PackageFormModal'

function PackagesInventory() {
  const [packages, setPackages] = useState([])
  const [loading, setLoading] = useState(true)
  const [showModal, setShowModal] = useState(false)
  const [selectedPackageId, setSelectedPackageId] = useState(null)

  useEffect(() => {
    loadPackages()
  }, [])

  const loadPackages = async () => {
    try {
      setLoading(true)
      const response = await packagesAPI.getAllForAdmin()
      if (response.success && response.data) {
        const mappedPackages = response.data.map(pkg => ({
          id: pkg.idPaquete,
          nombrePaquete: pkg.nombrePaquete,
          descripcion: pkg.descripcion || '',
          precio: pkg.precioBase ? parseFloat(pkg.precioBase) : 0,
          duracion: pkg.duracionDias ? `${pkg.duracionDias} ${pkg.duracionDias === 1 ? 'día' : 'días'}` : 'No especificado',
          categoria: 'aventura',
          imagen: `/images/${pkg.nombrePaquete.toLowerCase().replace(/\s+/g, '-')}.png`,
          cupoDisponible: 10,
          cupoTotal: 20,
          lugar: pkg.nombreLugar || '',
          ciudad: pkg.ciudadLugar || '',
          estado: pkg.estado || 'activo'
        }))
        setPackages(mappedPackages)
      }
    } catch (err) {
      console.error('Error al cargar paquetes:', err)
    } finally {
      setLoading(false)
    }
  }

  const handleEditPackage = (id) => {
    setSelectedPackageId(id)
    setShowModal(true)
  }

  const handleDeletePackage = async (id) => {
    if (!confirm('¿Estás seguro de eliminar este paquete? Esta acción no se puede deshacer.')) {
      return
    }

    try {
      const response = await packagesAPI.delete(id)
      if (response.success) {
        loadPackages()
      } else {
        alert(response.message || 'Error al eliminar el paquete')
      }
    } catch (err) {
      alert(err.response?.data?.message || 'Error al eliminar el paquete')
      console.error(err)
    }
  }

  const handleCreatePackage = () => {
    setSelectedPackageId(null)
    setShowModal(true)
  }

  const handleModalSuccess = () => {
    loadPackages()
  }

  if (loading) {
    return (
      <div className="text-center py-5">
        <div className="spinner-border text-primary" role="status">
          <span className="visually-hidden">Cargando...</span>
        </div>
      </div>
    )
  }

  return (
    <div id="packages-inventory-section">
      <div className="d-flex justify-content-between align-items-center mb-4">
        <div>
          <h4 className="mb-1">Inventario de Paquetes Turísticos</h4>
          <p className="text-muted mb-0">Gestiona todos los paquetes turísticos disponibles</p>
        </div>
        <button className="btn btn-primary-custom" onClick={handleCreatePackage}>
          <i className="fas fa-plus me-2"></i>Nuevo Paquete
        </button>
      </div>

      <div className="row">
        {packages.map(pkg => (
          <div key={pkg.id} className="col-lg-4 col-md-6 mb-4">
            <div className="card h-100 shadow-sm">
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
                <div className="d-flex justify-content-between align-items-start mb-2">
                  <h6 className="card-title mb-0">{pkg.nombrePaquete}</h6>
                  <span className={`badge ${pkg.estado === 'activo' ? 'bg-success' : 'bg-secondary'}`}>
                    {pkg.estado}
                  </span>
                </div>
                <p className="card-text small text-muted">
                  {pkg.descripcion.substring(0, 100)}...
                </p>
                <div className="row text-center mb-3">
                  <div className="col-4">
                    <small className="text-muted d-block">Precio</small>
                    <div className="fw-bold text-primary">{formatCurrency(pkg.precio)}</div>
                  </div>
                  <div className="col-4">
                    <small className="text-muted d-block">Disponible</small>
                    <div className={`fw-bold ${pkg.cupoDisponible > 0 ? 'text-success' : 'text-danger'}`}>
                      {pkg.cupoDisponible}
                    </div>
                  </div>
                  <div className="col-4">
                    <small className="text-muted d-block">Total</small>
                    <div className="fw-bold">{pkg.cupoTotal}</div>
                  </div>
                </div>
                <div className="mb-2">
                  <small className="text-muted">Duración:</small>
                  <span className="ms-2">{pkg.duracion}</span>
                </div>
                <div className="mb-3">
                  <small className="text-muted">Lugar:</small>
                  <span className="badge bg-secondary ms-2">{pkg.lugar}</span>
                </div>
              </div>
              <div className="card-footer bg-transparent">
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

      {packages.length === 0 && (
        <div className="text-center py-5">
          <i className="fas fa-box-open fa-3x text-muted mb-3"></i>
          <p className="text-muted">No hay paquetes registrados</p>
          <button className="btn btn-primary-custom" onClick={handleCreatePackage}>
            <i className="fas fa-plus me-2"></i>Crear Primer Paquete
          </button>
        </div>
      )}

      {/* Estadísticas rápidas */}
      <div className="row mt-4">
        <div className="col-md-3">
          <div className="card bg-primary text-white text-center">
            <div className="card-body py-3">
              <h5 className="mb-1">{packages.length}</h5>
              <small>Total Paquetes</small>
            </div>
          </div>
        </div>
        <div className="col-md-3">
          <div className="card bg-success text-white text-center">
            <div className="card-body py-3">
              <h5 className="mb-1">
                {packages.filter(p => p.estado === 'activo').length}
              </h5>
              <small>Activos</small>
            </div>
          </div>
        </div>
        <div className="col-md-3">
          <div className="card bg-warning text-white text-center">
            <div className="card-body py-3">
              <h5 className="mb-1">
                {packages.filter(p => p.estado === 'inactivo').length}
              </h5>
              <small>Inactivos</small>
            </div>
          </div>
        </div>
        <div className="col-md-3">
          <div className="card bg-info text-white text-center">
            <div className="card-body py-3">
              <h5 className="mb-1">
                {packages.filter(p => p.cupoDisponible <= 3 && p.cupoDisponible > 0).length}
              </h5>
              <small>Últimos Cupos</small>
            </div>
          </div>
        </div>
      </div>

      <PackageFormModal
        show={showModal}
        onHide={() => {
          setShowModal(false)
          setSelectedPackageId(null)
        }}
        packageId={selectedPackageId}
        onSuccess={handleModalSuccess}
      />
    </div>
  )
}

export default PackagesInventory