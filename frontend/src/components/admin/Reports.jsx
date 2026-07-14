import { useState } from 'react'
import { reportsAPI } from '../../services/api'

function Reports() {
  const [format, setFormat] = useState('pdf')

  const downloadFile = (data, filename) => {
    const url = window.URL.createObjectURL(new Blob([data]));
    const link = document.createElement('a');
    link.href = url;
    link.setAttribute('download', filename);
    document.body.appendChild(link);
    link.click();
    link.remove();
  }

  const generatePDFReport = async () => {
    try {
      const data = await reportsAPI.getPDF();
      downloadFile(data, 'reporte_reservas.pdf');
    } catch (error) {
      console.error('Error al descargar PDF', error);
      alert('Error al generar el reporte PDF');
    }
  }

  const generateExcelReport = async () => {
    try {
      const data = await reportsAPI.getExcel();
      downloadFile(data, 'reporte_reservas.xlsx');
    } catch (error) {
      console.error('Error al descargar Excel', error);
      alert('Error al generar el reporte Excel');
    }
  }

  const handleCustomReport = () => {
    if (format === 'pdf') {
      generatePDFReport()
    } else {
      generateExcelReport()
    }
  }

  const stats = [
    { title: 'Reservas Totales', value: '156', color: 'primary', icon: 'calendar-check' },
    { title: 'Ingresos Mensuales', value: 'S/ 45,680', color: 'success', icon: 'dollar-sign' },
    { title: 'Tasa de Ocupación', value: '89%', color: 'warning', icon: 'chart-line' },
    { title: 'Clientes Activos', value: '128', color: 'info', icon: 'users' }
  ]

  return (
    <div id="reports-section">
      <div className="d-flex justify-content-between align-items-center mb-4">
        <div>
          <h4 className="mb-1">Reportes y Análisis</h4>
          <p className="text-muted mb-0">Estadísticas y reportes del sistema</p>
        </div>
        <div className="btn-group">
          <button className="btn btn-danger" onClick={generatePDFReport}>
            <i className="fas fa-file-pdf me-2"></i>Exportar PDF
          </button>
          <button className="btn btn-success" onClick={generateExcelReport}>
            <i className="fas fa-file-excel me-2"></i>Exportar Excel
          </button>
        </div>
      </div>

      {/* Estadísticas rápidas */}
      <div className="row mb-4">
        {stats.map((stat, index) => (
          <div key={index} className="col-md-3 mb-3">
            <div className={`card bg-${stat.color} text-white`}>
              <div className="card-body">
                <div className="d-flex justify-content-between align-items-center">
                  <div>
                    <h4 className="mb-0">{stat.value}</h4>
                    <small>{stat.title}</small>
                  </div>
                  <i className={`fas fa-${stat.icon} fa-2x opacity-50`}></i>
                </div>
              </div>
            </div>
          </div>
        ))}
      </div>

      <div className="row">
        <div className="col-md-8">
          <div className="card shadow">
            <div className="card-header bg-primary text-white">
              <h5 className="mb-0">
                <i className="fas fa-chart-bar me-2"></i>
                Reportes Detallados
              </h5>
            </div>
            <div className="card-body">
              <div className="list-group">
                <button className="list-group-item list-group-item-action" onClick={generatePDFReport}>
                  <i className="fas fa-calendar text-primary me-2"></i>
                  Reporte de Reservas por Mes
                  <span className="badge bg-primary float-end">PDF</span>
                </button>
                <button className="list-group-item list-group-item-action" onClick={generateExcelReport}>
                  <i className="fas fa-users text-success me-2"></i>
                  Reporte de Clientes Activos
                  <span className="badge bg-success float-end">Excel</span>
                </button>
                <button className="list-group-item list-group-item-action" onClick={generatePDFReport}>
                  <i className="fas fa-dollar-sign text-warning me-2"></i>
                  Reporte de Ingresos y Gastos
                  <span className="badge bg-warning float-end">PDF</span>
                </button>
                <button className="list-group-item list-group-item-action" onClick={generateExcelReport}>
                  <i className="fas fa-box text-info me-2"></i>
                  Reporte de Inventario
                  <span className="badge bg-info float-end">Excel</span>
                </button>
                <button className="list-group-item list-group-item-action" onClick={generatePDFReport}>
                  <i className="fas fa-star text-purple me-2"></i>
                  Reporte de Satisfacción
                  <span className="badge bg-purple float-end">PDF</span>
                </button>
              </div>
            </div>
          </div>
        </div>

        <div className="col-md-4">
          <div className="card shadow">
            <div className="card-header bg-success text-white">
              <h5 className="mb-0">
                <i className="fas fa-cogs me-2"></i>
                Configuración de Reportes
              </h5>
            </div>
            <div className="card-body">
              <div className="mb-3">
                <label className="form-label">Rango de Fechas</label>
                <select className="form-select">
                  <option>Últimos 7 días</option>
                  <option>Últimos 30 días</option>
                  <option>Este mes</option>
                  <option>Mes anterior</option>
                  <option>Rango personalizado</option>
                </select>
              </div>
              <div className="mb-3">
                <label className="form-label">Tipo de Reporte</label>
                <select className="form-select">
                  <option>Resumen General</option>
                  <option>Detallado</option>
                  <option>Comparativo</option>
                </select>
              </div>
              <div className="mb-3">
                <label className="form-label">Formato</label>
                <div>
                  <div className="form-check form-check-inline">
                    <input
                      className="form-check-input"
                      type="radio"
                      name="format"
                      checked={format === 'pdf'}
                      onChange={() => setFormat('pdf')}
                    />
                    <label className="form-check-label">PDF</label>
                  </div>
                  <div className="form-check form-check-inline">
                    <input
                      className="form-check-input"
                      type="radio"
                      name="format"
                      checked={format === 'excel'}
                      onChange={() => setFormat('excel')}
                    />
                    <label className="form-check-label">Excel</label>
                  </div>
                </div>
              </div>
              <button className="btn btn-primary w-100" onClick={handleCustomReport}>
                <i className="fas fa-download me-2"></i>Generar Reporte Personalizado
              </button>
            </div>
          </div>
        </div>
      </div>

      <div className="alert alert-info mt-4">
        <i className="fas fa-info-circle me-2"></i>
        Los gráficos interactivos y análisis avanzados estarán disponibles en la próxima actualización del sistema.
      </div>
    </div>
  )
}

export default Reports