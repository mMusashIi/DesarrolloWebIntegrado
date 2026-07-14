import { useState, useEffect } from 'react'

function Dashboard() {
  const [stats, setStats] = useState({
    todayReservations: 0,
    monthlyIncome: 0,
    occupancyRate: 0,
    activePackages: 0,
    totalClients: 0,
    pendingTasks: 0
  })

  useEffect(() => {
    // Simular carga de datos
    setTimeout(() => {
      setStats({
        todayReservations: Math.floor(Math.random() * 20) + 5,
        monthlyIncome: Math.floor(Math.random() * 50000) + 20000,
        occupancyRate: Math.floor(Math.random() * 30) + 70,
        activePackages: 6,
        totalClients: Math.floor(Math.random() * 500) + 1000,
        pendingTasks: Math.floor(Math.random() * 10) + 3
      })
    }, 1000)
  }, [])

  const quickStats = [
    {
      title: 'Reservas Hoy',
      value: stats.todayReservations,
      color: 'primary',
      icon: 'calendar-day',
      change: '+12%'
    },
    {
      title: 'Ingresos Mensuales',
      value: `S/ ${stats.monthlyIncome.toLocaleString()}`,
      color: 'success',
      icon: 'dollar-sign',
      change: '+8%'
    },
    {
      title: 'Tasa de Ocupación',
      value: `${stats.occupancyRate}%`,
      color: 'info',
      icon: 'chart-line',
      change: '+5%'
    },
    {
      title: 'Paquetes Activos',
      value: stats.activePackages,
      color: 'warning',
      icon: 'box',
      change: 'Estable'
    }
  ]

  const recentActivities = [
    { time: '10:30 AM', activity: 'Nueva reserva creada', user: 'María González', status: 'success' },
    { time: '09:45 AM', activity: 'Paquete actualizado', user: 'Administrador', status: 'info' },
    { time: '09:15 AM', activity: 'Reserva cancelada', user: 'Carlos Mendoza', status: 'warning' },
    { time: '08:30 AM', activity: 'Nuevo usuario registrado', user: 'Ana López', status: 'success' },
    { time: '08:00 AM', activity: 'Inventario actualizado', user: 'Administrador', status: 'info' }
  ]

  return (
    <div id="dashboard-section">
      {/* Welcome Section */}
      <div className="row mb-4">
        <div className="col-12">
          <div className="card bg-gradient-primary text-white shadow">
            <div className="card-body">
              <div className="row align-items-center">
                <div className="col-md-8">
                  <h4 className="card-title mb-2">
                    <i className="fas fa-tachometer-alt me-2"></i>
                    Bienvenido al Panel de Control
                  </h4>
                  <p className="card-text mb-0 opacity-75">
                    Aquí puedes gestionar todas las operaciones de Buganvilla Tours. 
                    Revisa las estadísticas y accede rápidamente a las funciones principales.
                  </p>
                </div>
                <div className="col-md-4 text-md-end">
                  <div className="bg-white bg-opacity-25 p-3 rounded">
                    <small className="d-block">Sistema Actualizado</small>
                    <strong>{new Date().toLocaleDateString('es-PE')}</strong>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      {/* Quick Stats */}
      <div className="row mb-4">
        {quickStats.map((stat, index) => (
          <div key={index} className="col-xl-3 col-md-6 mb-4">
            <div className={`card border-left-${stat.color} shadow h-100 py-2`}>
              <div className="card-body">
                <div className="row no-gutters align-items-center">
                  <div className="col mr-2">
                    <div className="text-xs font-weight-bold text-primary text-uppercase mb-1">
                      {stat.title}
                    </div>
                    <div className="h5 mb-0 font-weight-bold text-gray-800">
                      {stat.value}
                    </div>
                    <div className="mt-2 mb-0 text-muted text-xs">
                      <span className={`text-${stat.change.includes('+') ? 'success' : 'warning'} me-2`}>
                        <i className={`fas fa-${stat.change.includes('+') ? 'arrow-up' : 'minus'} me-1`}></i>
                        {stat.change}
                      </span>
                      Desde ayer
                    </div>
                  </div>
                  <div className="col-auto">
                    <i className={`fas fa-${stat.icon} fa-2x text-${stat.color}`}></i>
                  </div>
                </div>
              </div>
            </div>
          </div>
        ))}
      </div>

      <div className="row">
        {/* Recent Activities */}
        <div className="col-lg-8 mb-4">
          <div className="card shadow">
            <div className="card-header bg-white">
              <h5 className="mb-0">
                <i className="fas fa-history me-2"></i>
                Actividad Reciente
              </h5>
            </div>
            <div className="card-body">
              <div className="table-responsive">
                <table className="table table-borderless">
                  <thead>
                    <tr>
                      <th>Hora</th>
                      <th>Actividad</th>
                      <th>Usuario</th>
                      <th>Estado</th>
                    </tr>
                  </thead>
                  <tbody>
                    {recentActivities.map((activity, index) => (
                      <tr key={index}>
                        <td className="text-muted">{activity.time}</td>
                        <td>{activity.activity}</td>
                        <td>{activity.user}</td>
                        <td>
                          <span className={`badge bg-${activity.status}`}>
                            {activity.status === 'success' ? 'Completado' : 
                             activity.status === 'warning' ? 'Pendiente' : 'En Proceso'}
                          </span>
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            </div>
          </div>
        </div>

        {/* System Status */}
        <div className="col-lg-4 mb-4">
          <div className="card shadow">
            <div className="card-header bg-white">
              <h5 className="mb-0">
                <i className="fas fa-server me-2"></i>
                Estado del Sistema
              </h5>
            </div>
            <div className="card-body">
              <div className="mb-3">
                <div className="d-flex justify-content-between mb-1">
                  <span>Uso de CPU</span>
                  <span className="text-success">24%</span>
                </div>
                <div className="progress" style={{ height: '6px' }}>
                  <div className="progress-bar bg-success" style={{ width: '24%' }}></div>
                </div>
              </div>
              
              <div className="mb-3">
                <div className="d-flex justify-content-between mb-1">
                  <span>Uso de Memoria</span>
                  <span className="text-warning">58%</span>
                </div>
                <div className="progress" style={{ height: '6px' }}>
                  <div className="progress-bar bg-warning" style={{ width: '58%' }}></div>
                </div>
              </div>
              
              <div className="mb-3">
                <div className="d-flex justify-content-between mb-1">
                  <span>Almacenamiento</span>
                  <span className="text-info">42%</span>
                </div>
                <div className="progress" style={{ height: '6px' }}>
                  <div className="progress-bar bg-info" style={{ width: '42%' }}></div>
                </div>
              </div>

              <div className="alert alert-success small mb-0">
                <i className="fas fa-check-circle me-2"></i>
                Todos los sistemas funcionando correctamente
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  )
}

export default Dashboard