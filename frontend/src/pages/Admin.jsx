import { useState } from 'react'
import { useAuth } from '../context/AuthContext'
import AdminSidebar from '../components/admin/AdminSidebar'
import Dashboard from '../components/admin/Dashboard'
import ReservationsManagement from '../components/admin/ReservationsManagement'
import PackagesInventory from '../components/admin/PackagesInventory'
import ProductsInventory from '../components/admin/ProductsInventory'
import Reports from '../components/admin/Reports'
import Transports from '../components/admin/Transports'

function Admin() {
  const { user, logout } = useAuth()
  const [activeSection, setActiveSection] = useState('dashboard')

  // Verificar si el usuario es administrador (ignorando mayúsculas/minúsculas)
  if (!user || user.rol.toLowerCase() !== 'admin') {
    return (
      <div className="container-fluid">
        <div className="row justify-content-center align-items-center min-vh-100">
          <div className="col-md-6 text-center">
            <div className="alert alert-danger">
              <i className="fas fa-exclamation-triangle fa-2x mb-3"></i>
              <h4>Acceso Denegado</h4>
              <p>No tienes permisos para acceder al panel de administración.</p>
              <a href="/" className="btn btn-primary-custom">
                Volver al Inicio
              </a>
            </div>
          </div>
        </div>
      </div>
    )
  }

  const renderSection = () => {
    switch (activeSection) {
      case 'dashboard':
        return <Dashboard />
      case 'reservations':
        return <ReservationsManagement />
      case 'packages-inventory':
        return <PackagesInventory />
      case 'products-inventory':
        return <ProductsInventory />
      case 'reports':
        return <Reports />
      case 'transports':
        return <Transports />
      default:
        return <Dashboard />
    }
  }

  const handleLogout = () => {
    if (confirm('¿Estás seguro de que quieres cerrar sesión?')) {
      logout()
      window.location.href = '/' // Redirigir a la página principal
    }
  }

  return (
    <div className="container-fluid p-0">
      <div className="row g-0">
        <AdminSidebar 
          activeSection={activeSection}
          onSectionChange={setActiveSection}
          onLogout={handleLogout}
          user={user}
        />
        
        <main className="col-md-9 ms-sm-auto col-lg-10 px-md-4" style={{ backgroundColor: '#f8f9fa', minHeight: '100vh' }}>
          {/* Header del Admin */}
          <div className="d-flex justify-content-between flex-wrap flex-md-nowrap align-items-center pt-4 pb-3 mb-4 border-bottom bg-white px-4 rounded shadow-sm">
            <div>
              <h1 className="h3 mb-1 fw-bold">
                {activeSection === 'dashboard' && 'Dashboard'}
                {activeSection === 'reservations' && 'Gestión de Reservas'}
                {activeSection === 'packages-inventory' && 'Inventario de Paquetes'}
                {activeSection === 'products-inventory' && 'Inventario de Productos'}
                {activeSection === 'reports' && 'Reportes y Análisis'}
                {activeSection === 'transports' && 'Gestión de Transportes'}
              </h1>
              <p className="text-muted mb-0">
                <i className="fas fa-home me-1"></i>
                Panel de Administración
              </p>
            </div>
            <div className="btn-toolbar mb-2 mb-md-0">
              <div className="btn-group me-2">
                <span className="badge bg-primary p-2">
                  <i className="fas fa-user me-1"></i>
                  {user.nombre}
                </span>
              </div>
              <div className="btn-group">
                <span className="badge bg-success p-2">
                  <i className="fas fa-circle me-1"></i>
                  En línea
                </span>
              </div>
            </div>
          </div>

          {/* Content Section */}
          <div className="container-fluid py-3">
            {renderSection()}
          </div>

          {/* Footer del Admin */}
          <footer className="mt-5 pt-4 border-top bg-white">
            <div className="container-fluid">
              <div className="row">
                <div className="col-12 text-center">
                  <p className="text-muted small">
                    <i className="fas fa-shield-alt me-1"></i>
                    Sistema de Administración Buganvilla Tours v1.0
                    <span className="mx-2">•</span>
                    {new Date().getFullYear()}
                  </p>
                </div>
              </div>
            </div>
          </footer>
        </main>
      </div>
    </div>
  )
}

export default Admin