function Transports() {
  const transports = [
    { id: 1, tipo: 'Bus Turístico', capacidad: 40, disponible: 32, conductor: 'Juan Pérez', estado: 'Disponible' },
    { id: 2, tipo: 'Van Ejecutiva', capacidad: 12, disponible: 8, conductor: 'María García', estado: 'En Mantenimiento' },
    { id: 3, tipo: 'Auto 4x4', capacidad: 4, disponible: 3, conductor: 'Carlos López', estado: 'Disponible' },
    { id: 4, tipo: 'Minibús', capacidad: 20, disponible: 15, conductor: 'Roberto Silva', estado: 'Disponible' }
  ]

  const drivers = [
    { id: 1, nombre: 'Juan Pérez', licencia: 'A-IIIb', telefono: '956 623396', estado: 'Activo', experiencia: '8 años' },
    { id: 2, nombre: 'María García', licencia: 'A-IIIb', telefono: '948 401267', estado: 'Activo', experiencia: '5 años' },
    { id: 3, nombre: 'Carlos López', licencia: 'A-IIa', telefono: '947929246', estado: 'Vacaciones', experiencia: '3 años' },
    { id: 4, nombre: 'Roberto Silva', licencia: 'A-IIIb', telefono: '956 784512', estado: 'Activo', experiencia: '10 años' }
  ]

  const handleAddDriver = () => {
    alert('Agregar nuevo conductor')
  }

  const handleAddVehicle = () => {
    alert('Agregar nuevo vehículo')
  }

  return (
    <div id="transports-section">
      <div className="d-flex justify-content-between align-items-center mb-4">
        <div>
          <h4 className="mb-1">Gestión de Transportes</h4>
          <p className="text-muted mb-0">Control de aforo, vehículos y conductores</p>
        </div>
        <div className="btn-group">
          <button className="btn btn-primary-custom" onClick={handleAddVehicle}>
            <i className="fas fa-bus me-2"></i>Nuevo Vehículo
          </button>
          <button className="btn btn-success" onClick={handleAddDriver}>
            <i className="fas fa-user-plus me-2"></i>Nuevo Conductor
          </button>
        </div>
      </div>

      {/* Vehículos */}
      <div className="card shadow mb-4">
        <div className="card-header bg-primary text-white">
          <h5 className="mb-0">
            <i className="fas fa-bus me-2"></i>
            Flota de Vehículos - Aforo y Disponibilidad
          </h5>
        </div>
        <div className="card-body">
          <div className="table-responsive">
            <table className="table table-striped">
              <thead>
                <tr>
                  <th>Vehículo</th>
                  <th>Capacidad</th>
                  <th>Disponible</th>
                  <th>Ocupación</th>
                  <th>Conductor</th>
                  <th>Estado</th>
                  <th>Acciones</th>
                </tr>
              </thead>
              <tbody>
                {transports.map(transport => {
                  const ocupacion = (transport.disponible / transport.capacidad) * 100
                  return (
                    <tr key={transport.id}>
                      <td>
                        <strong>{transport.tipo}</strong>
                      </td>
                      <td>{transport.capacidad} personas</td>
                      <td>{transport.disponible}</td>
                      <td>
                        <div className="d-flex align-items-center">
                          <div className="progress flex-grow-1 me-2" style={{ height: '8px' }}>
                            <div 
                              className={`progress-bar ${
                                ocupacion > 70 ? 'bg-success' : 
                                ocupacion > 30 ? 'bg-warning' : 'bg-danger'
                              }`}
                              style={{ width: `${ocupacion}%` }}
                            ></div>
                          </div>
                          <small>{Math.round(ocupacion)}%</small>
                        </div>
                      </td>
                      <td>{transport.conductor}</td>
                      <td>
                        <span className={`badge ${
                          transport.estado === 'Disponible' ? 'bg-success' : 'bg-warning'
                        }`}>
                          {transport.estado}
                        </span>
                      </td>
                      <td>
                        <button className="btn btn-sm btn-outline-primary me-1">
                          <i className="fas fa-edit"></i>
                        </button>
                        <button className="btn btn-sm btn-outline-info">
                          <i className="fas fa-info-circle"></i>
                        </button>
                      </td>
                    </tr>
                  )
                })}
              </tbody>
            </table>
          </div>
        </div>
      </div>

      {/* Conductores */}
      <div className="card shadow">
        <div className="card-header bg-success text-white">
          <h5 className="mb-0">
            <i className="fas fa-user-tie me-2"></i>
            Conductores
          </h5>
        </div>
        <div className="card-body">
          <div className="table-responsive">
            <table className="table table-striped">
              <thead>
                <tr>
                  <th>Nombre</th>
                  <th>Licencia</th>
                  <th>Teléfono</th>
                  <th>Experiencia</th>
                  <th>Estado</th>
                  <th>Acciones</th>
                </tr>
              </thead>
              <tbody>
                {drivers.map(driver => (
                  <tr key={driver.id}>
                    <td>
                      <strong>{driver.nombre}</strong>
                    </td>
                    <td>
                      <span className="badge bg-info">{driver.licencia}</span>
                    </td>
                    <td>{driver.telefono}</td>
                    <td>{driver.experiencia}</td>
                    <td>
                      <span className={`badge ${
                        driver.estado === 'Activo' ? 'bg-success' : 'bg-info'
                      }`}>
                        {driver.estado}
                      </span>
                    </td>
                    <td>
                      <button className="btn btn-sm btn-outline-primary me-1">
                        <i className="fas fa-edit"></i>
                      </button>
                      <button className="btn btn-sm btn-outline-success me-1">
                        <i className="fas fa-phone"></i>
                      </button>
                      <button className="btn btn-sm btn-outline-warning">
                        <i className="fas fa-calendar"></i>
                      </button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>

          <div className="row mt-4">
            <div className="col-md-3">
              <div className="card text-center bg-light">
                <div className="card-body py-3">
                  <h5 className="text-primary mb-1">{drivers.filter(d => d.estado === 'Activo').length}</h5>
                  <small>Conductores Activos</small>
                </div>
              </div>
            </div>
            <div className="col-md-3">
              <div className="card text-center bg-light">
                <div className="card-body py-3">
                  <h5 className="text-success mb-1">{transports.filter(t => t.estado === 'Disponible').length}</h5>
                  <small>Vehículos Disponibles</small>
                </div>
              </div>
            </div>
            <div className="col-md-3">
              <div className="card text-center bg-light">
                <div className="card-body py-3">
                  <h5 className="text-warning mb-1">
                    {transports.reduce((total, t) => total + t.capacidad, 0)}
                  </h5>
                  <small>Capacidad Total</small>
                </div>
              </div>
            </div>
            <div className="col-md-3">
              <div className="card text-center bg-light">
                <div className="card-body py-3">
                  <h5 className="text-info mb-1">
                    {transports.reduce((total, t) => total + t.disponible, 0)}
                  </h5>
                  <small>Disponibilidad Actual</small>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  )
}

export default Transports