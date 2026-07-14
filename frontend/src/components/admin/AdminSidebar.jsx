import React from 'react';
import { useNavigate } from 'react-router-dom';

function AdminSidebar({ activeSection, onSectionChange, onLogout, user }) {
  const navigate = useNavigate(); 
  
  const menuItems = [
    { id: 'dashboard', icon: 'tachometer-alt', label: 'Dashboard' },
    { id: 'reservations', icon: 'calendar-check', label: 'Reservas' },
    { id: 'packages-inventory', icon: 'box', label: 'Inventario De Paquetes' },
    { id: 'products-inventory', icon: 'warehouse', label: 'Inventario De Productos' },
    { id: 'reports', icon: 'chart-bar', label: 'Reportes' },
    { id: 'transports', icon: 'bus', label: 'Transportes' }
  ];

  const handleGoToHome = () => {
    navigate('/'); 
  };

  return (
    <nav className="col-md-3 col-lg-2 d-md-block admin-sidebar">
      <div className="position-sticky pt-3">
        <div className="text-center mb-4">
          <h5 className="text-white">
            <i className="fas fa-mountain me-2"></i>
            Buganvilla Admin
          </h5>
          <small className="text-light">Panel de Control</small>
        </div>
        
        {/* Información del usuario admin */}
        <div className="px-3 mb-4">
          <div className="text-center text-light">
            <i className="fas fa-user-shield fa-2x mb-2"></i>
            <h6 className="mb-1">{user?.nombre}</h6>
            <small className="text-light">{user?.email}</small>
            <div className="mt-2">
              <span className="badge bg-success">
                <i className="fas fa-shield-alt me-1"></i>
                Administrador
              </span>
            </div>
          </div>
        </div>

        <hr className="my-3 text-light" />
        
        {/* Navegación Principal */}
        <ul className="nav flex-column">
          {menuItems.map(item => (
            <li key={item.id} className="nav-item">
              <button
                className={`nav-link w-100 text-start text-light ${activeSection === item.id ? 'active bg-primary' : ''}`}
                onClick={() => onSectionChange(item.id)}
                style={{ transition: 'background 0.2s', borderRadius: '5px' }}
              >
                <i className={`fas fa-${item.icon} me-2`}></i>
                {item.label}
              </button>
            </li>
          ))}
        </ul>

        <hr className="my-4 text-light" />
        
        {/* Acciones de Utilidad */}
        <ul className="nav flex-column">
            {/* 🟢 BOTÓN FINAL: SIN BORDES Y ALINEADO A LA IZQUIERDA 🟢 */}
            <li className="nav-item mb-2">
                <button 
                    // Usamos home-link-admin para el hover externo
                    className="nav-link w-100 text-start text-light home-link-admin" 
                    onClick={handleGoToHome}
                    style={{ 
                        backgroundColor: 'transparent',
                        fontWeight: '600',
                        transition: 'all 0.2s',
                        borderRadius: '5px',
                        border: 'none',       // <-- ELIMINADO EL BORDE
                        boxShadow: 'none',    // <-- ELIMINADO CUALQUIER SOMBRA
                        paddingLeft: '1rem',  // Asegura la alineación
                        paddingRight: '1rem',
                    }}
                >
                    <i className="fas fa-home me-2"></i> Ir a la Web
                </button>
            </li>

          {/* Botón de Cerrar Sesión */}
          <li className="nav-item">
            <button className="nav-link w-100 text-start text-danger" onClick={onLogout}>
              <i className="fas fa-sign-out-alt me-2"></i>Cerrar Sesión
            </button>
          </li>
        </ul>

        {/* Información del sistema */}
        <div className="mt-4 px-3">
          <small className="text-light opacity-75">
            <i className="fas fa-info-circle me-1"></i>
            Sistema v1.0
          </small>
        </div>
      </div>
    </nav>
  );
}

export default AdminSidebar;