import { useState, useEffect, useRef } from 'react' // Agregamos useRef para la imagen
import { packagesAPI, lugaresAPI } from '../../services/api'

function PackageFormModal({ show, onHide, packageId, onSuccess }) {
    // ESTADOS DEL FORMULARIO Y LA IMAGEN
    const [formData, setFormData] = useState({
      nombrePaquete: '',
      descripcion: '',
      precioBase: '',
      duracionDias: '',
      cuposTotales: '', // 🟢 AÑADIDO: Campo para gestionar la capacidad total
      estado: 'activo',
      idLugar: '' // El valor se maneja como String aquí
    })

    const [imageFile, setImageFile] = useState(null); // Archivo para el envío
    const [imagePreviewUrl, setImagePreviewUrl] = useState(null); // URL para la vista previa
    const fileInputRef = useRef(null); // Ref al input de archivo
    
    const [lugares, setLugares] = useState([])
    const [loading, setLoading] = useState(false)
    const [error, setError] = useState('')

    // EFECTO PRINCIPAL (Carga de datos)
    useEffect(() => {
      if (show) {
        loadLugares()
        if (packageId) {
          loadPackage()
        } else {
          resetForm()
        }
      }
    }, [show, packageId])


    // LÓGICA DE CARGA Y RESET
    const loadLugares = async () => { /* ... (Mantener igual) ... */ 
      try {
          const response = await lugaresAPI.getAll()
          if (response.success) {
              setLugares(response.data || [])
          }
      } catch (err) {
          console.error('Error al cargar lugares:', err)
      }
    }

    const loadPackage = async () => {
      try {
        setLoading(true)
        const response = await packagesAPI.getByIdForAdmin(packageId)
        if (response.success && response.data) {
          const pkg = response.data
          setFormData({
            nombrePaquete: pkg.nombrePaquete || '',
            descripcion: pkg.descripcion || '',
            precioBase: pkg.precioBase ? pkg.precioBase.toString() : '',
            duracionDias: pkg.duracionDias ? pkg.duracionDias.toString() : '',
            cuposTotales: pkg.cuposTotales ? pkg.cuposTotales.toString() : '', // 🟢 Cargar el valor
            estado: pkg.estado || 'activo',
            // FIX PERSISTENCIA: Aseguramos que el idLugar sea siempre un String
            idLugar: pkg.idLugar ? String(pkg.idLugar) : '' 
          })
          // Aquí cargarías la URL de la imagen si el backend la devuelve (e.g., pkg.imagenUrl)
          // setImagePreviewUrl(pkg.imagenUrl || null); 
        }
      } catch (err) {
        setError('Error al cargar el paquete')
        console.error(err)
      } finally {
        setLoading(false)
      }
    }

    const resetForm = () => {
      setFormData({
        nombrePaquete: '',
        descripcion: '',
        precioBase: '',
        duracionDias: '',
        cuposTotales: '', // 🟢 Resetear el valor
        estado: 'activo',
        idLugar: ''
      })
      setError('')
      setImageFile(null); 
      setImagePreviewUrl(null); 
    }

    const handleInputChange = (e) => {
      const { name, value } = e.target
      setFormData(prev => ({
        ...prev,
        [name]: value
      }))
    }

    // HANDLERS DE IMAGEN
    const handleFileChange = (e) => {
        const file = e.target.files[0];
        if (file) {
            setImageFile(file);
            setImagePreviewUrl(URL.createObjectURL(file)); 
        } else {
            setImageFile(null);
            setImagePreviewUrl(null);
        }
    };

    const triggerFileInput = () => {
        fileInputRef.current.click();
    };


    // FUNCIÓN DE ENVÍO (Actualizada para FormData y Cupos Totales)
    const handleSubmit = async (e) => {
      e.preventDefault()
      setLoading(true)
      setError('')

      try {
          // 1. CREAR FORMDATA
          const formPackageData = new FormData();
          
          // 2. Adjuntar el DTO (todos los campos)
          formPackageData.append('nombrePaquete', formData.nombrePaquete);
          formPackageData.append('descripcion', formData.descripcion);
          formPackageData.append('precioBase', parseFloat(formData.precioBase));
          // Importante: Los campos de número deben ser parseados ANTES de enviarse si no son string
          formPackageData.append('duracionDias', formData.duracionDias ? parseInt(formData.duracionDias) : '');
          // 🟢 AGREGAR CUPOS TOTALES
          formPackageData.append('cuposTotales', formData.cuposTotales ? parseInt(formData.cuposTotales) : 0);
          
          formPackageData.append('estado', formData.estado);
          // FIX: Enviamos el ID del Lugar como String, el backend lo parseará.
          formPackageData.append('idLugar', formData.idLugar); 
          if (packageId) {
             formPackageData.append('idPaquete', packageId);
          }

          // 3. Adjuntar la imagen (el archivo)
          if (imageFile) {
              formPackageData.append('imagen', imageFile); 
          }


        let response
        if (packageId) {
        response = await packagesAPI.update(packageId, formPackageData)
      } else {
        response = await packagesAPI.create(formPackageData)
      }

        if (response.success) {
          onSuccess()
          onHide()
          resetForm()
        } else {
          setError(response.message || 'Error al guardar el paquete')
        }
      } catch (err) {
        setError(err.response?.data?.message || 'Error al guardar el paquete')
        console.error(err)
      } finally {
        setLoading(false)
      }
    }

    if (!show) return null

    return (
      <div 
        className="modal fade show" 
        style={{ display: 'block', backgroundColor: 'rgba(0,0,0,0.5)' }}
        tabIndex="-1"
      >
        <div className="modal-dialog modal-dialog-centered modal-lg">
          <div className="modal-content">
            <div className="modal-header">
              <h5 className="modal-title">
                <i className="fas fa-box me-2"></i>
                {packageId ? 'Editar Paquete' : 'Nuevo Paquete'}
              </h5>
              <button 
                type="button" 
                className="btn-close" 
                onClick={onHide}
                disabled={loading}
              ></button>
            </div>
            
            <form onSubmit={handleSubmit}>
              <div className="modal-body">
                {error && (
                  <div className="alert alert-danger">
                    <i className="fas fa-exclamation-triangle me-2"></i>
                    {error}
                  </div>
                )}

                {/* === ZONA DE SUBIDA DE IMAGEN === */}
                <div className="row mb-4">
                    <div className="col-12">
                        <div 
                            className="image-upload-area" 
                            onClick={triggerFileInput}
                        >
                            {imagePreviewUrl ? (
                                <img 
                                    src={imagePreviewUrl} 
                                    alt="Foto Referencial" 
                                    style={{ objectFit: 'cover', width: '100%', height: '100%' }}
                                />
                            ) : (
                                <>
                                    <i className="fas fa-camera fa-2x text-muted mb-2"></i>
                                    <p className="text-muted mb-0 fw-bold">Subir Foto Referencial</p>
                                    <small className="text-muted">Click para seleccionar archivo</small>
                                </>
                            )}
                            
                            {/* INPUT REAL (OCULTO) */}
                            <input
                                type="file"
                                ref={fileInputRef}
                                onChange={handleFileChange}
                                style={{ display: 'none' }}
                                accept="image/*"
                            />
                        </div>
                    </div>
                </div>
                {/* === FIN ZONA DE SUBIDA === */}

                {/* BOTONES AUXILIARES DE IMAGEN */}
                <div className="d-flex gap-3 mb-4">
                    <button 
                        type="button" 
                        className="btn btn-outline-secondary"
                        onClick={triggerFileInput} 
                        disabled={loading}
                    >
                        <i className="fas fa-folder-open me-2"></i> Seleccionar Archivo
                    </button>
                    {/* Botón "Tomar Foto" (Funcionalidad avanzada) */}
                    <button type="button" className="btn btn-outline-secondary" disabled={loading}>
                        <i className="fas fa-camera me-2"></i> Tomar Foto
                    </button>
                </div>
                
                {/* EL RESTO DE LOS CAMPOS... */}
                <div className="mb-3">
                <label htmlFor="nombrePaquete" className="form-label fw-bold">
                  Nombre del Paquete *
                </label>
                <input 
                  type="text" 
                  className="form-control"
                  id="nombrePaquete"
                  name="nombrePaquete"
                  value={formData.nombrePaquete}
                  onChange={handleInputChange}
                  required
                  disabled={loading}
                  maxLength={150}
                />
              </div>

              <div className="mb-3">
                <label htmlFor="descripcion" className="form-label fw-bold">
                  Descripción
                </label>
                <textarea 
                  className="form-control"
                  id="descripcion"
                  name="descripcion"
                  rows="4"
                  value={formData.descripcion}
                  onChange={handleInputChange}
                  disabled={loading}
                />
              </div>

              <div className="row">
                <div className="col-md-6 mb-3">
                  <label htmlFor="precioBase" className="form-label fw-bold">
                    Precio Base *
                  </label>
                  <div className="input-group">
                    <span className="input-group-text">S/</span>
                    <input 
                      type="number" 
                      className="form-control"
                      id="precioBase"
                      name="precioBase"
                      step="0.01"
                      min="0"
                      value={formData.precioBase}
                      onChange={handleInputChange}
                      required
                      disabled={loading}
                    />
                  </div>
                </div>

                <div className="col-md-6 mb-3">
                  <label htmlFor="duracionDias" className="form-label fw-bold">
                    Duración (días)
                  </label>
                  <input 
                    type="number" 
                    className="form-control"
                    id="duracionDias"
                    name="duracionDias"
                    min="1"
                    value={formData.duracionDias}
                    onChange={handleInputChange}
                    disabled={loading}
                  />
                </div>
              </div>

              <div className="row">
                {/* 🟢 Columna 1: Capacidad Total (col-md-4) */}
                <div className="col-md-4 mb-3">
                  <label htmlFor="cuposTotales" className="form-label fw-bold">
                    Capacidad Total *
                  </label>
                  <input 
                    type="number" 
                    className="form-control"
                    id="cuposTotales"
                    name="cuposTotales"
                    min="1"
                    value={formData.cuposTotales}
                    onChange={handleInputChange}
                    required
                    disabled={loading}
                  />
                </div>
                
                <div className="col-md-4 mb-3">
                  <label htmlFor="idLugar" className="form-label fw-bold">
                    Lugar *
                  </label>
                  <select 
                    className="form-select"
                    id="idLugar"
                    name="idLugar"
                    value={formData.idLugar}
                    onChange={handleInputChange}
                    required
                    disabled={loading}
                  >
                    <option value="">Seleccione un lugar</option>
                    {lugares.map(lugar => (
                      <option key={lugar.idLugar} value={lugar.idLugar}>
                        {lugar.nombreLugar} - {lugar.ciudad}
                      </option>
                    ))}
                  </select>
                </div>

                <div className="col-md-4 mb-3">
                  <label htmlFor="estado" className="form-label fw-bold">
                    Estado
                  </label>
                  <select 
                    className="form-select"
                    id="estado"
                    name="estado"
                    value={formData.estado}
                    onChange={handleInputChange}
                    disabled={loading}
                  >
                    <option value="activo">Activo</option>
                    <option value="inactivo">Inactivo</option>
                  </select>
                </div>
              </div>
            </div>

            <div className="modal-footer">
              <button 
                type="button" 
                className="btn btn-secondary"
                onClick={onHide}
                disabled={loading}
              >
                Cancelar
              </button>
              <button 
                type="submit" 
                className="btn btn-primary-custom"
                disabled={loading}
              >
                {loading ? (
                  <>
                    <span className="spinner-border spinner-border-sm me-2"></span>
                    Guardando...
                  </>
                ) : (
                  <>
                    <i className="fas fa-save me-2"></i>
                    {packageId ? 'Actualizar' : 'Crear'}
                  </>
                )}
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  )
}

export default PackageFormModal