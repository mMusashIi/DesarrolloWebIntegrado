import { useState, useEffect } from 'react'
import { formatCurrency, formatDate } from '../../utils/formatters'

function ReservationsManagement() {
  const [reservations, setReservations] = useState([])

  useEffect(() => {
    // Mock data
    const mockReservations = [
      {
        id: 'BT001',
        cliente: 'María González',
        paquete: 'Líneas de Nazca',
        fecha: '2025-01-15',
        personas: 2,
        estado: 'Confirmada',
        total: 500
      },
      {
        id: 'BT002',
        cliente: 'Carlos Mendoza',
        paquete: 'Islas Ballestas',
        fecha: '2025-01-18',
        personas: 4,
        estado: 'Pendiente',
        total: 720
      }
    ]
    setReservations(mockReservations)
  }, [])

  const handleCancelReservation = (id) => {
    if (confirm(`¿Estás seguro de cancelar la reserva ${id}?`)) {
      setReservations(prev => prev.filter(res => res.id !== id))
      alert(`Reserva ${id} cancelada`)
    }
  }

  return (
    <div id="reservations-section">
      <div className="card shadow">
        <div className="card-body">
          <div className="table-responsive">
            <table className="table table-striped">
              <thead>
                <tr>
                  <th>ID</th>
                  <th>Cliente</th>
                  <th>Paquete</th>
                  <th>Fecha</th>
                  <th>Personas</th>
                  <th>Estado</th>
                  <th>Total</th>
                  <th>Acciones</th>
                </tr>
              </thead>
              <tbody>
                {reservations.map(reserva => (
                  <tr key={reserva.id}>
                    <td>{reserva.id}</td>
                    <td>{reserva.cliente}</td>
                    <td>{reserva.paquete}</td>
                    <td>{formatDate(reserva.fecha)}</td>
                    <td>{reserva.personas}</td>
                    <td>
                      <span className={`badge bg-${reserva.estado === 'Confirmada' ? 'success' : 'warning'}`}>
                        {reserva.estado}
                      </span>
                    </td>
                    <td>{formatCurrency(reserva.total)}</td>
                    <td>
                      <button 
                        className="btn btn-sm btn-primary me-1"
                        onClick={() => alert(`Editar reserva: ${reserva.id}`)}
                      >
                        <i className="fas fa-edit"></i>
                      </button>
                      <button 
                        className="btn btn-sm btn-danger"
                        onClick={() => handleCancelReservation(reserva.id)}
                      >
                        <i className="fas fa-times"></i>
                      </button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </div>
  )
}

export default ReservationsManagement