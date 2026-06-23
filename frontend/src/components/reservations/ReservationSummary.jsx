import { formatCurrency } from '../../utils/formatters'

function ReservationSummary({ selectedPackage }) {
  if (!selectedPackage) {
    return (
      <div className="bg-light p-4 rounded-3 sticky-top" style={{ top: '100px' }}>
        <h4 className="mb-4">
          <i className="fas fa-receipt text-primary-custom me-2"></i>
          Resumen
        </h4>
        <div className="text-center text-muted py-4">
          <i className="fas fa-clipboard-list fa-2x mb-3"></i>
          <p>Selecciona un paquete para ver el resumen</p>
        </div>
      </div>
    )
  }

  return (
    <div className="bg-light p-4 rounded-3 sticky-top" style={{ top: '100px' }}>
      <h4 className="mb-4">
        <i className="fas fa-receipt text-primary-custom me-2"></i>
        Resumen
      </h4>
      
      <div className="mb-3">
        <img 
          src={'/images/placeholder.jpg'} 
          className="img-fluid rounded mb-2" 
          alt={selectedPackage.nombrePaquete}
          onError={(e) => {
            e.target.src = '/images/placeholder.jpg'
          }}
        />
        <h6 className="mb-2">{selectedPackage.nombrePaquete}</h6>
        <small className="text-muted">
          <i className="fas fa-clock me-1"></i>{selectedPackage.duracion}
        </small>
      </div>

      <div className="mb-3">
        <strong>Precio por persona:</strong>
        <div className="h5 text-primary-custom mt-1">
          {formatCurrency(selectedPackage.precio)}
        </div>
      </div>

      <div className="mb-3">
        <strong>Disponibilidad:</strong>
        <div className="mt-1">
          <span className={`badge ${selectedPackage.cupoDisponible > 3 ? 'bg-success' : 'bg-warning'}`}>
            {selectedPackage.cupoDisponible} cupos disponibles
          </span>
        </div>
      </div>

      <div className="mb-3">
        <strong>Incluye:</strong>
        <ul className="list-unstyled mt-2 small">
          {selectedPackage.incluye.map((item, index) => (
            <li key={index}>
              <i className="fas fa-check me-2 text-success"></i>
              {item}
            </li>
          ))}
        </ul>
      </div>

      {/* Contact Info Actualizada */}
      <div className="mt-4 p-3 bg-white rounded">
        <h6><i className="fas fa-phone text-primary-custom me-2"></i>¿Necesitas ayuda?</h6>
        <p className="mb-2 small">
          <strong>Teléfonos:</strong><br />
          956 623396<br />
          948 401267<br />
          947929246
        </p>
        <p className="mb-2 small">
          <strong>Email:</strong><br />
          reservas@buganvillatours.com.pe
        </p>
        <p className="mb-0 small">
          <strong>WhatsApp:</strong><br />
          <a 
            href="https://api.whatsapp.com/send?phone=51922571344&text=Hello!%20I%20am%20interested%20in%20knowing%20more%20information%20about%20your%20programs" 
            target="_blank" 
            rel="noopener noreferrer"
            className="text-primary-custom"
          >
            +51 922 571 344
          </a>
        </p>
      </div>
    </div>
  )
}

export default ReservationSummary