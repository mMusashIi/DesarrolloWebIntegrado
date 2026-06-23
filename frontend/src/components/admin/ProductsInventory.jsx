function ProductsInventory() {
  const products = [
    { id: 1, nombre: 'Habitación Standard', categoria: 'Hospedaje', stock: 15, disponible: 8, estado: 'Activo' },
    { id: 2, nombre: 'Habitación Suite', categoria: 'Hospedaje', stock: 5, disponible: 2, estado: 'Activo' },
    { id: 3, nombre: 'Almuerzo Buffet', categoria: 'Alimentación', stock: 50, disponible: 50, estado: 'Activo' },
    { id: 4, nombre: 'Cena Temática', categoria: 'Alimentación', stock: 30, disponible: 25, estado: 'Activo' },
    { id: 5, nombre: 'Equipo Sandboard', categoria: 'Equipos', stock: 20, disponible: 12, estado: 'Mantenimiento' },
    { id: 6, nombre: 'Binoculares', categoria: 'Equipos', stock: 15, disponible: 15, estado: 'Activo' }
  ]

  const handleEditProduct = (id) => {
    alert(`Editar producto: ${id}`)
  }

  const handleAddProduct = () => {
    alert('Agregar nuevo producto')
  }

  return (
    <div id="products-inventory-section">
      <div className="d-flex justify-content-between align-items-center mb-4">
        <div>
          <h4 className="mb-1">Inventario de Productos</h4>
          <p className="text-muted mb-0">Gestiona recursos adicionales y servicios</p>
        </div>
        <button className="btn btn-primary-custom" onClick={handleAddProduct}>
          <i className="fas fa-plus me-2"></i>Nuevo Producto
        </button>
      </div>

      <div className="card shadow">
        <div className="card-header bg-primary text-white">
          <h5 className="mb-0">
            <i className="fas fa-warehouse me-2"></i>
            Productos y Recursos
          </h5>
        </div>
        <div className="card-body">
          <div className="table-responsive">
            <table className="table table-striped">
              <thead>
                <tr>
                  <th>Producto</th>
                  <th>Categoría</th>
                  <th>Stock Total</th>
                  <th>Disponible</th>
                  <th>Estado</th>
                  <th>Acciones</th>
                </tr>
              </thead>
              <tbody>
                {products.map(product => (
                  <tr key={product.id}>
                    <td>
                      <strong>{product.nombre}</strong>
                    </td>
                    <td>
                      <span className={`badge ${
                        product.categoria === 'Hospedaje' ? 'bg-primary' :
                        product.categoria === 'Alimentación' ? 'bg-success' : 'bg-warning'
                      }`}>
                        {product.categoria}
                      </span>
                    </td>
                    <td>{product.stock}</td>
                    <td>
                      <div className="d-flex align-items-center">
                        <div className="progress flex-grow-1 me-2" style={{ height: '6px' }}>
                          <div 
                            className={`progress-bar ${
                              (product.disponible / product.stock) > 0.7 ? 'bg-success' : 
                              (product.disponible / product.stock) > 0.3 ? 'bg-warning' : 'bg-danger'
                            }`}
                            style={{ width: `${(product.disponible / product.stock) * 100}%` }}
                          ></div>
                        </div>
                        <small>{product.disponible}</small>
                      </div>
                    </td>
                    <td>
                      <span className={`badge ${
                        product.estado === 'Activo' ? 'bg-success' : 'bg-warning'
                      }`}>
                        {product.estado}
                      </span>
                    </td>
                    <td>
                      <button 
                        className="btn btn-sm btn-outline-primary me-1"
                        onClick={() => handleEditProduct(product.id)}
                      >
                        <i className="fas fa-edit"></i>
                      </button>
                      <button className="btn btn-sm btn-outline-info">
                        <i className="fas fa-chart-bar"></i>
                      </button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      </div>

      {/* Resumen por categorías */}
      <div className="row mt-4">
        <div className="col-md-4">
          <div className="card text-center">
            <div className="card-body">
              <i className="fas fa-hotel fa-2x text-primary mb-3"></i>
              <h5>Hospedaje</h5>
              <p className="text-muted">20 habitaciones disponibles</p>
              <small className="text-success">85% ocupación</small>
            </div>
          </div>
        </div>
        <div className="col-md-4">
          <div className="card text-center">
            <div className="card-body">
              <i className="fas fa-utensils fa-2x text-success mb-3"></i>
              <h5>Alimentación</h5>
              <p className="text-muted">80 servicios diarios</p>
              <small className="text-warning">Capacidad media</small>
            </div>
          </div>
        </div>
        <div className="col-md-4">
          <div className="card text-center">
            <div className="card-body">
              <i className="fas fa-tools fa-2x text-warning mb-3"></i>
              <h5>Equipos</h5>
              <p className="text-muted">35 equipos activos</p>
              <small className="text-info">3 en mantenimiento</small>
            </div>
          </div>
        </div>
      </div>
    </div>
  )
}

export default ProductsInventory